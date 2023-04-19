package co.tiagoaguiar.netflixremake.util

import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class CategoryTask {
    fun execute(url: String) {
        // nesse momento, estamos utilizando a UI-thread (1)
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {


                // nesse momento, estamos utilizando a NOVA-thread [processo paralelo] (2)
                //>>>>>>>AQUI é um código meio que PADRAO<<<<<<
                val requestURL = URL(url) // abrir uma URL
                val urlConnection =
                    requestURL.openConnection() as HttpURLConnection //abrir a conexão
                urlConnection.readTimeout = 2000 //tempo leitura (2s)
                urlConnection.connectTimeout = 2000 // tempo conexão (2s)

                val statusCode: Int = urlConnection.responseCode
                if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor")
                }
                // forma1: simples e rápida
                val stream = urlConnection.inputStream // sequencia bytes
                val jsonAsString = stream.bufferedReader().use { it.readText() } // bytes -> String
                Log.i("Teste", jsonAsString)

                // forma2: ???

            } catch (e: IOException ) {
                Log.e("Teste", e.message ?: "erro desconhecido", e)

            }

        }
    }
}