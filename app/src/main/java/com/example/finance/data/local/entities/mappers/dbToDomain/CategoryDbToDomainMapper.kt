package com.example.finance.data.local.entities.mappers.dbToDomain

import com.example.finance.data.local.entities.CategoryDb
import com.example.finance.domain.entities.Category

fun CategoryDb.toDomain(): Category = Category(
    id = this.id,
    name = this.name,
    type = this.type
)

fun List<CategoryDb>.toDomain(): List<Category> = this.map { it.toDomain() }