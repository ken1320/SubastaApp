const mongoose = require('mongoose');

const SubastaSchema = mongoose.Schema({
    titulo: {
        type: String,
        required: true,
        trim: true
    },
    descripcion: {
        type: String,
        required: true
    },
    precioInicial: {
        type: Number,
        required: true,
        min: 0
    },
    precioActual: { // Se actualiza con cada puja
        type: Number,
        default: function() { return this.precioInicial; } // Valor por defecto igual al inicial
    },
    fechaInicio: {
        type: Date,
        default: Date.now // La fecha actual al crear la subasta
    },
    fechaFin: {
        type: Date,
        required: true
    },
    estado: {
        type: String,
        enum: ['activa', 'finalizada', 'cancelada'],
        default: 'activa'
    },
    ganador: { // ID del usuario que ganó la subasta
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Usuario', // Si tuvieras un modelo de Usuario
        default: null
    },
    ultimaPuja: { // Para saber quién hizo la última puja
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Puja',
        default: null
    },
    // ¡¡¡NUEVO CAMPO PARA LA IMAGEN!!!
    imagenUrl: {
        type: String, // Las URLs son cadenas de texto
        default: null // Opcional: puedes poner un valor por defecto si no se proporciona
    }
});

module.exports = mongoose.model('Subasta', SubastaSchema);