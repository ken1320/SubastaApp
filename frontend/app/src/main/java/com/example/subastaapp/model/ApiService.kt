package com.example.subastaapp.model

import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody

interface ApiService {
    @GET("subastas")
    suspend fun getSubastas(): List<Subasta>

    @POST("subastas")
    suspend fun crearSubasta(@Body subasta: SubastaCreationRequest): Response<Subasta> // ¡NO CAMBIA LA FIRMA, PERO EL BODY AHORA PUEDE INCLUIR IMAGENURL!

    @POST("subastas/{id}/pujar")
    suspend fun pujar(@Path("id") id: String, @Body puja: PujaRequest): Response<Unit>

    @POST("subastas/{id}/finalizar")
    suspend fun finalizar(@Path("id") id: String): Response<Subasta>

    @DELETE("subastas/{id}")
    suspend fun eliminar(@Path("id") id: String): Response<Unit>
    @Multipart // Es crucial para enviar datos multipart
    @POST("upload") // La ruta de tu endpoint de subida en el backend (ej. /api/upload)
    suspend fun uploadImage(@Part image: MultipartBody.Part): retrofit2.Response<UploadResponse>
}

