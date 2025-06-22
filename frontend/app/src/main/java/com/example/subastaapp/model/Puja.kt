package com.example.subastaapp.model
import com.google.gson.annotations.SerializedName
import java.util.Date // Usaremos java.util.Date para las fechas para mayor compatibilidad con JSON de backend

// Data class para la información de una puja individual
data class Puja(
    @SerializedName("_id")
    val id: String,
    val subastaId: String,
    val pujador: String, // ID del pujador (coincide con 'nombre' en tus errores anteriores)
    val monto: Double, // Coincide con 'valor' en tus errores anteriores
    val fechaPuja: Date
)

// Data class para la información necesaria para realizar una puja
data class PujaRequest(
    val pujador: String, // El ID del pujador
    val monto: Double
)
