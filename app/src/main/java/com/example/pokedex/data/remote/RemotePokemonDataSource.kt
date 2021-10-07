package com.example.pokedex.data.remote

import com.example.pokedex.data.WebService
import com.example.pokedex.data.model.PokemonResponse

class RemotePokemonDataSource(private val webService: WebService) {
    suspend fun getPokemons(
        offset: Int, limit: Int
    ): PokemonResponse = webService.getPokemons(offset, limit)
}