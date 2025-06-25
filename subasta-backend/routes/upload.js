const express = require('express');
const multer = require('multer');
const path = require('path');
const fs = require('fs');

const router = express.Router();

// Asegurarse de que el directorio de subidas exista
const uploadDir = path.join(__dirname, '../public/uploads');
if (!fs.existsSync(uploadDir)){
    fs.mkdirSync(uploadDir, { recursive: true });
}

// Configuración de Multer para almacenamiento
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, uploadDir); // Directorio donde se guardarán los archivos
  },
  filename: function (req, file, cb) {
    // Generar un nombre de archivo único para evitar colisiones
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
    cb(null, file.fieldname + '-' + uniqueSuffix + path.extname(file.originalname));
  }
});

// Filtro para aceptar solo imágenes
const fileFilter = (req, file, cb) => {
  if (file.mimetype.startsWith('image')) {
    cb(null, true);
  } else {
    cb(new Error('¡Solo se permiten archivos de imagen!'), false);
  }
};

const upload = multer({ storage: storage, fileFilter: fileFilter });

// @route   POST /api/upload
// @desc    Subir un archivo de imagen
// @access  Public
router.post('/', upload.single('image'), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ msg: 'No se ha subido ningún archivo.' });
  }
  
  try {
    // Devuelve la URL pública del archivo subido.
    // OJO: La URL base (http://<tu_ip>:<puerto>) la debe conocer el cliente.
    // Aquí solo devolvemos la ruta relativa.
    const fileUrl = `/uploads/${req.file.filename}`;
    res.status(200).json({ 
        msg: 'Archivo subido con éxito', 
        filePath: fileUrl 
    });
  } catch (err) {
    console.error('Error al subir el archivo:', err);
    res.status(500).send('Error del servidor');
  }
});

module.exports = router;