package gamemodes.maze

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import java.util.*


class MazeGenerator(private val dimension: Int) {
    private val stack: Stack<Node> = Stack()
    private val rand: Random = Random()
    private val maze: Array<IntArray>
    private val border = 1

    init {
        maze = Array(dimension + border * 2) { IntArray(dimension + border * 2) }
    }

    private fun generateMaze() {
        stack.push(Node(border, border))
        while (!stack.empty()) {
            val next: Node = stack.pop()
            if (validNextNode(next)) {
                maze[next.y][next.x] = 1
                val neighbors = getNeighbors(next)
                randomlyAddNodesToStack(neighbors)
            }
        }
    }

    private fun validNextNode(node: Node): Boolean {
        var numNeighboringOnes = 0
        for (y in node.y - 1 until node.y + 2) {
            for (x in node.x - 1 until node.x + 2) {
                if (onGrid(x, y) && notNode(node, x, y) && maze[y][x] == 1) {
                    numNeighboringOnes++
                }
            }
        }
        return numNeighboringOnes < 3 && maze[node.y][node.x] != 1
    }

    private fun randomlyAddNodesToStack(nodes: ArrayList<Node>) {
        var targetIndex: Int
        while (!nodes.isEmpty()) {
            targetIndex = rand.nextInt(nodes.size)
            stack.push(nodes.removeAt(targetIndex))
        }
    }

    private fun getNeighbors(node: Node): ArrayList<Node> {
        val neighbors = ArrayList<Node>()
        for (y in node.y - 1 until node.y + 2) {
            for (x in node.x - 1 until node.x + 2) {
                if (onGrid(x, y) && notCorner(node, x, y)
                    && notNode(node, x, y)
                ) {
                    neighbors.add(Node(x, y))
                }
            }
        }
        return neighbors
    }

    private fun onGrid(x: Int, y: Int): Boolean {
        return x >= border && y >= border && x < dimension + border * 2 - 1 && y < dimension + border * 2 - 1
    }

    private fun notCorner(node: Node, x: Int, y: Int): Boolean {
        return x == node.x || y == node.y
    }

    private fun notNode(node: Node, x: Int, y: Int): Boolean {
        return !(x == node.x && y == node.y)
    }

    internal class Node(val x: Int, val y: Int)




    fun buildMaze(sizeOfMaze: Int = dimension, sizeOfBlocks: Int, height: Int, center: Location) {
        val mazeGenerator = MazeGenerator(sizeOfMaze)
        mazeGenerator.generateMaze()
        buildBottomLayer(
            center,
            mazeGenerator.maze,
            arrayOf(Material.OAK_LOG.createBlockData()),
            arrayOf(Material.DIRT_PATH.createBlockData()),
            sizeOfBlocks
        )
        buildWalls(
            center,
            mazeGenerator.maze,
            arrayOf(Material.QUARTZ_BLOCK.createBlockData(),
                Material.QUARTZ_BRICKS.createBlockData(),
                Material.QUARTZ_PILLAR.createBlockData(),
                Material.SMOOTH_QUARTZ.createBlockData(),
                Material.CHISELED_QUARTZ_BLOCK.createBlockData()),
            height,
            sizeOfBlocks
        )
    }

    private fun buildBottomLayer(
        location: Location,
        maze: Array<IntArray>,
        wallBlockData: Array<BlockData>,
        floorBlockData: Array<BlockData>,
        blockSize: Int = 3
    ) {
        buildTask(
            center = location,
            maze = maze,
            wallBlockData = wallBlockData,
            spaceBlockData = floorBlockData,
            blockSize = blockSize,
            yOffset = 0,
            yLimits = 1)
    }

    private fun buildWalls(
        location: Location,
        maze: Array<IntArray>,
        wallBlockData: Array<BlockData>,
        height: Int,
        blockSize: Int = 3
    ) {
        buildTask(
            center = location,
            maze = maze,
            wallBlockData = wallBlockData,
            spaceBlockData = arrayOf(Material.AIR.createBlockData()),
            blockSize = blockSize,
            yOffset = 1,
            yLimits = height)
    }

    private fun buildTask(
        blockSize: Int = 3,
        center: Location,
        maze: Array<IntArray>,
        wallBlockData: Array<BlockData>,
        spaceBlockData: Array<BlockData>,
        yOffset: Int,
        yLimits: Int
    ){
        val centerOffset: Double = Math.round((dimension * blockSize) / 2.0).toDouble()
        var currentBlockData: BlockData
        val tempLocation = center.clone().subtract(centerOffset, 0.0, centerOffset)
        for (positions in maze) {
            for (posType in positions) {
                for (x in 0 until blockSize) {
                    for (z in 0 until blockSize) {
                        for(y in yOffset until yLimits) {
                            val temp: Location = tempLocation.clone()
                            temp.add(x.toDouble(), y.toDouble(), z.toDouble())

                            if (posType == 0) {
                                temp.block.blockData = getRandomBlockData(wallBlockData)
                            } else {
                                temp.block.blockData = getRandomBlockData(spaceBlockData)
                            }
                        }
                    }
                }
                tempLocation.add(0.0, 0.0, blockSize.toDouble())
            }
            tempLocation.subtract(0.0, 0.0, positions.size.toDouble() * blockSize.toDouble())
            tempLocation.add(blockSize.toDouble(), 0.0, 0.0)
        }
    }

    private fun getRandomBlockData(blockData: Array<BlockData>): BlockData {
        return blockData[rand.nextInt(blockData.size)]
    }
}