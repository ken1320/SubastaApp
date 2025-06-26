package com.example.subastaapp.model

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Respuesta de la subida de un archivo.
 */
data class UploadResponse(
    val msg: String, // Mensaje de confirmación.
    val filePath: String // Ruta del archivo subido.
)

/**
 * Interfaz para operaciones de API de subastas y subida.
 */
interface SubastaApi {
    /** Obtiene todas las subastas. */
    @GET("api/subastas")
    suspend fun getSubastas(): List<Subasta>

    /** Crea una nueva subasta. */
    @POST("api/subastas")
    suspend fun crearSubasta(@Body subasta: SubastaCreationRequest): Response<Subasta>

    /** Sube una imagen. */
    @Multipart
    @POST("api/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<UploadResponse>

    /** Ocupa un puesto en una subasta. */
    @POST("api/subastas/{id}/ocuparPuesto")
    suspend fun ocuparPuesto(
        @Path("id") subastaId: String,
        @Body request: OcuparPuestoRequest
    ): Response<Void>

    /** Finaliza una subasta. */
    @POST("api/subastas/{id}/finalizar")
    suspend fun finalizar(@Path("id") subastaId: String): Response<Void>

    /** Elimina una subasta. */
    @DELETE("api/subastas/{id}")
    suspend fun eliminar(@Path("id") subastaId: String): Response<Void>
}
