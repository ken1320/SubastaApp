package com.example.subastaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.subastaapp.view.* // Asegúrate de que DetalleSubastaScreen esté en este paquete o impórtalo específicamente
import com.example.subastaapp.viewmodel.SubastaViewModel
import com.example.subastaapp.view.DetalleSubastaScreen // ¡¡¡NUEVA IMPORTACIÓN o verifica la existente!!!
// import com.example.subastaapp.model.Subasta // Esta línea probablemente no es necesaria y puedes eliminarla

@Composable
fun SubastaNavHost(
    navController: NavHostController,
    viewModel: SubastaViewModel
) {
    NavHost(navController = navController, startDestination = "lista") {
        composable("lista") {
            val subastasState by viewModel.subastas.collectAsState()
            ListaSubastasScreen(
                subastas = subastasState,
                onCrearSubasta = { navController.navigate("crear") },
                onVerDetalles = { subasta ->
                    viewModel.seleccionarSubasta(subasta)
                    navController.navigate("detalle")
                }
            )
        }

        composable("crear") {
            CrearSubastaScreen(
                // ¡CAMBIO! El callback ahora recibe una Uri
                onCrear = { titulo, descripcion, precioInicial, fechaFin, imagenUri ->
                    // ¡CAMBIO! Llamamos a la nueva versión de crearSubasta en el ViewModel
                    viewModel.crearSubasta(
                        titulo,
                        descripcion,
                        precioInicial,
                        fechaFin,
                        imagenUri
                    ) {
                        navController.popBackStack()
                    }
                },
                onCancelar = { navController.popBackStack() }
            )
        }

        composable("detalle") {
            // No necesitamos pasar 'subasta' directamente aquí porque DetalleSubastaScreen
            // ya obtiene la subasta seleccionada del ViewModel internamente.
            // Tampoco necesitamos los callbacks individuales aquí, ya que DetalleSubastaScreen
            // llamará directamente a las funciones del ViewModel.
            DetalleSubastaScreen(
                navController = navController, // Pasa el navController
                viewModel = viewModel // Pasa el viewModel
            )
            // Ya no es necesario el subastaDetalle?.let { ... } aquí, ya que
            // DetalleSubastaScreen maneja el caso de subasta nula internamente
            // como lo definimos en la última corrección.
        }
    }
}