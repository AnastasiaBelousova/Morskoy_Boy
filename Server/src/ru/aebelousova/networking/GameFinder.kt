package ru.aebelousova.networking

import java.net.Socket

object GameFinder {
    var expectingClientName: String? = null
    var expectingClient: Communicator? = null
    fun addToQueue (name: String, player: Communicator) {
        if (expectingClient == null) {
            expectingClient = player
            expectingClientName = name
        }
        else {
            GameInstance(expectingClient!!, player, expectingClientName!!, name)
            expectingClient?.sendData("gamefound=$name")
            player.sendData("gamefound=$expectingClientName")
            expectingClient = null
            expectingClientName = null
        }
    }
}