package gamemodes.maze.generator

import gamemodes.maze.generator.generation.PassageTree
import org.bukkit.Location
import java.util.function.Consumer

class MazeGenerator(private val width: Int, private val height: Int) {
    private var grid: Array<Array<MazeCell?>> = Array(height) { arrayOfNulls(width) }

    init {
        fillGrid()
    }

    private fun fillGrid() {
        fillAlternately()
        fillGaps()
        makeEntranceAndExit()
        generatePassages()
    }

    private fun putCell(row: Int, column: Int, type: MazeCell.Type) {
        grid[row][column] = MazeCell(row, column, type)
    }

    private fun fillAlternately() {
        for (i in 0 until height) {
            for (j in 0 until width) {
                if ((i and 1) == 0 || (j and 1) == 0) {
                    putCell(i, j, MazeCell.Type.WALL)
                } else {
                    putCell(i, j, MazeCell.Type.PASSAGE)
                }
            }
        }
    }

    private fun fillGaps() {
        if (height % 2 == 0) wallLastRow()
        if (width % 2 == 0) wallLastColumn()
    }

    private fun wallLastColumn() {
        for (i in 0 until height) putCell(i, width - 1, MazeCell.Type.WALL)
    }

    private fun wallLastRow() {
        for (i in 0 until width) putCell(height - 1, i, MazeCell.Type.WALL)
    }

    private fun getExitColumn(): Int {
        return width - 3 + width % 2
    }

    private fun makeEntranceAndExit() {
        putCell(0, 1, MazeCell.Type.PASSAGE)
        putCell(height - 1, getExitColumn(), MazeCell.Type.PASSAGE)
        if (height % 2 == 0) putCell(height - 2, getExitColumn(), MazeCell.Type.PASSAGE)
    }

    private fun generatePassages() {
        PassageTree(height, width)
            .generate()
            .forEach(putCell())
    }

    private fun putCell(): Consumer<MazeCell> {
        return Consumer<MazeCell> { mazeCell: MazeCell -> grid[mazeCell.getRow()][mazeCell.getColumn()] = mazeCell }
    }

    fun getEntrance(): MazeCell? {
        return grid[0][1]
    }

    fun getExit(): MazeCell? {
        return grid[height - 1][getExitColumn()]
    }

    fun getMaze(): Array<Array<MazeCell?>> {
        return grid
    }
}