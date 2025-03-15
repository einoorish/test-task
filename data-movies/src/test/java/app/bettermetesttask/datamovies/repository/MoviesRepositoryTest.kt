package app.bettermetesttask.datamovies.repository

import app.bettermetesttask.datamovies.database.entities.MovieEntity
import app.bettermetesttask.datamovies.repository.stores.MoviesLocalStore
import app.bettermetesttask.datamovies.repository.stores.MoviesMapper
import app.bettermetesttask.datamovies.repository.stores.MoviesRestStore
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
@ExperimentalCoroutinesApi
class MoviesRepositoryImplTest {

    @Mock
    lateinit var localStore: MoviesLocalStore

    @Mock
    lateinit var remoteStore: MoviesRestStore

    @Mock
    lateinit var mapper: MoviesMapper

    private lateinit var repository: MoviesRepositoryImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = MoviesRepositoryImpl(localStore, remoteStore, mapper)
    }



    @Test
    fun `getAll retrieves from remote, saves to local, and returns mapped movies`() = runTest {
        val dataModels = listOf(movieEntity1, movieEntity2)
        val domainModels = listOf(movie1, movie2)
        whenever(mapper.mapToLocal).thenReturn { movie ->
            when(movie){
                movie1 -> movieEntity1
                movie2 -> movieEntity2
                else -> unknownEntity
            }
        }
        whenever(remoteStore.getAll()).thenReturn(domainModels)

        val result = repository.getAll()

        assertTrue(result is Result.Success)
        assertEquals(domainModels, (result as? Result.Success)?.data)
        verify(remoteStore).getAll()
        verify(localStore).insertAll(dataModels)
    }

    @Test
    fun `getAll falls back to local when remote fetch fails`() = runTest {
        val dataModels = listOf(movieEntity2, movieEntity1)
        val domainModels = listOf(movie2, movie1)
        whenever(mapper.mapFromLocal).thenReturn { movie ->
            when(movie){
                movieEntity1 -> movie1
                movieEntity2 -> movie2
                else -> unknownMovie
            }
        }
        whenever(remoteStore.getAll()).thenThrow(RuntimeException())
        whenever(localStore.getAll()).thenReturn(dataModels)

        val result = repository.getAll()

        assertTrue(result is Result.Success)
        assertEquals(domainModels, (result as? Result.Success)?.data)
        verify(remoteStore).getAll()
        verify(localStore).getAll()
        verify(localStore, never()).insertAll(any())
    }

    @Test
    fun `getAll returns error when both remote and local throw exception`() = runTest {
        val localException = RuntimeException()
        whenever(remoteStore.getAll()).thenThrow(RuntimeException())
        whenever(localStore.getAll()).thenThrow(localException)

        val result = repository.getAll()

        assertTrue(result is Result.Error)
        assertEquals(localException, (result as? Result.Error)?.error)
        verify(remoteStore).getAll()
        verify(localStore).getAll()
        verify(localStore, never()).insertAll(any())
    }

    @Test
    fun `get uses only local store `() = runTest {
        repository.get(1)

        verify(localStore).get(1)
        verify(remoteStore, never()).getAll()
    }

    @Test
    fun `get retrieves and maps a movie from local store`() = runTest {
        whenever(localStore.get(1)).thenReturn(movieEntity2)
        whenever(mapper.mapFromLocal).thenReturn { movie ->
            when(movie){
                movieEntity1 -> movie1
                movieEntity2 -> movie2
                else -> unknownMovie
            }
        }

        val result = repository.get(1)

        assertTrue(result is Result.Success)
        assertEquals(movie2, (result as? Result.Success)?.data)
        verify(localStore).get(1)
    }

    @Test
    fun `get returns error when movie is not found`() = runTest {
        whenever(localStore.get(1)).thenReturn(null)

        val result = repository.get(1)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `get returns error when store throws Exception`() = runTest {
        whenever(localStore.get(1)).thenThrow(RuntimeException())

        val result = repository.get(1)

        assertTrue(result is Result.Error)
    }

    companion object {
        private val movieEntity1 = MovieEntity(1, "title1", "description1", "111")
        private val movieEntity2 = MovieEntity(2, "2", "", null)
        private val unknownEntity = MovieEntity(-1, "", "", null)
        private val movie1 = Movie(1, "title1", "description1", "111")
        private val movie2 = Movie(2, "2", "", null)
        private val unknownMovie = Movie(-1, "", "", null)
    }
}