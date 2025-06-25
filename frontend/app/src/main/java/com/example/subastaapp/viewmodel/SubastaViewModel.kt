package com.example.subastaapp.viewmodel

import android.content.Context // ¡Correcto!
import android.net.Uri // ¡Correcto!
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subastaapp.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull // ¡Correcto!
import okhttp3.MultipartBody // ¡Correcto!
import okhttp3.RequestBody.Companion.toRequestBody // ¡Correcto!
import retrofit2.HttpException
import java.io.IOException
import java.util.Date

// Asegúrate de que el constructor de tu ViewModel reciba el Context
class SubastaViewModel(private val context: Context) : ViewModel() { // <-- Aquí ya recibes el Context

    private val _subastas = MutableStateFlow<List<Subasta>>(emptyList())
    val subastas: StateFlow<List<Subasta>> = _subastas

    private val _subastaSeleccionada = MutableStateFlow<Subasta?>(null)
    val subastaSeleccionada: StateFlow<Subasta?> = _subastaSeleccionada.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchSubastas()
    }

    fun seleccionarSubasta(subasta: Subasta?) {
        _subastaSeleccionada.value = subasta
    }

    fun fetchSubastas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                println("DEBUG: ViewModel attempting to fetch auctions.")
                val fetchedSubastas = RetrofitClient.api.getSubastas()
                _subastas.value = fetchedSubastas
                println("DEBUG: Auctions successfully loaded: ${fetchedSubastas.size} items.")

                fetchedSubastas.forEach { subasta ->
                    println("DEBUG (FETCHED): Subasta cargada: ID=${subasta.id}, Titulo='${subasta.titulo}', ImagenURL='${subasta.imagenUrl}', Puestos: ${subasta.puestos.size}")
                }

                _subastaSeleccionada.value?.let { currentSelected ->
                    val updatedSelected = fetchedSubastas.find { it.id == currentSelected.id }
                    if (updatedSelected != null) {
                        _subastaSeleccionada.value = updatedSelected
                        println("DEBUG: Updated selected auction with fresh data.")
                    }
                }

            } catch (e: IOException) {
                _error.value = "Network error: ${e.message}"
                println("ERROR: Network error fetching auctions: ${e.message}")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _error.value = "HTTP Error ${e.code()}: ${errorBody ?: e.message()}"
                println("ERROR: HTTP error fetching auctions: ${e.code()} - ${errorBody}")
            } catch (e: Exception) {
                _error.value = "Unexpected error fetching auctions: ${e.message}"
                println("ERROR: Unexpected error fetching auctions: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
                println("DEBUG: Auction fetching finished. Loading: false.")
            }
        }
    }

    fun crearSubasta(
        titulo: String,
        descripcion: String,
        precioInicial: Double,
        fechaFin: Date,
        imagenUri: Uri?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                var imageUrl: String? = null

                // Paso 1: Si hay un Uri de imagen, súbela primero al servidor
                imagenUri?.let { uri ->
                    val contentResolver = context.contentResolver // <-- ¡CORRECCIÓN 1: Usar 'context' en lugar de 'application'!
                    val inputStream = contentResolver.openInputStream(uri)

                    // Lee los bytes del InputStream
                    val byteArray = inputStream?.readBytes()
                    inputStream?.close() // Es importante cerrar el InputStream

                    // Verifica que los bytes no sean nulos y no estén vacíos
                    if (byteArray != null && byteArray.isNotEmpty()) {
                        // Define el tipo de medio (MIME type)
                        val mediaType = "image/*".toMediaTypeOrNull()

                        // <-- ¡CORRECCIÓN 2 y 3: Asegurar que se llama a toRequestBody en un ByteArray no nulo
                        // y que el tipo de medio es claro!
                        val requestBody = byteArray.toRequestBody(mediaType)

                        // "image" debe coincidir con el nombre del campo esperado por el backend
                        // en tu ruta de subida (ej. upload.single('image'))
                        val imagePart = MultipartBody.Part.createFormData("image", "nombre_imagen.jpg", requestBody)

                        println("DEBUG: Intentando subir imagen desde URI: $uri")
                        val uploadResponse = RetrofitClient.api.uploadImage(imagePart)

                        if (uploadResponse.isSuccessful) {
                            imageUrl = uploadResponse.body()?.filePath
                            println("DEBUG: Imagen subida exitosamente. Path: $imageUrl")
                        } else {
                            val errorBody = uploadResponse.errorBody()?.string()
                            _error.value = "Error al subir imagen: ${uploadResponse.code()} - ${errorBody}"
                            println("ERROR: Fallo al subir imagen: ${uploadResponse.code()} - ${errorBody}")
                            _isLoading.value = false
                            return@launch // Detener la ejecución si la subida falla
                        }
                    } else {
                        println("DEBUG: No se pudo leer la imagen del URI o la imagen está vacía. La subasta se creará sin imagen.")
                        // Aquí puedes decidir si esto es un error o si simplemente la subasta se crea sin imagen.
                        // Para este caso, continuaremos con imageUrl = null, lo cual es el comportamiento por defecto.
                    }
                }

                // Paso 2: Crear la SubastaCreationRequest con la URL de la imagen obtenida
                val subastaData = SubastaCreationRequest(
                    titulo = titulo,
                    descripcion = descripcion,
                    precioInicial = precioInicial,
                    fechaFin = fechaFin,
                    imagenUrl = imageUrl // Usa la URL obtenida de la subida
                )

                println("DEBUG: ViewModel intentando crear subasta con datos: $subastaData")
                val response = RetrofitClient.api.crearSubasta(subastaData)

                if (response.isSuccessful) {
                    println("DEBUG: Subasta creada exitosamente. Código: ${response.code()}, Body: ${response.body()}")
                    onSuccess()
                    fetchSubastas() // Recargar subastas
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("ERROR: Fallo al crear subasta: ${response.code()} - ${errorBody}")
                    _error.value = "Error al crear subasta: ${response.code()} - ${errorBody}"
                }
            } catch (e: IOException) {
                println("ERROR: Excepción de red al crear subasta: ${e.message}")
                _error.value = "Error de red: ${e.message}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                println("ERROR: Excepción HTTP al crear subasta: ${e.code()} - ${errorBody ?: e.message()}")
                _error.value = "Error HTTP ${e.code()}: ${errorBody ?: e.message()}"
            } catch (e: Exception) {
                println("ERROR: Excepción inesperada al crear subasta: ${e.message}")
                e.printStackTrace()
                _error.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
                println("DEBUG: Proceso de crearSubasta finalizado. Loading: false.")
            }
        }
    }

    fun ocuparPuesto(subastaId: String, puestoNumero: Int, montoPuja: Double, pujadorId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Limpia el error al iniciar la operación
            try {
                val ocuparPuestoRequest = OcuparPuestoRequest(
                    puestoNumero = puestoNumero,
                    montoPuja = montoPuja,
                    pujadorId = pujadorId
                )
                println("DEBUG: ViewModel attempting to occupy puesto $puestoNumero on auction $subastaId with: $ocuparPuestoRequest")
                val response = RetrofitClient.api.ocuparPuesto(subastaId, ocuparPuestoRequest)
                if (response.isSuccessful) {
                    println("DEBUG: Puesto $puestoNumero occupied successfully on auction $subastaId.")
                    onSuccess()
                    fetchSubastas()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("ERROR: Failed to occupy puesto $puestoNumero on auction $subastaId: ${response.code()} - ${errorBody}")
                    _error.value = "Error ocupando puesto: ${response.code()} - ${errorBody}"
                }
            } catch (e: IOException) {
                println("ERROR: Network error when occupying puesto: ${e.message}")
                _error.value = "Network error: ${e.message}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                println("ERROR: HTTP error when occupying puesto: ${e.code()}: ${errorBody ?: e.message()}")
                _error.value = "HTTP Error ${e.code()}: ${errorBody ?: e.message()}"
            } catch (e: Exception) {
                println("ERROR: Unexpected error when occupying puesto: ${e.message}")
                e.printStackTrace()
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
                println("DEBUG: Occupying puesto process finished.")
            }
        }
    }

    fun finalizar(subastaId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Limpia el error al iniciar la operación
            try {
                println("DEBUG: ViewModel attempting to finalize auction: $subastaId")
                val response = RetrofitClient.api.finalizar(subastaId)
                if (response.isSuccessful) {
                    println("DEBUG: Auction $subastaId finalized successfully.")
                    fetchSubastas()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("ERROR: Failed to finalize auction $subastaId: ${response.code()} - ${errorBody}")
                    _error.value = "Error finalizing auction: ${response.code()} - ${errorBody}"
                }
            } catch (e: IOException) {
                println("ERROR: Network error finalizing auction: ${e.message}")
                _error.value = "Network error: ${e.message}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                println("ERROR: HTTP error finalizing auction: ${e.code()}: ${errorBody ?: e.message()}")
                _error.value = "HTTP Error ${e.code()}: ${errorBody ?: e.message()}"
            } catch (e: Exception) {
                println("ERROR: Unexpected error finalizing auction: ${e.message}")
                e.printStackTrace()
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
                println("DEBUG: Finalizing auction process finished.")
            }
        }
    }

    fun eliminar(subastaId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Limpia el error al iniciar la operación
            try {
                println("DEBUG: ViewModel attempting to delete auction: $subastaId")
                val response = RetrofitClient.api.eliminar(subastaId)
                if (response.isSuccessful) {
                    println("DEBUG: Auction $subastaId deleted successfully.")
                    onSuccess()
                    fetchSubastas()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("ERROR: Failed to delete auction $subastaId: ${response.code()} - ${errorBody}")
                    _error.value = "Error deleting auction: ${response.code()} - ${errorBody}"
                }
            } catch (e: IOException) {
                println("ERROR: Network error deleting auction: ${e.message}")
                _error.value = "Network error: ${e.message}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                println("ERROR: HTTP error deleting auction: ${e.code()}: ${errorBody ?: e.message()}")
                _error.value = "HTTP Error ${e.code()}: ${errorBody ?: e.message()}"
            } catch (e: Exception) {
                println("ERROR: Unexpected error deleting auction: ${e.message}")
                e.printStackTrace()
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
                println("DEBUG: Deleting auction process finished.")
            }
        }
    }

    // ¡NUEVA FUNCIÓN! Para que la UI pueda limpiar el estado de error
    fun clearError() {
        _error.value = null
    }
}