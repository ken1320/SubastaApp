package com.example.subastaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext // ¡NUEVA IMPORTACIÓN!
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.subastaapp.navigation.SubastaNavHost
import com.example.subastaapp.viewmodel.SubastaViewModel
import androidx.lifecycle.ViewModelProvider // ¡NUEVA IMPORTACIÓN!

// ¡NUEVO! Factory para poder pasar el Context al ViewModel
class SubastaViewModelFactory(private val context: android.content.Context) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubastaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SubastaViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            // ¡CAMBIO! Creamos el ViewModel usando nuestro Factory
            val context = LocalContext.current.applicationContext
            val subastaViewModel: SubastaViewModel = viewModel(
                factory = SubastaViewModelFactory(context)
            )
            SubastaNavHost(navController, subastaViewModel)
        }
    }
}