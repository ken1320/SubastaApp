package com.example.subastaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.subastaapp.view.* // Asegúrate de que tus pantallas estén aquí
import com.example.subastaapp.viewmodel.SubastaViewModel
import com.example.subastaapp.model.Subasta
import com.example.subastaapp.model.SubastaCreationRequest
import java.util.Date

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
                onCrear = { titulo, descripcion, precioInicial, fechaFin ->
                    // *** DEBUGGING: Added line to verify onCrear reception in NavHost ***
                    println("DEBUG: onCrear received in NavHost. Creating SubastaCreationRequest...")
                    val subastaRequest = SubastaCreationRequest(
                        titulo = titulo,
                        descripcion = descripcion,
                        precioInicial = precioInicial,
                        fechaFin = fechaFin
                    )
                    viewModel.crearSubasta(subastaRequest) {
                        // *** DEBUGGING: Added line to verify success callback ***
                        println("DEBUG: createSubasta callback received in NavHost. Navigating back.")
                        navController.popBackStack() // Go back to the previous screen (list) after creating
                    }
                },
                onCancelar = { navController.popBackStack() }
            )
        }

        composable("detalle") {
            val subastaDetalle by viewModel.subastaSeleccionada.collectAsState()

            subastaDetalle?.let { subasta ->
                DetalleSubastaScreen(
                    subasta = subasta,
                    onRealizarPuja = { subastaId, montoPuja, pujadorId ->
                        viewModel.pujar(subastaId, montoPuja, pujadorId)
                    },
                    onFinalizarSubasta = { subastaId ->
                        viewModel.finalizar(subastaId)
                    },
                    onEliminarSubasta = { subastaId ->
                        viewModel.eliminar(subastaId) {
                            navController.popBackStack()
                        }
                    }
                )
            } ?: run {
                navController.popBackStack()
            }
        }
    }
}