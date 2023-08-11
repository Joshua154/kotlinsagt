package framework

import FuxelSagt
import framework.manager.MessageManager
import framework.manager.PlayerManager

class Framework(private val fuxelSagt: FuxelSagt) {

    private val playerManager = PlayerManager(this)
    private val messageManager = MessageManager(this)

    fun getPlayerManager(): PlayerManager {
        return playerManager
    }

    fun getFuxelSagt(): FuxelSagt {
        return this.fuxelSagt
    }
}