package com.example.subastaapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 1. Cambia el tipo de 'api' de ApiService a SubastaApi
    val api: SubastaApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            // 2. Cambia ApiService::class.java a SubastaApi::class.java
            .create(SubastaApi::class.java)
    }
}