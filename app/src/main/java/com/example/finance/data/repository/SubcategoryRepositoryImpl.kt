package com.example.finance.data.repository

import com.example.finance.data.local.dao.SubcategoryDao
import com.example.finance.data.local.entities.mappers.toDb
import com.example.finance.domain.entities.Subcategory
import com.example.finance.domain.entities.mappers.toDomain
import com.example.finance.domain.repository.SubcategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SubcategoryRepositoryImpl(
    private val subcategoryDao: SubcategoryDao,
    private val dispatcher: CoroutineDispatcher
) : SubcategoryRepository {

    override fun getAll(): Flow<List<Subcategory>> = subcategoryDao.getAll()
        .map { it.toDomain() }
        .flowOn(dispatcher)

    override suspend fun getObjectById(objectId: Int): Subcategory =
        withContext(dispatcher) { subcategoryDao.getSubcategoryById(objectId).toDomain() }

    override suspend fun insert(obj: Subcategory) =
        withContext(dispatcher) { subcategoryDao.insert(obj.toDb()) }

    override suspend fun update(obj: Subcategory) =
        withContext(dispatcher) { subcategoryDao.update(obj.toDb()) }

    override suspend fun delete(obj: Subcategory) =
        withContext(dispatcher) { subcategoryDao.delete(obj.toDb()) }

    override suspend fun deleteSubcategoriesByIds(subcategoriesIds: List<Int>) =
        withContext(dispatcher) { subcategoryDao.deleteSubcategoriesByIds(subcategoriesIds) }

    override suspend fun getCategorySubcategories(categoryId: Int): List<Subcategory> =
        withContext(dispatcher) { subcategoryDao.getCategorySubcategories(categoryId).toDomain() }
}