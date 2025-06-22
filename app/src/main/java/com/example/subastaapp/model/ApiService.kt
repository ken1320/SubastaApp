package com.example.subastaapp.model

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("subastas")
    suspend fun getSubastas(): List<Subasta>

    @POST("subastas")
    suspend fun crearSubasta(@Body subasta: SubastaCreationRequest): Response<Subasta>

    @POST("subastas/{id}/pujar")
    suspend fun pujar(@Path("id") id: String, @Body puja: PujaRequest): Response<Unit>

    @POST("subastas/{id}/finalizar")
    suspend fun finalizar(@Path("id") id: String): Response<Subasta>

    @DELETE("subastas/{id}")
    suspend fun eliminar(@Path("id") id: String): Response<Unit>
}