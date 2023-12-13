package framework

import FuxelSagt
import framework.manager.MessageManager
import framework.manager.PlayerManager
import framework.manager.TablistManager

class Framework(private val fuxelSagt: FuxelSagt) {

    private val playerManager = PlayerManager(this)
    private val messageManager = MessageManager(this)
    private val tablistManager = TablistManager(this);

    fun getPlayerManager(): PlayerManager {
        return this.playerManager
    }

    fun getFuxelSagt(): FuxelSagt {
        return this.fuxelSagt
    }

    fun getTablistManager(): TablistManager {
        return this.tablistManager
    }
}