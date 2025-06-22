package com.example.subastaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subastaapp.model.RetrofitClient
import com.example.subastaapp.model.Subasta
import com.example.subastaapp.model.PujaRequest
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

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
                println("DEBUG: ViewModel attempting to fetch auctions.") // DEBUG
                val fetchedSubastas = RetrofitClient.api.getSubastas()
                _subastas.value = fetchedSubastas
                println("DEBUG: Auctions successfully loaded: ${fetchedSubastas.size} items.") // DEBUG
            } catch (e: IOException) {
                _error.value = "Network error: ${e.message}"
                println("ERROR: Network error fetching auctions: ${e.message}") // DEBUG
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _error.value = "HTTP Error ${e.code()}: ${errorBody ?: e.message()}"
                println("ERROR: HTTP error fetching auctions: ${e.code()} - ${errorBody}") // DEBUG
            } catch (e: Exception) {
                _error.value = "Unexpected error fetching auctions: ${e.message}"
                println("ERROR: Unexpected error fetching auctions: ${e.message}") // DEBUG
                e.printStackTrace()
            } finally {
                _isLoading.value = false
                println("DEBUG: Auction fetching finished. Loading: false.") // DEBUG
            }
        }
    }

    fun crearSubasta(subastaData: SubastaCreationRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // *** DEBUGGING: ViewModel attempting to create auction ***
                println("DEBUG: ViewModel attempting to create auction with data: $subastaData")
                val response = RetrofitClient.api.crearSubasta(subastaData)
                if (response.isSuccessful) {
                    // *** DEBUGGING: Auction created successfully ***
                    println("DEBUG: Auction created successfully. Code: ${response.code()}, Body: ${response.body()}")
                    onSuccess()
                    fetchSubastas() // Reload the list after creation
                } else {
                    val errorBody = response.errorBody()?.string()
                    // *** DEBUGGING: Failed to create auction (unsuccessful response) ***
                    println("ERROR: Failed to create auction: ${response.code()} - ${errorBody}")
                    _error.value = "Error creating auction: ${response.code()} - ${errorBody}"
                }
            } catch (e: IOException) {
                // *** DEBUGGING: Network exception ***
                println("ERROR: Network exception when creating auction: ${e.message}")
                _error.value = "Network error: ${e.message}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                // *** DEBUGGING: HTTP exception ***
                println("ERROR: HTTP exception when creating auction: ${e.code()} - ${errorBody ?: e.message()}")
                _error.value = "HTTP Error ${e.code()}: ${errorBody ?: e.message()}"
            } catch (e: Exception) {
                // *** DEBUGGING: Unexpected exception ***
                println("ERROR: Unexpected exception when creating auction: ${e.message}")
                e.printStackTrace()
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
                // *** DEBUGGING: createSubasta process finished ***
                println("DEBUG: createSubasta process finished. Loading: false.")
            }
        }
    }

    fun pujar(subastaId: String, montoPuja: Double, pujadorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val pujaRequest = PujaRequest(monto = montoPuja, pujador = pujadorId) // Corrected: pujador
                println("DEBUG: ViewModel attempting to bid on auction $subastaId with: $pujaRequest") // DEBUG
                val response = RetrofitClient.api.pujar(subastaId, pujaRequest)
                if (response.isSuccessful) {
                    println("DEBUG: Bid placed successfully on auction $subastaId.") // DEBUG
                    fetchSubastas()
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("ERROR: Failed to bid on auction $subastaId: ${response.code()} - ${errorBody}") // DEBUG
                    _error.value = "Error bidding: ${response.code()} - ${errorBody}"
                }
            } catch (e: IOException) {
                println("ERROR: Network error when bidding: ${e.message}") // DEBUG
                _error.value = "Network error: ${e.message}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                println("ERROR: HTTP error when bidding: ${e.code()}: ${errorBody ?: e.message()}") // DEBUG
                _error.value = "HTTP Error ${e.code()}: ${errorBody ?: e.message()}"
            } catch (e: Exception) {
                println("ERROR: Unexpected error when bidding: ${e.message}") // DEBUG
                e.printStackTrace()
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
                println("DEBUG: Bidding process finished.") // DEBUG
            }
        }
    }

    fun finalizar(subastaId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                println("DEBUG: ViewModel attempting to finalize auction: $subastaId") // DEBUG
                val response = RetrofitClient.api.finalizar(subastaId)
                if (response.isSuccessful) {
                    println("DEBUG: Auction $subastaId finalized successfully.") // DEBUG
                    fetchSubastas() // Reload the list
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("ERROR: Failed to finalize auction $subastaId: ${response.code()} - ${errorBody}") // DEBUG
                    _error.value = "Error finalizing auction: ${response.code()} - ${errorBody}"
                }
            } catch (e: IOException) {
                println("ERROR: Network error finalizing auction: ${e.message}") // DEBUG
                _error.value = "Network error: ${e.message}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                println("ERROR: HTTP error finalizing auction: ${e.code()}: ${errorBody ?: e.message()}") // DEBUG
                _error.value = "HTTP Error ${e.code()}: ${errorBody ?: e.message()}"
            } catch (e: Exception) {
                println("ERROR: Unexpected error finalizing auction: ${e.message}") // DEBUG
                e.printStackTrace()
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
                println("DEBUG: Finalizing auction process finished.") // DEBUG
            }
        }
    }

    fun eliminar(subastaId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                println("DEBUG: ViewModel attempting to delete auction: $subastaId") // DEBUG
                val response = RetrofitClient.api.eliminar(subastaId)
                if (response.isSuccessful) {
                    println("DEBUG: Auction $subastaId deleted successfully.") // DEBUG
                    onSuccess() // Execute success action (e.g., go back to the list)
                    fetchSubastas() // Reload the list
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("ERROR: Failed to delete auction $subastaId: ${response.code()} - ${errorBody}") // DEBUG
                    _error.value = "Error deleting auction: ${response.code()} - ${errorBody}"
                }
            } catch (e: IOException) {
                println("ERROR: Network error deleting auction: ${e.message}") // DEBUG
                _error.value = "Network error: ${e.message}"
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                println("ERROR: HTTP error deleting auction: ${e.code()}: ${errorBody ?: e.message()}") // DEBUG
                _error.value = "HTTP Error ${e.code()}: ${errorBody ?: e.message()}"
            } catch (e: Exception) {
                println("ERROR: Unexpected error deleting auction: ${e.message}") // DEBUG
                e.printStackTrace()
                _error.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
                println("DEBUG: Deleting auction process finished.") // DEBUG
            }
        }
    }
}