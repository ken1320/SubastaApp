const express = require('express');
const router = express.Router();
const Subasta = require('../models/Subasta');
const Puja = require('../models/Puja'); // Todavía se usa si quieres un historial detallado de cada puja

// @route   GET /api/subastas
// @desc    Obtener todas las subastas
router.get('/', async (req, res) => {
    try {
        // Necesitamos poblar los puestos si quieres ver la info detallada de ocupadoPor
        // Esto asume que tienes un modelo de Usuario y que pujadorId es el ID de un usuario
        const subastas = await Subasta.find()
                                        .populate({
                                            path: 'puestos.ocupadoPor', // Popula el campo ocupadoPor dentro de cada objeto en el array 'puestos'
                                            select: 'nombre' // Selecciona solo el campo 'nombre' del usuario si existe
                                        })
                                        .sort({ fechaInicio: -1 });
        res.json(subastas);
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Error del servidor');
    }
});

// @route   POST /api/subastas
// @desc    Crear una nueva subasta
router.post('/', async (req, res) => {
    const { titulo, descripcion, precioInicial, fechaFin, imagenUrl } = req.body;

    if (!titulo || !descripcion || !precioInicial || !fechaFin) {
        return res.status(400).json({ msg: 'Por favor, introduce todos los campos obligatorios: titulo, descripcion, precioInicial, fechaFin' });
    }
    if (new Date(fechaFin) <= new Date()) {
        return res.status(400).json({ msg: 'La fecha de fin debe ser futura' });
    }

    try {
        const nuevaSubasta = new Subasta({
            titulo,
            descripcion,
            precioInicial,
            fechaFin,
            imagenUrl,
            // Los 'puestos' se inicializarán automáticamente con el 'default' en el esquema
        });

        const subasta = await nuevaSubasta.save();
        res.status(201).json(subasta);
    } catch (err) {
        console.error('Error al crear la subasta:', err.message);
        res.status(500).send('Error del servidor al crear la subasta');
    }
});

// @route   POST /api/subastas/:id/ocuparPuesto
// @desc    Ocupar un puesto específico en una subasta
router.post('/:id/ocuparPuesto', async (req, res) => {
    const { puestoNumero, montoPuja, pujadorId } = req.body;
    const { id } = req.params;

    // Validaciones
    if (!puestoNumero || !montoPuja || !pujadorId) {
        return res.status(400).json({ msg: 'Número de puesto, monto de puja y ID del pujador son obligatorios' });
    }
    if (puestoNumero < 1 || puestoNumero > 100) {
        return res.status(400).json({ msg: 'El número de puesto debe estar entre 1 y 100' });
    }

    try {
        const subasta = await Subasta.findById(id);

        if (!subasta) {
            return res.status(404).json({ msg: 'Subasta no encontrada' });
        }
        if (subasta.estado !== 'activa') {
            return res.status(400).json({ msg: 'La subasta no está activa para ocupar puestos' });
        }
        if (new Date() > new Date(subasta.fechaFin)) {
            subasta.estado = 'finalizada'; // Actualizar estado si la fecha ha pasado
            await subasta.save();
            return res.status(400).json({ msg: 'La subasta ha finalizado' });
        }

        const puestoIndex = puestoNumero - 1; // Los arrays son base 0
        if (subasta.puestos[puestoIndex].ocupadoPor !== null) {
            return res.status(400).json({ msg: `El puesto ${puestoNumero} ya está ocupado.` });
        }
        if (montoPuja <= subasta.precioInicial) { // Si hay una oferta mínima general
            return res.status(400).json({ msg: `La puja debe ser mayor que el precio inicial (${subasta.precioInicial})` });
        }

        // Ocupar el puesto
        subasta.puestos[puestoIndex].ocupadoPor = pujadorId;
        subasta.puestos[puestoIndex].montoPuja = montoPuja;
        subasta.puestos[puestoIndex].fechaOcupacion = new Date();

        // Opcional: Actualizar el precioActual de la subasta al monto más alto si es necesario
        if (montoPuja > subasta.precioActual) {
            subasta.precioActual = montoPuja;
        }

        await subasta.save();

        res.status(200).json({ msg: `Puesto ${puestoNumero} ocupado con éxito`, subastaActualizada: subasta });
    } catch (err) {
        console.error('Error al ocupar el puesto:', err.message);
        res.status(500).send('Error del servidor al ocupar el puesto');
    }
});

// @route   POST /api/subastas/:id/finalizar
// @desc    Finalizar una subasta y determinar el ganador del puesto
router.post('/:id/finalizar', async (req, res) => {
    const { id } = req.params;

    try {
        const subasta = await Subasta.findById(id).populate('puestos.ocupadoPor'); // Popula para obtener info del ganador

        if (!subasta) {
            return res.status(404).json({ msg: 'Subasta no encontrada' });
        }
        if (subasta.estado === 'finalizada') {
            return res.status(400).json({ msg: 'La subasta ya está finalizada' });
        }

        subasta.estado = 'finalizada';

        // Lógica para determinar el puesto y la puja ganadora
        let mejorPuesto = null;
        let mejorPuja = 0;
        let ganadorId = null;

        subasta.puestos.forEach(puesto => {
            if (puesto.ocupadoPor && puesto.montoPuja > mejorPuja) {
                mejorPuja = puesto.montoPuja;
                mejorPuesto = puesto.numero;
                ganadorId = puesto.ocupadoPor._id; // Al ser populado, ._id es el ID del usuario
            }
        });

        subasta.puestoGanador = mejorPuesto;
        subasta.pujaGanadora = mejorPuja;
        subasta.ganadorId = ganadorId; // Guarda el ID del ganador

        await subasta.save();

        res.status(200).json({
            msg: 'Subasta finalizada con éxito',
            subasta,
            resultado: {
                puestoGanador: mejorPuesto,
                pujaGanadora: mejorPuja,
                ganadorNombre: subasta.puestos.find(p => p.numero === mejorPuesto)?.ocupadoPor?.nombre || 'N/A' // Si el nombre del usuario se populó
            }
        });
    } catch (err) {
        console.error('Error al finalizar la subasta:', err.message);
        res.status(500).send('Error del servidor al finalizar');
    }
});

// @route   DELETE /api/subastas/:id
// @desc    Eliminar una subasta
router.delete('/:id', async (req, res) => {
    const { id } = req.params;

    try {
        const subasta = await Subasta.findById(id);

        if (!subasta) {
            return res.status(404).json({ msg: 'Subasta no encontrada' });
        }

        await Subasta.deleteOne({ _id: id }); // O puedes usar findByIdAndDelete(id)

        res.status(200).json({ msg: 'Subasta eliminada con éxito' });
    } catch (err) {
        console.error('Error al eliminar la subasta:', err.message);
        res.status(500).send('Error del servidor al eliminar');
    }
});

module.exports = router;