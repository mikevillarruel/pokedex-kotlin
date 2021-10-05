package com.example.pokedex.ui.home.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.pokedex.data.model.Pokemon
import com.example.pokedex.databinding.PokemonItemBinding

class PokemonAdapter(
    private val pokemonList: List<Pokemon>,
    private val onClick: (Pokemon) -> Unit
) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    class PokemonViewHolder(
        private val itemBinding: PokemonItemBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: Pokemon) {
            val urlSplit = item.url.split("/")
            val id = urlSplit[urlSplit.size - 2]
            itemBinding.txtPokemonName.text = item.name
            itemBinding.txtPokemonNumber.text = "#$id"

            Glide.with(context)
                .asBitmap()
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png")
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val palette = Palette.from(resource).generate()
                        itemBinding.constraintCard.setBackgroundColor(palette.getDominantColor(0))
                        itemBinding.imgPokemon.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PokemonAdapter.PokemonViewHolder {
        val itemBinding = PokemonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = PokemonViewHolder(itemBinding, parent.context)

        itemBinding.constraintCard.setOnClickListener {
            onClick(pokemonList[holder.bindingAdapterPosition])
        }

        return holder
    }

    override fun onBindViewHolder(holder: PokemonAdapter.PokemonViewHolder, position: Int) {
        holder.bind(pokemonList[position])
    }

    override fun getItemCount(): Int = pokemonList.size
}