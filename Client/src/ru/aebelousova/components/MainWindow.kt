package ru.aebelousova.components

import Client
import java.awt.Dimension
import javax.swing.*
val window = MainWindow()

class MainWindow: JFrame("Морской бой") {
    private var playerField = Field()
    private var opponentField = Field()
    private val btnFindGame = JButton("Найти игру")
    private val btnPlayWithComp = JButton("Играть против компьютера")
    private val client: Client
        get() = Client.getInstance()
    private val lblName = JLabel("default")
    private val lblWins = JLabel("Побед: ")
    private val lblLosses = JLabel("Поражений: ")
    private val lblOpponent = JLabel()
    private val btnReady = JButton("Готов!")

    inner class LoginWindow: JFrame("Авторизация") {
        private val lblStatus = JLabel("Введите логин и пароль")
        private val taLogin = JTextArea("user1")
        private val taPassword = JPasswordField("vfabjpb")
        private val btnAccept = JButton("Войти")
        init {
            defaultCloseOperation = WindowConstants.HIDE_ON_CLOSE
            minimumSize = Dimension(200, 100)
            btnAccept.addActionListener { client.communicator.sendData("login=${taLogin.text}=${taPassword.password.joinToString("")}") }
            client.communicator.addDataRecievedListener(::loginResult)

            val gl = GroupLayout(contentPane)
            layout = gl
            gl.setHorizontalGroup(
                    gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(lblStatus)
                            .addComponent(taLogin)
                            .addComponent(taPassword)
                            .addComponent(btnAccept)
            )
            gl.setVerticalGroup(
                    gl.createSequentialGroup()
                            .addComponent(lblStatus)
                            .addComponent(taLogin)
                            .addComponent(taPassword)
                            .addComponent(btnAccept)
            )
            pack()
            isVisible = true
        }
        private fun loginResult(data: String) {
            val vls = data.split('=', limit = 3)
            if(vls[0]=="login") {
                if(vls[1]=="ok"){
                    val (user, wins, losses) = vls[2].split(':', limit = 3)
                    lblName.text = user
                    lblWins.text += wins
                    lblLosses.text += losses
                    this.dispose()
                }
                else {
                    lblStatus.text = "Неверный логин или пароль!"
                }
            }

        }
    }

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        minimumSize = Dimension(500, 500)
        btnFindGame.addActionListener { client.communicator.sendData("find_game=") }
        btnPlayWithComp.addActionListener { playWithComp() }
        btnReady.addActionListener { client.communicator.sendData("ready") }

        val gl = GroupLayout(contentPane)
        layout = gl
        gl.setHorizontalGroup(
                gl.createSequentialGroup()
                        .addGap(4)
                        .addGroup(gl.createParallelGroup()
                                .addComponent(lblName)
                                .addComponent(lblWins)
                                .addComponent(lblLosses)
                                .addComponent(btnFindGame)
                                .addComponent(btnPlayWithComp))
                        .addGap(4)
                        .addGroup(gl.createParallelGroup()
                                .addComponent(opponentField)
                                .addComponent(playerField))
                        .addGap(4)
                        .addGroup(gl.createParallelGroup()
                                .addComponent(lblOpponent)
                                .addComponent(btnReady))
        )
        gl.setVerticalGroup(
                gl.createSequentialGroup()
                        .addGap(4)
                        .addGroup(gl.createParallelGroup()
                                .addGroup(gl.createSequentialGroup()
                                        .addComponent(lblName)
                                        .addComponent(lblWins)
                                        .addComponent(lblLosses)
                                        .addComponent(btnFindGame)
                                        .addComponent(btnPlayWithComp)
                                        .addComponent(lblOpponent))
                                .addComponent(opponentField))
                        .addGap(4)
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(playerField)
                                .addComponent(btnReady))
                        .addGap(4)
        )
        pack()
        isVisible = true
        LoginWindow()
    }

    fun StartGame(name: String?) {
        playerField = Field()
        opponentField = Field()
        btnFindGame.isEnabled = false
        btnPlayWithComp.isEnabled = false
        lblOpponent.text = name
        GameHandler(playerField, opponentField, btnReady, client.communicator)
        btnFindGame.isEnabled = true
        btnPlayWithComp.isEnabled = true
    }

    private fun playWithComp() {

    }
}