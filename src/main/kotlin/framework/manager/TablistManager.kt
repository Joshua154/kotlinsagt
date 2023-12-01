package framework.manager

import framework.Framework
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team

class TablistManager(private val framework: Framework) {

    init {
        setupTeams()
    }

    private fun setupTeams() {
        if (framework.getFuxelSagt().server.scoreboardManager.mainScoreboard.getTeam("player") == null) {
            framework.getFuxelSagt().server.scoreboardManager.mainScoreboard.registerNewTeam("player")
        }
        if (framework.getFuxelSagt().server.scoreboardManager.mainScoreboard.getTeam("spectator") == null) {
            framework.getFuxelSagt().server.scoreboardManager.mainScoreboard.registerNewTeam("spectator")
        }
        if (framework.getFuxelSagt().server.scoreboardManager.mainScoreboard.getTeam("fuxel") == null) {
            framework.getFuxelSagt().server.scoreboardManager.mainScoreboard.registerNewTeam("fuxel")
        }

    }

    fun sendTablistToPlayer(player: Player) {
        val tabListTeam: Team?  // nullable, aber eig nicht
        val component: Component = framework.getPlayerManager().getFormat(player)
        val teamName: String = when {
            framework.getPlayerManager().isFuxel(player) -> "fuxel"
            framework.getPlayerManager().isSpectator(player) -> "spectator"
            else -> "player"
        }
        tabListTeam = if (player.scoreboard.getTeam(teamName) == null) {
            player.scoreboard.registerNewTeam(teamName);
        } else {
            player.scoreboard.getTeam(teamName);
        }
        if (tabListTeam != null) {
            tabListTeam.color(framework.getPlayerManager().getType(player).namedColor)
            tabListTeam.addPlayer(player)
        }
        player.sendPlayerListHeader(Component.text("   §6FuxelSagt"))
        player.sendPlayerListFooter(Component.text("\n   §7In Partnerschaft mit Zyonic.de   \n"));
    }

    fun update() {
        framework.getFuxelSagt().server.onlinePlayers.forEach() {
            sendTablistToPlayer(it)
        }
    }
}