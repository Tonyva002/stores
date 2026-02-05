package com.pangea.stores.ui.home.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pangea.stores.R
import com.pangea.stores.databinding.ItemStoreBinding
import com.pangea.stores.domain.model.Store

class StoreViewHolder(
    private val binding: ItemStoreBinding,
    private val onClick: (Store) -> Unit,
    private val onFavorite: (Store) -> Unit,
    private val onDelete: (Store) -> Unit
) : RecyclerView.ViewHolder( binding.root){


    fun bind(store: Store) = with(binding){
        tvName.text = store.name
        cbFavorite.isChecked = store.isFavorite

        Glide.with(imgPhoto.context)
            .load(store.photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .into(imgPhoto)

        root.setOnClickListener {
            onClick(store)
        }

        cbFavorite.setOnClickListener {
            onFavorite(store)
        }

        root.setOnLongClickListener {
            onDelete(store)
            true
        }
    }
}