package com.example.finance.data.repository

import com.example.finance.data.local.dao.CategoryDao
import com.example.finance.data.local.entities.mappers.CategoryDomainToDbMapper
import com.example.finance.domain.entities.Category
import com.example.finance.domain.entities.CategoryWithSubcategories
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.entities.mappers.CategoryDbToDomainMapper
import com.example.finance.domain.entities.mappers.CategoryWithSubcategoriesDbToDomainMapper
import com.example.finance.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val categoryDbToDomainMapper: CategoryDbToDomainMapper,
    private val categoryDomainToDbMapper: CategoryDomainToDbMapper,
    private val categoryWithSubcategoriesDbToDomainMapper: CategoryWithSubcategoriesDbToDomainMapper
) : CategoryRepository {

    override fun getCategoriesByType(type: OperationType): Flow<List<Category>> =
        categoryDao.getCategoriesByType(type).map { categories ->
            categories.map(categoryDbToDomainMapper)
        }

    override suspend fun getCategoryWithSubcategoriesById(
        categoryId: Int
    ): CategoryWithSubcategories {
        val categoryWithSubcategoriesDb = categoryDao.getCategoryWithSubcategoriesById(categoryId)
        return categoryWithSubcategoriesDbToDomainMapper(categoryWithSubcategoriesDb)
    }

    override fun getAll(): Flow<List<Category>> = categoryDao.getAll().map { categories ->
        categories.map(categoryDbToDomainMapper)
    }

    override suspend fun getObjectById(objectId: Int): Category {
        val categoryDb = categoryDao.getCategoryById(objectId)
        return categoryDbToDomainMapper(categoryDb)
    }

    override suspend fun insert(obj: Category) = categoryDao.insert(categoryDomainToDbMapper(obj))

    override suspend fun insertAll(objects: List<Category>) =
        categoryDao.insertAll(objects.map(categoryDomainToDbMapper))

    override suspend fun update(obj: Category) = categoryDao.update(categoryDomainToDbMapper(obj))

    override suspend fun delete(obj: Category) = categoryDao.delete(categoryDomainToDbMapper(obj))

    override suspend fun deleteObjectById(objectId: Int) = categoryDao.deleteCategoryById(objectId)
}