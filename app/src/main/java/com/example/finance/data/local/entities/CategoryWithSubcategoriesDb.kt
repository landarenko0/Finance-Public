package com.example.finance.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithSubcategoriesDb(
    @Embedded val category: CategoryDb,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val subcategories: List<SubcategoryDb>
)
