package com.yei.dev.weather_app.presentation.search

import com.yei.dev.weather_app.domain.model.Location
import com.yei.dev.weather_app.domain.usecase.SearchLocationsUseCase
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherSearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Idle with empty query`() = runTest {
        // Given
        val useCase = providesUseCaseMock()
        val sut = providesSut(useCase)
        val query = ""

        // When & Then
        val state = sut.state.value
        assertTrue(state.searchQuery == query)
        assertTrue(state.uiState is SearchUiState.Idle)
    }

    @Test
    fun `onEvent OnSearchQueryChanged should update search query`() = runTest {
        // Given
        val useCase = providesUseCaseMock()
        val sut = providesSut(useCase)
        val query = "Mosquera"

        // When
        sut.onEvent(SearchEvent.OnSearchQueryChanged(query))

        // Then
        val state = sut.state.value
        assertEquals(query, state.searchQuery)
    }

    @Test
    fun `onEvent OnSearchQueryChanged with valid query should trigger search after debounce`() = runTest {
        // Given
        val useCase = providesUseCaseMock()
        val sut = providesSut(useCase)
        val query = "Mosquera"

        // When
        sut.onEvent(SearchEvent.OnSearchQueryChanged(query))
        advanceTimeBy(500L)
        advanceUntilIdle()

        // Then
        val state = sut.state.value
        assertTrue(state.uiState is SearchUiState.Success)
        assertTrue((state.uiState as SearchUiState.Success).locations.size == 3)
    }

    @Test
    fun `onEvent OnSearchQueryChanged with query less than 3 characters should not trigger search`() = runTest {
        // Given
        val useCase = providesUseCaseMock()
        val sut = providesSut(useCase)
        val query = "Mo"

        // When
        sut.onEvent(SearchEvent.OnSearchQueryChanged(query))
        advanceTimeBy(500L)
        advanceUntilIdle()

        // Then
        val state = sut.state.value
        assertTrue(state.uiState is SearchUiState.Idle)
    }



    @Test
    fun `onEvent OnSearchQueryChanged with empty results should show Empty state`() = runTest {
        // Given
        val useCase = providesUseCaseMock(returnEmptyList = true)
        val sut = providesSut(useCase)
        val query = "NonExistentCity"

        // When
        sut.onEvent(SearchEvent.OnSearchQueryChanged(query))
        advanceTimeBy(500L)
        advanceUntilIdle()

        // Then
        val state = sut.state.value
        assertTrue(state.uiState is SearchUiState.Empty)
        assertEquals(query, state.searchQuery)
    }

    @Test
    fun `onEvent OnSearchQueryChanged with error should show Error state`() = runTest {
        // Given
        val useCase = providesUseCaseMock(returnError = true)
        val sut = providesSut(useCase)
        val query = "Mosquera"

        // When
        sut.onEvent(SearchEvent.OnSearchQueryChanged(query))
        advanceTimeBy(500L)
        advanceUntilIdle()

        // Then
        val state = sut.state.value
        assertTrue(state.uiState is SearchUiState.Error)
        assertTrue((state.uiState as SearchUiState.Error).message.isNotEmpty())
    }

    @Test
    fun `onEvent OnClearSearch should reset to initial state`() = runTest {
        // Given
        val useCase = providesUseCaseMock()
        val sut = providesSut(useCase)
        sut.onEvent(SearchEvent.OnSearchQueryChanged("Mosquera"))
        advanceTimeBy(500L)
        advanceUntilIdle()

        // When
        sut.onEvent(SearchEvent.OnClearSearch)

        // Then
        val state = sut.state.value
        assert(state.searchQuery == "")
        assert(state.uiState is SearchUiState.Idle)
    }

    @Test
    fun `onEvent OnRetry should trigger search again with current query`() = runTest {
        // Given
        val useCase = providesUseCaseMock(returnError = true)
        val sut = providesSut(useCase)
        val query = "Mosquera"
        sut.onEvent(SearchEvent.OnSearchQueryChanged(query))
        advanceTimeBy(500L)
        advanceUntilIdle()

        // When
        every { useCase(any()) } returns flowOf(Result.success(providesLocationList()))
        sut.onEvent(SearchEvent.OnRetry)
        advanceUntilIdle()

        // Then
        val state = sut.state.value
        assertTrue(state.uiState is SearchUiState.Success || state.uiState is SearchUiState.Loading)
    }

    @Test
    fun `onEvent OnSearchQueryChanged with empty query should show Idle state`() = runTest {
        // Given
        val useCase = providesUseCaseMock()
        val sut = providesSut(useCase)
        sut.onEvent(SearchEvent.OnSearchQueryChanged("Mosquera"))
        advanceTimeBy(500L)
        advanceUntilIdle()

        // When
        sut.onEvent(SearchEvent.OnSearchQueryChanged(""))
        advanceTimeBy(500L)
        advanceUntilIdle()

        // Then
        val state = sut.state.value
        assert(state.searchQuery == "")
        assert(state.uiState is SearchUiState.Idle)
    }

    @Test
    fun `debounce should cancel previous search when typing quickly`() = runTest {
        // Given
        val useCase = providesUseCaseMock()
        val sut = providesSut(useCase)

        // When - Simular escritura rápida
        sut.onEvent(SearchEvent.OnSearchQueryChanged("M"))
        advanceTimeBy(200L)
        sut.onEvent(SearchEvent.OnSearchQueryChanged("Mo"))
        advanceTimeBy(200L)
        sut.onEvent(SearchEvent.OnSearchQueryChanged("Mos"))
        advanceTimeBy(200L)
        sut.onEvent(SearchEvent.OnSearchQueryChanged("Mosq"))
        advanceTimeBy(500L) // Esperar debounce completo
        advanceUntilIdle()

        // Then - Solo debe buscar "Mosq"
        val state = sut.state.value
        assert(state.searchQuery == "Mosq")
        assert(state.uiState is SearchUiState.Success)
    }

    private fun providesSut(useCase: SearchLocationsUseCase) =
        WeatherSearchViewModel(useCase)

    private fun providesUseCaseMock(
        returnEmptyList: Boolean = false,
        returnError: Boolean = false
    ) = mockk<SearchLocationsUseCase>().apply {
        when {
            returnError -> {
                every { this@apply(any()) } returns flowOf(Result.failure(Exception("Network error")))
            }
            returnEmptyList -> {
                every { this@apply(any()) } returns flowOf(Result.success(emptyList()))
            }
            else -> {
                every { this@apply(any()) } returns flowOf(Result.success(providesLocationList()))
            }
        }
    }

    private fun providesLocationList() = listOf(
        Location(
            id = 1L,
            name = "Mosquera",
            region = "Cundinamarca",
            country = "Colombia",
            lat = 4.7059,
            lon = -74.2302
        ),
        Location(
            id = 2L,
            name = "Funza",
            region = "Cundinamarca",
            country = "Colombia",
            lat = 4.7167,
            lon = -74.2167
        ),
        Location(
            id = 3L,
            name = "Madrid",
            region = "Cundinamarca",
            country = "Colombia",
            lat = 4.7333,
            lon = -74.2667
        )
    )
}
