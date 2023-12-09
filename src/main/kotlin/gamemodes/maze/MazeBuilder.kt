package gamemodes.maze

import gamemodes.maze.generator.MazeCell
import gamemodes.maze.generator.MazeGenerator
import org.apache.commons.lang3.ObjectUtils
import org.apache.commons.lang3.ObjectUtils.Null
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.floor


class MazeBuilder(private val dimension: Int) {
    /*
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

            internal class Node(val x: Int, val y: Int)*/

    private val rand: Random = Random()
    private val maze: MazeGenerator = MazeGenerator(dimension, dimension)
    private var spawnLocation: Location? = null
    private lateinit var finishLine: Pair<Location, Vector>

    fun buildMaze(sizeOfMaze: Int = dimension, sizeOfBlocks: Int, height: Int, center: Location) {
        val mazeBuilder = MazeBuilder(sizeOfMaze)

//        val wallBlockData = arrayOf(Material.OAK_LEAVES.createBlockData())
//        val floorBlockData = arrayOf(Material.DIRT_PATH.createBlockData())
//        val underWallBlockData = arrayOf(Material.OAK_LOG.createBlockData())


        val wallBlockData = arrayOf(
            Material.SMOOTH_QUARTZ.createBlockData(), Material.QUARTZ_BRICKS.createBlockData(),
            Material.QUARTZ_BLOCK.createBlockData(), Material.QUARTZ_PILLAR.createBlockData()
        )
        val floorBlockData = arrayOf(Material.GRAY_CONCRETE.createBlockData())
        val underWallBlockData = floorBlockData

        buildBottomLayer(
            center,
            mazeBuilder.maze,
            underWallBlockData,
            floorBlockData,
            sizeOfBlocks
        )
        buildWalls(
            center,
            mazeBuilder.maze,
            wallBlockData,
            height,
            sizeOfBlocks
        )
        mazeBuilder.maze.getEntrance()?.let {
            buildSpawn(
                sizeOfBlocks = sizeOfBlocks,
                height = height,
                wallBlockData = wallBlockData,
                floorBlockData = floorBlockData,
                underWallBlockData = underWallBlockData,
                entrance = cellToLocation(center, it, sizeOfMaze, sizeOfBlocks)
            )
        }
        mazeBuilder.maze.getExit()?.let {
            buildExit(
                sizeOfBlocks = sizeOfBlocks,
                height = height,
                wallBlockData = wallBlockData,
                floorBlockData = floorBlockData,
                underWallBlockData = underWallBlockData,
                exit = cellToLocation(center, it, sizeOfMaze, sizeOfBlocks)
            )
        }
    }

    private fun buildBottomLayer(
        location: Location,
        maze: MazeGenerator,
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
            yLimits = 1
        )
    }

    private fun buildWalls(
        location: Location,
        maze: MazeGenerator,
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
            yLimits = height
        )
    }

    private fun buildTask(
        blockSize: Int = 3,
        center: Location,
        maze: MazeGenerator,
        wallBlockData: Array<BlockData>,
        spaceBlockData: Array<BlockData>,
        yOffset: Int,
        yLimits: Int
    ) {
        val centerOffset: Double = Math.round((dimension * blockSize) / 2.0).toDouble()
        val tempLocation = center.clone().subtract(centerOffset, 0.0, centerOffset)
        for (row in maze.getMaze()) {
            for (cell in row) {
                for (x in 0 until blockSize) {
                    for (z in 0 until blockSize) {
                        for (y in yOffset until yLimits) {
                            val temp: Location = tempLocation.clone()
                            temp.add(x.toDouble(), y.toDouble(), z.toDouble())

                            if (cell == null || cell.isWall()) {
                                temp.block.blockData = getRandomBlockData(wallBlockData)
                            } else {
                                temp.block.blockData = getRandomBlockData(spaceBlockData)
                            }
                        }
                    }
                }
                tempLocation.add(0.0, 0.0, blockSize.toDouble())
            }
            tempLocation.subtract(0.0, 0.0, row.size.toDouble() * blockSize.toDouble())
            tempLocation.add(blockSize.toDouble(), 0.0, 0.0)
        }
    }

    fun buildMazePlatform(
        size: Int,
        blockSize: Int,
        height: Int,
        wallBlockData: Array<BlockData>,
        floorBlockData: Array<BlockData>,
        underWallBlockData: Array<BlockData>,
        location: Location
    ) {
        location.block.blockData = Material.YELLOW_CONCRETE.createBlockData()
        for (y in 0 until height) {
            for (x in 0 until size) {
                for (z in 0 until size) {
                    val temp: Location = location.clone()
                    temp.add(x + 1.0, y.toDouble(), z + 1.0)


                    if (x < blockSize || x > size - 1 - blockSize || z < blockSize || z > size - 1 - blockSize) {
                        if (y == 0) {
                            temp.block.blockData = getRandomBlockData(underWallBlockData)
                        } else {
                            temp.block.blockData = getRandomBlockData(wallBlockData)
                        }
                    } else {
                        if (y == 0) {
                            temp.block.blockData = getRandomBlockData(floorBlockData)
                        }
                    }
                }
            }
        }
    }

    fun buildSpawn(
        sizeOfBlocks: Int,
        platformSize: Int = sizeOfBlocks * 3 * 2,
        height: Int,
        entrance: Location,
        wallBlockData: Array<BlockData>,
        floorBlockData: Array<BlockData>,
        underWallBlockData: Array<BlockData>
    ) {
        var workLocation = entrance.clone().subtract(platformSize.toDouble(), 0.0, platformSize * 0.5 - 2)

        buildMazePlatform(
            platformSize,
            sizeOfBlocks,
            height,
            wallBlockData,
            floorBlockData,
            underWallBlockData,
            workLocation.clone()
        )

        workLocation = entrance.clone().add(0.0, 0.0, sizeOfBlocks.toDouble())
        for (x in 0 until sizeOfBlocks) {
            for (z in 0 until sizeOfBlocks) {
                for (y in 0 until height) {
                    val temp: Location = workLocation.clone()
                    temp.subtract(x.toDouble(), -y.toDouble(), z.toDouble())
                    if (y == 0) {
                        temp.block.blockData = getRandomBlockData(floorBlockData)
                    } else {
                        if (x == 0)
                            temp.block.blockData = Material.WHITE_STAINED_GLASS.createBlockData()
                        else
                            temp.block.blockData = Material.AIR.createBlockData()
                    }
                }
            }
        }
        this.spawnLocation = entrance.clone()
            .add(-2.5*sizeOfBlocks, 1.0, floor(sizeOfBlocks/2.0) + 1.5)
            .setDirection(Vector(1,0,0)
        )
    }

    fun buildExit(
        sizeOfBlocks: Int,
        platformSize: Int = sizeOfBlocks * 3 * 2,
        height: Int,
        exit: Location,
        wallBlockData: Array<BlockData>,
        floorBlockData: Array<BlockData>,
        underWallBlockData: Array<BlockData>
    ) {
        var workLocation = exit.clone().add(sizeOfBlocks.toDouble(), 0.0, platformSize * -0.5 + 2)

        buildMazePlatform(
            platformSize,
            sizeOfBlocks,
            height,
            wallBlockData,
            floorBlockData,
            underWallBlockData,
            workLocation.clone()
        )

        workLocation = exit.add(sizeOfBlocks.toDouble() + 1, 0.0, 1.0)
        finishLine = Pair(workLocation.clone().add(sizeOfBlocks - 1.0, 1.0, 0.0), Vector(0.0, 0.0, sizeOfBlocks - 1.0))
        for (x in 0 until sizeOfBlocks) {
            for (z in 0 until sizeOfBlocks) {
                for (y in 0 until height) {
                    val temp: Location = workLocation.clone()
                    temp.add(x.toDouble(), y.toDouble(), z.toDouble())
                    if (y == 0) {
                        //only two rows
                        if (x >= sizeOfBlocks - 2) {
                            //checkerboard
                            if ((x + (z % 2)) % 2 == 0)
                                temp.block.blockData = Material.BLACK_CONCRETE.createBlockData()
                            else
                                temp.block.blockData = Material.WHITE_CONCRETE.createBlockData()
                        } else {
                            temp.block.blockData = getRandomBlockData(floorBlockData)
                        }
                    } else {
                        temp.block.blockData = Material.AIR.createBlockData()
                    }
                }
            }
        }
    }

    private fun getRandomBlockData(blockData: Array<BlockData>): BlockData {
        return blockData[rand.nextInt(blockData.size)]
    }

    fun cellToLocation(center: Location, cell: MazeCell, sizeOfMaze: Int, sizeOfBlocks: Int): Location {
        return center.clone().add(cell.getOffset(sizeOfMaze, sizeOfMaze).multiply(sizeOfBlocks.toDouble()))
    }

    fun getSpawnLocation(): Location? {
        return spawnLocation
    }

    fun finishLine(): Pair<Location, Vector> {
        return finishLine
    }
}