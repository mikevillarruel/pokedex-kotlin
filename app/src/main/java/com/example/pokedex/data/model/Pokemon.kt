package com.example.pokedex.data.model

data class PokemonResponse(
    val count: Long = 0,
    val next: String = "",
    val previous: Any? = null,
    val results: List<Pokemon> = listOf<Pokemon>()
)

data class Pokemon(
    val name: String,
    val url: String
)
