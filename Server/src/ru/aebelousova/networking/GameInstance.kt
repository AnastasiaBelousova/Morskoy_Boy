package ru.aebelousova.networking

import java.util.*
import kotlin.concurrent.thread

class GameInstance(private val player1: Communicator,
                   private val player2: Communicator,
                   private val name1: String,
                   private val name2: String) {
    var player1Ready: Boolean = false
    var player2Ready: Boolean = false
    var gameWinner: Int = 0
    var hitToken: Communicator? = if (Random(System.currentTimeMillis()).nextBoolean()) player1 else player2
    init {
        player1.addDataRecievedListener(::p1CommandsListner)
        player2.addDataRecievedListener(::p2CommandsListner)

        thread { startGame() }
    }

    private fun startGame() {
        while(!player1Ready || !player2Ready ) {
            Unit
        }
        while (gameWinner == 0) {
            hitToken?.sendData("send_token")
            hitToken = null
            while (hitToken == null) {
                if (gameWinner!=0)
                    break
            }
        }
        if (gameWinner == 1)
            Server.getInstance().sendGameResults(name1, name2)
        else
            Server.getInstance().sendGameResults(name2, name1)
        player1.removeDataRecievedListener(::p1CommandsListner)
        player2.removeDataRecievedListener(::p2CommandsListner)
    }

    private fun p1CommandsListner(data: String) {
        val vls = data.split('=', limit = 2)
        when (vls[0]) {
            "ready" -> player1Ready = true
            "attack" -> player2.sendData("attack=${vls[1]}")
            "miss" -> {
                hitToken = player1
                player2.sendData("miss=${vls[1]}")
            }
            "hit" -> {
                hitToken = player2
                player2.sendData("hit=${vls[1]}")
            }
            "lost" -> {
                gameWinner = 2
                player2.sendData("endgame")
            }
        }
    }

    private fun p2CommandsListner(data: String) {
        val vls = data.split('=', limit = 2)
        when (vls[0]) {
            "ready" -> player2Ready = true
            "attack" -> player1.sendData("attack=${vls[1]}")
            "miss" -> {
                hitToken = player2
                player1.sendData("miss=${vls[1]}")
            }
            "hit" -> {
                hitToken = player1
                player1.sendData("hit=${vls[1]}")
            }
            "lost" -> {
                gameWinner = 1
                player1.sendData("endgame")
            }
        }
    }
}