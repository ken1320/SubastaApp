package com.example.subastaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.subastaapp.view.* // Asegúrate de que DetalleSubastaScreen esté en este paquete o impórtalo específicamente
import com.example.subastaapp.viewmodel.SubastaViewModel
import com.example.subastaapp.ui.DetalleSubastaScreen // ¡¡¡NUEVA IMPORTACIÓN o verifica la existente!!!
import com.example.subastaapp.model.SubastaCreationRequest
// import com.example.subastaapp.model.Subasta // Esta línea probablemente no es necesaria y puedes eliminarla
import java.util.Date // Esta línea probablemente no es necesaria y puedes eliminarla

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
                onCrear = { titulo, descripcion, precioInicial, fechaFin, imagenUrl ->
                    println("DEBUG: onCrear received in NavHost. Creating SubastaCreationRequest...")
                    val subastaRequest = SubastaCreationRequest(
                        titulo = titulo,
                        descripcion = descripcion,
                        precioInicial = precioInicial,
                        fechaFin = fechaFin,
                        imagenUrl = imagenUrl
                    )
                    viewModel.crearSubasta(subastaRequest) {
                        println("DEBUG: createSubasta callback received in NavHost. Navigating back.")
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