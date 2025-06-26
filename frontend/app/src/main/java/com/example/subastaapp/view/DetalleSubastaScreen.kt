package com.example.subastaapp.view

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.subastaapp.viewmodel.SubastaViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.draw.clip
import com.example.subastaapp.model.RetrofitClient // Importación del cliente Retrofit para la URL base.

/**
 * Pantalla de detalles de una subasta específica.
 * Muestra información, puestos, formulario de oferta y acciones (finalizar/eliminar).
 *
 * @param navController Controlador de navegación.
 * @param viewModel ViewModel para acceder a datos y lógica de subastas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleSubastaScreen(
    navController: NavController,
    viewModel: SubastaViewModel
) {
    val subasta by viewModel.subastaSeleccionada.collectAsState() // Subasta seleccionada.
    val isLoading by viewModel.isLoading.collectAsState() // Estado de carga.
    val error by viewModel.error.collectAsState() // Mensaje de error.

    var pujadorId by remember { mutableStateOf("") } // ID del pujador.
    var montoOferta by remember { mutableStateOf("") } // Monto de la oferta.
    var selectedPuesto by remember { mutableStateOf<Int?>(null) } // Puesto seleccionado para ofertar.

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) // Formato de fecha.

    var uiValidationMessage by remember { mutableStateOf<String?>(null) } // Mensaje de validación de UI.

    // Muestra indicador de progreso si está cargando.
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    // Muestra errores del ViewModel como Snackbar.
    error?.let { errorMessage ->
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(errorMessage) {
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.clearError()
        }
        SnackbarHost(hostState = snackbarHostState)
    }

    // Muestra mensajes de validación de UI como Snackbar.
    uiValidationMessage?.let { localMessage ->
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(localMessage) {
            snackbarHostState.showSnackbar(localMessage)
            uiValidationMessage = null
        }
        SnackbarHost(hostState = snackbarHostState)
    }

    // Contenido de la pantalla si hay una subasta seleccionada.
    subasta?.let { currentSubasta ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalles: ${currentSubasta.titulo}", fontSize = 18.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(10), // Define una cuadrícula de 10 columnas.
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                state = rememberLazyGridState()
            ) {
                // Título de la subasta.
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = currentSubasta.titulo,
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Imagen de la subasta (si existe).
                if (!currentSubasta.imagenUrl.isNullOrEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        val fullImageUrl = "${RetrofitClient.BASE_URL}${currentSubasta.imagenUrl}"
                        Image(
                            painter = rememberAsyncImagePainter(fullImageUrl),
                            contentDescription = "Imagen de la subasta",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(12.dp)) }
                }

                // Precio inicial de la subasta.
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Oferta Mínima: $${String.format(Locale.getDefault(), "%.2f", currentSubasta.precioInicial)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Fechas de inicio y fin.
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Inicio: ${dateFormatter.format(currentSubasta.fechaInicio)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Fin: ${dateFormatter.format(currentSubasta.fechaFin)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(12.dp)) }

                // Estado de la subasta con color.
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Estado: ${currentSubasta.estado.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }}",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 16.sp,
                        color = when (currentSubasta.estado) {
                            "activa" -> Color(0xFF4CAF50)
                            "finalizada" -> Color(0xFFF44336)
                            else -> Color.Gray
                        },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Título de la sección de puestos.
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Puestos (1-100):",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Cuadrícula de puestos.
                items(currentSubasta.puestos) { puesto ->
                    val isOccupied = puesto.ocupadoPor != null // Si el puesto está ocupado.
                    val isSelected = selectedPuesto == puesto.numero // Si el puesto está seleccionado.

                    Surface(
                        modifier = Modifier
                            .aspectRatio(1f) // Mantiene proporción 1:1.
                            .clickable(enabled = !isOccupied && currentSubasta.estado == "activa") {
                                selectedPuesto = if (isSelected) null else puesto.numero // Alterna selección.
                            }
                            .border(
                                width = 1.dp,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isOccupied -> Color.Red
                                    else -> Color.Gray.copy(alpha = 0.5f)
                                },
                                shape = RoundedCornerShape(2.dp)
                            )
                            .background(
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                                    isOccupied -> Color.Red.copy(alpha = 0.2f)
                                    else -> Color.LightGray.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(2.dp)
                            ),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "${puesto.numero}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOccupied) Color.Black else Color.Black
                            )
                            if (isOccupied) {
                                Text(
                                    text = "$${puesto.montoPuja.toInt()}",
                                    fontSize = 8.sp,
                                    color = Color.DarkGray
                                )
                                puesto.ocupadoPor?.let {
                                    Text(text = it, fontSize = 6.sp)
                                }
                            }
                        }
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(12.dp)) }

                // Formulario para ocupar puesto (visible si la subasta está activa).
                if (currentSubasta.estado == "activa") {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        OutlinedTextField(
                            value = pujadorId,
                            onValueChange = { pujadorId = it },
                            label = { Text("ID pujador", fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 56.dp),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(6.dp)) }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        OutlinedTextField(
                            value = montoOferta,
                            onValueChange = { newValue ->
                                val filteredValue = newValue.filter { it.isDigit() || it == '.' }
                                if (filteredValue.count { it == '.' } <= 1) {
                                    montoOferta = filteredValue
                                }
                            },
                            label = { Text("Monto oferta", fontSize = 14.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 56.dp),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(12.dp)) }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Button(
                            onClick =  {
                                val subastaIdActual = currentSubasta.id
                                val puesto = selectedPuesto
                                val monto = montoOferta.toDoubleOrNull()

                                if (puesto != null && monto != null && pujadorId.isNotBlank()) {
                                    if (monto > currentSubasta.precioInicial) {
                                        viewModel.ocuparPuesto(subastaIdActual, puesto, monto, pujadorId) {
                                            pujadorId = ""
                                            montoOferta = ""
                                            selectedPuesto = null
                                            uiValidationMessage = null
                                        }
                                    } else {
                                        uiValidationMessage = "La oferta debe ser mayor que la mínima (${currentSubasta.precioInicial})"
                                    }
                                } else {
                                    uiValidationMessage = "Completa puesto, ID y monto."
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            enabled = selectedPuesto != null && montoOferta.isNotBlank() && pujadorId.isNotBlank()
                        ) {
                            Text("Ocupar Puesto", fontSize = 16.sp)
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(12.dp)) }
                }

                // Resultados de la subasta (visible si está finalizada).
                if (currentSubasta.estado == "finalizada") {
                    item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(12.dp)) }
                    item(span = { GridItemSpan(maxLineSpan) }) { Divider() }
                    item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(12.dp)) }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "Resultados Finales:",
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    if (currentSubasta.puestoGanador != null && currentSubasta.pujaGanadora != null) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "Puesto Ganador: ${currentSubasta.puestoGanador}",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "Puja Ganadora: $${String.format(Locale.getDefault(), "%.2f", currentSubasta.pujaGanadora)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        val ganadorPuesto = currentSubasta.puestos.find { it.numero == currentSubasta.puestoGanador }
                        ganadorPuesto?.ocupadoPor?.let { nombre ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Text(
                                    text = "Ganador: $nombre",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } ?: item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "Ganador: No especificado",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "No hubo pujas válidas.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Botones de acción (Finalizar, Eliminar).
                item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(12.dp)) }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentSubasta.estado == "activa") {
                            Button(
                                onClick = {
                                    viewModel.finalizar(currentSubasta.id)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Finalizar", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Button(
                            onClick = {
                                viewModel.eliminar(currentSubasta.id) {
                                    navController.popBackStack()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("Eliminar", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Selecciona una subasta para ver los detalles.", fontSize = 16.sp)
        }
    }
}
