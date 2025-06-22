package com.example.subastaapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val api: ApiService by lazy {
        Retrofit.Builder()
            // Si el backend corre en localhost en tu PC y usas un emulador de Android:
            .baseUrl("http://10.0.2.2:3000/api/")
            // SI YA TIENES TU BACKEND DESPLEGADO EN UN SERVIDOR REAL, CAMBIA ESTO:
            // .baseUrl("https://tu-backend-subastas.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}