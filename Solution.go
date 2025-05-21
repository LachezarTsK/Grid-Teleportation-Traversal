
package main

type Point struct {
    row    int
    column int
}

func NewPoint(row int, column int) Point {
    point := Point{
        row:    row,
        column: column,
    }
    return point
}

type Step struct {
    row               int
    column            int
    distanceFromStart int
}

func NewStep(row int, column int, distanceFromStart int) Step {
    step := Step{
        row:               row,
        column:            column,
        distanceFromStart: distanceFromStart,
    }
    return step
}

const EMPTY byte = '.'
const OBSTACLE byte = '#'

const ALPHABET_SIZE int = 26
const NOT_POSSIBLE_TO_REACH_GOAL int = -1

var UP = [2]int{-1, 0}
var DOWN = [2]int{1, 0}
var LEFT = [2]int{0, -1}
var RIGHT = [2]int{0, 1}
var MOVES = [4][2]int{UP, DOWN, LEFT, RIGHT}

var rows int
var columns int
var teleportPoints [][]Point

func minMoves(matrix []string) int {
    rows = len(matrix)
    columns = len(matrix[0])
    start := NewPoint(0, 0)
    goal := NewPoint(rows - 1, columns - 1)
    teleportPoints = createTeleportPoints(matrix)

    return findMinMovesFromStartToGoal(matrix, start, goal)
}

func findMinMovesFromStartToGoal(matrix []string, start Point, goal Point) int {
    queue := make([]Step, 1)
    queue[0] = NewStep(start.row, start.column, 0)

    visited := make([][]bool, rows)
    for row := 0; row < rows; row++ {
        visited[row] = make([]bool, columns)
    }
    visited[start.row][start.column] = true

    var charOnCurrentPoint = matrix[start.row][start.column]
    if isUpperCaseLetter(charOnCurrentPoint) {
        handleTeleport(queue[0].distanceFromStart, charOnCurrentPoint, &queue, &visited)
    }

    for len(queue) > 0 {
        current := queue[0]
        queue = queue[1:]

        if current.row == goal.row && current.column == goal.column {
            return current.distanceFromStart
        }

        for _, move := range MOVES {
            nextRow := current.row + move[0]
            nextColumn := current.column + move[1]

            if !isInMatrix(nextRow, nextColumn) ||
                matrix[nextRow][nextColumn] == OBSTACLE ||
                visited[nextRow][nextColumn] {
                continue
            }

            charOnCurrentPoint = matrix[nextRow][nextColumn]
            if isUpperCaseLetter(charOnCurrentPoint) {
                handleTeleport(1 + current.distanceFromStart, charOnCurrentPoint, &queue, &visited)
                continue
            }

            queue = append(queue, NewStep(nextRow, nextColumn, 1 + current.distanceFromStart))
            visited[nextRow][nextColumn] = true
        }
    }

    return NOT_POSSIBLE_TO_REACH_GOAL
}

func handleTeleport(distanceFromStart int, charOnCurrentPoint byte, queue *[]Step, visited *[][]bool) {
    index := charOnCurrentPoint - 'A'
    for _, point := range teleportPoints[index] {
        *queue = append(*queue, NewStep(point.row, point.column, distanceFromStart))
        (*visited)[point.row][point.column] = true
    }
    teleportPoints[index] = teleportPoints[index][:0]
}

func createTeleportPoints(matrix []string) [][]Point {
    points := make([][]Point, ALPHABET_SIZE)
    for i := range ALPHABET_SIZE {
        points[i] = make([]Point, 0)
    }

    for row := range rows {
        for column := range columns {

            letter := matrix[row][column]
            if isUpperCaseLetter(letter) {
                index := letter - 'A'
                points[index] = append(points[index], NewPoint(row, column))
            }
        }
    }
    return points
}

func isUpperCaseLetter(letter byte) bool {
    return 'A' <= letter && letter <= 'Z'
}

func isInMatrix(row int, column int) bool {
    return row >= 0 && row < rows && column >= 0 && column < columns
}
