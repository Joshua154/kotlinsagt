package gamemodes.maze.generator.generation

import java.util.stream.IntStream

class DisjointSet(size: Int) {
    private val parent = IntArray(size)
    private val rank = IntArray(size)

    init {
        IntStream.range(0, size).forEach { i: Int ->
            this.makeSet(
                i
            )
        }
    }

    private fun makeSet(i: Int) {
        parent[i] = i
        rank[i] = 0
    }

    fun find(i: Int): Int {
        if (i != parent[i]) parent[i] = find(parent[i])
        return parent[i]
    }

    fun union(i: Int, j: Int): Boolean {
        val iRoot = find(i)
        val jRoot = find(j)
        if (iRoot == jRoot) return false
        if (rank[iRoot] < rank[jRoot]) {
            parent[iRoot] = jRoot
        } else {
            parent[jRoot] = iRoot
            if (rank[iRoot] == rank[jRoot]) rank[iRoot]++
        }
        return true
    }
}