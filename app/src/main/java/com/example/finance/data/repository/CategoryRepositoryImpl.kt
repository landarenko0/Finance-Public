package com.example.finance.data.repository

import com.example.finance.data.local.dao.CategoryDao
import com.example.finance.data.local.entities.mappers.toDb
import com.example.finance.domain.entities.Category
import com.example.finance.domain.entities.CategoryWithSubcategories
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.entities.mappers.toDomain
import com.example.finance.domain.repository.CategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val dispatcher: CoroutineDispatcher
) : CategoryRepository {

    override fun getCategoriesByType(type: OperationType): Flow<List<Category>> =
        categoryDao.getCategoriesByType(type).map { it.toDomain() }.flowOn(dispatcher)

    override fun getCategoryWithSubcategoriesById(
        categoryId: Int
    ): Flow<CategoryWithSubcategories?> = categoryDao.getCategoryWithSubcategoriesById(categoryId)
        .map { it?.toDomain() }
        .flowOn(dispatcher)

    override fun getAll(): Flow<List<Category>> = categoryDao.getAll()
        .map { it.toDomain() }
        .flowOn(dispatcher)

    override suspend fun getObjectById(objectId: Int): Category =
        withContext(dispatcher) { categoryDao.getCategoryById(objectId).toDomain() }

    override suspend fun insert(obj: Category) =
        withContext(dispatcher) { categoryDao.insert(obj.toDb()) }

    override suspend fun update(obj: Category) =
        withContext(dispatcher) { categoryDao.update(obj.toDb()) }

    override suspend fun delete(obj: Category) =
        withContext(dispatcher) { categoryDao.delete(obj.toDb()) }
}