package com.example.finance.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.finance.domain.datastore.AccountIdRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

private val ACCOUNT_ID = intPreferencesKey("account_id")

class AccountIdRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    dispatcher: CoroutineDispatcher
) : AccountIdRepository {

    override val data: Flow<Int?> = dataStore.data.map { it[ACCOUNT_ID] }.flowOn(dispatcher)

    override suspend fun updateAccountId(accountId: Int) {
        try {
            dataStore.edit { preferences ->
                preferences[ACCOUNT_ID] = accountId
            }
        } catch (ex: Exception) {
            throw ex
        }
    }
}