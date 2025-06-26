package com.example.subastaapp

import com.example.subastaapp.model.Subasta
import com.example.subastaapp.model.Puesto // Asegúrate de importar Puesto si Subasta lo usa
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull // ¡Necesario para verificar valores nulos!
import org.junit.Test
import java.util.Date

class SubastaTest {

    @Test
    fun `Subasta data class puede ser instanciada con valores correctos y pujaGanadora no nula`() {
        val id = "subasta123"
        val titulo = "Obra de Arte Moderna"
        val descripcion = "Una pieza única para coleccionistas."
        val precioInicial = 100.0
        val precioActual = 120.50
        val fechaInicio = Date(System.currentTimeMillis() - 3600000) // Hace 1 hora
        val fechaFin = Date(System.currentTimeMillis() + 86400000) // En 24 horas
        val estado = "activa"
        val imagenUrl = "http://example.com/imagen.jpg"

        val puestos = listOf<Puesto>() // Lista vacía de Puesto para simplificar

        val puestoGanador: Int? = null

        // ¡CORRECCIÓN CLAVE! Definimos pujaGanadora como un Double no nulo para esta prueba.
        val pujaGanadoraConValor: Double = 150.0

        val ganadorId: String? = null

        val subasta = Subasta(
            id = id,
            titulo = titulo,
            descripcion = descripcion,
            precioInicial = precioInicial,
            precioActual = precioActual,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin,
            estado = estado,
            imagenUrl = imagenUrl,
            puestos = puestos,
            puestoGanador = puestoGanador,
            pujaGanadora = pujaGanadoraConValor, // Pasamos el valor no nulo
            ganadorId = ganadorId
        )

        assertNotNull(subasta)
        assertEquals(id, subasta.id)
        assertEquals(titulo, subasta.titulo)
        assertEquals(descripcion, subasta.descripcion)
        assertEquals(precioInicial, subasta.precioInicial, 0.001)
        assertEquals(precioActual, subasta.precioActual, 0.001)
        assertEquals(fechaInicio, subasta.fechaInicio)
        assertEquals(fechaFin, subasta.fechaFin)
        assertEquals(estado, subasta.estado)
        assertEquals(imagenUrl, subasta.imagenUrl)
        assertEquals(puestos, subasta.puestos)
        assertEquals(puestoGanador, subasta.puestoGanador)

        // ¡CORRECCIÓN CLAVE! Usamos '!!' para decirle al compilador que estamos seguros de que
        // subasta.pujaGanadora no es nulo en este punto, permitiendo la comparación.
        assertEquals(pujaGanadoraConValor, subasta.pujaGanadora!!, 0.001)
        assertEquals(ganadorId, subasta.ganadorId)
    }

    @Test
    fun `Subasta data class puede ser instanciada con pujaGanadora nula`() {
        val id = "subasta124"
        val titulo = "Subasta sin pujas"
        val descripcion = "Descripción simple para una subasta sin pujas aún."
        val precioInicial = 50.0
        val precioActual = 50.0
        val fechaInicio = Date(System.currentTimeMillis() - 1000)
        val fechaFin = Date(System.currentTimeMillis() + 3600000)
        val estado = "activa"
        val imagenUrl: String? = null
        val puestos = listOf<Puesto>()
        val puestoGanador: Int? = null
        val pujaGanadora: Double? = null // Definimos explícitamente como nulo para esta prueba
        val ganadorId: String? = null

        val subasta = Subasta(
            id = id,
            titulo = titulo,
            descripcion = descripcion,
            precioInicial = precioInicial,
            precioActual = precioActual,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin,
            estado = estado,
            imagenUrl = imagenUrl,
            puestos = puestos,
            puestoGanador = puestoGanador,
            pujaGanadora = pujaGanadora, // Pasamos el valor nulo
            ganadorId = ganadorId
        )

        assertNotNull(subasta)
        assertNull(subasta.pujaGanadora) // ¡VERIFICAMOS QUE ES NULO!
        // También puedes añadir otras aserciones para las demás propiedades si lo deseas
    }
}