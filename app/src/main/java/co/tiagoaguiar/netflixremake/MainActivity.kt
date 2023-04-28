package co.tiagoaguiar.netflixremake

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Movie
import co.tiagoaguiar.netflixremake.util.CategoryTask

class MainActivity : AppCompatActivity(), CategoryTask.Callback {

    private lateinit var progress: ProgressBar

    // m-v-c (model - [view/controller] activity)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progress = findViewById(R.id.progress_main)

        val categories = mutableListOf<Category>()

        val adapter = CategoryAdapter(categories)
        val rv: RecyclerView = findViewById(R.id.rv_main)
        rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv.adapter = adapter

        CategoryTask(this).execute("https://api.tiagoaguiar.co/netflixapp/home?apiKey=f47cbd00-e981-4b94-a66f-8f8ee06e8314")
    }

    override fun onPreExecute() {
        progress.visibility = View.VISIBLE
    }

    override fun onResult(categories: List<Category>) {
        //  aqui será quando o CategoryTask chamará de volta
        //  (callback) - listener
        Log.i("Teste MainActivity", categories.toString())
        progress.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        progress.visibility = View.GONE
    }
}