package com.example.finance.data.local.entities.mappers

import com.example.finance.data.local.entities.SubcategoryDb
import com.example.finance.domain.entities.Subcategory

fun Subcategory.toDb(): SubcategoryDb = SubcategoryDb(
    id = this.id,
    name = this.name,
    categoryId = this.categoryId
)

fun List<Subcategory>.toDb(): List<SubcategoryDb> = this.map { it.toDb() }