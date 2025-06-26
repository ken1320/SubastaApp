package com.example.subastaapp.view

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla para crear una nueva subasta.
 * Permite ingresar detalles, seleccionar una fecha y elegir una imagen.
 *
 * @param onCrear Callback para guardar la nueva subasta.
 * @param onCancelar Callback para cancelar la creación.
 */
@Composable
fun CrearSubastaScreen(
    onCrear: (titulo: String, descripcion: String, precioInicial: Double, fechaFin: Date, imagenUri: Uri?) -> Unit,
    onCancelar: () -> Unit
) {
    var titulo by remember { mutableStateOf("") } // Estado para el título.
    var descripcion by remember { mutableStateOf("") } // Estado para la descripción.
    var precioInicialText by remember { mutableStateOf("") } // Estado para el precio inicial (texto).
    var selectedDate by remember { mutableStateOf<Date?>(null) } // Estado para la fecha seleccionada.

    var imagenUri by remember { mutableStateOf<Uri?>(null) } // Estado para la URI de la imagen.
    val context = LocalContext.current // Contexto actual de la composición.

    // Launcher para seleccionar una imagen de la galería.
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri // Actualiza la URI de la imagen seleccionada.
    }

    val calendar = Calendar.getInstance() // Instancia del calendario.

    // Listener para el selector de fecha.
    val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(year, month, dayOfMonth, 23, 59, 59) // Establece la fecha seleccionada.
        selectedDate = selectedCalendar.time
    }

    // Cuadro de diálogo para seleccionar la fecha.
    val datePickerDialog = DatePickerDialog(
        context,
        dateSetListener,
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Crear Nueva Subasta", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))

        // Campo de texto para el título.
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título de la Subasta") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Campo de texto para la descripción.
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Campo de texto para el precio inicial (solo números).
        OutlinedTextField(
            value = precioInicialText,
            onValueChange = { newValue ->
                if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                    precioInicialText = newValue
                }
            },
            label = { Text("Precio Inicial") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón para seleccionar imagen.
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Seleccionar Imagen")
            }
            // Previsualización de la imagen seleccionada.
            imagenUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.size(80.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para la fecha de fin (solo lectura, abre el selector).
        OutlinedTextField(
            value = selectedDate?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) } ?: "",
            onValueChange = { /* No permitir edición manual */ },
            label = { Text("Fecha de Fin") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            trailingIcon = {
                IconButton(onClick = {
                    datePickerDialog.show() // Muestra el selector de fecha.
                }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Seleccionar Fecha")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Habilita el botón "Crear" si todos los campos requeridos están llenos.
        val isButtonEnabled = titulo.isNotBlank() &&
                descripcion.isNotBlank() &&
                precioInicialText.toDoubleOrNull() != null &&
                selectedDate != null

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Botón para cancelar.
            Button(
                onClick = onCancelar,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Cancelar")
            }
            // Botón para crear la subasta.
            Button(
                onClick = {
                    val precio = precioInicialText.toDoubleOrNull()
                    if (isButtonEnabled && selectedDate != null && precio != null) {
                        onCrear(
                            titulo,
                            descripcion,
                            precio,
                            selectedDate!!,
                            imagenUri // Pasa la URI de la imagen.
                        )
                    }
                },
                enabled = isButtonEnabled // Estado de habilitación del botón.
            ) {
                Text("Crear")
            }
        }
    }
}
