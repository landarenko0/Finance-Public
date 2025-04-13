package com.example.finance.domain.entities.mappers

import com.example.finance.data.local.entities.GroupedCategoriesDb
import com.example.finance.domain.entities.GroupedCategories

fun GroupedCategoriesDb.toDomain(): GroupedCategories = GroupedCategories(
    operationType = this.operationType,
    categoryName = this.categoryName,
    totalSum = this.totalSum,
    categoryId = this.categoryId
)

fun List<GroupedCategoriesDb>.toDomain(): List<GroupedCategories> = this.map { it.toDomain() }