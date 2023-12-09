package framework.configuration

import framework.configuration.configurator.ValueConfigurator
import framework.gamemode.GameMode
import org.bukkit.Material
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField


class ConfigurableValueInterface<T : Any> {

    private val name: String;
    private val shortenedName: String;
    private val material: Material;

    private val currentValue: T;

    private val property: KProperty<T>;

    private val type: Type;
    private val klass: KClass<T>;

    private val gameMode: KClass<out GameMode>;

    companion object {

        public val defaults: MutableMap<KClass<out GameMode>, Map<KProperty<*>, Any>> = HashMap();

    }

    constructor(
        name: String,
        shortenedName: String,
        material: Material,
        type: Type,
        currentValue: T,
        gameMode: KClass<out GameMode>,
        property: KProperty<*>
    ) {
        this.name = name;
        this.shortenedName = shortenedName;
        this.material = material;
        this.currentValue = currentValue;
        this.property = property as KProperty<T>;
        this.klass = currentValue::class as KClass<T>;

        this.type = if (type == Type.DEFAULT) Type.fromClass(this.klass)!! else type;
        this.gameMode = gameMode
    }

    fun getName(): String {
        return this.name;
    }

    fun getShortenedName(): String {
        return this.shortenedName;
    }

    fun getMaterial(): Material {
        return this.material;
    }

    /**
     * Returns the _latest_ value that configured in the game mode that was currently active on creation of this value interface.
     *
     * Might not actually be the latest. To get the latest get the newest configurable Values from the current GameMode's class and then get the current value of the desired configurable value.
     */
    fun getCurrentValue(): T {
        return this.currentValue;
    }

    fun getDefaultValue() : T {
        return defaults[this.gameMode]!![this.property] as T;
    }

    fun getGameMode(): KClass<out GameMode> {
        return this.gameMode;
    }

    fun getKProperty(): KProperty<T> {
        return this.property;
    }

    fun getType(): Type {
        return this.type;
    }

    fun getKlass(): KClass<T> {
        return this.klass;
    }

    fun changeValue(newValue: T, gameMode: GameMode) {
        this.property.javaField!!.set(gameMode, newValue);
        gameMode.applyConfiguration();
    }

    fun getConfigurator(): ValueConfigurator<out Any> {
        return ValueConfigurator.make(this)!!;
    }

    enum class Type {
        DEFAULT,
        INTEGER_MAX_64,
        LIMITLESS_INTEGER,
        FLOAT,
        STRING,
        BOOLEAN;

        companion object {
            fun fromClass(clazz: KClass<out Any>): Type? {
                return when (clazz) {
                    Short::class -> INTEGER_MAX_64
                    Integer::class -> INTEGER_MAX_64
                    Long::class -> INTEGER_MAX_64
                    Float::class -> FLOAT
                    Double::class -> FLOAT
                    Boolean::class -> BOOLEAN
                    String::class.java -> STRING
                    else -> {
                        println(clazz)
                        null
                    }
                }
            }
        }
    }

}
