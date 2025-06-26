package com.example.subastaapp

import com.example.subastaapp.model.Subasta
import com.example.subastaapp.model.Puesto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Date

/**
 * Clase de pruebas unitarias para la clase de datos [Subasta].
 */
class SubastaTest {

    /**
     * Prueba que la clase [Subasta] puede ser instanciada correctamente
     * con un valor no nulo para [pujaGanadora].
     */
    @Test
    fun `Subasta data class puede ser instanciada con valores correctos y pujaGanadora no nula`() {
        val id = "subasta123"
        val titulo = "Obra de Arte Moderna"
        val descripcion = "Una pieza única para coleccionistas."
        val precioInicial = 100.0
        val precioActual = 120.50
        val fechaInicio = Date(System.currentTimeMillis() - 3600000)
        val fechaFin = Date(System.currentTimeMillis() + 86400000)
        val estado = "activa"
        val imagenUrl = "http://example.com/imagen.jpg"
        val puestos = listOf<Puesto>()
        val puestoGanador: Int? = null
        val pujaGanadoraConValor: Double = 150.0 // Valor no nulo para la puja ganadora.
        val ganadorId: String? = null

        // Instancia la clase Subasta.
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
            pujaGanadora = pujaGanadoraConValor,
            ganadorId = ganadorId
        )

        // Verifica que la instancia no es nula.
        assertNotNull(subasta)
        // Verifica que los valores instanciados son correctos.
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
        // Verifica que pujaGanadora tiene el valor esperado y no es nulo.
        assertEquals(pujaGanadoraConValor, subasta.pujaGanadora!!, 0.001)
        assertEquals(ganadorId, subasta.ganadorId)
    }

    /**
     * Prueba que la clase [Subasta] puede ser instanciada correctamente
     * con un valor nulo para [pujaGanadora].
     */
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
        val pujaGanadora: Double? = null // Valor nulo para la puja ganadora.
        val ganadorId: String? = null

        // Instancia la clase Subasta con pujaGanadora nula.
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
            pujaGanadora = pujaGanadora,
            ganadorId = ganadorId
        )

        // Verifica que la instancia no es nula.
        assertNotNull(subasta)
        // Verifica que pujaGanadora es nulo.
        assertNull(subasta.pujaGanadora)
    }
}
