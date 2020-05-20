package ru.aebelousova.networking

import java.io.*
import java.net.Socket
import kotlin.concurrent.thread

class Communicator(
    private var socket: Socket
) {

    private var active: Boolean
    private var communicationProcess: Thread? = null
    private var dataReceivedListeners = mutableListOf<(String)->Unit>()
    val isAlive: Boolean
        get() = active && socket.isConnected
    init {
        active = true
    }

    fun addDataRecievedListener(l: (String)->Unit){
        dataReceivedListeners.add(l)
    }
    fun removeDataRecievedListener(l: (String)->Unit){
        dataReceivedListeners.remove(l)
    }

    private fun communicate(){
        while (isAlive){
            try{
                val value = receiveData()
                if (value!=null) {
                    dataReceivedListeners.forEach {
                        it.invoke(value)
                    }
                }
            } catch (e: Exception){
                active = false
                if (!socket.isClosed) socket.close()
                println("Обмен данными неожиданно прекращен!")
            }
        }
    }

    private fun receiveData(): String? {
        var data: String? = null
        if (isAlive){
            try{
                val br = BufferedReader(InputStreamReader(socket.getInputStream()))
                data = br.readLine()
            } catch (e: Exception){
                println("Не удалось прочитать данные из сети")
                active = false
                data = null
            }
        }
        return data
    }

    fun sendData(data: String){
        try {
            if (isAlive){
                val pw = PrintWriter(socket.getOutputStream())
                pw.println(data)
                pw.flush()
            }
        } catch (e: Exception){
            println("Не удалось отправить данные в сеть")
            active = false
        }
    }

    fun start(){
        thread{
            if (communicationProcess?.isAlive == true)
                stop()
            active = true
            communicationProcess = thread{
                communicate()
            }
        }
    }

    fun stop(){
        active = false
        if (communicationProcess?.isAlive == true){
            communicationProcess?.interrupt()
            communicationProcess?.join()
        }
        if (!socket.isClosed) socket.close()
    }
}