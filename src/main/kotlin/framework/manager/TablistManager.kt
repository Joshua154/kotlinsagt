package framework.manager

import framework.Framework
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.scoreboard.Team

class TablistManager(private val framework: Framework) {

    fun setupTeams() {
        if (framework.getFuxelSagt().server.scoreboardManager.mainScoreboard.getTeam("player") == null) {
            framework.getFuxelSagt().server.scoreboardManager.mainScoreboard.registerNewTeam("player")
        }
        if (framework.getFuxelSagt().server.scoreboardManager.mainScoreboard.getTeam("spectator") == null) {
            framework.getFuxelSagt().server.scoreboardManager.mainScoreboard.registerNewTeam("player")
        }
        if (framework.getFuxelSagt().server.scoreboardManager.mainScoreboard.getTeam("fuxel") == null) {
            framework.getFuxelSagt().server.scoreboardManager.mainScoreboard.registerNewTeam("fuxel")
        }

    }

    fun update() {
        framework.getFuxelSagt().server.onlinePlayers.forEach() {
            val tabListTeam: Team?  // nullable, aber eig nicht
            val component: Component = framework.getPlayerManager().getFormat(it)
            val teamName: String = when {
                framework.getPlayerManager().isFuxel(it) -> "fuxel"
                framework.getPlayerManager().isSpectator(it) -> "spectator"
                else -> "player"
            }
            if(it.scoreboard.getTeam(teamName) == null) {
                tabListTeam = it.scoreboard.registerNewTeam(teamName);
            } else {
                tabListTeam = it.scoreboard.getTeam(teamName);
            }
            tabListTeam?.color(framework.getPlayerManager().getType(it).namedColor)
            tabListTeam?.addPlayer(it)
            // it.player.sendPlayerListHeader()
            it.sendPlayerListFooter(Component.text("\n   §7In Partnerschaft mit Zyonic.de   \n"));

        }
    }
}