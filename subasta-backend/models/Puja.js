const mongoose = require('mongoose');

const PujaSchema = mongoose.Schema({
    monto: {
        type: Number,
        required: true,
        min: 0
    },
    subasta: { // Referencia a la subasta a la que pertenece esta puja
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Subasta',
        required: true
    },
    pujador: { // ID del usuario que realiza la puja
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Usuario', // Si tuvieras un modelo de Usuario
        required: true // Opcional, si siempre requieres un usuario para pujar
    },
    fechaPuja: {
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model('Puja', PujaSchema);