const mongoose = require('mongoose');

// Definir el esquema para cada Puesto dentro de la subasta
const PuestoSchema = mongoose.Schema({
  numero: Number,
  // 🔁 CAMBIAR ESTO
  // ocupadoPor: { type: mongoose.Schema.Types.ObjectId, ref: 'Usuario' },
  ocupadoPor: { type: String }, // Solo nombre del comprador

  montoPuja: Number,
  fechaOcupacion: { type: Date, default: Date.now }
});


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
    // precioActual y ultimaPuja ya no son directamente aplicables a la subasta general
    // Ahora, cada puesto tendrá su propio monto de puja.
    // Podrías mantener precioActual si quieres que represente la puja más alta en general.
    precioActual: { // Se actualizará con la puja más alta entre todos los puestos
        type: Number,
        default: function() { return this.precioInicial; }
    },
    fechaInicio: {
        type: Date,
        default: Date.now
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
    // Ganador ya no es un solo ID, será un objeto con puesto y puja ganadora
    // ganador: { // Este campo se determinará al finalizar
    //     type: mongoose.Schema.Types.ObjectId,
    //     ref: 'Usuario',
    //     default: null
    // },
    // ultimaPuja ya no aplica de la misma manera
    // ultimaPuja: {
    //     type: mongoose.Schema.Types.ObjectId,
    //     ref: 'Puja',
    //     default: null
    // },
    imagenUrl: {
        type: String,
        default: null
    },
    // ¡¡¡NUEVO CAMPO CRÍTICO PARA LOS PUESTOS!!!
    puestos: {
        type: [PuestoSchema], // Array de objetos Puesto
        default: () => Array.from({ length: 100 }, (_, i) => ({
            numero: i + 1,
            ocupadoPor: null,
            montoPuja: 0,
            fechaOcupacion: null
        }))
    },
    // Campos para el ganador final (se llenan al finalizar)
    puestoGanador: {
        type: Number,
        default: null
    },
    pujaGanadora: {
        type: Number,
        default: null
    },
    ganadorId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Usuario',
        default: null
    }
});

module.exports = mongoose.model('Subasta', SubastaSchema);