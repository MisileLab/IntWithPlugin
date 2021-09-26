package misilelab

import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class EventListener: Listener {
    private var teamintlife = 5
    private var notteamintlife = 5
    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        val player = e.entity
        if (player.killer != null) {
            if (player.name in player.scoreboard.getTeam("intteam")!!.entries) {
                if (teamintlife <= 0) {
                    player.gameMode = GameMode.SPECTATOR
                }
                else {
                    teamintlife -= 1
                }
                player.sendMessage("$player.name님이 사망하였습니다. 현재 $teamintlife 번 리스폰 가능합니다.")
            }
            else if (player.name in player.scoreboard.getTeam("notintteam")!!.entries) {
                if (notteamintlife <= 0) {
                    player.gameMode = GameMode.SPECTATOR
                }
                else {
                    notteamintlife -= 1
                }
                player.sendMessage("$player.name님이 사망하였습니다. 현재 $notteamintlife 번 리스폰 가능합니다.")
            }
        }
    }
    fun resetlife() {
        teamintlife = 5
        notteamintlife = 5
    }
}