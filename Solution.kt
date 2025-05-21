
import java.util.*
import kotlin.collections.ArrayList

class Solution {

    private data class Point(val row: Int, val column: Int) {}

    private data class Step(val row: Int, val column: Int, val distanceFromStart: Int) {}

    private companion object {
        const val EMPTY = '.'
        const val OBSTACLE = '#'

        const val ALPHABET_SIZE = 26
        const val NOT_POSSIBLE_TO_REACH_GOAL = -1

        val UP = intArrayOf(-1, 0)
        val DOWN = intArrayOf(1, 0)
        val LEFT = intArrayOf(0, -1)
        val RIGHT = intArrayOf(0, 1)
        val MOVES = arrayOf(UP, DOWN, LEFT, RIGHT)
    }

    private var rows: Int = 0
    private var columns: Int = 0
    private lateinit var teleportPoints: Array<ArrayList<Point>>

    fun minMoves(matrix: Array<String>): Int {
        rows = matrix.size
        columns = matrix[0].length
        val start = Point(0, 0)
        val goal = Point(rows - 1, columns - 1)
        teleportPoints = createTeleportPoints(matrix)

        return findMinMovesFromStartToGoal(matrix, start, goal)
    }

    private fun findMinMovesFromStartToGoal(matrix: Array<String>, start: Point, goal: Point): Int {
        val queue = LinkedList<Step>()
        queue.add(Step(start.row, start.column, 0))

        val visited = Array<BooleanArray>(rows) { BooleanArray(columns) }
        visited[start.row][start.column] = true

        var charOnCurrentPoint = matrix[start.row][start.column]
        if (isUpperCaseLetter(charOnCurrentPoint)) {
            handleTeleport(queue.peek().distanceFromStart, charOnCurrentPoint, queue, visited)
        }

        while (!queue.isEmpty()) {
            val current = queue.poll()
            if (current.row == goal.row && current.column == goal.column) {
                return current.distanceFromStart
            }

            for (move in MOVES) {
                val nextRow = current.row + move[0]
                val nextColumn = current.column + move[1]

                if (!isInMatrix(nextRow, nextColumn)
                    || matrix[nextRow][nextColumn] == OBSTACLE
                    || visited[nextRow][nextColumn]) {
                    continue
                }

                charOnCurrentPoint = matrix[nextRow][nextColumn]
                if (isUpperCaseLetter(charOnCurrentPoint)) {
                    handleTeleport(1 + current.distanceFromStart, charOnCurrentPoint, queue, visited)
                    continue
                }

                queue.add(Step(nextRow, nextColumn, 1 + current.distanceFromStart))
                visited[nextRow][nextColumn] = true
            }
        }

        return NOT_POSSIBLE_TO_REACH_GOAL
    }

    private fun handleTeleport(distanceFromStart: Int, charOnCurrentPoint: Char, queue: Queue<Step>, visited: Array<BooleanArray>) {
        for (point in teleportPoints[charOnCurrentPoint - 'A']) {
            queue.add(Step(point.row, point.column, distanceFromStart))
            visited[point.row][point.column] = true
        }
        teleportPoints[charOnCurrentPoint - 'A'].clear()
    }

    private fun createTeleportPoints(matrix: Array<String>): Array<ArrayList<Point>> {
        val points = Array<ArrayList<Point>>(ALPHABET_SIZE) { ArrayList<Point>() }

        for (row in 0..<rows) {
            for (column in 0..<columns) {

                val letter = matrix[row][column]
                if (isUpperCaseLetter(letter)) {
                    points[letter - 'A'].add(Point(row, column))
                }
            }
        }
        return points
    }

    private fun isUpperCaseLetter(letter: Char): Boolean {
        return letter in 'A'..'Z'
    }

    private fun isInMatrix(row: Int, column: Int): Boolean {
        return row in 0..<rows && column in 0..<columns
    }
}
