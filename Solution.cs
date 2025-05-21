
using System;
using System.Collections.Generic;

public class Solution
{
    private record Point(int row, int column) { }

    private record Step(int row, int column, int distanceFromStart) { }

    private static readonly char EMPTY = '.';
    private static readonly char OBSTACLE = '#';

    private static readonly int ALPHABET_SIZE = 26;
    private static readonly int NOT_POSSIBLE_TO_REACH_GOAL = -1;

    private static readonly int[] UP = { -1, 0 };
    private static readonly int[] DOWN = { 1, 0 };
    private static readonly int[] LEFT = { 0, -1 };
    private static readonly int[] RIGHT = { 0, 1 };
    private static readonly int[][] MOVES = { UP, DOWN, LEFT, RIGHT };

    private int rows;
    private int columns;
    private List<Point>[]? teleportPoints;

    public int MinMoves(string[] matrix)
    {
        rows = matrix.Length;
        columns = matrix[0].Length;
        Point start = new Point(0, 0);
        Point goal = new Point(rows - 1, columns - 1);
        teleportPoints = CreateTeleportPoints(matrix);

        return FindMinMovesFromStartToGoal(matrix, start, goal);
    }

    private int FindMinMovesFromStartToGoal(string[] matrix, Point start, Point goal)
    {
        Queue<Step> queue = new Queue<Step>();
        queue.Enqueue(new Step(start.row, start.column, 0));

        bool[][] visited = new bool[rows][];
        for (int row = 0; row < rows; ++row)
        {
            visited[row] = new bool[columns];
        }
        visited[start.row][start.column] = true;

        char charOnCurrentPoint = matrix[start.row][start.column];
        if (IsUpperCaseLetter(charOnCurrentPoint))
        {
            HandleTeleport(queue.Peek().distanceFromStart, charOnCurrentPoint, queue, visited);
        }

        while (queue.Count > 0)
        {
            Step current = queue.Dequeue();
            if (current.row == goal.row && current.column == goal.column)
            {
                return current.distanceFromStart;
            }

            foreach (int[] move in MOVES)
            {
                int nextRow = current.row + move[0];
                int nextColumn = current.column + move[1];

                if (!IsInMatrix(nextRow, nextColumn)
                        || matrix[nextRow][nextColumn] == OBSTACLE
                        || visited[nextRow][nextColumn])
                {
                    continue;
                }

                charOnCurrentPoint = matrix[nextRow][nextColumn];
                if (IsUpperCaseLetter(charOnCurrentPoint))
                {
                    HandleTeleport(1 + current.distanceFromStart, charOnCurrentPoint, queue, visited);
                    continue;
                }

                queue.Enqueue(new Step(nextRow, nextColumn, 1 + current.distanceFromStart));
                visited[nextRow][nextColumn] = true;
            }
        }

        return NOT_POSSIBLE_TO_REACH_GOAL;
    }

    private void HandleTeleport(int distanceFromStart, char charOnCurrentPoint, Queue<Step> queue, bool[][] visited)
    {
        foreach (Point point in teleportPoints![charOnCurrentPoint - 'A'])
        {
            queue.Enqueue(new Step(point.row, point.column, distanceFromStart));
            visited[point.row][point.column] = true;
        }
        teleportPoints[charOnCurrentPoint - 'A'].Clear();
    }

    private List<Point>[] CreateTeleportPoints(string[] matrix)
    {
        List<Point>[] points = new List<Point>[ALPHABET_SIZE];
        for (int i = 0; i < ALPHABET_SIZE; ++i)
        {
            points[i] = new List<Point>();
        }

        for (int row = 0; row < rows; ++row)
        {
            for (int column = 0; column < columns; ++column)
            {

                char letter = matrix[row][column];
                if (IsUpperCaseLetter(letter))
                {
                    points[letter - 'A'].Add(new Point(row, column));
                }
            }
        }
        return points;
    }

    private bool IsUpperCaseLetter(char letter)
    {
        return letter >= 'A' && letter <= 'Z';
    }

    private bool IsInMatrix(int row, int column)
    {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }
}
