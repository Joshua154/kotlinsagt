package gamemodes.maze.generator

import org.bukkit.Location
import org.bukkit.util.Vector
import java.util.*

class MazeCell(private val row: Int, private val column: Int, private val type: Type) {
    enum class Type {
        PASSAGE,
        WALL,
        ESCAPE
    }

    fun getRow(): Int {
        return row
    }

    fun getColumn(): Int {
        return column
    }

    fun isPassage(): Boolean {
        return type == Type.PASSAGE
    }

    fun isWall(): Boolean {
        return type == Type.WALL
    }

    fun isEscape(): Boolean {
        return type == Type.ESCAPE
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val mazeCell: MazeCell = other as MazeCell
        return row == mazeCell.row && column == mazeCell.column && type == mazeCell.type
    }

    override fun hashCode(): Int {
        return Objects.hash(row, column, type)
    }

    fun getOffset(width: Int, height: Int): Vector {
        var x = (row - 1) - width/2
        var z = (column - 1) - height/2

        return Vector(x.toDouble(), 0.0, z.toDouble())
    }
}