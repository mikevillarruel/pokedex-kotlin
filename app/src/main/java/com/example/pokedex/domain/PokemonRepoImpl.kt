package com.example.pokedex.domain

import com.example.pokedex.data.model.PokemonResponse
import com.example.pokedex.data.remote.RemotePokemonDataSource

class PokemonRepoImpl(private val dataSource: RemotePokemonDataSource) : PokemonRepo {
    override suspend fun getPokemons(): PokemonResponse = dataSource.getPokemons()
}