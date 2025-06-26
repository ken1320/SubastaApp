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

@ExperimentalCoroutinesApi
class SubastaViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SubastaViewModel
    private val mockContext: Context = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SubastaViewModel(mockContext)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun clearError_estableceErrorANulo() = runTest {
        // Arrange (Preparar)
        // Simula que ya hay un error establecido en el ViewModel
        val errorField = viewModel.javaClass.getDeclaredField("_error")
        errorField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val mutableErrorFlow = errorField.get(viewModel) as MutableStateFlow<String?>
        mutableErrorFlow.value = "Algún mensaje de error de prueba"

        // Act (Actuar)
        viewModel.clearError()
        // Permite que las coroutines pendientes se completen
        advanceUntilIdle()

        // Assert (Afirmar)
        // Verifica que el valor del StateFlow 'error' es ahora nulo
        val currentError = viewModel.error.first()
        assertNull(currentError)
    }
}