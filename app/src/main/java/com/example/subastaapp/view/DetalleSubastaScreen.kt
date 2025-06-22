package com.example.subastaapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.subastaapp.model.Puja
import com.example.subastaapp.model.Subasta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetalleSubastaScreen(
    subasta: Subasta,
    onRealizarPuja: (subastaId: String, montoPuja: Double, pujadorId: String) -> Unit,
    onFinalizarSubasta: (subastaId: String) -> Unit,
    onEliminarSubasta: (subastaId: String) -> Unit
) {
    var pujadorIdInput by remember { mutableStateOf("") }
    var valorOfertaInput by remember { mutableStateOf("") }

    // NOTA: Para un historial completo de pujas, tu modelo Subasta debería tener un 'List<Puja>'.
    // Actualmente, solo tienes 'ultimaPuja'. Aquí mostramos 'ultimaPuja' como "historial".
    val historialPujas: List<Puja> = remember(subasta.id, subasta.ultimaPuja) {
        if (subasta.ultimaPuja != null) listOf(subasta.ultimaPuja) else emptyList()
        // Si tu backend enviara un campo 'pujas: List<Puja>' en Subasta, usarías:
        // subasta.pujas.orEmpty().sortedByDescending { it.fechaPuja }
    }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Column(modifier = Modifier.padding(16.dp)) {
        // Título ahora es 'titulo'
        Text("Detalles de Subasta: ${subasta.titulo}", style = MaterialTheme.typography.titleLarge)

        // Imagen URL. Se usa un placeholder si es null.
        Image(
            painter = rememberAsyncImagePainter(subasta.imagenUrl ?: "https://via.placeholder.com/150"), // Placeholder si no hay imagen
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(200.dp).padding(vertical = 8.dp)
        )

        // Fechas de inicio y fin
        Text("Fecha de inicio: ${dateFormatter.format(subasta.fechaInicio)}")
        Text("Fecha de fin: ${dateFormatter.format(subasta.fechaFin)}")
        Text("Precio inicial: \$${String.format(Locale.getDefault(), "%.2f", subasta.precioInicial)}")
        Text("Precio actual: \$${String.format(Locale.getDefault(), "%.2f", subasta.precioActual)}")

        // Lógica para mostrar ganador/oferta más alta
        if (subasta.estado == "finalizada" && subasta.ganador != null) {
            val pujaGanadora = historialPujas.firstOrNull { it.pujador == subasta.ganador }
            if (pujaGanadora != null) {
                Text("👑 Ganador: ${pujaGanadora.pujador} (\$${String.format(Locale.getDefault(), "%.2f", pujaGanadora.monto)})", color = MaterialTheme.colorScheme.primary)
            } else {
                Text("👑 Ganador ID: ${subasta.ganador}", color = MaterialTheme.colorScheme.primary)
            }
        } else if (subasta.precioActual > subasta.precioInicial && subasta.estado == "activa") {
            Text("🔺 Oferta más alta actual: \$${String.format(Locale.getDefault(), "%.2f", subasta.precioActual)}")
        } else if (subasta.estado == "activa") {
            Text("No hay ofertas aún.")
        } else if (subasta.estado == "finalizada" && subasta.ganador == null) {
            Text("Subasta finalizada sin ganador (no hubo pujas).")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sección para realizar una nueva puja
        if (subasta.estado == "activa") {
            OutlinedTextField(
                value = pujadorIdInput,
                onValueChange = { pujadorIdInput = it },
                label = { Text("Tu ID de pujador") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = valorOfertaInput,
                onValueChange = { newValue ->
                    if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        valorOfertaInput = newValue
                    }
                },
                label = { Text("Valor de oferta (ej. 100.50)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val montoDoble = valorOfertaInput.toDoubleOrNull()
                    if (pujadorIdInput.isNotBlank() && montoDoble != null && montoDoble > subasta.precioActual) {
                        onRealizarPuja(subasta.id, montoDoble, pujadorIdInput)
                        pujadorIdInput = ""
                        valorOfertaInput = ""
                    } else {
                        println("Error: Asegúrate de que tu ID de pujador no esté vacío, la oferta sea un número válido y mayor al precio actual.")
                    }
                },
                enabled = pujadorIdInput.isNotBlank() && valorOfertaInput.isNotBlank() && valorOfertaInput.toDoubleOrNull() != null && valorOfertaInput.toDoubleOrNull()!! > subasta.precioActual,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Realizar Puja")
            }
        } else {
            Text("Esta subasta no está activa para recibir pujas.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
        }

        // Historial de pujas
        Text("Historial de Pujas:", style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
            items(historialPujas) { puja ->
                Text("- Pujador ID: ${puja.pujador}: \$${String.format(Locale.getDefault(), "%.2f", puja.monto)} el ${dateFormatter.format(puja.fechaPuja)}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de acción
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { onFinalizarSubasta(subasta.id) },
                enabled = subasta.estado == "activa"
            ) {
                Text("Finalizar Subasta")
            }
            Button(
                onClick = { onEliminarSubasta(subasta.id) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar Subasta", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}