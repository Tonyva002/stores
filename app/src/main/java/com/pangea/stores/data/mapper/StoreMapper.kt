package com.pangea.stores.data.mapper

import com.pangea.stores.data.local.entity.StoreEntity
import com.pangea.stores.domain.model.Store

fun StoreEntity.toDomain() = Store(
   id = id,
    name = name,
    phone = phone,
    website = website,
    photoUrl = photoUrl,
    isFavorite = isFavorite
)

fun Store.toEntity() = StoreEntity(
    id = id,
    name = name,
    phone = phone,
    website = website,
    photoUrl = photoUrl,
    isFavorite = isFavorite

)