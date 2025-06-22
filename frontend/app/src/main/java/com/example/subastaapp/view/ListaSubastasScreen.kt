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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaSubastasScreen(
    subastas: List<Subasta>,
    onCrearSubasta: () -> Unit,
    onVerDetalles: (Subasta) -> Unit
) {
    var textoBusqueda by remember { mutableStateOf("") }
    val subastasFiltradas = subastas.filter {
        it.titulo.contains(textoBusqueda, ignoreCase = true)
    }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text("Listado de Subastas", style = MaterialTheme.typography.titleLarge)
            Button(onClick = onCrearSubasta) { Text("Nueva Subasta") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            label = { Text("Buscar por título") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (subastasFiltradas.isEmpty()) {
            Text("No hay subastas para mostrar.", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn {
                items(subastasFiltradas) { subasta ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        onClick = { onVerDetalles(subasta) }
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

                                // --- LÓGICA DE VISUALIZACIÓN DE GANADOR/OFERTA ACTUAL ---
                                if (subasta.estado == "finalizada" && subasta.puestoGanador != null && subasta.pujaGanadora != null) {
                                    // Buscar el puesto ganador para obtener el nombre del pujador si está populado
                                    val ganadorPuesto = subasta.puestos.find { it.numero == subasta.puestoGanador }
                                    val nombreGanador = ganadorPuesto?.ocupadoPor?.nombre

                                    if (nombreGanador != null) {
                                        Text("👑 Ganador: $nombreGanador (\$${String.format(Locale.getDefault(), "%.2f", subasta.pujaGanadora)})",
                                            color = MaterialTheme.colorScheme.primary)
                                    } else {
                                        // Si el nombre no está populado, mostrar el ID del ganador
                                        Text("👑 Ganador ID: ${subasta.ganadorId} (\$${String.format(Locale.getDefault(), "%.2f", subasta.pujaGanadora)})",
                                            color = MaterialTheme.colorScheme.primary)
                                    }
                                } else if (subasta.estado == "activa" && subasta.precioActual > subasta.precioInicial) {
                                    // Si la subasta está activa y hay una oferta actual mayor que la inicial
                                    Text("Oferta actual: \$${String.format(Locale.getDefault(), "%.2f", subasta.precioActual)}")
                                } else {
                                    // Si no hay ofertas o la subasta está en otro estado no finalizado/activo con puja
                                    Text("Sin ofertas aún / ${subasta.estado}")
                                }
                                // --- FIN LÓGICA ---
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