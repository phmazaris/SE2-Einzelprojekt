package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int?): List<GameResult> {
        // 2.2.1 Sortierung: Tiebreaker bei gleichem Score geändert von { it.id } auf { it.timeInSeconds }
        val sorted = gameResultService.getGameResults().sortedWith(compareBy({ -it.score }, { it.timeInSeconds }))

        // 2.2.2 Zusätzliche Abfrage: Optionaler rank-Parameter – gibt Spieler auf Platz rank ± 3 zurück
        if (rank == null) return sorted

        if (rank < 1 || rank > sorted.size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid rank")
        }

        val index = rank - 1
        val from = maxOf(0, index - 3)
        val to = minOf(sorted.size, index + 4)
        return sorted.subList(from, to)
    }

}