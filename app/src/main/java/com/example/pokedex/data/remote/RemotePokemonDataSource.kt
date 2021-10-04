package com.example.pokedex.data.remote

import com.example.pokedex.data.WebService
import com.example.pokedex.data.model.PokemonResponse

class RemotePokemonDataSource(private val webService: WebService) {
    suspend fun getPokemons(): PokemonResponse = webService.getPokemons(0, 898)
}