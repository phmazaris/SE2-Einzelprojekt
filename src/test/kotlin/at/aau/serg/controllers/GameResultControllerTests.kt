package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin

// 2.2.3 Tests: Neue Testklasse für 100% Coverage von GameResultController
class GameResultControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: GameResultController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = GameResultController(mockedService)
    }

    @Test
    fun test_getGameResult_existingId_returnsGameResult() {
        val gameResult = GameResult(1, "player1", 20, 10.0)
        whenever(mockedService.getGameResult(1)).thenReturn(gameResult)

        val res = controller.getGameResult(1)

        verify(mockedService).getGameResult(1)
        assertEquals(gameResult, res)
    }

    @Test
    fun test_getGameResult_nonExistingId_returnsNull() {
        whenever(mockedService.getGameResult(99)).thenReturn(null)

        val res = controller.getGameResult(99)

        verify(mockedService).getGameResult(99)
        assertNull(res)
    }

    @Test
    fun test_getAllGameResults_returnsListFromService() {
        val results = listOf(
            GameResult(1, "player1", 20, 10.0),
            GameResult(2, "player2", 15, 12.0)
        )
        whenever(mockedService.getGameResults()).thenReturn(results)

        val res = controller.getAllGameResults()

        verify(mockedService).getGameResults()
        assertEquals(2, res.size)
        assertEquals(results, res)
    }

    @Test
    fun test_getAllGameResults_emptyList() {
        whenever(mockedService.getGameResults()).thenReturn(emptyList())

        val res = controller.getAllGameResults()

        verify(mockedService).getGameResults()
        assertEquals(0, res.size)
    }

    @Test
    fun test_addGameResult_callsService() {
        val gameResult = GameResult(0, "player1", 25, 8.5)

        controller.addGameResult(gameResult)

        verify(mockedService).addGameResult(gameResult)
    }

    @Test
    fun test_deleteGameResult_callsService() {
        controller.deleteGameResult(1)

        verify(mockedService).deleteGameResult(1)
    }

}
