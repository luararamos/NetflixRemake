package co.tiagoaguiar.netflixremake.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class DowloadImageTask(private val callback: Callback){
    private val handler = android.os.Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    interface Callback {
        fun onResult(bitmap: Bitmap)

    }

    fun execute(url: String) {
        executor.execute {
            var urlConnection: HttpsURLConnection? = null
            var stream: InputStream? = null
            try {
                val requestURL = URL(url)
                urlConnection = requestURL.openConnection() as HttpsURLConnection  //abrir a conexão
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000

                stream = urlConnection.inputStream
                val bitmap = BitmapFactory.decodeStream(stream)

                handler.post { // UI Thread
                    callback.onResult(bitmap)
                }
            } catch (e: IOException) {
                val message = e.message ?: "erro desconhecido"
                Log.e("Teste", message, e)
            } finally {
                urlConnection?.disconnect()
                stream?.close()
            }

        }
    }
}