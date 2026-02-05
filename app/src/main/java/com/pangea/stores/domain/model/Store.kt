package com.pangea.stores.domain.model

data class Store (
    val id: Long = 0,
    val name: String = "",
    val phone: String = "",
    val website: String = "",
    val photoUrl: String = "",
    val isFavorite: Boolean = false

    )