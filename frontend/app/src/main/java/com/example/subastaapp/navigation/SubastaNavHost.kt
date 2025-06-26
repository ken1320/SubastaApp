package com.example.subastaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.subastaapp.view.*
import com.example.subastaapp.viewmodel.SubastaViewModel

/**
 * Define la estructura de navegación de la aplicación de subastas.
 * Gestiona las transiciones entre las diferentes pantallas.
 *
 * @param navController Controlador de navegación para manejar las rutas.
 * @param viewModel ViewModel que proporciona los datos y lógica de negocio.
 */
@Composable
fun SubastaNavHost(
    navController: NavHostController,
    viewModel: SubastaViewModel
) {
    NavHost(navController = navController, startDestination = "lista") {
        /**
         * Ruta para la pantalla de lista de subastas.
         */
        composable("lista") {
            val subastasState by viewModel.subastas.collectAsState() // Observa el estado de las subastas.
            ListaSubastasScreen(
                subastas = subastasState, // Pasa la lista de subastas.
                onCrearSubasta = { navController.navigate("crear") }, // Navega a la pantalla de creación.
                onVerDetalles = { subasta ->
                    viewModel.seleccionarSubasta(subasta) // Selecciona la subasta para ver detalles.
                    navController.navigate("detalle") // Navega a la pantalla de detalle.
                }
            )
        }

        /**
         * Ruta para la pantalla de creación de subastas.
         */
        composable("crear") {
            CrearSubastaScreen(
                onCrear = { titulo, descripcion, precioInicial, fechaFin, imagenUri ->
                    // Llama a la función de crear subasta en el ViewModel.
                    viewModel.crearSubasta(
                        titulo,
                        descripcion,
                        precioInicial,
                        fechaFin,
                        imagenUri
                    ) {
                        navController.popBackStack() // Vuelve a la pantalla anterior al crear.
                    }
                },
                onCancelar = { navController.popBackStack() } // Vuelve a la pantalla anterior al cancelar.
            )
        }

        /**
         * Ruta para la pantalla de detalle de una subasta.
         */
        composable("detalle") {
            // La pantalla de detalle obtiene la subasta directamente del ViewModel.
            DetalleSubastaScreen(
                navController = navController, // Pasa el navController.
                viewModel = viewModel // Pasa el viewModel.
            )
        }
    }
}
