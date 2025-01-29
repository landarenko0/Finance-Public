package com.example.finance.domain.entities.mappers

import com.example.finance.data.local.entities.CategoryWithSubcategoriesDb
import com.example.finance.domain.entities.CategoryWithSubcategories

class CategoryWithSubcategoriesDbToDomainMapper(
    private val categoryDbToDomainMapper: CategoryDbToDomainMapper,
    private val subcategoryDbToDomainMapper: SubcategoryDbToDomainMapper
) : (CategoryWithSubcategoriesDb) -> CategoryWithSubcategories {

    override fun invoke(
        categoryWithSubcategories: CategoryWithSubcategoriesDb
    ): CategoryWithSubcategories {
        val categoryDomain = categoryDbToDomainMapper(categoryWithSubcategories.category)
        val subcategoriesDomain =
            categoryWithSubcategories.subcategories.map { subcategoryDbToDomainMapper(it) }

        return CategoryWithSubcategories(
            category = categoryDomain,
            subcategories = subcategoriesDomain
        )
    }
}