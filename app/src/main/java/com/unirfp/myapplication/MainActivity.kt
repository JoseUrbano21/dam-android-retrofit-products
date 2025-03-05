package com.unirfp.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.unirfp.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    // Variable para implementar la interfaz en la app
    private val productApiService = RetrofitClient.getProductApiService()

    // Definimos variables dinámicas y las inicializamos
    private var productList by mutableStateOf(emptyList<Product>())
    private var errorMessage by mutableStateOf<String?>(null)

    // Variable para guardar el texto de búsqueda para filtrar la lista
    private var searchQuery by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        fetchProductList()
        setContent {
            MyApplicationTheme {
                DisplayData(
                    productList,
                    searchQuery,
                    { newQuery -> searchQuery = newQuery },
                    errorMessage
                )
            }
        }
    }

    // Obtener la lista de productos de la API
    private fun fetchProductList() {
        // Ejecutamos un hilo en segundo plano para no bloquear el hilo principal
        CoroutineScope(Dispatchers.IO).launch {
            // Guardamos la respuesta de la API en una variable
            val response = productApiService.getProducts()
            // Si obtenemos respuesta de la API, guardamos la lista obtenida en una variable
            if (response.isSuccessful) {
                val resultado = response.body()?.results ?: emptyList()
                // Mostramos los resultados en el logcat
                resultado.forEach { product ->
                    Log.d(
                        "API_RESPONSE",
                        "Producto: ${product.name}, Precio: ${product.price}, Imagen: ${product.image}"
                    )
                }
                // Cambiamos al entorno principal y guardamos la lista en la variable declarada al principio
                withContext(Dispatchers.Main) {
                    productList = resultado
                }
            } else {
                // Si da error la API, guardamos el mensaje del error en una variable
                val error = response.errorBody()?.string() ?: "Error desconocido"
                // Mostramos el error, así como el código
                Log.e("Error API", "${response.code()}: $error")
                withContext(Dispatchers.Main) {
                    errorMessage = "Error al cargar productos. Inténtalo más tarde."
                }
            }
        }
    }

    // *************** FUNCIONES JETPACK COMPOSE (UI) ***************
    @Composable
    fun DisplayData(
        productList: List<Product>,
        searchQuery: String,
        onSearchQueryChanged: (String) -> Unit,
        errorMessage: String?
    ) {
        errorMessage?.let { message ->
            // Si el error no es nulo (hay error), lo muestra en la app
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = message, textAlign = TextAlign.Center)
            }
        } ?: MostrarListaProductos(
            // En caso de que no haya error (sea nulo), muestra la lista
            productList = productList,
            searchQuery = searchQuery,
            onSearchQueryChanged = onSearchQueryChanged
        )
    }

    @Composable
    fun MostrarListaProductos(
        // Pasamos como parámetros la lista de productos
        productList: List<Product>,
        searchQuery: String,
        onSearchQueryChanged: (String) -> Unit
    ) {
        Column {
            // Barra de búsqueda
            TextField(
                value = searchQuery,
                onValueChange = { newQuery ->
                    onSearchQueryChanged(newQuery)
                },
                label = { Text("Buscar Producto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .windowInsetsPadding(WindowInsets.statusBars)
            )

            // Filtrar la lista de Pokémon
            val filteredList =
                productList.filter { it.name.contains(searchQuery, ignoreCase = true) }

            // Componente de JPC para mostrar la lista
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Recorremos la lista y mostramos cada elemento
                items(filteredList) { product ->
                    Log.d("ImagenURL", "Cargando imagen desde: ${product.image}")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Imagen del producto
                            Image(
                                painter = rememberAsyncImagePainter(model = product.image),
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                                contentScale = ContentScale.Fit
                            )
                            // Nombre del producto
                            Text(text = product.name, textAlign = TextAlign.Center)
                            // Precio
                            Text(text = "Precio: ${product.price}€", textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}