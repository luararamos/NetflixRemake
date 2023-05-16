package co.tiagoaguiar.netflixremake

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import co.tiagoaguiar.netflixremake.databinding.ActivityMovieBinding
import co.tiagoaguiar.netflixremake.model.Movie
import co.tiagoaguiar.netflixremake.model.MovieDetail
import co.tiagoaguiar.netflixremake.util.DowloadImageTask
import co.tiagoaguiar.netflixremake.util.MovieTask

class MovieActivity : AppCompatActivity(), MovieTask.Callback {

    private lateinit var binding: ActivityMovieBinding
    private lateinit var adapter: MovieAdapter
    private val movies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id =
            intent?.getIntExtra("id", 0) ?: throw IllegalStateException("ID não foi encontrado")

        val url =
            "https://api.tiagoaguiar.co/netflixapp/movie/$id?apiKey=f47cbd00-e981-4b94-a66f-8f8ee06e8314"

        MovieTask(this).execute(url)

        val toolbar: Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null


        adapter = MovieAdapter(movies, R.layout.movie_item_similar)
        binding.movieRvSimilar.layoutManager = GridLayoutManager(this, 3)
        binding.movieRvSimilar.adapter = adapter

    }

    override fun onPreExecute() {
        binding.movieProgress.visibility = View.VISIBLE
    }

    override fun onFailure(message: String) {
        binding.movieProgress.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onResult(movieDetail: MovieDetail) {
        binding.movieProgress.visibility = View.GONE
        Log.i("Teste", movieDetail.toString())

        binding.movieTxtTitle.text = movieDetail.movie.title
        binding.movieTxtDesc.text = movieDetail.movie.desc
        binding.movieTxtCast.text = getString(R.string.cast, movieDetail.movie.cast)

        movies.clear()
        movies.addAll(movieDetail.similars)
        adapter.notifyDataSetChanged()

        DowloadImageTask(object : DowloadImageTask.Callback{
            override fun onResult(bitmap: Bitmap) {
                // busquei o desenhavel (layer-list)
                val layerDrawable: LayerDrawable =
                    ContextCompat.getDrawable(this@MovieActivity, R.drawable.shadows) as LayerDrawable
                // busquei o filme que eu quero
                val movieCover = BitmapDrawable(resources, bitmap)
                //atribui a esse layer-list o novo filme
                layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)
                // set no imageview
                val coverImg: ImageView = findViewById(R.id.movie_img)
                coverImg.setImageDrawable(layerDrawable)
            }

        }).execute(movieDetail.movie.coverUrl)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}