package listener

import FuxelSagt
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener(val fuxelSagt: FuxelSagt) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // fuxelsagt.getTablistManager().sendTablistToPlayer(event.player)
        // playermanager.addPlayer(event.player)

//        val gui = TeleportGUI(fuxelSagt)
//        gui.open(event.player)
        event.player.teleport(Bukkit.getWorld("world")!!.spawnLocation); // TODO: Replace with proper lobby
    }

}