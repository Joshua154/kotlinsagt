package listener

import FuxelSagt
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener(val fuxelSagt: FuxelSagt) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
//        if (event.player.hasPermission("fuxelsagt.spec")) {
//            fuxelSagt.getFramework().getPlayerManager().addSpectator(event.player)
//        } else {
            fuxelSagt.getFramework().getPlayerManager().addPlayer(event.player)
//        }
        fuxelSagt.getFramework().getTablistManager().sendTablistToPlayer(event.player)

        Bukkit.getLogger().info( "Players:" + fuxelSagt.getFramework().getPlayerManager().getPlayerUUIDList().toString() );
//        val gui = TeleportGUI(fuxelSagt)
//        gui.open(event.player)
        event.player.teleport(Bukkit.getWorld("world")!!.spawnLocation); // TODO: Replace with proper lobby
    }

}