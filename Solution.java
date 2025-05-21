
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Solution {

    private record Point(int row, int column) {}

    private record Step(int row, int column, int distanceFromStart) {}

    private static final char EMPTY = '.';
    private static final char OBSTACLE = '#';

    private static final int ALPHABET_SIZE = 26;
    private static final int NOT_POSSIBLE_TO_REACH_GOAL = -1;

    private static final int[] UP = {-1, 0};
    private static final int[] DOWN = {1, 0};
    private static final int[] LEFT = {0, -1};
    private static final int[] RIGHT = {0, 1};
    private static final int[][] MOVES = {UP, DOWN, LEFT, RIGHT};

    private int rows;
    private int columns;
    private List<Point>[] teleportPoints;

    public int minMoves(String[] matrix) {
        rows = matrix.length;
        columns = matrix[0].length();
        Point start = new Point(0, 0);
        Point goal = new Point(rows - 1, columns - 1);
        teleportPoints = createTeleportPoints(matrix);

        return findMinMovesFromStartToGoal(matrix, start, goal);
    }

    private int findMinMovesFromStartToGoal(String[] matrix, Point start, Point goal) {
        Queue<Step> queue = new LinkedList<>();
        queue.add(new Step(start.row, start.column, 0));

        boolean[][] visited = new boolean[rows][columns];
        visited[start.row][start.column] = true;

        char charOnCurrentPoint = matrix[start.row].charAt(start.column);
        if (isUpperCaseLetter(charOnCurrentPoint)) {
            handleTeleport(queue.peek().distanceFromStart, charOnCurrentPoint, queue, visited);
        }

        while (!queue.isEmpty()) {
            Step current = queue.poll();
            if (current.row == goal.row && current.column == goal.column) {
                return current.distanceFromStart;
            }

            for (int[] move : MOVES) {
                int nextRow = current.row + move[0];
                int nextColumn = current.column + move[1];

                if (!isInMatrix(nextRow, nextColumn)
                        || matrix[nextRow].charAt(nextColumn) == OBSTACLE
                        || visited[nextRow][nextColumn]) {
                    continue;
                }

                charOnCurrentPoint = matrix[nextRow].charAt(nextColumn);
                if (isUpperCaseLetter(charOnCurrentPoint)) {
                    handleTeleport(1 + current.distanceFromStart, charOnCurrentPoint, queue, visited);
                    continue;
                }

                queue.add(new Step(nextRow, nextColumn, 1 + current.distanceFromStart));
                visited[nextRow][nextColumn] = true;
            }
        }

        return NOT_POSSIBLE_TO_REACH_GOAL;
    }

    private void handleTeleport(int distanceFromStart, char charOnCurrentPoint, Queue<Step> queue, boolean[][] visited) {
        for (Point point : teleportPoints[charOnCurrentPoint - 'A']) {
            queue.add(new Step(point.row, point.column, distanceFromStart));
            visited[point.row][point.column] = true;
        }
        teleportPoints[charOnCurrentPoint - 'A'].clear();
    }

    private List<Point>[] createTeleportPoints(String[] matrix) {
        List<Point>[] points = new ArrayList[ALPHABET_SIZE];
        for (int i = 0; i < ALPHABET_SIZE; ++i) {
            points[i] = new ArrayList<>();
        }

        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < columns; ++column) {

                char letter = matrix[row].charAt(column);
                if (isUpperCaseLetter(letter)) {
                    points[letter - 'A'].add(new Point(row, column));
                }
            }
        }
        return points;
    }

    private boolean isUpperCaseLetter(char letter) {
        return letter >= 'A' && letter <= 'Z';
    }

    private boolean isInMatrix(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }
}
