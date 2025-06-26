package com.example.subastaapp.model

import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody

/**
 * Interfaz para operaciones de la API de subastas.
 */
interface ApiService {
    /** Obtiene todas las subastas. */
    @GET("subastas")
    suspend fun getSubastas(): List<Subasta>

    /** Crea una nueva subasta. */
    @POST("subastas")
    suspend fun crearSubasta(@Body subasta: SubastaCreationRequest): Response<Subasta>

    /** Realiza una puja en una subasta. */
    @POST("subastas/{id}/pujar")
    suspend fun pujar(@Path("id") id: String, @Body puja: PujaRequest): Response<Unit>

    /** Finaliza una subasta. */
    @POST("subastas/{id}/finalizar")
    suspend fun finalizar(@Path("id") id: String): Response<Subasta>

    /** Elimina una subasta. */
    @DELETE("subastas/{id}")
    suspend fun eliminar(@Path("id") id: String): Response<Unit>

    /** Sube una imagen al servidor. */
    @Multipart
    @POST("upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): retrofit2.Response<UploadResponse>
}
