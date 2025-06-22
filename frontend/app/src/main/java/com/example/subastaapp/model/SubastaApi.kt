package com.example.subastaapp.model

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path

interface SubastaApi {
    @GET("subastas")
    suspend fun getSubastas(): List<Subasta>

    @POST("subastas")
    suspend fun crearSubasta(@Body subasta: SubastaCreationRequest): Response<Subasta>

    // ¡¡¡VERIFICA ESTA FUNCIÓN!!!
    @POST("subastas/{id}/ocuparPuesto")
    suspend fun ocuparPuesto(
        @Path("id") subastaId: String,
        @Body request: OcuparPuestoRequest
    ): Response<Void> // O Response<Subasta> si quieres la subasta actualizada, pero Response<Void> está bien para que no espere un cuerpo de respuesta específico.

    @POST("subastas/{id}/finalizar")
    suspend fun finalizar(@Path("id") subastaId: String): Response<Void>

    @DELETE("subastas/{id}")
    suspend fun eliminar(@Path("id") subastaId: String): Response<Void>
}