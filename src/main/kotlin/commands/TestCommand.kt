package commands

import FuxelSagt
import gamemodes.maze.MazeGenerator
import guis.GameModeSelection
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


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

        fuxelSagt.getGameControlManager().giveItems(sender)


//        MazeGenerator(args[0].toInt()).buildMaze(
//            sizeOfBlocks = args[1].toInt(),
//            height = args[2].toInt(),
//            location = sender.location.clone().subtract(0.0, args[2].toDouble(), 0.0)
//        )

        return true
    }
}