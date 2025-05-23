
// const {Queue} = require('@datastructures-js/queue');
/*
 Queue is internally included in the solution file on leetcode.
 When running the code on leetcode it should stay commented out. 
 It is mentioned here just for information about the external library 
 that is applied for this data structure.
 */

class Util {

    static EMPTY = '.';
    static OBSTACLE = '#';
    static ALPHABET_SIZE = 26;
    static NOT_POSSIBLE_TO_REACH_GOAL = -1;
    static UP = [-1, 0];
    static DOWN = [1, 0];
    static LEFT = [0, -1];
    static RIGHT = [0, 1];
    static MOVES = [Util.UP, Util.DOWN, Util.LEFT, Util.RIGHT];

    /**
     * @param {number} rows
     * @param {number} columns
     * @param {Point[][]} teleportPoints
     */
    constructor(rows, columns, teleportPoints) {
        this.rows = rows;
        this.columns = columns;
        this.teleportPoints = teleportPoints;
    }
}

let util;

/**
 * @param {string[]} matrix
 * @return {number}
 */
var minMoves = function (matrix) {
    util = new Util(matrix.length, matrix[0].length, createTeleportPoints(matrix));
    const start = new Point(0, 0);
    const goal = new Point(util.rows - 1, util.columns - 1);
    return findMinMovesFromStartToGoal(matrix, start, goal);
};

/**
 * @param {number} row
 * @param {number} column
 */
function Point(row, column) {
    this.row = row;
    this.column = column;
}

/**
 * @param {number} row
 * @param {number} column
 * @param {number} distanceFromStart
 */
function Step(row, column, distanceFromStart) {
    this.row = row;
    this.column = column;
    this.distanceFromStart = distanceFromStart;
}

/**
 * @param {string[]} matrix
 * @param {Point} start
 * @param {Point} goal 
 * @return {number} 
 */
function findMinMovesFromStartToGoal(matrix, start, goal) {
    const queue = new Queue(); // Queue<Step>
    queue.enqueue(new Step(start.row, start.column, 0));

    const visited = Array.from(new Array(util.rows), () => new Array(util.columns).fill(false));
    visited[start.row][start.column] = true;

    let charOnCurrentPoint = matrix[start.row].charAt(start.column);
    if (isUpperCaseLetter(charOnCurrentPoint)) {
        handleTeleport(queue.front().distanceFromStart, charOnCurrentPoint, queue, visited);
    }

    while (!queue.isEmpty()) {
        const current = queue.dequeue();
        if (current.row === goal.row && current.column === goal.column) {
            return current.distanceFromStart;
        }

        for (let move of Util.MOVES) {
            const nextRow = current.row + move[0];
            const nextColumn = current.column + move[1];

            if (!isInMatrix(nextRow, nextColumn)
                    || matrix[nextRow].charAt(nextColumn) === Util.OBSTACLE
                    || visited[nextRow][nextColumn]) {
                continue;
            }

            charOnCurrentPoint = matrix[nextRow].charAt(nextColumn);
            if (isUpperCaseLetter(charOnCurrentPoint)) {
                handleTeleport(1 + current.distanceFromStart, charOnCurrentPoint, queue, visited);
                continue;
            }

            queue.enqueue(new Step(nextRow, nextColumn, 1 + current.distanceFromStart));
            visited[nextRow][nextColumn] = true;
        }
    }

    return Util.NOT_POSSIBLE_TO_REACH_GOAL;
}

/**
 * @param {number} distanceFromStart
 * @param {string} charOnCurrentPoint
 * @param {Queue<Step>} queue 
 * @param {boolean[][]} visited
 * @return {void} 
 */
function handleTeleport(distanceFromStart, charOnCurrentPoint, queue, visited) {
    const index = charOnCurrentPoint.codePointAt(0) - 'A'.codePointAt(0);
    for (let point of util.teleportPoints[index]) {
        queue.enqueue(new Step(point.row, point.column, distanceFromStart));
        visited[point.row][point.column] = true;
    }
    util.teleportPoints[index].length = 0;
}

/**
 * @param {string[]} matrix
 * @return {Point[][]} teleportPoints
 */
function createTeleportPoints(matrix) {
    const points = Array.from(new Array(Util.ALPHABET_SIZE), () => new Array());

    for (let row = 0; row < matrix.length; ++row) {
        for (let column = 0; column < matrix[0].length; ++column) {

            const letter = matrix[row].charAt(column);
            if (isUpperCaseLetter(letter)) {
                const index = letter.codePointAt(0) - 'A'.codePointAt(0);
                points[index].push(new Point(row, column));
            }
        }
    }
    return points;
}

/**
 * @param {string} letter
 * @return {boolean}
 */
function isUpperCaseLetter(letter) {
    return letter >= 'A' && letter <= 'Z';
}

/**
 * @param {number} row
 * @param {number} column 
 * @return {boolean}
 */
function isInMatrix(row, column) {
    return row >= 0 && row < util.rows && column >= 0 && column < util.columns;
}
