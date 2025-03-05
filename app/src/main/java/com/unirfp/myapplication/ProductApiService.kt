package com.unirfp.myapplication

import retrofit2.Response
import retrofit2.http.GET

// Interfaz de la API para definir los métodos para acceder a los datos de la API (endpoints)
interface ProductApiService {

    // Anotación GET para hacer la llamada http al endpoint "products" que se añadirá a la URL base
    @GET("products")
    suspend fun getProducts(): Response<ProductResponse>
}