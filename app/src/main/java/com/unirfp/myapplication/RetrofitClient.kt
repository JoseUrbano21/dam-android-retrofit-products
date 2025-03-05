package com.unirfp.myapplication

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// ************** CREACION Y CONFIGURACION DE INSTANCIA RETROFIT **************
private fun getRetrofit(): Retrofit {
    return Retrofit.Builder() // Inicia la construccion de Retrofit
        .baseUrl("https://peticiones.online/api/") // Definimos la URL base
        .addConverterFactory(GsonConverterFactory.create()) // Usa Gson para convertir JSON en objetos Kotlin
        .build() // Construye y devuelve la instancia de Retrofit
}

// ************** SINGLETON DE RETROFIT PARA NO CREAR MULTIPLES INSTANCIAS EN LA APP **************
object RetrofitClient {
    // Instancia única de Retrofit obtenida con getRetrofit()
    private val retrofit: Retrofit = getRetrofit()

    // Metodo para obtener la implementación de ProductoApiService
    fun getProductApiService(): ProductApiService {
        // Retrofit crea una implementación de la interfaz ProductoApiService para manejar las llamadas a la API
        return retrofit.create(ProductApiService::class.java)
    }
}