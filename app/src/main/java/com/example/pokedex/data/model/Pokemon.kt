package com.example.pokedex.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class PokemonResponse(
    val count: Long = 0,
    val next: String = "",
    val previous: Any? = null,
    val results: List<Pokemon> = listOf<Pokemon>()
)

@Parcelize
data class Pokemon(
    val name: String = "",
    val url: String = ""
) : Parcelable
