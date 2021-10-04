package com.example.pokedex.domain

import com.example.pokedex.data.model.PokemonResponse

interface PokemonRepo {
    suspend fun getPokemons(): PokemonResponse
}