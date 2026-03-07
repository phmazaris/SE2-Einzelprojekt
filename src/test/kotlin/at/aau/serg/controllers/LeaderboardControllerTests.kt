package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin
import org.springframework.web.server.ResponseStatusException

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    // 2.2.1 Sortierung: Assertions angepasst – bei gleichem Score wird nun nach kürzerer timeInSeconds sortiert statt nach id
    @Test
    fun test_getLeaderboard_sameScore_CorrectIdSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(second, res[0])
        assertEquals(third, res[1])
        assertEquals(first, res[2])
    }

    // 2.2.2 Zusätzliche Abfrage: Tests für optionalen rank-Parameter
    @Test
    fun test_getLeaderboard_withRank_returnsCorrectWindow() {
        val players = (1..10).map { GameResult(it.toLong(), "player$it", 100 - it, 10.0 + it) }
        whenever(mockedService.getGameResults()).thenReturn(players)

        val res = controller.getLeaderboard(5)

        assertEquals(7, res.size)
        assertEquals(players[1], res[0]) // rank 2
        assertEquals(players[4], res[3]) // rank 5 (requested)
        assertEquals(players[7], res[6]) // rank 8
    }

    @Test
    fun test_getLeaderboard_withRank1_returnsTopPlayers() {
        val players = (1..10).map { GameResult(it.toLong(), "player$it", 100 - it, 10.0 + it) }
        whenever(mockedService.getGameResults()).thenReturn(players)

        val res = controller.getLeaderboard(1)

        assertEquals(4, res.size)
        assertEquals(players[0], res[0]) // rank 1 (requested)
        assertEquals(players[3], res[3]) // rank 4
    }

    @Test
    fun test_getLeaderboard_withLastRank_returnsBottomPlayers() {
        val players = (1..10).map { GameResult(it.toLong(), "player$it", 100 - it, 10.0 + it) }
        whenever(mockedService.getGameResults()).thenReturn(players)

        val res = controller.getLeaderboard(10)

        assertEquals(4, res.size)
        assertEquals(players[6], res[0]) // rank 7
        assertEquals(players[9], res[3]) // rank 10 (requested)
    }

    @Test
    fun test_getLeaderboard_withNegativeRank_returns400() {
        whenever(mockedService.getGameResults()).thenReturn(listOf(GameResult(1, "p", 10, 5.0)))

        val ex = assertFailsWith<ResponseStatusException> {
            controller.getLeaderboard(-1)
        }
        assertEquals(400, ex.statusCode.value())
    }

    @Test
    fun test_getLeaderboard_withRankTooLarge_returns400() {
        whenever(mockedService.getGameResults()).thenReturn(listOf(GameResult(1, "p", 10, 5.0)))

        val ex = assertFailsWith<ResponseStatusException> {
            controller.getLeaderboard(5)
        }
        assertEquals(400, ex.statusCode.value())
    }

    @Test
    fun test_getLeaderboard_withRankZero_returns400() {
        whenever(mockedService.getGameResults()).thenReturn(listOf(GameResult(1, "p", 10, 5.0)))

        val ex = assertFailsWith<ResponseStatusException> {
            controller.getLeaderboard(0)
        }
        assertEquals(400, ex.statusCode.value())
    }

}