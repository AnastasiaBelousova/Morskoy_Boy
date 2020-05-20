package ru.aebelousova.components

import java.awt.Color
import javax.swing.JButton

class Cell(val _x: Int, val _y: Int): JButton()  {
    var status: Status = Status.None
        set(value) {
            field = value
            when(value) {
                Status.Hit -> background = Color.RED
                Status.Miss -> background = Color.DARK_GRAY
                Status.Ship -> background = Color.WHITE
                Status.None -> background = Color.CYAN
            }
            repaint()
        }
    init {
        background = Color.CYAN
    }
}

enum class Status {
    None,
    Miss,
    Ship,
    Hit
}