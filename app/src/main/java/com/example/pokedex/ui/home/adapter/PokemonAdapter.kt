package com.example.pokedex.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokedex.data.model.Pokemon
import com.example.pokedex.databinding.PokemonItemBinding

class PokemonAdapter(private val pokemonList: List<Pokemon>) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    class PokemonViewHolder(
        private val itemBinding: PokemonItemBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: Pokemon) {
            val urlSplit = item.url.split("/")
            val id = urlSplit[urlSplit.size - 2]

            Glide.with(context)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png")
                .into(itemBinding.imgPokemon)

            itemBinding.txtPokemonName.text = item.name
            itemBinding.txtPokemonNumber.text = "#$id"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PokemonAdapter.PokemonViewHolder {
        val itemBinding =
            PokemonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = PokemonViewHolder(itemBinding, parent.context)
        return holder
    }

    override fun onBindViewHolder(holder: PokemonAdapter.PokemonViewHolder, position: Int) {
        holder.bind(pokemonList[position])
    }

    override fun getItemCount(): Int = pokemonList.size
}