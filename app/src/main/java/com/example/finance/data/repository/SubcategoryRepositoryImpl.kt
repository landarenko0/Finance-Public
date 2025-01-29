package com.example.finance.data.repository

import com.example.finance.data.local.dao.SubcategoryDao
import com.example.finance.data.local.entities.mappers.SubcategoryDomainToDbMapper
import com.example.finance.domain.entities.Subcategory
import com.example.finance.domain.entities.mappers.SubcategoryDbToDomainMapper
import com.example.finance.domain.repository.SubcategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubcategoryRepositoryImpl(
    private val subcategoryDao: SubcategoryDao,
    private val subcategoryDbToDomainMapper: SubcategoryDbToDomainMapper,
    private val subcategoryDomainToDbMapper: SubcategoryDomainToDbMapper
) : SubcategoryRepository {

    override fun getAll(): Flow<List<Subcategory>> = subcategoryDao.getAll().map { subcategories ->
        subcategories.map(subcategoryDbToDomainMapper)
    }

    override suspend fun getObjectById(objectId: Int): Subcategory {
        val subcategoryDb = subcategoryDao.getSubcategoryById(objectId)
        return subcategoryDbToDomainMapper(subcategoryDb)
    }

    override suspend fun insert(obj: Subcategory) =
        subcategoryDao.insert(subcategoryDomainToDbMapper(obj))

    override suspend fun insertAll(objects: List<Subcategory>) =
        subcategoryDao.insertAll(objects.map(subcategoryDomainToDbMapper))

    override suspend fun update(obj: Subcategory) = subcategoryDao.update(subcategoryDomainToDbMapper(obj))

    override suspend fun delete(obj: Subcategory) =
        subcategoryDao.delete(subcategoryDomainToDbMapper(obj))

    override suspend fun deleteObjectById(objectId: Int) =
        subcategoryDao.deleteSubCategoryById(objectId)

    override suspend fun deleteSubcategoriesByIds(subcategoriesIds: List<Int>) =
        subcategoryDao.deleteSubcategoriesByIds(subcategoriesIds)
}