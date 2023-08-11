package framework.gamemodes

import framework.Framework
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import javax.xml.stream.Location

class TNTRun(private val framework: Framework, private val name: String): GameMode(framework, "test", "TNT Run") {
    override fun start() {
        load()
        registerEventListener()
        for (player in framework.getFuxelSagt().server.onlinePlayers) {
            // tpToGameSpawn(player)
            player.sendMessage("TNT Run started")
        }
    }

    override fun stop() {

    }

    override fun reset() {
        TODO("Not yet implemented")
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if(!isPlayer(event.player)) return

        val blockLocation = event.to.block.location
        if(blockLocation == event.from.block.location) return

        val world = event.player.location.world
        val fuxelSagt = framework.getFuxelSagt()
        fuxelSagt.server.scheduler.runTaskLater(fuxelSagt, Runnable {
            blockLocation.subtract(0.0, 1.0, 0.0)
            world.getBlockAt(blockLocation).type = Material.AIR

            blockLocation.subtract(0.0, 1.0, 0.0)
            world.getBlockAt(blockLocation).type = Material.AIR
        }, 20L)
    }
}