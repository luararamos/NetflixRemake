package co.tiagoaguiar.netflixremake.util

import android.util.Log
import java.io.IOException
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask {
    fun execute(url: String) {
        // nesse momento, estamos utilizando a UI-thread (1)
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            // nesse momento, estamos utilizando a NOVA-thread [processo paralelo] (2)
            //>>>>>>>AQUI é um código meio que PADRAO<<<<<<
            try {
                val requestURL = URL(url) // abrir uma URL
                val urlConnection =
                    requestURL.openConnection() as HttpsURLConnection //abrir a conexão
                urlConnection.readTimeout = 2000 //tempo leitura (2s)
                urlConnection.connectTimeout = 2000 // tempo conexão (2s)

                val statusCode: Int = urlConnection.responseCode //statusCode 202/404...
                if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor")
                }
                // forma1: simples e rápida
                val stream = urlConnection.inputStream // sequencia bytes
                val jsonAsString = stream.bufferedReader().use { it.readText() } // bytes -> String
                Log.i("Teste", jsonAsString)

                // forma2: ???


            } catch (e: IOException) {
                Log.e("Teste", e.message ?: "erro desconhecido", e)

            }
            //>>>>>>>AQUI é um código meio que PADRAO<<<<<<

        }
    }
}