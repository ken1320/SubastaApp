package com.example.subastaapp.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Representa un puesto dentro de una subasta.
 */
data class Puesto(
    val numero: Int, // Número del puesto.
    val ocupadoPor: String? = null, // ID del ocupante.
    val montoPuja: Double, // Monto de la puja.
    val fechaOcupacion: Date? = null // Fecha de ocupación.
)

/**
 * Representa un usuario simplificado.
 */
data class UsuarioSimple(
    @SerializedName("_id")
    val id: String, // ID del usuario.
    val nombre: String // Nombre del usuario.
)

/**
 * Representa una subasta con todos sus detalles.
 */
data class Subasta(
    @SerializedName("_id")
    val id: String, // ID de la subasta.
    val titulo: String, // Título de la subasta.
    val descripcion: String, // Descripción.
    val precioInicial: Double, // Precio de inicio.
    val precioActual: Double, // Precio actual.
    val fechaInicio: Date, // Fecha de inicio.
    val fechaFin: Date, // Fecha de fin.
    val estado: String, // Estado.
    val imagenUrl: String? = null, // URL de la imagen.
    val puestos: List<Puesto>, // Lista de puestos.
    val puestoGanador: Int? = null, // Puesto ganador.
    val pujaGanadora: Double? = null, // Puja ganadora.
    val ganadorId: String? = null // ID del ganador.
)

/**
 * Datos para crear una nueva subasta.
 */
data class SubastaCreationRequest(
    val titulo: String, // Título.
    val descripcion: String, // Descripción.
    val precioInicial: Double, // Precio inicial.
    val fechaFin: Date, // Fecha de fin.
    val imagenUrl: String? = null // URL de la imagen (opcional).
)

/**
 * Solicitud para ocupar un puesto en una subasta.
 */
data class OcuparPuestoRequest(
    val puestoNumero: Int, // Número de puesto.
    val montoPuja: Double, // Monto de la puja.
    val pujadorId: String // ID del pujador.
)
