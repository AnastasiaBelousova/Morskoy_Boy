import ru.aebelousova.components.MainWindow
import ru.aebelousova.components.window
import ru.aebelousova.networking.Communicator
import java.net.Socket

class Client private constructor() {
    companion object {
        private val client: Client = Client()

        fun getInstance(): Client {
            return client
        }
    }
    val communicator : Communicator
//    var name: String = "default"
//    var wins: Int = 0
//    var losses: Int = 0

    init {
        communicator = Communicator(Socket("localhost",5703))
        communicator.addDataRecievedListener(::dataRecieved)
        communicator.start()
    }

    private fun dataRecieved(data: String){
        val vls = data.split('=', limit = 2)
        when(vls[0]) {
            "gamefound" -> {
                window.StartGame(vls[1])
            }
        }
    }
}