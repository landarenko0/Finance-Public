package com.example.finance.domain.usecases

import com.example.finance.domain.repository.BaseRepository
import kotlinx.coroutines.flow.Flow

class GetAllUseCase<T>(private val repository: BaseRepository<T>) {

    operator fun invoke(): Flow<List<T>> = repository.getAll()
}

class GetObjectByIdUseCase<T>(private val repository: BaseRepository<T>) {

    suspend operator fun invoke(objectId: Int): T = repository.getObjectById(objectId)
}

class InsertUseCase<T>(private val repository: BaseRepository<T>) {

    suspend operator fun invoke(obj: T) = repository.insert(obj)
}

class InsertAllUseCase<T>(private val repository: BaseRepository<T>) {

    suspend operator fun invoke(objects: List<T>) = repository.insertAll(objects)
}

class UpdateUseCase<T>(private val repository: BaseRepository<T>) {

    suspend operator fun invoke(obj: T) = repository.update(obj)
}

class DeleteUseCase<T>(private val repository: BaseRepository<T>) {

    suspend operator fun invoke(obj: T) = repository.delete(obj)
}

class DeleteObjectByIdUseCase<T>(private val repository: BaseRepository<T>) {

    suspend operator fun invoke(objectId: Int) = repository.deleteObjectById(objectId)
}