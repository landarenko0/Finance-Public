package com.example.finance.domain.entities

data class CategoryWithSubcategories(
    val category: Category,
    val subcategories: List<Subcategory>
)
