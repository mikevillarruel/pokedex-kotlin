package com.example.pokedex.ui.home

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.R
import com.example.pokedex.core.Result
import com.example.pokedex.data.RetrofitClient.webService
import com.example.pokedex.data.model.Pokemon
import com.example.pokedex.data.remote.RemotePokemonDataSource
import com.example.pokedex.databinding.FragmentHomeBinding
import com.example.pokedex.domain.PokemonRepoImpl
import com.example.pokedex.presentation.PokemonViewModel
import com.example.pokedex.presentation.PokemonViewModelFactory
import com.example.pokedex.ui.home.adapter.PokemonAdapter

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private var listPokemon: MutableList<Pokemon> = mutableListOf()
    private lateinit var adapter: PokemonAdapter
    private var limit = 50
    private var offset = 0
    private var statusBarSize = 0

    private val viewModel by viewModels<PokemonViewModel> {
        PokemonViewModelFactory(
            PokemonRepoImpl(
                RemotePokemonDataSource(
                    webService = webService
                )
            )
        )
    }

    private fun pokemonClick(pokemon: Pokemon) {
        val action = HomeFragmentDirections.actionHomeFragmentToPokemonDetailFragment(pokemon)
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
        adapter = PokemonAdapter(listPokemon, onClick = { pokemon -> pokemonClick(pokemon) })
        binding.rvHome.adapter = adapter

        view.setOnApplyWindowInsetsListener { view, insets ->
            statusBarSize = insets.systemWindowInsetTop
            Log.d("data", "$statusBarSize")
            insets
        }

        binding.rvHome.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                itemPosition: Int,
                parent: RecyclerView
            ) {
                super.getItemOffsets(outRect, itemPosition, parent)
                val columns = (binding.rvHome.layoutManager as GridLayoutManager).spanCount
                if (itemPosition < columns) {
                    outRect.top = statusBarSize
                }
            }
        })

        binding.rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = (binding.rvHome.layoutManager as LinearLayoutManager)
                val lastItem = 1 + layoutManager.findLastCompletelyVisibleItemPosition()

                if (lastItem == offset && listPokemon.size == offset) {
                    loadMore()
                }
            }
        })

        if (listPokemon.size == 0) {
            loadMore()
        }
    }

    fun loadMore() {
        viewModel.getPokemons(offset, limit).observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    if (listPokemon.size == 0) {
                        binding.progressBar.visibility = View.VISIBLE
                    } else {
                        listPokemon.add(Pokemon())
                        adapter.notifyItemInserted(offset)
                    }
                }
                is Result.Success -> {

                    if (listPokemon.size == 0) {
                        binding.progressBar.visibility = View.GONE
                    } else {
                        listPokemon.removeLast()
                        adapter.notifyItemRemoved(offset)
                    }

                    val next = result.data.next

                    if (!next.isNullOrEmpty()) {
                        val nextData = next.split("=", "&")
                        offset = nextData[1].toInt()
                        limit = nextData[3].toInt()
                    }

                    result.data.results.forEach {
                        listPokemon.add(it)
                    }

                    adapter.notifyItemInserted(listPokemon.size - 1)

                }
                is Result.Failure -> {

                    binding.progressBar.visibility = View.GONE

                    if (listPokemon.size == 0) {
                        binding.progressBar.visibility = View.GONE
                    } else {
                        listPokemon.removeLast()
                        adapter.notifyItemRemoved(offset)
                    }

                    Toast.makeText(
                        requireContext(),
                        "Error: ${result.exception}",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        })
    }
}