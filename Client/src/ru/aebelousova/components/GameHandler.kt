package ru.aebelousova.components

import ru.aebelousova.networking.Communicator
import javax.swing.JButton

class GameHandler(var playerField: Field, private var opponentField: Field, val btnReady: JButton, val communicator: Communicator) {
    private var ready = false
    init {
        communicator.addDataRecievedListener(::dataReceived)
        playerField = Field.PlayerField()
        opponentField = Field.OpponentField(communicator)
        playerField.isEnabled = true
        opponentField.isEnabled = false
        btnReady.isEnabled = false
        btnReady.addActionListener { ready = !ready }
        startGame()
        communicator.removeDataRecievedListener(::dataReceived)
    }

    private fun startGame() {
        while (playerField.shipCellCount() != 20 || !ready) {
            btnReady.isEnabled = playerField.shipCellCount() == 20
        }
        while (playerField.shipCellCount() > 0) {

            if (playerField.shipCellCount() == 0)
                communicator.sendData("lost")
        }
    }

    private fun dataReceived(data: String) {
        val vls = data.split("=", limit = 2)
        when(vls[0]){
            "send_token" -> {
                opponentField.isEnabled = true
            }
            "attack" -> {
                val (x, y) = vls[1].split(';', limit = 2)
                if (playerField.getStatus(x.toInt(), y.toInt()) == Status.Ship) {
                    communicator.sendData("hit=$x;$y")
                    playerField.setStatus(x.toInt(), y.toInt(), Status.Hit)
                }
                else {
                    communicator.sendData("miss=$x;$y")
                    playerField.setStatus(x.toInt(), y.toInt(), Status.Miss)
                }
            }
            "miss" -> {
                val (x, y) = vls[1].split(';', limit = 2)
                opponentField.setStatus(x.toInt(), y.toInt(), Status.Miss)
            }
            "hit" -> {
                val (x, y) = vls[1].split(';', limit = 2)
                opponentField.setStatus(x.toInt(), y.toInt(), Status.Hit)
            }
            "endgame" -> {
                playerField.isEnabled = false
                opponentField.isEnabled = false
            }
        }
    }
}