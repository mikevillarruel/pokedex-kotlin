package com.example.pokedex.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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
    private var listPokemon: List<Pokemon> = listOf()
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


        if (listPokemon.size == 0) {
            viewModel.getPokemons().observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Log.d("Pokemons", result.data.results.toString())

                        listPokemon = result.data.results

                        binding.rvHome.adapter =
                            PokemonAdapter(
                                listPokemon,
                                onClick = { pokemon -> pokemonClick(pokemon) })
                    }
                    is Result.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Error: ${result.exception}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            })
        } else {
            binding.rvHome.adapter =
                PokemonAdapter(
                    listPokemon,
                    onClick = { pokemon -> pokemonClick(pokemon) })
        }
    }
}