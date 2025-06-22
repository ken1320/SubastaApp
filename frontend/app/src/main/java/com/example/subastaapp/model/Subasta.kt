package com.example.subastaapp.model

import java.util.Date

// Nuevo data class para representar cada puesto
data class Puesto(
    val numero: Int,
    val ocupadoPor: UsuarioSimple? = null, // Podría ser null si no está ocupado
    val montoPuja: Double,
    val fechaOcupacion: Date? = null
)

// Nuevo data class para un usuario simple para populación
data class UsuarioSimple(
    val _id: String, // Asumiendo que el ID del usuario es _id en MongoDB
    val nombre: String // Asumiendo que tienes un campo 'nombre' en tu modelo de Usuario
)

data class Subasta(
    val id: String, // Corresponde al _id de MongoDB
    val titulo: String,
    val descripcion: String,
    val precioInicial: Double,
    val precioActual: Double, // Será la puja más alta en general
    val fechaInicio: Date,
    val fechaFin: Date,
    val estado: String,
    val imagenUrl: String? = null,
    val puestos: List<Puesto>, // ¡Nuevo campo! Lista de 100 puestos
    val puestoGanador: Int? = null, // Campo para el resultado final
    val pujaGanadora: Double? = null, // Campo para el resultado final
    val ganadorId: String? = null // Campo para el resultado final
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
    val pujadorId: String // Esto debería ser el ID del usuario
)