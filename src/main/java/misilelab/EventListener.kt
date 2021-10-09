package misilelab

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Score
import org.bukkit.scoreboard.Scoreboard

var nochatting = mutableListOf<Player>()

class EventListener: Listener {
    private var intteamlife: Score? = null
    private var viewerlife: Score? = null
    private var scoreboard: Scoreboard? = null
    private var teamlife: Objective? = null
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        scoreboardsetup(e.player)
    }

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        scoreboardsetup(e.entity)
        if ((intteamlife != null) && (viewerlife != null)) {
            val player: Player = e.entity
            if (player.name in player.scoreboard.getTeam("intteam")!!.entries) {
                if (intteamlife?.score!! <= 0) {
                    player.gameMode = GameMode.SPECTATOR
                    nochatting.add(player)
                }
                else {
                    intteamlife?.score = intteamlife?.score!! - 1
                }
            }
            else if (player.name in player.scoreboard.getTeam("viewerteam")?.entries!!) {
                if (viewerlife?.score!! <= 0) {
                    player.gameMode = GameMode.SPECTATOR
                    nochatting.add(player)
                }
                else {
                    viewerlife?.score = viewerlife?.score!! - 1
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

    fun setlife(intteamlifea: Int, viewerlifea: Int) {
        intteamlife?.score = intteamlifea
        viewerlife?.score = viewerlifea
    }

    fun scoreboardsetup(player: Player) {
        scoreboard = player.scoreboard
        if (teamlife == null) {
            teamlife = scoreboard?.getObjective("teamlife")
            if (teamlife == null) {
                teamlife = scoreboard?.registerNewObjective("teamlife", "dummy", Component.text("teamlife"))
            }
        }
        if (teamlife != null) {
            viewerlife = teamlife?.getScore("viewerteam")
            intteamlife = teamlife?.getScore("intteam")
        }
    }
}
