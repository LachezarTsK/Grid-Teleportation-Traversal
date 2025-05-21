
#include <span>
#include <array>
#include <deque>
#include <string>
#include <vector>
using namespace std;

class Solution {

    struct Point {
        int row = 0;
        int column = 0;

        Point(int row, int column) :row{ row }, column{ column } {}
    };

    struct Step {
        int row = 0;
        int column = 0;
        int distanceFromStart = 0;

        Step(int row, int column, int distanceFromStart)
             :row{ row }, column{ column }, distanceFromStart{ distanceFromStart } {
        }
    };

    static const char EMPTY = '.';
    static const char OBSTACLE = '#';

    static const int ALPHABET_SIZE = 26;
    static const int NOT_POSSIBLE_TO_REACH_GOAL = -1;

    static constexpr array<int, 2> UP = { -1, 0 };
    static constexpr array<int, 2> DOWN = { 1, 0 };
    static constexpr array<int, 2> LEFT = { 0, -1 };
    static constexpr array<int, 2> RIGHT = { 0, 1 };
    static constexpr array<array<int, 2>, 4> MOVES = { UP, DOWN, LEFT, RIGHT };

    int rows = 0;
    int columns = 0;
    vector<vector<Point>> teleportPoints;

public:
    int minMoves(const vector<string>& matrix) {
        rows = matrix.size();
        columns = matrix[0].size();
        Point start(0, 0);
        Point goal(rows - 1, columns - 1);
        teleportPoints = createTeleportPoints(matrix);

        return findMinMovesFromStartToGoal(matrix, start, goal);
    }

private:
    int findMinMovesFromStartToGoal(span<const string> matrix, const Point& start, const Point& goal) {
        deque<Step> queue;
        queue.emplace_back(start.row, start.column, 0);

        vector<vector<bool>> visited(rows, vector<bool>(columns));
        visited[start.row][start.column] = true;

        char charOnCurrentPoint = matrix[start.row][start.column];
        if (isUpperCaseLetter(charOnCurrentPoint)) {
                handleTeleport(queue.front().distanceFromStart, charOnCurrentPoint, queue, visited);
        }

        while (!queue.empty()) {
            Step current = queue.front();
            queue.pop_front();
            if (current.row == goal.row && current.column == goal.column) {
                return current.distanceFromStart;
            }

            for (const auto& move : MOVES) {
                int nextRow = current.row + move[0];
                int nextColumn = current.column + move[1];

                if (!isInMatrix(nextRow, nextColumn)
                        || matrix[nextRow][nextColumn] == OBSTACLE
                        || visited[nextRow][nextColumn]) {
                        continue;
                }

                charOnCurrentPoint = matrix[nextRow][nextColumn];
                if (isUpperCaseLetter(charOnCurrentPoint)) {
                        handleTeleport(1 + current.distanceFromStart, charOnCurrentPoint, queue, visited);
                    continue;
                }

                queue.emplace_back(nextRow, nextColumn, 1 + current.distanceFromStart);
                visited[nextRow][nextColumn] = true;
            }
        }

        return NOT_POSSIBLE_TO_REACH_GOAL;
    }

    void handleTeleport(int distanceFromStart, char charOnCurrentPoint, deque<Step>& queue, span<vector<bool>> visited) {
        for (const auto& point : teleportPoints[charOnCurrentPoint - 'A']) {
            queue.emplace_back(point.row, point.column, distanceFromStart);
            visited[point.row][point.column] = true;
        }
        teleportPoints[charOnCurrentPoint - 'A'].clear();
    }

    vector<vector<Point>> createTeleportPoints(span<const string> matrix) const {
        vector<vector<Point>> points(ALPHABET_SIZE);

        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < columns; ++column) {

                char letter = matrix[row][column];
                if (isUpperCaseLetter(letter)) {
                        points[letter - 'A'].emplace_back(row, column);
                }
            }
        }
        return points;
    }

    bool isUpperCaseLetter(char letter) const {
        return letter >= 'A' && letter <= 'Z';
    }

    bool isInMatrix(int row, int column) const {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }
};
