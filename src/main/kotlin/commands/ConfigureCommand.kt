package commands

import FuxelSagt
import guis.ConfigurationGUI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ConfigureCommand(private val fuxelSagt: FuxelSagt) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) ConfigurationGUI(fuxelSagt).open(sender)
        else sender.sendMessage(Component.text("Dieser Befehl kann nur von Spielern ausgeführt werden!").color(NamedTextColor.RED))
        return true;
    }
}