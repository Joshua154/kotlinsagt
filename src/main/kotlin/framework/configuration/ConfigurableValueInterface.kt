package framework.configuration

import framework.gamemodes.GameMode
import org.bukkit.Material
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType


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
        this.clazz =
            (this::class.java.getGenericSuperclass() as ParameterizedType).getActualTypeArguments()[0] as Class<T>;

        if (this.field.type != this.clazz) throw IllegalArgumentException("The type of the provided filed has to match the generic type of this class! (f:" + this.field.type.name + " != c:" + this.clazz.name + ")");

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
                    Short::class.java -> INTEGER
                    Integer::class.java -> INTEGER
                    Long::class.java -> INTEGER
                    Float::class.java -> FLOAT
                    Double::class.java -> FLOAT
                    String::class.java -> STRING
                    else -> null
                }
            }
        }
    }

}
