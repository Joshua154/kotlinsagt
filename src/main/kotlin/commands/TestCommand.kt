package commands

import FuxelSagt
import guis.GameModeSelection
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File


class TestCommand(private val fuxelSagt: FuxelSagt) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return false
        }

        val gui = GameModeSelection(fuxelSagt)
        gui.open(sender)

        fuxelSagt.server.worlds.forEach{
            println(it.name)
        }
        return true
    }
}