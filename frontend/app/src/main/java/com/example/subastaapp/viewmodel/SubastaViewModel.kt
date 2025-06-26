package com.example.subastaapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subastaapp.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import java.util.Date

/**
 * ViewModel para gestionar los datos y la lógica de negocio de las subastas.
 * Interactúa con la API y expone el estado a la UI.
 *
 * @param context Contexto de la aplicación para operaciones como la lectura de URIs.
 */
class SubastaViewModel(private val context: Context) : ViewModel() {

    private val _subastas = MutableStateFlow<List<Subasta>>(emptyList())
    val subastas: StateFlow<List<Subasta>> = _subastas // Flujo de la lista de subastas.

    private val _subastaSeleccionada = MutableStateFlow<Subasta?>(null)
    val subastaSeleccionada: StateFlow<Subasta?> = _subastaSeleccionada.asStateFlow() // Subasta actualmente seleccionada.

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading // Estado de carga de operaciones.

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow() // Mensaje de error.

    init {
        fetchSubastas() // Carga las subastas al inicializar el ViewModel.
    }

    /** Establece la subasta seleccionada. */
    fun seleccionarSubasta(subasta: Subasta?) {
        _subastaSeleccionada.value = subasta
    }

    /** Obtiene todas las subastas de la API. */
    fun fetchSubastas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val fetchedSubastas = RetrofitClient.api.getSubastas()
                _subastas.value = fetchedSubastas
                _subastaSeleccionada.value?.let { currentSelected ->
                    val updatedSelected = fetchedSubastas.find { it.id == currentSelected.id }
                    if (updatedSelected != null) {
                        _subastaSeleccionada.value = updatedSelected
                    }
                }
            } catch (e: IOException) {
                _error.value = "Network error: ${e.message}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _error.value = "HTTP Error ${e.code()}: ${errorBody ?: e.message()}"
            } catch (e: Exception) {
                _error.value = "Unexpected error fetching auctions: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Crea una nueva subasta, incluyendo la subida de imagen si se proporciona. */
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

                // Si hay URI de imagen, súbela primero.
                imagenUri?.let { uri ->
                    val contentResolver = context.contentResolver
                    val inputStream = contentResolver.openInputStream(uri)
                    val byteArray = inputStream?.readBytes()
                    inputStream?.close()

                    if (byteArray != null && byteArray.isNotEmpty()) {
                        val mediaType = "image/*".toMediaTypeOrNull()
                        val requestBody = byteArray.toRequestBody(mediaType)
                        val imagePart = MultipartBody.Part.createFormData("image", "nombre_imagen.jpg", requestBody)

                        val uploadResponse = RetrofitClient.api.uploadImage(imagePart)

                        if (uploadResponse.isSuccessful) {
                            imageUrl = uploadResponse.body()?.filePath
                        } else {
                            val errorBody = uploadResponse.errorBody()?.string()
                            _error.value = "Error al subir imagen: ${uploadResponse.code()} - ${errorBody}"
                            _isLoading.value = false
                            return@launch
                        }
                    }
                }

                // Crea la subasta con la URL de la imagen.
                val subastaData = SubastaCreationRequest(
                    titulo = titulo,
                    descripcion = descripcion,
                    precioInicial = precioInicial,
                    fechaFin = fechaFin,
                    imagenUrl = imageUrl
                )

                val response = RetrofitClient.api.crearSubasta(subastaData)

                if (response.isSuccessful) {
                    onSuccess()
                    fetchSubastas() // Recarga las subastas.
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Error al crear subasta: ${response.code()} - ${errorBody}"
                }
            } catch (e: IOException) {
                _error.value = "Error de red: ${e.message}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _error.value = "Error HTTP ${e.code()}: ${errorBody ?: e.message()}"
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Ocupa un puesto en una subasta específica. */
    fun ocuparPuesto(subastaId: String, puestoNumero: Int, montoPuja: Double, pujadorId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val ocuparPuestoRequest = OcuparPuestoRequest(
                    puestoNumero = puestoNumero,
                    montoPuja = montoPuja,
                    pujadorId = pujadorId
                )
                val response = RetrofitClient.api.ocuparPuesto(subastaId, ocuparPuestoRequest)
                if (response.isSuccessful) {
                    onSuccess()
                    fetchSubastas()
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Error ocupando puesto: ${response.code()} - ${errorBody}"
                }
            } catch (e: IOException) {
                _error.value = "Network error: ${e.message}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _error.value = "HTTP Error ${e.code()}: ${errorBody ?: e.message()}"
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Finaliza una subasta específica. */
    fun finalizar(subastaId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.api.finalizar(subastaId)
                if (response.isSuccessful) {
                    fetchSubastas()
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Error finalizing auction: ${response.code()} - ${errorBody}"
                }
            } catch (e: IOException) {
                _error.value = "Network error: ${e.message}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _error.value = "HTTP Error ${e.code()}: ${errorBody ?: e.message()}"
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Elimina una subasta específica. */
    fun eliminar(subastaId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.api.eliminar(subastaId)
                if (response.isSuccessful) {
                    onSuccess()
                    fetchSubastas()
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Error deleting auction: ${response.code()} - ${errorBody}"
                }
            } catch (e: IOException) {
                _error.value = "Network error: ${e.message}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _error.value = "HTTP Error ${e.code()}: ${errorBody ?: e.message()}"
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Limpia el estado de error. */
    fun clearError() {
        _error.value = null
    }
}
