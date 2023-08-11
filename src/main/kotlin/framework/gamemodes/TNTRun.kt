package framework.gamemodes

import framework.Framework
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent

class TNTRun(private val framework: Framework, private val name: String): GameMode(framework, "test", "TNT Run") {
    override fun start() {
        load()
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
    fun onPlayerMove(event: PlayerMoveEvent){

    }
}