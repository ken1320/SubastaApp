package com.example.subastaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subastaapp.model.OcuparPuestoRequest
import com.example.subastaapp.model.RetrofitClient
import com.example.subastaapp.model.Subasta
import com.example.subastaapp.model.SubastaCreationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.Date

class SubastaViewModel : ViewModel() {

    private val _subastas = MutableStateFlow<List<Subasta>>(emptyList())
    val subastas: StateFlow<List<Subasta>> = _subastas

    private val _subastaSeleccionada = MutableStateFlow<Subasta?>(null)
    val subastaSeleccionada: StateFlow<Subasta?> = _subastaSeleccionada.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Mantén _error como private MutableStateFlow para encapsulación
    private val _error = MutableStateFlow<String?>(null)
    // Expón solo StateFlow para que la UI pueda leerlo
    val error: StateFlow<String?> = _error.asStateFlow() // Usa .asStateFlow() para asegurarte de que sea inmutable desde fuera

    init {
        fetchSubastas()
    }

    fun seleccionarSubasta(subasta: Subasta?) {
        _subastaSeleccionada.value = subasta
    }

    fun fetchSubastas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Limpia el error al iniciar una nueva carga
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

    fun crearSubasta(subastaData: SubastaCreationRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                println("DEBUG: ViewModel attempting to create auction with data: $subastaData")
                val response = RetrofitClient.api.crearSubasta(subastaData)
                if (response.isSuccessful) {
                    println("DEBUG: Auction created successfully. Code: ${response.code()}, Body: ${response.body()}")

                    val createdSubasta = response.body()
                    if (createdSubasta != null) {
                        println("DEBUG (CREATED): Subasta creada (desde la respuesta del backend): ID=${createdSubasta.id}, Titulo='${createdSubasta.titulo}', ImagenURL='${createdSubasta.imagenUrl}'")
                    } else {
                        println("DEBUG (CREATED): Respuesta de creación de subasta exitosa, pero cuerpo nulo.")
                    }

                    onSuccess()
                    fetchSubastas() // Reload the list after creation
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("ERROR: Failed to create auction: ${response.code()} - ${errorBody}")
                    _error.value = "Error creating auction: ${response.code()} - ${errorBody}"
                }
            } catch (e: IOException) {
                println("ERROR: Network exception when creating auction: ${e.message}")
                _error.value = "Network error: ${e.message}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                println("ERROR: HTTP exception when creating auction: ${e.code()} - ${errorBody ?: e.message()}")
                _error.value = "HTTP Error ${e.code()}: ${errorBody ?: e.message()}"
            } catch (e: Exception) {
                println("ERROR: Unexpected exception when creating auction: ${e.message}")
                e.printStackTrace()
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
                println("DEBUG: createSubasta process finished. Loading: false.")
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