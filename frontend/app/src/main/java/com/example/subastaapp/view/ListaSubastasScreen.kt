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

                                if (subasta.estado == "finalizada" && subasta.ganador != null) {
                                    val pujaGanadora = subasta.ultimaPuja
                                    if (pujaGanadora != null) {
                                        Text("👑 Ganador: ${pujaGanadora.pujador} (\$${String.format(Locale.getDefault(), "%.2f", pujaGanadora.monto)})",
                                            color = MaterialTheme.colorScheme.primary)
                                    } else {
                                        Text("👑 Ganador ID: ${subasta.ganador}", color = MaterialTheme.colorScheme.primary)
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