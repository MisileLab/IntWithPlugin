package misilelab

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

var nochatting = mutableListOf<Player>()

class EventListener: Listener {
    private var intteamlife: Objective? = null
    private var viewerlife: Objective? = null
    private var scoreboard: Scoreboard? = null
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        if (scoreboard == null) {
            scoreboard = e.player.scoreboard
        }
        if (intteamlife == null) {
            intteamlife = scoreboard?.getObjective("intteamlife")
            if (intteamlife == null) {
                intteamlife = scoreboard?.registerNewObjective("intteamlife", "dummy", null)
            }
        }
        if (viewerlife == null) {
            viewerlife = scoreboard?.getObjective("viewerlife")
            if (viewerlife == null) {
                viewerlife = scoreboard?.registerNewObjective("viewerlife", "dummy", null)
            }
        }
    }

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        if ((intteamlife != null) && (viewerlife != null)) {
            val player: Player = e.entity
            if (player.name in player.scoreboard.getTeam("intteam")!!.entries) {
                if (intteamlife?.getScore(player.name)?.score!! <= 0) {
                    player.gameMode = GameMode.SPECTATOR
                    nochatting.add(player)
                }
                else {
                    intteamlife!!.getScore(player.name).score = intteamlife!!.getScore(player.name).score - 1
                }
            }
            else if (player.name in player.scoreboard.getTeam("notintteam")!!.entries) {
                if (viewerlife?.getScore(player.name)?.score!! <= 0) {
                    player.gameMode = GameMode.SPECTATOR
                    nochatting.add(player)
                }
                else {
                    viewerlife!!.getScore(player.name).score = viewerlife?.getScore(player.name)?.score!! - 1
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

    fun setlife(intteamlifea: Int, viewerlifea: Int, intteam: Team, viewerteam: Team) {
        intteamlife?.getScore(intteam.name)!!.score = intteamlifea
        viewerlife?.getScore(viewerteam.name)!!.score = viewerlifea
    }
}
