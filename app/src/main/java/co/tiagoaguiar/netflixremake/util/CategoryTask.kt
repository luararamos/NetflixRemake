package co.tiagoaguiar.netflixremake.util

import android.util.Log
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
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

                val stream = urlConnection.inputStream // sequencia bytes

                // forma1: simples e rápida
                // val jsonAsString = stream.bufferedReader().use { it.readText() } // bytes -> String

                // forma2: bytes -> String
                val buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)

                // o JSON está preparado para ser convertido em um DATA CLASS
                Log.i("Teste", jsonAsString)

            } catch (e: IOException) {
                Log.e("Teste", e.message ?: "erro desconhecido", e)

            }
            //>>>>>>>AQUI é um código meio que PADRAO<<<<<<

        }
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