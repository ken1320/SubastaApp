package com.example.subastaapp.model

import java.util.Date
import com.google.gson.annotations.SerializedName

// Data class para la Subasta completa que viene del backend
data class Subasta(
    @SerializedName("_id")
    val id: String,
    val titulo: String,
    val descripcion: String,
    val precioInicial: Double,
    val precioActual: Double,
    val fechaInicio: Date,
    val fechaFin: Date,
    val estado: String, // "activa", "finalizada", "cancelada"
    val ganador: String? = null,
    val ultimaPuja: Puja? = null,
    val imagenUrl: String? = null, // Ya existente, importante para la visualización
    val pujas: List<Puja>? = null
)

// Data class para la información necesaria para crear una nueva subasta
data class SubastaCreationRequest(
    val titulo: String,
    val descripcion: String,
    val precioInicial: Double,
    val fechaFin: Date,
    val imagenUrl: String? = null // ¡NUEVO! Para enviar la URL de la imagen al crear
)