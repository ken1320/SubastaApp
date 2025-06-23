package com.example.subastaapp.model

import com.google.gson.annotations.SerializedName
import java.util.Date

// Nuevo data class para representar cada puesto
data class Puesto(
    val numero: Int,
    val ocupadoPor: String? = null,
    val montoPuja: Double,
    val fechaOcupacion: Date? = null
)

// Nuevo data class para un usuario simple para populación
data class UsuarioSimple(
    @SerializedName("_id") // Le dice a GSON que mapee el campo "_id" del JSON aquí
    val id: String,        // Usamos 'id' por consistencia en Kotlin
    val nombre: String
)

data class Subasta(
    @SerializedName("_id") // <--- AÑADE ESTA LÍNEA
    val id: String, // Ahora GSON sabe que _id en JSON corresponde a este campo 'id'
    val titulo: String,
    val descripcion: String,
    val precioInicial: Double,
    val precioActual: Double,
    val fechaInicio: Date,
    val fechaFin: Date,
    val estado: String,
    val imagenUrl: String? = null,
    val puestos: List<Puesto>,
    val puestoGanador: Int? = null,
    val pujaGanadora: Double? = null,
    val ganadorId: String? = null
)

// SubastaCreationRequest se mantiene igual, ya que la inicialización de 'puestos' la hace el backend
data class SubastaCreationRequest(
    val titulo: String,
    val descripcion: String,
    val precioInicial: Double,
    val fechaFin: Date,
    val imagenUrl: String? = null
)

// PujaRequest cambia para ser una 'OcuparPuestoRequest'
data class OcuparPuestoRequest(
    val puestoNumero: Int,
    val montoPuja: Double,
    val pujadorId: String // Aquí le puedes cambiar el nombre si quieres: 'comprador'
)
