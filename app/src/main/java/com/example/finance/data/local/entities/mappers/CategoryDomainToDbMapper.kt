package com.example.finance.data.local.entities.mappers

import com.example.finance.data.local.entities.CategoryDb
import com.example.finance.domain.entities.Category

class CategoryDomainToDbMapper : (Category) -> CategoryDb {

    override fun invoke(category: Category): CategoryDb = CategoryDb(
        id = category.id,
        name = category.name,
        type = category.type
    )
}