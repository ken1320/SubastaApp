package com.example.subastaapp

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.subastaapp.viewmodel.SubastaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Clase de pruebas unitarias para [SubastaViewModel].
 * Utiliza InstantTaskExecutorRule para ejecutar tareas de LiveData de forma síncrona.
 * Utiliza un TestDispatcher para controlar las coroutines en las pruebas.
 */
@ExperimentalCoroutinesApi
class SubastaViewModelTest {

    // Regla que permite ejecutar tareas de LiveData de forma instantánea en los tests.
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SubastaViewModel // Instancia del ViewModel a probar.
    private val mockContext: Context = mockk(relaxed = true) // Mock del contexto de Android.

    private val testDispatcher = StandardTestDispatcher() // Dispatcher de prueba para coroutines.

    /**
     * Configuración inicial antes de cada prueba.
     * Establece el Dispatcher principal para las coroutines.
     * Inicializa el ViewModel con un contexto mock.
     */
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher) // Establece el dispatcher principal para coroutines.
        viewModel = SubastaViewModel(mockContext) // Inicializa el ViewModel.
    }

    /**
     * Limpieza después de cada prueba.
     * Restablece el Dispatcher principal.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain() // Restablece el dispatcher principal.
    }

    /**
     * Prueba la función `clearError()` del ViewModel.
     * Verifica que el StateFlow `_error` se establece en nulo después de llamar a `clearError()`.
     */
    @Test
    fun clearError_estableceErrorANulo() = runTest {
        // Arrange (Preparar):
        // Simula que ya hay un error establecido en el ViewModel.
        val errorField = viewModel.javaClass.getDeclaredField("_error")
        errorField.isAccessible = true // Permite acceso al campo privado.
        @Suppress("UNCHECKED_CAST")
        // Obtiene el MutableStateFlow del campo privado _error.
        val mutableErrorFlow = errorField.get(viewModel) as MutableStateFlow<String?>
        mutableErrorFlow.value = "Algún mensaje de error de prueba" // Establece un error de prueba.

        // Act (Actuar):
        viewModel.clearError() // Llama a la función que se está probando.
        advanceUntilIdle() // Permite que las coroutines pendientes se completen.

        // Assert (Afirmar):
        // Verifica que el valor del StateFlow 'error' es ahora nulo.
        val currentError = viewModel.error.first() // Obtiene el primer valor emitido por el StateFlow.
        assertNull(currentError) // Afirma que el error es nulo.
    }
}
