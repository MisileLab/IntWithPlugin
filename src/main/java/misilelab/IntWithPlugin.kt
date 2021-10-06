@file:Suppress("PLUGIN_IS_NOT_ENABLED")

package misilelab

import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.writeLines
import org.bukkit.Bukkit


@Suppress("unused")
class IntWithPlugin: JavaPlugin() {

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
                                team!!.setAllowFriendlyFire(false)
                            }
                            if (hasname(player.name, team!!)) {
                                team!!.removeEntry(player.name)
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
                            var team = scoreboard.getTeam("notintteam")
                            if (team == null) {
                                team = scoreboard.registerNewTeam("notintteam")
                                team!!.setAllowFriendlyFire(false)
                            }
                            if (!hasname(player.name, team!!)) {
                                team!!.addEntry(player.name)
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
                            var team = scoreboard.getTeam("notintteam")
                            if (team == null) {
                                team = scoreboard.registerNewTeam("notintteam")
                                team!!.setAllowFriendlyFire(false)
                            }
                            if (hasname(player.name, team!!)) {
                                team!!.removeEntry(player.name)
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
                    val teamviewer = scoreboard.getTeam("notintteam")
                    var nonestring = ""
                    try {
                        FileInputStream("data.properties")
                    } catch (ex: FileNotFoundException) {
                        val lines = mutableListOf("intteamlife=30", "notintteamlife=30")
                        Path("data.properties").createFile().writeLines(lines)
                    }
                    val file = FileInputStream("data.properties")
                    val prop = Properties()
                    prop.load(file)
                    val teamintlife = (prop.getProperty("intteamlife")).toInt()
                    val notteamintlife = (prop.getProperty("notintteamlife")).toInt()
                    for (i in player.world.players) {
                        if (teamint != null && teamviewer != null) {
                            if (!hasname(i.name, teamint) || !hasname(i.name, teamviewer)) {
                                noneplayers.add(i)
                            }
                        }
                    }
                    if (noneplayers.isNotEmpty()) {
                        for (i in noneplayers) {
                            val playername = i.name
                            nonestring = "$nonestring$playername, "
                        }
                    }
                    if (nonestring != "") {
                        player.sendMessage("$nonestring 이라는 사람(들)이 아직 팀에 참여하지 않았습니다.")
                    }
                    else {
                        // -250 64 256
                        if (teamint != null && teamviewer != null) {
                            val teamintx = ((-1250..1250).random()).toDouble()
                            val teamintz = ((-1256..1256).random()).toDouble()
                            val viewerx = teamintx + ((1000..2000).random()).toDouble()
                            val viewerz = teamintz + ((1000..2000).random()).toDouble()
                            for (i in teamint.entries) {
                                val playerlol = Bukkit.getPlayer(i)
                                if (playerlol != null) {
                                    playerlol.bedSpawnLocation = Location(player.world, teamintx, playerlol.location.y, teamintz)
                                }
                            }
                            for (i in teamviewer.entries) {
                                val playerlol = Bukkit.getPlayer(i)
                                if (playerlol != null) {
                                    playerlol.bedSpawnLocation = Location(player.world, viewerx, playerlol.location.y, viewerz)
                                }
                            }
                            EventListener().setlife((teamintlife), (notteamintlife))
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
        server.pluginManager.registerEvents(EventListener(), this)
    }

    override fun onDisable() {
        val lifeobject: EventListener.Life? = EventListener().getlife()
        if (lifeobject != null) {
            try {
                FileInputStream("data.properties")
            } catch (ex: FileNotFoundException) {
                val notintteamlife = lifeobject.notteamintlife
                val intteamlife = lifeobject.teamintlife
                val lines = mutableListOf("intteamlife=$intteamlife", "notintteamlife=$notintteamlife")
                Path("data.properties").createFile().writeLines(lines)
            }
        }
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
