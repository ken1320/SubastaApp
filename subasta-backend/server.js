const express = require('express');
const connectDB = require('./config/db'); // Importa la función de conexión a DB
const cors = require('cors'); // Importa CORS
require('dotenv').config(); // Para cargar variables de entorno

const app = express();

// Conectar a la base de datos
connectDB();

// Middleware
app.use(express.json({ extended: false })); // Para parsear el body de las peticiones en formato JSON
app.use(cors()); // Habilitar CORS para todas las rutas

// Definir Rutas
app.use('/api/subastas', require('./routes/subastas')); // Todas las rutas de subastas

// Ruta de prueba
app.get('/', (req, res) => {
    res.send('API de Subastas funcionando!');
});

const PORT = process.env.PORT || 5000; // Usa el puerto de .env o 5000 por defecto

app.listen(PORT, () => console.log(`Servidor corriendo en el puerto ${PORT}`));