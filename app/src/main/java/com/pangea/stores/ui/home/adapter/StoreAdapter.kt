package com.pangea.stores.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.pangea.stores.databinding.ItemStoreBinding
import com.pangea.stores.domain.model.Store


class StoreAdapter(
    private val onClick: (Store) -> Unit,
    private val onFavorite: (Store) -> Unit,
    private val onDelete: (Store) -> Unit
) : ListAdapter<Store, StoreViewHolder> (DiffCallback){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoreViewHolder {
        val binding = ItemStoreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoreViewHolder(binding, onClick, onFavorite, onDelete)
    }

    override fun onBindViewHolder(
        holder: StoreViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Store>(){
            override fun areItemsTheSame(
                oldItem: Store,
                newItem: Store
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Store,
                newItem: Store
            ): Boolean = oldItem == newItem


        }
    }
}