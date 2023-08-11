package framework.manager

import framework.Framework

public class MessageContainer(private val messageType: MessageType, private val message: String) {
    public fun getMessageType(): MessageType {
        return this.messageType;
    }

    public fun getMessage(): String {
        return this.message;
    }
}

public enum class MessageType {
    JOIN_MESSAGE,
    QUIT_MESSAGE,
    PREFIX
}

class MessageManager(private val framework: Framework) {

    private val messages: Array<MessageContainer> = this.framework.getFuxelSagt().getConfiguration().messages




}