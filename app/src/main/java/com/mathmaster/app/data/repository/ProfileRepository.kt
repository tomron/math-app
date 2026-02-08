package com.mathmaster.app.data.repository

import com.mathmaster.app.data.db.ProfileDao
import com.mathmaster.app.data.db.ProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for profile data operations.
 * Provides a clean API over the ProfileDao.
 */
open class ProfileRepository(private val profileDao: ProfileDao) {

    open fun getAllProfiles(): Flow<List<ProfileEntity>> {
        return profileDao.getAllProfiles()
    }

    open suspend fun getProfileById(id: Long): ProfileEntity? {
        return profileDao.getProfileById(id)
    }

    open suspend fun getProfileByName(name: String): ProfileEntity? {
        return profileDao.getProfileByName(name)
    }

    open suspend fun createProfile(name: String): Result<Long> {
        return try {
            // Check if name already exists
            val existing = profileDao.getProfileByName(name)
            if (existing != null) {
                return Result.failure(IllegalArgumentException("Profile name already exists"))
            }

            val initials = generateInitials(name)
            val profile = ProfileEntity(name = name, initials = initials)
            val id = profileDao.insert(profile)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    open suspend fun deleteProfile(profile: ProfileEntity) {
        profileDao.delete(profile)
    }

    open suspend fun deleteProfileById(id: Long) {
        profileDao.deleteById(id)
    }

    private fun generateInitials(name: String): String {
        val words = name.trim().split("\\s+".toRegex())
        return when {
            words.isEmpty() -> ""
            words.size == 1 -> words[0].take(2).uppercase()
            else -> "${words[0].first()}${words[1].first()}".uppercase()
        }
    }
}
