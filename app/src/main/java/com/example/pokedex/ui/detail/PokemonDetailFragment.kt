package com.example.pokedex.ui.detail

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.pokedex.R
import com.example.pokedex.data.model.Pokemon
import com.example.pokedex.databinding.FragmentPokemonDetailBinding

class PokemonDetailFragment : Fragment(R.layout.fragment_pokemon_detail) {

    private lateinit var binding: FragmentPokemonDetailBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPokemonDetailBinding.bind(view)

        arguments?.let {
            val item = it.getParcelable<Pokemon>("pokemon") ?: Pokemon()
            binding.txtPokemonName.text = item.name

            val urlSplit = item.url.split("/")
            val id = urlSplit[urlSplit.size - 2]

            binding.txtPokemonNumber.text = "#$id"

            Glide.with(requireContext())
                .asBitmap()
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png")
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val palette = Palette.from(resource).generate()
                        binding.cardView.setBackgroundColor(palette.getDominantColor(0))
                        binding.imgPokemon.setImageBitmap(resource)
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
}