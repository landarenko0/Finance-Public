package com.example.finance.domain.repository

import com.example.finance.domain.entities.Subcategory

interface SubcategoryRepository : BaseRepository<Subcategory> {

    suspend fun deleteSubcategoriesByIds(subcategoriesIds: List<Int>)
}