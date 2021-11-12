// Inspiration: https://github.com/TypicalDevStuff/sudoku-generator
package com.example.sudokusolver

import android.util.Log
import java.lang.Math.max
import java.lang.Math.sqrt

object SudokuSolver {
    // solved?
    var status = false
    var rows = 9
    var columns = rows
    var squareSides = sqrt(rows.toDouble()).toInt()
    var indexList = mutableListOf<Pair<Int, Int>>()

    var maxIndex = 0

    var grid = arrayOf<Array<Int>>()

    fun fill(array: Array<Int>): Pair<Array<Int>, Boolean> {
        grid = parse1Dto2D(array)
        return(solve())
    }

    fun printBoard(board: Array<Array<Int>>) {
        for (i in board.indices) {
            Log.i("Board: ", board[i].contentToString())
        }
    }

    fun solve(): Pair<Array<Int>, Boolean> {
        getIndex(grid)
        var finalBoard = traverse(0, grid)
        // transform to 1D
        return Pair(parse2Dto1D(finalBoard.first), finalBoard.second)
    }

    fun getIndex(board: Array<Array<Int>>) {
        var rowI = 0
        var colI = 0
        for (i in (0..80)) {
            rowI = i / rows
            colI = i % columns
            // only add it empty cell
            if(board[rowI][colI] == 0) {
                indexList.add(Pair(i, removeUsedValues(i, board).count()))
            }
        }
        // Stops working here...
        // Both sortBy and sortByDescending get stuck in loops
        // But same logic works as long as they're sorted by boardindex...weird
        //indexList.sortByDescending { it.second }
        Log.i("size: ", indexList.count().toString())
    }

    // gets pretty far but still not working, let's fix later!
    fun traverse(index: Int,  board: Array<Array<Int>>): Pair<Array<Array<Int>>, Boolean> {
        val range = removeUsedValues(indexList.elementAt(index).first, board)
        // check if we got a good board
        var result = Pair(board, status)
        var rowIndex = indexList.elementAt(index).first / rows
        var colIndex = indexList.elementAt(index).first % columns
        var newBoard = copyArray(board)

        for (candidateValue in range) {
            newBoard[rowIndex][colIndex] = candidateValue
            if(index == indexList.count()-1) {
                status = true
                return Pair(newBoard, status)
            }
            var temp = traverse(index + 1, newBoard)
            if(temp.second == true) {
                return temp
            }
        }

        // does not update if board is unsolvable!!
        return result
    }

    private fun copyArray(old: Array<Array<Int>>): Array<Array<Int>> {
        val newArray: Array<Array<Int>> = Array<Array<Int>>(9){ Array<Int>(9) {0} }
        for (i in (0..8)) {
            newArray[i] = old[i].copyOf()
        }
        return newArray
    }

    fun removeUsedValues(index: Int, board: Array<Array<Int>>): List<Int> {
        var rowIndex = index / rows
        var colIndex = index % columns
        // If square is prefilled, return only that value
        if(board[rowIndex][colIndex] != 0) {
            return listOf(board[rowIndex][colIndex])
        }

        var noRow = removeRow(index, board)
        var noCol = removeCol(noRow, index, board)
        var noSquare = removeSquare(noCol, index, board)

        return noSquare
    }

    // WORKS
    fun removeRow(index: Int, board: Array<Array<Int>>): MutableList<Int> {
        var rowIndex = index / rows

        var newRange: MutableList<Int> = (1..9).toMutableList()

        board[rowIndex].forEach {
            if (it != 0) {
                newRange.remove(it)
            }
        }

        return newRange
    }

    fun removeCol (range: MutableList<Int>, index: Int, board: Array<Array<Int>>): MutableList<Int> {
        var colIndex = index % columns
        var newRange: MutableList<Int> = range

        board.forEach {
            if (it[colIndex] != 0) {
                newRange.remove(it[colIndex])
            }
        }

        return newRange
    }

    fun removeSquare (range: MutableList<Int>, index: Int, board: Array<Array<Int>>): MutableList<Int> {
        val rowStart = findBoxStart(index/rows)
        val rowEnd = findBoxEnd(rowStart)
        val columnStart = findBoxStart(index%columns)
        val columnEnd = findBoxEnd(columnStart)

        var newRange: MutableList<Int> = range

        for (i in rowStart until rowEnd) {
            for (j in columnStart until columnEnd) {
                if (board[i][j] != 0) {
                    newRange.remove(board[i][j])
                }
            }
        }

        return newRange
    }

    private fun findBoxStart(index: Int) = index - index % squareSides

    private fun findBoxEnd(index: Int) = index + squareSides

    fun parse1Dto2D(oneD: Array<Int>): Array<Array<Int>> {
        var newgrid = Array(9) {
            Array(9, {0})
        }
        for(i in oneD.indices) {
            var rowIndex = i / rows
            var colIndex = i % columns
            newgrid[rowIndex][colIndex] = oneD[i]
        }
        return newgrid
    }

    fun parse2Dto1D(twoD: Array<Array<Int>>): Array<Int> {
        var newgrid = Array(81) {0}
        for(i in (0 until rows)) {
            for (j in (0 until columns)) {
                newgrid[i*(rows) + j] = twoD[i][j]
            }
        }
        return newgrid
    }
}