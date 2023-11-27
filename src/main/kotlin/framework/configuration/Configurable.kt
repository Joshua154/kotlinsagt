package framework.configuration

import org.bukkit.Material

@Target(allowedTargets = [AnnotationTarget.FIELD])
@Retention(value = AnnotationRetention.RUNTIME)
annotation class Configurable(val displayItem: Material, val name: String)
