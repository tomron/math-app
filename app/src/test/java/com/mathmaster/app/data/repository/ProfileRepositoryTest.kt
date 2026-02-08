package com.mathmaster.app.data.repository

import com.mathmaster.app.data.db.ProfileDao
import com.mathmaster.app.data.db.ProfileEntity
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProfileRepositoryTest {

    private lateinit var repository: ProfileRepository
    private lateinit var fakeDao: FakeProfileDao

    @BeforeEach
    fun setup() {
        fakeDao = FakeProfileDao()
        repository = ProfileRepository(fakeDao)
    }

    @Test
    fun `createProfile generates correct initials for single word name`() = runTest {
        val result = repository.createProfile("John")

        assertTrue(result.isSuccess)
        val profile = fakeDao.profiles.first()
        assertEquals("JO", profile.initials)
    }

    @Test
    fun `createProfile generates correct initials for two word name`() = runTest {
        val result = repository.createProfile("John Doe")

        assertTrue(result.isSuccess)
        val profile = fakeDao.profiles.first()
        assertEquals("JD", profile.initials)
    }

    @Test
    fun `createProfile fails when name already exists`() = runTest {
        repository.createProfile("John")
        val result = repository.createProfile("John")

        assertTrue(result.isFailure)
        assertEquals(1, fakeDao.profiles.size)
    }

    @Test
    fun `deleteProfile removes profile from dao`() = runTest {
        repository.createProfile("John")
        val profile = fakeDao.profiles.first()

        repository.deleteProfile(profile)

        assertTrue(fakeDao.profiles.isEmpty())
    }
}

// Fake implementation for testing
class FakeProfileDao : ProfileDao {
    val profiles = mutableListOf<ProfileEntity>()
    private var nextId = 1L

    override fun getAllProfiles() = flowOf(profiles.toList())

    override suspend fun getProfileById(profileId: Long): ProfileEntity? {
        return profiles.find { it.id == profileId }
    }

    override suspend fun getProfileByName(name: String): ProfileEntity? {
        return profiles.find { it.name == name }
    }

    override suspend fun insert(profile: ProfileEntity): Long {
        val id = nextId++
        profiles.add(profile.copy(id = id))
        return id
    }

    override suspend fun delete(profile: ProfileEntity) {
        profiles.remove(profile)
    }

    override suspend fun deleteById(profileId: Long) {
        profiles.removeAll { it.id == profileId }
    }
}
