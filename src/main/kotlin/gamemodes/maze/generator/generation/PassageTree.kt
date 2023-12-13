package gamemodes.maze.generator.generation

import gamemodes.maze.generator.MazeCell
import java.util.*
import java.util.stream.Collectors

class PassageTree(height: Int, width: Int) {
    private val height = (height - 1) / 2
    private val width = (width - 1) / 2

    fun generate(): List<MazeCell> {
        val edges = createEdges()
        Collections.shuffle(edges)
        val tree = buildRandomSpanningTree(edges)
        return createPassages(tree)
    }

    private fun createEdges(): List<Edge> {
        val edges = ArrayList<Edge>()
        for (column in 1 until width) {
            edges.add(
                Edge(
                    toIndex(0, column),
                    toIndex(0, column - 1)
                )
            )
        }
        for (row in 1 until height) {
            edges.add(
                Edge(
                    toIndex(row, 0),
                    toIndex(row - 1, 0)
                )
            )
        }
        for (row in 1 until height) {
            for (column in 1 until width) {
                edges.add(
                    Edge(
                        toIndex(row, column),
                        toIndex(row, column - 1)
                    )
                )
                edges.add(
                    Edge(
                        toIndex(row, column),
                        toIndex(row - 1, column)
                    )
                )
            }
        }
        return edges
    }

    private fun toIndex(row: Int, column: Int): Int {
        return row * width + column
    }

    private fun buildRandomSpanningTree(edges: List<Edge>): List<Edge> {
        val disjointSets = DisjointSet(width * height)
        return edges
            .stream()
            .filter { edge: Edge -> connects(edge, disjointSets) }
            .collect(Collectors.toList())
    }

    private fun connects(edge: Edge?, disjointSet: DisjointSet): Boolean {
        return disjointSet.union(edge!!.firstCell, edge.secondCell)
    }

    private fun createPassages(spanningTree: List<Edge>): List<MazeCell> {
        return spanningTree
            .stream()
            .map { (firstCell, secondCell): Edge ->
                val first: MazeCell = fromIndex(firstCell)
                val second: MazeCell = fromIndex(secondCell)
                getPassage(first, second)
            }.collect(Collectors.toList())
    }

    private fun fromIndex(index: Int): MazeCell {
        val row = index / width
        val column = index % width
        return MazeCell(row, column, MazeCell.Type.PASSAGE)
    }

    private fun getPassage(first: MazeCell, second: MazeCell): MazeCell {
        val row: Int = first.getRow() + second.getRow() + 1
        val column: Int = first.getColumn() + second.getColumn() + 1
        return MazeCell(row, column, MazeCell.Type.PASSAGE)
    }
}