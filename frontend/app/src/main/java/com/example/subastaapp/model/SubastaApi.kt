package com.example.subastaapp.model

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

data class UploadResponse(
    val msg: String,
    val filePath: String
)

interface SubastaApi {
    @GET("api/subastas")
    suspend fun getSubastas(): List<Subasta>

    @POST("api/subastas") // ¡¡¡CAMBIA ESTO!!! Añade "/api/"
    suspend fun crearSubasta(@Body subasta: SubastaCreationRequest): Response<Subasta>

    @Multipart
    @POST("api/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<UploadResponse>

    @POST("api/subastas/{id}/ocuparPuesto") // ¡¡¡Asegúrate de que esta también tenga "/api/"!!!
    suspend fun ocuparPuesto(
        @Path("id") subastaId: String,
        @Body request: OcuparPuestoRequest
    ): Response<Void>

    @POST("api/subastas/{id}/finalizar") // ¡¡¡Asegúrate de que esta también tenga "/api/"!!!
    suspend fun finalizar(@Path("id") subastaId: String): Response<Void>

    @DELETE("api/subastas/{id}") // ¡¡¡Asegúrate de que esta también tenga "/api/"!!!
    suspend fun eliminar(@Path("id") subastaId: String): Response<Void>
}