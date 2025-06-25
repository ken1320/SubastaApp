package com.example.subastaapp.model

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ¡NUEVO! Define la URL base como una constante accesible públicamente
    const val BASE_URL = "http://10.0.2.2:3000/" // La URL base de tu servidor

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Aumentar timeout de conexión
        .readTimeout(30, TimeUnit.SECONDS)    // Aumentar timeout de lectura
        .writeTimeout(30, TimeUnit.SECONDS)   // Aumentar timeout de escritura
        .build()

    val api: SubastaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Usa la constante definida arriba
            .client(okHttpClient) // ¡AÑADIR EL CLIENTE PERSONALIZADO!
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SubastaApi::class.java)
    }
}