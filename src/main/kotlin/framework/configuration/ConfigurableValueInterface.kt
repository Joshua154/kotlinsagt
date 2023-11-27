package framework.configuration

import framework.gamemode.GameMode
import org.bukkit.Material
import java.lang.reflect.Field


class ConfigurableValueInterface<T : Any> {

    private val name: String;
    private val material: Material;

    private val defaultValue: T;

    private val field: Field;

    private val type: Type;
    private val clazz: Class<T>;

    constructor(name: String, material: Material, field: Field, defaultValue: T) {
        this.name = name;
        this.material = material;
        this.defaultValue = defaultValue;
        this.field = field;
        this.clazz = defaultValue.javaClass;

        this.type = Type.fromClass(this.clazz)!!;
    }

    fun getName(): String {
        return this.name;
    }

    fun getMaterial(): Material {
        return this.material;
    }

    fun getDefaultValue(): T {
        return this.defaultValue;
    }

    fun getField(): Field {
        return this.field;
    }

    fun getType(): Type {
        return this.type;
    }

    fun getClazz(): Class<T> {
        return this.clazz;
    }

    fun changeValue(newValue: T, gameMode: GameMode) {
        this.field.set(gameMode, newValue);
        gameMode.applyConfiguration();
    }

    public enum class Type {
        INTEGER,
        FLOAT,
        STRING;

        companion object {
            fun fromClass(clazz: Class<out Any>): Type? {
                return when (clazz) {
                    Short::class.java, java.lang.Short.TYPE -> INTEGER
                    Integer::class.java, java.lang.Integer.TYPE -> INTEGER
                    Long::class.java, java.lang.Long.TYPE -> INTEGER
                    Float::class.java, java.lang.Float.TYPE -> FLOAT
                    Double::class.java, java.lang.Double.TYPE -> FLOAT
                    String::class.java -> STRING
                    else -> null
                }
            }
        }
    }

}
