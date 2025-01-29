package com.example.finance.data.local.entities.mappers

import com.example.finance.data.local.entities.SubcategoryDb
import com.example.finance.domain.entities.Subcategory

class SubcategoryDomainToDbMapper : (Subcategory) -> SubcategoryDb {

    override fun invoke(subcategory: Subcategory): SubcategoryDb = SubcategoryDb(
        id = subcategory.id,
        name = subcategory.name,
        categoryId = subcategory.categoryId
    )
}