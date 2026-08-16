package com.smartcity.greenpassport.feature.games.presentation.maze

enum class MazeCellType {
    WALL,
    PATH,
    ITEM,
    START,
    EXIT,
}

data class MazePosition(val row: Int, val col: Int)

private val rawLayout = listOf(
    "S....",
    "###.#",
    ".....",
    ".#I#.",
    ".I..E",
)

fun parseMazeLayout(): List<List<MazeCellType>> = rawLayout.map { row ->
    row.map { char ->
        when (char) {
            '#' -> MazeCellType.WALL
            'I' -> MazeCellType.ITEM
            'S' -> MazeCellType.START
            'E' -> MazeCellType.EXIT
            else -> MazeCellType.PATH
        }
    }
}

fun findStartPosition(grid: List<List<MazeCellType>>): MazePosition {
    grid.forEachIndexed { row, cells ->
        cells.forEachIndexed { col, cell ->
            if (cell == MazeCellType.START) return MazePosition(row, col)
        }
    }
    error("Maze layout is missing a start cell")
}
