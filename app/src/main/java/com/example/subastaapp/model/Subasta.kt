package com.example.subastaapp.model

import java.util.Date

// Data class para la Subasta completa que viene del backend
data class Subasta(
    val id: String,
    val titulo: String, // Coincide con 'nombre' en tus errores anteriores
    val descripcion: String,
    val precioInicial: Double, // Coincide con 'ofertaMinima' en tus errores anteriores
    val precioActual: Double,
    val fechaInicio: Date,
    val fechaFin: Date,
    val estado: String, // "activa", "finalizada", "cancelada"
    val ganador: String? = null, // ID del pujador ganador (si lo hay)
    val ultimaPuja: Puja? = null, // La última puja realizada en la subasta
    val imagenUrl: String? = null // Añadido para la imagen, si tu backend lo proporciona
    // Si tu backend devuelve el historial completo de pujas, descomenta la siguiente línea:
    // val pujas: List<Puja>? = null
)

// Data class para la información necesaria para crear una nueva subasta
data class SubastaCreationRequest(
    val titulo: String,
    val descripcion: String,
    val precioInicial: Double,
    val fechaFin: Date
)

