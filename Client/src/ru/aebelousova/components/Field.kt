package ru.aebelousova.components

import ru.aebelousova.networking.Communicator
import java.awt.GridLayout
import javax.swing.JPanel

open class Field: JPanel(GridLayout(10,10)) {
    protected val gameCells = Array(10) { x -> Array(10) { y-> Cell(x, y)} }
    init {
        for (row in gameCells) {
            for (cell in row) {
                add(cell)
            }
        }
    }
    fun shipCellCount() : Int {
        var cnt = 0
        for (row in gameCells)
            for (cell in row)
                if (cell.status == Status.Ship)
                    cnt++
        return cnt
    }
    fun getStatus(x: Int, y: Int) : Status {
        return gameCells[x][y].status
    }
    fun setStatus(x: Int, y: Int, status: Status) {
        gameCells[x][y].status = status
    }

    class OpponentField(communicator: Communicator): Field() {
        init {
            for (row in gameCells)
                for (cell in row)
                    cell.addActionListener{ communicator.sendData("attack=${cell._x};${cell._y}")}
        }
    }

    class PlayerField : Field() {
        init {
            for (row in gameCells)
                for (cell in row) {
                    cell.addActionListener{
                        val status = getStatus(cell._x, cell._y)
                        if (status == Status.None) setStatus(cell._x, cell._y, Status.Ship)
                        else setStatus(cell._x, cell._y, Status.None)
                    }
                }
        }
    }
}
