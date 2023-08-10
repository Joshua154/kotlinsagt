package listener

import FuxelSagt
import guis.TeleportGUI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener(val fuxelSagt: FuxelSagt): Listener {


    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // fuxelsagt.getTablistManager().sendTablistToPlayer(event.player)
        // playermanager.addPlayer(event.player)

        val gui = TeleportGUI(fuxelSagt)
        gui.open(event.player)
    }

}