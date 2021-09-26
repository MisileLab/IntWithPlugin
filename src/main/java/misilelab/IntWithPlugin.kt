package misilelab

import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

@Suppress("unused")
class IntWithPlugin: JavaPlugin() {

    private var teamintx: Double? = null
    private var teaminty: Double? = null
    private var teamintz: Double? = null
    private var teamintlocation: Location? = null
    private var notteamintx: Double? = null
    private var notteaminty: Double? = null
    private var notteamintz: Double? = null
    private var notteamintlocation: Location? = null

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
                            if (team != null) {
                                team = scoreboard.registerNewTeam("intteam")
                                team.setAllowFriendlyFire(false)
                            }
                            if (!hasname(player.name, team!!)) {
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
                            if (team != null) {
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
                then("setlocation") {
                    then("x" to double()) {
                        executes { context ->
                            val x: Double by context
                            notteamintx = x
                        }
                        then("y" to double()) {
                            executes { context ->
                                val y: Double by context
                                teaminty = y
                            }
                            then("z" to double()) {
                                executes { context ->
                                    val player: Player = sender as Player
                                    val z: Double by context
                                    teamintz = z
                                    teamintlocation = Location(player.world, teamintx!!, teaminty!!, teamintz!!)
                                }
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
                            if (team != null) {
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
                            if (team != null) {
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
                then("setlocation") {
                    then("x" to double()) {
                        executes { context ->
                            val x: Double by context
                            notteamintx = x
                        }
                        then("y" to double()) {
                            executes { context ->
                                val y: Double by context
                                notteaminty = y
                            }
                            then("z" to double()) {
                                executes { context ->
                                    val player: Player = sender as Player
                                    val z: Double by context
                                    notteamintz = z
                                    notteamintlocation = Location(player.world, notteamintx!!, notteaminty!!, notteamintz!!)
                                }
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
                    for (i in player.world.players) {
                        if (!(hasname(player.name, teamint!!) || hasname(player.name, teamviewer!!))) {
                            noneplayers.add(i)
                        }
                    }
                    if (noneplayers.isNotEmpty()) {
                        for (i in noneplayers) {
                            nonestring = "$nonestring$i, "
                        }
                    }
                    if (nonestring == "") {
                        player.sendMessage("$nonestring 이라는 사람(들)이 아직 팀에 참여하지 않았습니다.")
                    }
                    else {
                        for (i in teamint!!.entries) {
                            player.bedSpawnLocation = teamintlocation
                        }
                        for (i in teamviewer!!.entries) {
                            player.bedSpawnLocation = notteamintlocation
                        }
                        EventListener().resetlife()
                        player.sendMessage("세팅이 완료되었습니다!")
                    }
                }
            }
        }
        server.pluginManager.registerEvents(EventListener(), this)
    }
}

fun hasname(name: String, team: Team): Boolean {
    var result = false
    for (i in team.entries) {
        if (i == name) {
            result = true
            break
        }
    }
    return result
}