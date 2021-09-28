package misilelab

import org.bukkit.GameMode
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class EventListener: Listener {
    private var teamintlife = 5
    private var notteamintlife = 5
    @EventHandler
    fun onDeath(e: EntityDeathEvent) {
        if (((e.entity.killer == null) || (e.entity.killer!!.type == EntityType.PLAYER)) && (e.entity.type == EntityType.PLAYER)) {
            val player: Player = e.entity as Player
            if (player.name in player.scoreboard.getTeam("intteam")!!.entries) {
                if (teamintlife <= 0) {
                    player.gameMode = GameMode.SPECTATOR
                }
                else {
                    teamintlife -= 1
                }
                val playername = player.name
                teammessage(true, player, "$playername 님이 사망하였습니다. 현재 $teamintlife 번 리스폰 가능합니다.")
            }
            else if (player.name in player.scoreboard.getTeam("notintteam")!!.entries) {
                if (notteamintlife <= 0) {
                    player.gameMode = GameMode.SPECTATOR
                }
                else {
                    notteamintlife -= 1
                }
                val playername = player.name
                teammessage(false, player, "$playername 님이 사망하였습니다. 현재 $notteamintlife 번 리스폰 가능합니다.")
            }
        }
    }
    fun resetlife() {
        teamintlife = 5
        notteamintlife = 5
    }

    fun teammessage(intteam: Boolean, player: Player, message: String) {
        for (i in player.world.players) {
            if (i.name in player.scoreboard.getTeam("notteamint")!!.entries && !intteam) {
                player.sendMessage(message)
            }
            else if (i.name in player.scoreboard.getTeam("teamint")!!.entries && intteam) {
                player.sendMessage(message)
            }
        }
    }
}