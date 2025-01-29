package com.example.finance.domain.entities.mappers

import com.example.finance.data.local.entities.CategoryDb
import com.example.finance.domain.entities.Category

class CategoryDbToDomainMapper : (CategoryDb) -> Category {

    override fun invoke(categoryDb: CategoryDb): Category = Category(
        id = categoryDb.id,
        name = categoryDb.name,
        type = categoryDb.type
    )
}