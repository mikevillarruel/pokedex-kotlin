package com.example.pokedex.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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
    private var limit = 30
    private var offset = 0
    private var last = limit - 1

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

        binding.rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = (binding.rvHome.layoutManager as LinearLayoutManager)
                val lastItem = layoutManager.findLastCompletelyVisibleItemPosition()

                if ((lastItem + 1).mod(limit) == 0 && lastItem == last) {

                    listPokemon.add(Pokemon())

                    adapter.notifyDataSetChanged()

                    offset = offset + limit
                    last = last + limit

                    loadMore(offset, limit)
                }
            }
        })

        if (listPokemon.size == 0) {
            loadMore(offset, limit)
        }
    }

    fun loadMore(offset: Int, limit: Int) {
        viewModel.getPokemons(offset, limit).observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    if (listPokemon.size == 0) {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
                is Result.Success -> {

                    if (listPokemon.size == 0) {
                        binding.progressBar.visibility = View.GONE
                    } else {
                        listPokemon.removeLast()
                    }

                    if (result.data.count == listPokemon.size.toLong()) return@Observer

                    result.data.results.forEach {
                        listPokemon.add(it)
                    }

                    adapter.notifyDataSetChanged()

                }
                is Result.Failure -> {

                    binding.progressBar.visibility = View.GONE

                    if (listPokemon.size == 0) {
                        binding.progressBar.visibility = View.GONE
                    } else {
                        listPokemon.removeLast()
                        adapter.notifyDataSetChanged()
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