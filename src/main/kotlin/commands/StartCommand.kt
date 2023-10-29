package commands

import FuxelSagt
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class StartCommand(private val fuxelSagt: FuxelSagt) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        this.fuxelSagt.getGameModeManager().getCurrentGameMode().ifPresentOrElse({ gamemode ->
            if (gamemode.getPlayers().size < gamemode.minPlayers) {
                sender.sendMessage(ChatColor.RED.toString() + "Es sind nicht genügend Spieler da (${gamemode.getPlayers().size}), um das Spiel zu starten! Es werden mindestens ${gamemode.minPlayers} Spieler benötigt!");
            } else if (gamemode.getPlayers().size > gamemode.maxPlayers) {
                sender.sendMessage(ChatColor.RED.toString() + "Es sind zu viele Spieler da (${gamemode.getPlayers().size}), um das Spiel zu starten! Es können maximal ${gamemode.maxPlayers} Spieler mitspielen!");
            } else {
                Bukkit.getOnlinePlayers().forEach { player ->
                    this.fuxelSagt.sendPlayerMessage(
                        player,
                        Component.text("${gamemode.displayName} startet jetzt!")
                    );
                }
                gamemode.start();
            }
        }, {
            sender.sendMessage(ChatColor.RED.toString() + "Dieser Befehl kann nicht ausgeführt werden, wenn kein Spielmodus ausgewählt ist!");
        })

        return true;
    }
}