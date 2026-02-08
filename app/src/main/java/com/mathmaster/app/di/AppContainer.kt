package com.mathmaster.app.di

import android.content.Context
import com.mathmaster.app.data.db.AppDatabase
import com.mathmaster.app.data.db.ProfileDao
import com.mathmaster.app.data.repository.ProfileRepository

/**
 * Manual dependency injection container for the app.
 * Provides singleton instances of repositories and DAOs.
 */
class AppContainer(context: Context) {
    private val database: AppDatabase = AppDatabase.getInstance(context)

    val profileDao: ProfileDao = database.profileDao()
    val profileRepository: ProfileRepository = ProfileRepository(profileDao)
}
