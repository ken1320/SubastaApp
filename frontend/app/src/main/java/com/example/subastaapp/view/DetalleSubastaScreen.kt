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
import com.example.subastaapp.model.RetrofitClient // <-- ¡IMPORTADO CORRECTAMENTE!

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleSubastaScreen(
    navController: NavController,
    viewModel: SubastaViewModel
) {
    val subasta by viewModel.subastaSeleccionada.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var pujadorId by remember { mutableStateOf("") }
    var montoOferta by remember { mutableStateOf("") }
    var selectedPuesto by remember { mutableStateOf<Int?>(null) }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    var uiValidationMessage by remember { mutableStateOf<String?>(null) }

    // Mostrar un CircularProgressIndicator si está cargando
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    // Mostrar error general del ViewModel
    error?.let { errorMessage ->
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(errorMessage) {
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.clearError()
        }
        SnackbarHost(hostState = snackbarHostState)
    }

    // Mostrar mensaje de validación de UI
    uiValidationMessage?.let { localMessage ->
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(localMessage) {
            snackbarHostState.showSnackbar(localMessage)
            uiValidationMessage = null
        }
        SnackbarHost(hostState = snackbarHostState)
    }

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
                columns = GridCells.Fixed(10), // Esta definición se aplicará a los 'items' que no son de 'item' único
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                state = rememberLazyGridState() // Añadir un estado si necesitas controlar el scroll
            ) {
                // Nombre de la Subasta
                item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
                    Text(
                        text = currentSubasta.titulo,
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Imagen de la Subasta
                if (!currentSubasta.imagenUrl.isNullOrEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
                        // CONSTRUYE LA URL COMPLETA AQUÍ, usando la constante BASE_URL
                        val fullImageUrl = "${RetrofitClient.BASE_URL}${currentSubasta.imagenUrl}"

                        Image(
                            painter = rememberAsyncImagePainter(fullImageUrl), // <-- Se usa la URL COMPLETA
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

                // Oferta Mínima (Precio Inicial de la Subasta)
                item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
                    Text(
                        text = "Oferta Mínima: $${String.format(Locale.getDefault(), "%.2f", currentSubasta.precioInicial)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Fechas de la subasta
                item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
                    Text(
                        text = "Inicio: ${dateFormatter.format(currentSubasta.fechaInicio)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
                    Text(
                        text = "Fin: ${dateFormatter.format(currentSubasta.fechaFin)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(12.dp)) }

                // Estado de la subasta
                item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
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

                // --- Matriz Visual de Puestos (10x10 Grid) ---
                item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
                    Text(
                        text = "Puestos (1-100):",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Aquí van los items de la cuadrícula de puestos (estos sí respetarán GridCells.Fixed(10))
                items(currentSubasta.puestos) { puesto ->
                    val isOccupied = puesto.ocupadoPor != null
                    val isSelected = selectedPuesto == puesto.numero

                    Surface(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable(enabled = !isOccupied && currentSubasta.estado == "activa") {
                                selectedPuesto = if (isSelected) null else puesto.numero
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

                // --- Formulario para ocupar puesto ---
                if (currentSubasta.estado == "activa") {
                    item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
                        OutlinedTextField(
                            value = pujadorId,
                            onValueChange = { pujadorId = it },
                            label = { Text("ID pujador", fontSize = 14.sp) }, // También puedes limpiar el label
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 56.dp),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(6.dp)) }

                    item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
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

                    item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
                        Button(
                            onClick =  {
                                // Accedemos a currentSubasta aquí dentro, ya que estamos en el scope del 'let'
                                val subastaIdActual = currentSubasta.id
                                val puesto = selectedPuesto
                                val monto = montoOferta.toDoubleOrNull()

                                if (puesto != null && monto != null && pujadorId.isNotBlank()) {
                                    if (monto > currentSubasta.precioInicial) {
                                        // Usamos la variable local segura que no puede ser nula
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

                // --- Resultados de la Subasta (si finalizada) ---
                if (currentSubasta.estado == "finalizada") {
                    item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(12.dp)) }
                    item(span = { GridItemSpan(maxLineSpan) }) { Divider() }
                    item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(12.dp)) }
                    item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
                        Text(
                            text = "Resultados Finales:",
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    if (currentSubasta.puestoGanador != null && currentSubasta.pujaGanadora != null) {
                        item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
                            Text(
                                text = "Puesto Ganador: ${currentSubasta.puestoGanador}",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
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
                        } ?: item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
                            Text(
                                text = "Ganador: No especificado",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
                            Text(
                                text = "No hubo pujas válidas.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // --- Botones de Acción (Finalizar, Eliminar) ---
                item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(12.dp)) }
                item(span = { GridItemSpan(maxLineSpan) }) { // Ocupa toda la fila
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