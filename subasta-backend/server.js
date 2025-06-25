// server.js
const express = require('express');
const connectDB = require('./config/db');
const cors = require('cors');
const path = require('path');
require('dotenv').config();

const app = express();

// Conectar a la base de datos
connectDB();

// IMPORTANTE: Asegúrate de que tus modelos sean requeridos para que Mongoose los registre
require('./models/Subasta');
require('./models/Puja');
require('./models/Usuario'); // <--- AÑADE ESTA LÍNEA

// Middleware
app.use(express.json({ extended: false }));
app.use(cors());

// Definir Rutas
app.use('/api/subastas', require('./routes/subastas'));
app.use('/api/upload', require('./routes/upload'));

// Ruta de prueba
app.get('/', (req, res) => {
    res.send('API de Subastas funcionando!');
});
app.use(express.static(path.join(__dirname, 'public')));

const PORT = process.env.PORT || 5000;

app.listen(PORT, () => console.log(`Servidor corriendo en el puerto ${PORT}`));