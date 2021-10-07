package misilelab

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

var nochatting = mutableListOf<Player>()

class EventListener: Listener {
    private var teamintlife: Int? = null
    private var notteamintlife: Int? = null
    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        if ((teamintlife != null) && (notteamintlife != null)) {
            val player: Player = e.entity
            if (player.name in player.scoreboard.getTeam("intteam")!!.entries) {
                if (teamintlife!! <= 0) {
                    player.gameMode = GameMode.SPECTATOR
                    nochatting.add(player)
                }
                else {
                    teamintlife = teamintlife!! - 1
                }
            }
            else if (player.name in player.scoreboard.getTeam("notintteam")!!.entries) {
                if (notteamintlife!! <= 0) {
                    player.gameMode = GameMode.SPECTATOR
                    nochatting.add(player)
                }
                else {
                    notteamintlife = notteamintlife!! - 1
                }
            }
        }
    }

    @EventHandler
    fun onchatting(e: AsyncChatEvent) {
        val player: Player = e.player
        if (player in nochatting) {
            e.isCancelled = true
            player.sendMessage("당신은 채팅을 칠 수 없습니다.")
        }
    }
    fun setlife(teamintlifea: Int, notteamintlifea: Int) {
        teamintlife = teamintlifea
        notteamintlife = notteamintlifea
    }

    data class Life(val teamintlife: Int, val notteamintlife: Int)

    fun getlife(): Life? {
        return if (teamintlife == null || notteamintlife == null) null else Life(teamintlife!!, notteamintlife!!)
    }
}