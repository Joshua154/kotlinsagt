package framework.configuration

import org.bukkit.Material

@Target(allowedTargets = [AnnotationTarget.FIELD, AnnotationTarget.PROPERTY])
@Retention(value = AnnotationRetention.RUNTIME)
annotation class Configurable(val displayItem: Material, val name: String, val shortenedName: String = "", val type: ConfigurableValueInterface.Type = ConfigurableValueInterface.Type.DEFAULT)
