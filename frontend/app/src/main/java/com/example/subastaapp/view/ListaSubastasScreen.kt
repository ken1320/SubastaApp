package com.example.subastaapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.subastaapp.model.Subasta
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Pantalla que muestra una lista de subastas.
 * Permite buscar subastas y navegar a sus detalles o crear nuevas.
 *
 * @param subastas Lista de subastas a mostrar.
 * @param onCrearSubasta Callback para crear una nueva subasta.
 * @param onVerDetalles Callback para ver los detalles de una subasta.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaSubastasScreen(
    subastas: List<Subasta>,
    onCrearSubasta: () -> Unit,
    onVerDetalles: (Subasta) -> Unit
) {
    var textoBusqueda by remember { mutableStateOf("") } // Texto de búsqueda para filtrar subastas.
    val subastasFiltradas = subastas.filter { // Lista de subastas filtradas por título.
        it.titulo.contains(textoBusqueda, ignoreCase = true)
    }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Formato de fecha.

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text("Listado de Subastas", style = MaterialTheme.typography.titleLarge)
            Button(onClick = onCrearSubasta) { Text("Nueva Subasta") } // Botón para crear nueva subasta.
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para buscar subastas por título.
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            label = { Text("Buscar por título") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Muestra mensaje si no hay subastas filtradas.
        if (subastasFiltradas.isEmpty()) {
            Text("No hay subastas para mostrar.", modifier = Modifier.padding(16.dp))
        } else {
            // Lista de subastas.
            LazyColumn {
                items(subastasFiltradas) { subasta ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        onClick = { onVerDetalles(subasta) } // Al hacer clic, ver detalles.
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(subasta.titulo, style = MaterialTheme.typography.titleMedium)
                                Text("Fecha de inicio: ${dateFormatter.format(subasta.fechaInicio)}")

                                // Lógica para mostrar ganador o oferta actual.
                                if (subasta.estado == "finalizada" && subasta.puestoGanador != null && subasta.pujaGanadora != null) {
                                    val ganadorPuesto = subasta.puestos.find { it.numero == subasta.puestoGanador }
                                    val nombreGanador = ganadorPuesto?.ocupadoPor

                                    if (nombreGanador != null) {
                                        Text("👑 Ganador: $nombreGanador (\$${String.format(Locale.getDefault(), "%.2f", subasta.pujaGanadora)})",
                                            color = MaterialTheme.colorScheme.primary)
                                    } else {
                                        Text("👑 Ganador ID: ${subasta.ganadorId} (\$${String.format(Locale.getDefault(), "%.2f", subasta.pujaGanadora)})",
                                            color = MaterialTheme.colorScheme.primary)
                                    }
                                } else if (subasta.estado == "activa" && subasta.precioActual > subasta.precioInicial) {
                                    Text("Oferta actual: \$${String.format(Locale.getDefault(), "%.2f", subasta.precioActual)}")
                                } else {
                                    Text("Sin ofertas aún / ${subasta.estado}")
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { onVerDetalles(subasta) }) {
                                Text("Ver Detalles")
                            }
                        }
                    }
                }
            }
        }
    }
}
