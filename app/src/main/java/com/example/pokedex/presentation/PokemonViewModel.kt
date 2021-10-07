package com.example.pokedex.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.example.pokedex.core.Result
import com.example.pokedex.domain.PokemonRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

class PokemonViewModel(private val repo: PokemonRepo) : ViewModel() {
    fun getPokemons(offset: Int, limit: Int) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            if (offset != 0) {
                delay(500L)
            }
            emit(Result.Success(repo.getPokemons(offset, limit)))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}

class PokemonViewModelFactory(private val repo: PokemonRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(PokemonRepo::class.java).newInstance(repo)
    }
}