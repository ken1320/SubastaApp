const express = require('express');
const router = express.Router();
const Subasta = require('../models/Subasta'); // Asegúrate de que la ruta sea correcta
const Puja = require('../models/Puja');     // Asegúrate de que la ruta sea correcta

// @route   GET /api/subastas
// @desc    Obtener todas las subastas
// @access  Public (o lo que corresponda en tu app)
router.get('/', async (req, res) => {
    try {
        // .find() recupera todos los documentos.
        // .populate('ultimaPuja') si quieres que el objeto de la última puja se incluya en la respuesta.
        // .sort({ fechaInicio: -1 }) para ordenar por fecha de inicio descendente.
        const subastas = await Subasta.find().populate('ultimaPuja').sort({ fechaInicio: -1 });
        res.json(subastas);
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Error del servidor');
    }
});

// @route   POST /api/subastas
// @desc    Crear una nueva subasta
// @access  Public (o lo que corresponda en tu app)
router.post('/', async (req, res) => {
    // DESESTRUCTURACIÓN: Extrae los campos del cuerpo de la solicitud (req.body).
    // ¡Aquí se incluye 'imagenUrl' que tu aplicación Android envía!
    const { titulo, descripcion, precioInicial, fechaFin, imagenUrl } = req.body;

    // --- Validaciones básicas (puedes añadir más si es necesario) ---
    if (!titulo || !descripcion || !precioInicial || !fechaFin) {
        return res.status(400).json({ msg: 'Por favor, introduce todos los campos obligatorios: titulo, descripcion, precioInicial, fechaFin' });
    }
    // Opcional: Validar que imagenUrl no esté vacía si es requerida
    // if (!imagenUrl || imagenUrl.trim() === '') {
    //     return res.status(400).json({ msg: 'La URL de la imagen es obligatoria' });
    // }

    // Validación de fecha de fin
    if (new Date(fechaFin) <= new Date()) {
        return res.status(400).json({ msg: 'La fecha de fin debe ser futura' });
    }

    try {
        // Crea una nueva instancia del modelo Subasta con los datos recibidos.
        // ¡Aquí se asigna 'imagenUrl' al modelo de Mongoose!
        const nuevaSubasta = new Subasta({
            titulo,
            descripcion,
            precioInicial,
            // precioActual se establecerá al precioInicial por defecto en el esquema
            // fechaInicio se establecerá a la fecha actual por defecto en el esquema
            fechaFin,
            estado: 'activa', // El estado por defecto ya está en el esquema, pero puedes forzarlo aquí si quieres
            imagenUrl // Esto es un shorthand para 'imagenUrl: imagenUrl'
        });

        // Guarda la nueva subasta en la base de datos.
        const subasta = await nuevaSubasta.save();

        // Responde con el estado 201 (Created) y el objeto de la subasta recién creada.
        // Esta respuesta incluirá la imagenUrl si se guardó correctamente.
        res.status(201).json(subasta);
    } catch (err) {
        console.error('Error al crear la subasta:', err.message);
        // Puedes añadir más detalles de error si 'err' contiene propiedades específicas (ej. err.code)
        res.status(500).send('Error del servidor al crear la subasta');
    }
});

// @route   POST /api/subastas/:id/pujar
// @desc    Realizar una puja en una subasta
// @access  Public (o lo que corresponda en tu app)
router.post('/:id/pujar', async (req, res) => {
    const { monto, pujadorId } = req.body; // pujadorId sería el ID del usuario que puja
    const { id } = req.params;

    if (!monto || !pujadorId) {
        return res.status(400).json({ msg: 'Monto de puja y ID del pujador son obligatorios' });
    }

    try {
        const subasta = await Subasta.findById(id);

        if (!subasta) {
            return res.status(404).json({ msg: 'Subasta no encontrada' });
        }
        if (subasta.estado !== 'activa') {
            return res.status(400).json({ msg: 'La subasta no está activa para pujar' });
        }
        // Verificar si la fecha de fin ha pasado antes de permitir la puja
        if (new Date() > new Date(subasta.fechaFin)) {
            // Opcional: Actualizar el estado a 'finalizada' si la fecha ha pasado y no se ha hecho manualmente
            if (subasta.estado === 'activa') {
                subasta.estado = 'finalizada';
                await subasta.save();
            }
            return res.status(400).json({ msg: 'La subasta ha finalizado' });
        }
        if (monto <= subasta.precioActual) {
            return res.status(400).json({ msg: `La puja debe ser mayor que el precio actual (${subasta.precioActual})` });
        }

        const nuevaPuja = new Puja({
            monto,
            subasta: id,
            pujador: pujadorId // Aquí iría el ID del usuario
        });

        await nuevaPuja.save();

        // Actualizar la subasta con el nuevo precio y la última puja
        subasta.precioActual = monto;
        subasta.ultimaPuja = nuevaPuja._id; // Guardar la referencia a la última puja
        await subasta.save();

        res.status(200).json({ msg: 'Puja realizada con éxito', nuevaPuja, subastaActualizada: subasta });
    } catch (err) {
        console.error('Error al realizar la puja:', err.message);
        res.status(500).send('Error del servidor al pujar');
    }
});

// @route   POST /api/subastas/:id/finalizar
// @desc    Finalizar una subasta manualmente
// @access  Public (o lo que corresponda en tu app)
router.post('/:id/finalizar', async (req, res) => {
    const { id } = req.params;

    try {
        // Popula 'ultimaPuja' para poder acceder al 'pujador' si existe
        const subasta = await Subasta.findById(id).populate('ultimaPuja');

        if (!subasta) {
            return res.status(404).json({ msg: 'Subasta no encontrada' });
        }
        if (subasta.estado === 'finalizada') {
            return res.status(400).json({ msg: 'La subasta ya está finalizada' });
        }

        subasta.estado = 'finalizada';
        if (subasta.ultimaPuja) {
            subasta.ganador = subasta.ultimaPuja.pujador; // El ganador es el último pujador
        } else {
            subasta.ganador = null; // No hubo pujas
        }

        await subasta.save();

        res.status(200).json({ msg: 'Subasta finalizada con éxito', subasta });
    } catch (err) {
        console.error('Error al finalizar la subasta:', err.message);
        res.status(500).send('Error del servidor al finalizar');
    }
});

// @route   DELETE /api/subastas/:id
// @desc    Eliminar una subasta
// @access  Public (o lo que corresponda en tu app)
router.delete('/:id', async (req, res) => {
    const { id } = req.params;

    try {
        const subasta = await Subasta.findById(id);

        if (!subasta) {
            return res.status(404).json({ msg: 'Subasta no encontrada' });
        }

        // Eliminar la subasta por su ID
        await Subasta.deleteOne({ _id: id }); // O puedes usar findByIdAndDelete(id)

        res.status(200).json({ msg: 'Subasta eliminada con éxito' });
    } catch (err) {
        console.error('Error al eliminar la subasta:', err.message);
        res.status(500).send('Error del servidor al eliminar');
    }
});

module.exports = router;