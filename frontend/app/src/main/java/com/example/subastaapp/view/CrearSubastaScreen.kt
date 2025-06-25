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

@Composable
fun CrearSubastaScreen(
    // El callback ahora pasa una Uri en lugar de una String para la imagen
    onCrear: (titulo: String, descripcion: String, precioInicial: Double, fechaFin: Date, imagenUri: Uri?) -> Unit,
    onCancelar: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precioInicialText by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Date?>(null) }

    // Estado para la URI de la imagen seleccionada
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    // Launcher para seleccionar una imagen de la galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    // --- INICIO DE LA CORRECCIÓN ---

    // 1. Se declara la variable 'calendar' ANTES de ser usada.
    val calendar = Calendar.getInstance()

    // 2. Se define el listener de forma explícita para evitar errores de inferencia de tipo.
    val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(year, month, dayOfMonth, 23, 59, 59)
        selectedDate = selectedCalendar.time
        println("DEBUG: Fecha seleccionada en DatePicker Callback: ${selectedDate?.let { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(it) }}")
    }

    // 3. Se crea el DatePickerDialog usando las variables corregidas.
    val datePickerDialog = DatePickerDialog(
        context,
        dateSetListener,
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // --- FIN DE LA CORRECCIÓN ---

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Crear Nueva Subasta", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título de la Subasta") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

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
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Seleccionar Imagen")
            }
            // Previsualización de la imagen seleccionada
            imagenUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.size(80.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

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
                    println("DEBUG: Clic en el icono de calendario. Mostrando DatePicker.")
                    datePickerDialog.show()
                }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Seleccionar Fecha")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        val isButtonEnabled = titulo.isNotBlank() &&
                descripcion.isNotBlank() &&
                precioInicialText.toDoubleOrNull() != null &&
                selectedDate != null

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = onCancelar,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    val precio = precioInicialText.toDoubleOrNull()
                    if (isButtonEnabled && selectedDate != null && precio != null) {
                        // Pasamos la URI de la imagen, no una URL de texto
                        onCrear(
                            titulo,
                            descripcion,
                            precio,
                            selectedDate!!,
                            imagenUri // Pasamos la Uri directamente
                        )
                    }
                },
                enabled = isButtonEnabled
            ) {
                Text("Crear")
            }
        }
    }
}