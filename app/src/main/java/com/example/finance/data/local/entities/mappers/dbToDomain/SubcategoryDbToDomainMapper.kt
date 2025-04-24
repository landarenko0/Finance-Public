package com.example.finance.data.local.entities.mappers.dbToDomain

import com.example.finance.data.local.entities.SubcategoryDb
import com.example.finance.domain.entities.Subcategory

fun SubcategoryDb.toDomain(): Subcategory = Subcategory(
    id = this.id,
    categoryId = this.categoryId,
    name = this.name
)

fun List<SubcategoryDb>.toDomain(): List<Subcategory> = this.map { it.toDomain() }