package com.example.subastaapp.view

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.KeyboardType

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CrearSubastaScreen(
    // ¡NUEVO!: Añadir imagenUrl al callback
    onCrear: (titulo: String, descripcion: String, precioInicial: Double, fechaFin: Date, imagenUrl: String?) -> Unit,
    onCancelar: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precioInicialText by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var imagenUrl by remember { mutableStateOf("") } // ¡NUEVO! Estado para la URL de la imagen

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth, 23, 59, 59)
            selectedDate = selectedCalendar.time
            println("DEBUG: Fecha seleccionada en DatePicker Callback: ${selectedDate?.let { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(it) }}")
        },
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

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título de la Subasta") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
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
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // ¡NUEVO!: Campo para la URL de la imagen
        OutlinedTextField(
            value = imagenUrl,
            onValueChange = { imagenUrl = it },
            label = { Text("URL de la Imagen (opcional)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

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

        println("DEBUG: Titulo: '$titulo', isNotBlank: ${titulo.isNotBlank()}")
        println("DEBUG: Descripcion: '$descripcion', isNotBlank: ${descripcion.isNotBlank()}")
        println("DEBUG: PrecioInicialText: '$precioInicialText', toDoubleOrNull: ${precioInicialText.toDoubleOrNull()}")
        println("DEBUG: SelectedDate: $selectedDate")
        println("DEBUG: ImagenUrl: '$imagenUrl'") // DEBUG
        println("DEBUG: Button Enabled: $isButtonEnabled")

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
                    if (titulo.isNotBlank() && descripcion.isNotBlank() && precio != null && selectedDate != null) {
                        println("DEBUG: Llamando a onCrear con: T=$titulo, D=$descripcion, P=$precio, F=$selectedDate, Img=$imagenUrl")
                        // ¡CAMBIO CLAVE!: Pasar imagenUrl al callback
                        onCrear(
                            titulo,
                            descripcion,
                            precio,
                            selectedDate!!,
                            imagenUrl.ifBlank { null } // Si el campo está vacío, enviar null
                        )
                    } else {
                        println("DEBUG: Faltan campos o son inválidos para crear la subasta.")
                    }
                },
                enabled = isButtonEnabled
            ) {
                Text("Crear")
            }
        }
    }
}