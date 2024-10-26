package com.site.pochak.app.core.datastore


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_HANDLE_KEY = stringPreferencesKey("user_handle")
    }

    fun getAccessToken(): Flow<String?> {
        return dataStore.data.map {
            it[ACCESS_TOKEN_KEY]
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return dataStore.data.map {
            it[REFRESH_TOKEN_KEY]
        }
    }

    fun getUserHandle(): Flow<String?> {
        return dataStore.data.map {
            it[USER_HANDLE_KEY]
        }
    }

    suspend fun saveAccessToken(accessToken: String) {
        dataStore.edit {
            it[ACCESS_TOKEN_KEY] = accessToken
        }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit {
            it[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun saveUserHandle(userHandle: String) {
        dataStore.edit {
            it[USER_HANDLE_KEY] = userHandle
        }
    }

    suspend fun deleteAccessToken() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN_KEY)
        }
    }

    suspend fun deleteRefreshToken() {
        dataStore.edit {
            it.remove(REFRESH_TOKEN_KEY)
        }
    }

    suspend fun deleteUserHandle() {
        dataStore.edit {
            it.remove(USER_HANDLE_KEY)
        }
    }

    suspend fun saveUserData(accessToken: String, refreshToken: String, userHandle: String) {
        dataStore.edit {
            it[ACCESS_TOKEN_KEY] = accessToken
            it[REFRESH_TOKEN_KEY] = refreshToken
            it[USER_HANDLE_KEY] = userHandle
        }
    }
}