package com.example.subastaapp.model
import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Representa una puja individual.
 */
data class Puja(
    @SerializedName("_id")
    val id: String, // ID de la puja.
    val subastaId: String, // ID de la subasta.
    val pujador: String, // ID del pujador.
    val monto: Double, // Monto de la puja.
    val fechaPuja: Date // Fecha de la puja.
)

/**
 * Datos para realizar una puja.
 */
data class PujaRequest(
    val pujador: String, // ID del pujador.
    val monto: Double // Monto a pujar.
)
