package util.configuration

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import framework.manager.MessageContainer
import framework.manager.MessageManager
import framework.manager.MessageType
import org.apache.logging.log4j.message.Message
import java.io.File

class Config {

    class Settings {
    }

    val settings: Settings = Settings()

    val messages = arrayOf(MessageContainer(MessageType.PREFIX, "FuxelSagt"))

    //Reading & Converting
    companion object {
        private val gson : Gson = GsonBuilder().setPrettyPrinting().create();

        fun fromFile(file: File): Config {
            return if(file.exists()) {
                this.fromString(file.readText())
            } else {
                toFile(file, Config());
            }
        }

        fun toFile(file: File, config: Config): Config {
            file.mkdirs()
            if(file.exists()) {
                file.delete()
            }
            file.createNewFile()
            file.writeText(this.gson.toJson(config, Config::class.java))
            return Config()
        }

        fun fromString(string: String): Config {
            return gson.fromJson(string, Config::class.java)
        }
    }
}
