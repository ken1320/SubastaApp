package com.example.subastaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.subastaapp.view.*
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
                // ¡CAMBIO CLAVE!: Ahora el callback onCrear recibe imagenUrl
                onCrear = { titulo, descripcion, precioInicial, fechaFin, imagenUrl ->
                    println("DEBUG: onCrear received in NavHost. Creating SubastaCreationRequest...")
                    val subastaRequest = SubastaCreationRequest(
                        titulo = titulo,
                        descripcion = descripcion,
                        precioInicial = precioInicial,
                        fechaFin = fechaFin,
                        imagenUrl = imagenUrl // ¡NUEVO!: Pasar la URL de la imagen aquí
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