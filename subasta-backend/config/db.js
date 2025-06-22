const mongoose = require('mongoose');
require('dotenv').config(); // Cargar variables de entorno

const connectDB = async () => {
    try {
        const conn = await mongoose.connect(process.env.MONGO_URI);
        console.log(`MongoDB Conectado: ${conn.connection.host}`);
    } catch (error) {
        console.error(`Error de conexión a MongoDB: ${error.message}`);
        process.exit(1); // Salir del proceso con error
    }
};

module.exports = connectDB;
