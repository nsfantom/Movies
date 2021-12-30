package com.example.moviedb.screens

import com.example.moviedb.constants.Credentials
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviedb.App
import com.example.moviedb.R
import com.example.moviedb.adapter.MovieAdapter
import com.example.moviedb.api.MovieApi
import com.example.moviedb.model.MovieModel
import com.example.moviedb.response.MoviesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SimilarMovieListFragment : Fragment(), MovieAdapter.OnMovieClickListener {

    @Inject
    lateinit var movieApi: MovieApi

    lateinit var recyclerView: RecyclerView

    lateinit var adapter: MovieAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_similar_movie_list, container, false)

        recyclerView = view.findViewById(R.id.similar_movie_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        val movieId = SimilarMovieListFragmentArgs.fromBundle(requireArguments()).movieId

        setUpAdapter(movieId)

        return view
    }

    private fun setUpAdapter(movieId: Int) {
        val responseCall = movieApi.getSimilarMovies(movieId, Credentials.API_KEY)

        responseCall.enqueue(object : Callback<MoviesResponse> {
            override fun onResponse(
                call: Call<MoviesResponse>,
                response: Response<MoviesResponse>
            ) {
                if (response.isSuccessful) {
                    val movieList = response.body()?.popularMovies

                    adapter = context?.let {
                        movieList?.let { it1 ->
                            MovieAdapter(
                                it1,
                                it,
                                this@SimilarMovieListFragment
                            )
                        }
                    }!!

                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                Log.i("SIMILAR_MOVIES_FRAGMENT", "ERROR: " + t.message)
            }
        })
    }

    override fun onMovieClick(position: Int, view: View, movies: List<MovieModel>) {
        view.findNavController()
            .navigate(
                SimilarMovieListFragmentDirections.actionSimilarMovieListFragmentToMovieDetailFragment(
                    movies[position].id
                )
            )
    }
}