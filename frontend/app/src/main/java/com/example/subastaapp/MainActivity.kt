package com.example.subastaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.subastaapp.navigation.SubastaNavHost
import com.example.subastaapp.viewmodel.SubastaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val subastaViewModel: SubastaViewModel = viewModel()
            SubastaNavHost(navController, subastaViewModel)
        }
    }
}