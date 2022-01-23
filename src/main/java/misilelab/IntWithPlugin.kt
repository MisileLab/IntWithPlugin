package misilelab

import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.apache.commons.lang.StringUtils
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import org.bukkit.Bukkit
import org.bukkit.Material

private val Location.groundlocation: Location
    get() {
        val location = this
        while (true) {
            if (location.block.type == Material.AIR) {
                location.y -= 1
                if (location.block.type != Material.AIR) {
                    location.y += 1
                    break
                }
            }
            else {
                location.y -= 1
            }
        }
        return location
    }

@Suppress("unused")
class IntWithPlugin: JavaPlugin() {
    private val eventlistener = EventListener()

    override fun onEnable() {
        kommand {
            register("intteam") {
                then("add") {
                    then("player" to player()) {
                        executes { context ->
                            val player: Player by context
                            val playersender: Player = sender as Player
                            val scoreboard: Scoreboard = player.scoreboard
                            var team = scoreboard.getTeam("intteam")
                            if (team == null) {
                                logger.info("team is null")
                                team = scoreboard.registerNewTeam("intteam")
                                team.setAllowFriendlyFire(false)
                            }
                            if (!hasname(player.name, team)) {
                                team.addEntry(player.name)
                                playersender.sendMessage("플레이어가 팀에 넣어졌습니다.")
                            }
                            else {
                                playersender.sendMessage("플레이어가 팀에 이미 들어갔습니다.")
                            }
                        }
                    }
                }
                then("remove") {
                    then("player" to player()) {
                        executes { context ->
                            val player: Player by context
                            val playersender: Player = sender as Player
                            val scoreboard: Scoreboard = player.scoreboard
                            var team = scoreboard.getTeam("intteam")
                            if (team == null) {
                                team = scoreboard.registerNewTeam("intteam")
                                team?.setAllowFriendlyFire(false)
                            }
                            if (hasname(player.name, team)) {
                                team?.removeEntry(player.name)
                                playersender.sendMessage("플레이어가 팀에서 제거되었습니다.")
                            }
                            else {
                                playersender.sendMessage("플레이어가 팀에 이미 없습니다.")
                            }
                        }
                    }
                }
            }
            register("viewer") {
                then("add") {
                    then("player" to player()) {
                        executes { context ->
                            val player: Player by context
                            val playersender: Player = sender as Player
                            val scoreboard: Scoreboard = player.scoreboard
                            var team = scoreboard.getTeam("viewerteam")
                            if (team == null) {
                                team = scoreboard.registerNewTeam("viewerteam")
                                team?.setAllowFriendlyFire(false)
                            }
                            if (!hasname(player.name, team)) {
                                team?.addEntry(player.name)
                                playersender.sendMessage("플레이어가 팀에 넣어졌습니다.")
                            }
                            else {
                                playersender.sendMessage("플레이어가 팀에 이미 들어갔습니다.")
                            }
                        }
                    }
                }
                then("remove") {
                    then("player" to player()) {
                        executes { context ->
                            val player: Player by context
                            val playersender: Player = sender as Player
                            val scoreboard: Scoreboard = player.scoreboard
                            var team = scoreboard.getTeam("viewerteam")
                            if (team == null) {
                                team = scoreboard.registerNewTeam("viewerteam")
                                team?.setAllowFriendlyFire(false)
                            }
                            if (hasname(player.name, team)) {
                                team?.removeEntry(player.name)
                                playersender.sendMessage("플레이어가 팀에서 제거되었습니다.")
                            }
                            else {
                                playersender.sendMessage("플레이어가 팀에 이미 없습니다.")
                            }
                        }
                    }
                }
            }
            register("start") {
                executes {
                    val noneplayers = mutableListOf<Player>()
                    val player: Player = sender as Player
                    val scoreboard: Scoreboard = player.scoreboard
                    val teamint = scoreboard.getTeam("intteam")
                    val teamviewer = scoreboard.getTeam("viewerteam")
                    var nonestring = ""
                    eventlistener.scoreboardsetup(player)
                    for (i in player.world.players) {
                        if ((teamint != null && teamviewer != null) && (!hasname(i.name, teamint) && !hasname(i.name, teamviewer))) {
                            noneplayers.add(i)
                        }
                    }
                    if (noneplayers.isNotEmpty()) {
                        for (i in noneplayers) {
                            val playername = i.name
                            nonestring = "$nonestring$playername, "
                        }
                        StringUtils.removeEnd(nonestring, ", ")
                    }
                    if (nonestring != "") {
                        player.sendMessage("$nonestring 이라는 사람(들)이 아직 팀에 참여하지 않았습니다.")
                    }
                    else {
                        if (teamint != null && teamviewer != null) {
                            val playerlocation = player.location
                            val teamintx = playerlocation.x + ((1000..10000).random()).toDouble()
                            val teamintz = playerlocation.z + ((1000..10000).random()).toDouble()
                            val viewerx = teamintx + ((1000..7500).random()).toDouble()
                            val viewerz = teamintz + ((1000..7500).random()).toDouble()
                            for (i in teamint.entries) {
                                val playerlol = Bukkit.getPlayer(i)
                                if (playerlol != null) {
                                    val location = Location(playerlol.world, teamintx, playerlol.location.y, teamintz).groundlocation
                                    playerlol.bedSpawnLocation = location
                                    playerlol.teleport(location)
                                }
                            }
                            for (i in teamviewer.entries) {
                                val playerlol = Bukkit.getPlayer(i)
                                if (playerlol != null) {
                                    val location = Location(playerlol.world, viewerx, playerlol.location.y, viewerz).groundlocation
                                    playerlol.bedSpawnLocation = location
                                    playerlol.teleport(location)
                                }
                            }
                            eventlistener.setlife(30, 30)
                            player.sendMessage("세팅이 완료되었습니다!")
                        }
                        else {
                            player.sendMessage("아직 팀이 만들어지지 않았습니다. /intteam add <플레이어>와 /viewer add <플레이어>를 한 번 쳐보세요.")
                        }
                    }
                }
            }
        }
        logger.info("plugin enabled")
        server.pluginManager.registerEvents(eventlistener, this)
    }
}

fun hasname(name: String, team: Team?): Boolean {
    var result = false
    if (team != null) {
        for (i in team.entries) {
            if (i == name) {
                result = true
                break
            }
        }
    }
    return result
}
