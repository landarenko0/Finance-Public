package com.example.finance.data.local.entities.mappers

import com.example.finance.data.local.entities.CategoryDb
import com.example.finance.domain.entities.Category

fun Category.toDb(): CategoryDb = CategoryDb(
    id = this.id,
    name = this.name,
    type = this.type
)

fun List<Category>.toDb(): List<CategoryDb> = this.map { it.toDb() }