package com.example.finance.domain.entities.mappers

import com.example.finance.data.local.entities.SubcategoryDb
import com.example.finance.domain.entities.Subcategory

class SubcategoryDbToDomainMapper : (SubcategoryDb) -> Subcategory {

    override fun invoke(subcategoryDb: SubcategoryDb): Subcategory = Subcategory(
        id = subcategoryDb.id,
        categoryId = subcategoryDb.categoryId,
        name = subcategoryDb.name
    )
}