package co.tiagoaguiar.netflixremake.util

import android.util.Log
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask(private val callback: Callback) {

    interface Callback {
        fun onResult(categories: List<Category>)

    }

    fun execute(url: String) {
        // nesse momento, estamos utilizando a UI-thread (1)
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            var urlConnection: HttpsURLConnection? = null
            var buffer: BufferedInputStream? = null
            var stream: InputStream? = null
            // nesse momento, estamos utilizando a NOVA-thread [processo paralelo] (2)
            //>>>>>>>AQUI é um código meio que PADRAO<<<<<<
            try {
                val requestURL = URL(url)  // abrir uma URL
                urlConnection = requestURL.openConnection() as HttpsURLConnection  //abrir a conexão
                urlConnection.readTimeout = 2000 //tempo leitura (2s)
                urlConnection.connectTimeout = 2000 // tempo conexão (2s)

                val statusCode: Int = urlConnection.responseCode
                if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor!")
                }

                stream = urlConnection.inputStream // sequencia bytes

                // forma1: simples e rápida
                // val jsonAsString = stream.bufferedReader().use { it.readText() } // bytes -> String

                // forma2: bytes -> String
                buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)

                // o JSON está preparado para ser convertido em um DATA CLASS
                val categories = toCategories(jsonAsString)
                callback.onResult(categories)

            } catch (e: IOException) {
                Log.e("Teste", e.message ?: "erro desconhecido", e)
            } finally {
                urlConnection?.disconnect()
                stream?.close()
                buffer?.close()
            }
            //>>>>>>>AQUI é um código meio que PADRAO<<<<<<

        }
    }

    private fun toCategories(jsonAsString: String) : List<Category> {
        val categories = mutableListOf<Category>()

        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategories = jsonRoot.getJSONArray("category")
        for (i in 0 until jsonCategories.length()) {
            val jsonCategory = jsonCategories.getJSONObject(i)

            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>()
            for (j in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(id, coverUrl))
            }

            categories.add(Category(title, movies))
        }

        return categories
    }

    private fun toString(stream: InputStream): String {
        val bytes = ByteArray(1024)
        val baos = ByteArrayOutputStream()
        var read: Int
        while (true) {
            read = stream.read(bytes)
            if (read <= 0) {
                break
            }
            baos.write(bytes, 0, read)
        }
        return String(baos.toByteArray())

    }
}