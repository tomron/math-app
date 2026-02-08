package com.mathmaster.app.ui.profile

import app.cash.turbine.test
import com.mathmaster.app.data.db.ProfileEntity
import com.mathmaster.app.data.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileSelectionViewModelTest {

    private lateinit var viewModel: ProfileSelectionViewModel
    private lateinit var fakeRepository: FakeProfileRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeProfileRepository()
        viewModel = ProfileSelectionViewModel(fakeRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState starts with empty profiles list`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.profiles.isEmpty())
            assertFalse(state.isLoading)
            assertNull(state.error)
            assertFalse(state.showCreateDialog)
        }
    }

    @Test
    fun `showCreateDialog updates showCreateDialog state`() = runTest {
        viewModel.showCreateDialog()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.showCreateDialog)
        }
    }

    @Test
    fun `hideCreateDialog updates showCreateDialog state and clears error`() = runTest {
        viewModel.showCreateDialog()
        viewModel.hideCreateDialog()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.showCreateDialog)
            assertNull(state.error)
        }
    }

    @Test
    fun `createProfile with valid name succeeds`() = runTest {
        var callbackInvoked = false
        var returnedId = -1L

        viewModel.createProfile("John Doe") { id ->
            callbackInvoked = true
            returnedId = id
        }

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertFalse(state.showCreateDialog)
            assertNull(state.error)
        }

        assertTrue(callbackInvoked)
        assertEquals(1L, returnedId)
    }

    @Test
    fun `createProfile with blank name shows error`() = runTest {
        viewModel.createProfile("   ") { }

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals("Name cannot be empty", state.error)
        }
    }

    @Test
    fun `createProfile with duplicate name shows error`() = runTest {
        fakeRepository.shouldFailCreation = true

        viewModel.createProfile("John") { }

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.error)
        }
    }

    @Test
    fun `deleteProfile calls repository`() = runTest {
        val profile = ProfileEntity(id = 1, name = "John", initials = "JO")

        viewModel.deleteProfile(profile)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(fakeRepository.deleteCalled)
    }

    @Test
    fun `profiles are loaded from repository on init`() = runTest {
        val profiles = listOf(
            ProfileEntity(id = 1, name = "John", initials = "JO"),
            ProfileEntity(id = 2, name = "Jane", initials = "JA")
        )
        fakeRepository.profiles = profiles

        val newViewModel = ProfileSelectionViewModel(fakeRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        newViewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.profiles.size)
            assertEquals("John", state.profiles[0].name)
            assertEquals("Jane", state.profiles[1].name)
        }
    }
}

class FakeProfileRepository : ProfileRepository(
    com.mathmaster.app.data.repository.FakeProfileDao()
) {
    var profiles: List<ProfileEntity> = emptyList()
    var shouldFailCreation = false
    var deleteCalled = false

    override fun getAllProfiles() = flowOf(profiles)

    override suspend fun createProfile(name: String): Result<Long> {
        return if (shouldFailCreation) {
            Result.failure(IllegalArgumentException("Profile name already exists"))
        } else {
            Result.success(1L)
        }
    }

    override suspend fun deleteProfile(profile: ProfileEntity) {
        deleteCalled = true
    }
}
