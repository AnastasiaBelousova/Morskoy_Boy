package ru.aebelousova.networking

import java.net.ServerSocket
import java.net.Socket
import java.sql.*
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class Server private constructor(){

    private val serverSocket: ServerSocket
    private var stop = false
    private val connectedClient = mutableListOf<ConnectedClient>()

    companion object {
        private const val PORT = 5703
        private val srv: Server = Server()

        fun getInstance(): Server {
            return srv
        }
    }

    inner class ConnectedClient(socket: Socket){

        private val communicator: Communicator
        private var name: String? = null

        init{
            communicator = Communicator(socket)
            communicator.addDataRecievedListener(::dataReceived)
            communicator.start()
        }

        private fun dataReceived(data: String){
            //Формат сообщений:  команда=данные
            val vls = data.split("=", limit = 2)
            if (vls.size == 2){
                when (vls[0]){
                    "login" -> login(vls[1])
                    "find_game" -> findGame()
                }
            }
        }

        private fun login(data: String) {
            val (user, password) = data.split('=', limit = 2)
            val rs = stmt.executeQuery("SELECT * FROM users WHERE login = '$user' AND password = '$password'")
            if (rs.next()) {
                val wins = rs.getInt("wins")
                val losses = rs.getInt("losses")
                communicator.sendData("login=ok=$user:$wins:$losses")
                name = user
            }
            else {
                communicator.sendData("login=fail")
            }
        }

        private fun findGame() {
            GameFinder.addToQueue(name!!, communicator)
        }
    }

    private val db : Connection//соединение с СУБД
    private val host = "localhost"
    private val db_port = "3306"
    private val db_name = "clients"
    private val stmt: Statement

    init{
        serverSocket = ServerSocket(PORT)
        print("Пароль от СУБД: ")
        val psw = readLine() ?: ""
        val connectionProperties = Properties()
        connectionProperties["user"] = "ana_evg"
        connectionProperties["password"] = psw
        connectionProperties["serverTimezone"] = "UTC"
        try {
            db = DriverManager.getConnection("jdbc:mysql://$host:$db_port/$db_name", connectionProperties) //подключаемся к СУБД
        }
        catch (ex: Exception) {
            //ловим ошибку при создании соединения
            ex.printStackTrace()
            println("Не найдена база данных. Дальнейшная работа невозможна.")
            exitProcess(1)
        }
        stmt = db.createStatement()
        println("SERVER STARTED")
        thread {
            while (!stop) {
                acceptClient()
            }
        }
    }

    private fun acceptClient() {
        println("Ожидание подключения")
        val s = serverSocket.accept()
        println("Новый клиент подключен")
        connectedClient.add(ConnectedClient(s))
    }

    fun sendGameResults(winner: String, loser: String) {
        var rs = stmt.executeQuery("SELECT * FROM users WHERE login = '$winner'")
        if (rs.next())
            rs.updateInt("wins", rs.getInt("wins") + 1)
        rs = stmt.executeQuery("SELECT * FROM users WHERE login = '$loser'")
        if (rs.next())
            rs.updateInt("losses", rs.getInt("losses") + 1)
    }
}