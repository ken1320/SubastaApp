package com.example.subastaapp.model

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Configuración y proveedor de Retrofit para la API.
 */
object RetrofitClient {

    /** URL base del servidor de la API. */
    const val BASE_URL = "http://10.0.2.2:3000/"

    /** Cliente HTTP con tiempos de espera extendidos. */
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /** Instancia lazy de la API de Subastas. */
    val api: SubastaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SubastaApi::class.java)
    }
}
