package com.example.finance.domain.entities.mappers

import com.example.finance.data.local.entities.CategoryWithSubcategoriesDb
import com.example.finance.domain.entities.CategoryWithSubcategories

fun CategoryWithSubcategoriesDb.toDomain(): CategoryWithSubcategories = CategoryWithSubcategories(
    category = this.category.toDomain(),
    subcategories = this.subcategories.map { it.toDomain() }
)

fun List<CategoryWithSubcategoriesDb>.toDomain(): List<CategoryWithSubcategories> =
    this.map { it.toDomain() }