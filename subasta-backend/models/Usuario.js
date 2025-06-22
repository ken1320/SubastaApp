// models/Usuario.js
const mongoose = require('mongoose');

const UsuarioSchema = mongoose.Schema({
    nombre: {
        type: String,
        required: true
    },
    email: {
        type: String,
        required: true,
        unique: true
    },
    password: {
        type: String,
        required: true
    },
    fechaRegistro: {
        type: Date,
        default: Date.now
    }
    // Agrega aquí cualquier otro campo relacionado con el usuario
});

module.exports = mongoose.model('Usuario', UsuarioSchema);