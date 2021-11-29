package com.vaca.udpsameport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.vaca.udpsameport.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

class MainActivity : AppCompatActivity() {


    lateinit var binding:ActivityMainBinding




  lateinit  var channel:DatagramChannel
    private val buf: ByteBuffer = ByteBuffer.allocate(600)
    private val bufReceive: ByteBuffer = ByteBuffer.allocate(600)
    val byteArray=ByteArray(500){
        0.toByte()
    }


    fun bytebuffer2ByteArray(buffer: ByteBuffer): ByteArray? {
        buffer.flip()
        val len = buffer.limit() - buffer.position()
        val bytes = ByteArray(len)
        for (i in bytes.indices) {
            bytes[i] = buffer.get()
        }
        return bytes
    }


    fun initUdp(){
        try {
            channel = DatagramChannel.open();
            channel.socket().bind(InetSocketAddress(13207));
        } catch (e: IOException) {

            e.printStackTrace();
        }
    }

    fun StartListen() {
        while (true) {
            try {
                bufReceive.clear()
                channel.receive(bufReceive)
                val receiveByteArray=bytebuffer2ByteArray(bufReceive)
                if (receiveByteArray != null) {
                    val receiveString=String(receiveByteArray)

                    try {
                        if(receiveString.substring(0,1)=="{"){
                            val receiveJson=JSONObject(receiveString)
                            val ip=receiveJson.getString("ip")
                            val port=receiveJson.getInt("port")
                            send2Destination("fuck",ip,port)
                        }else{
                            Log.e("good",receiveString)
                        }

                    }catch (e:Exception){

                        e.printStackTrace()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    fun send(message: String) {
        try {
            val configInfo = message.toByteArray()
            buf.clear()
            buf.put(configInfo)
            buf.flip()
            channel.send(buf, InetSocketAddress("192.168.5.101", 8888))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun send2Destination(message: String,ip:String,port:Int) {
        try {
            val configInfo = message.toByteArray()
            buf.clear()
            buf.put(configInfo)
            buf.flip()
            channel.send(buf, InetSocketAddress(ip, port))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUdp()

        val gx=JSONObject()

        Thread{
            while (true){
                send("{}")
                Thread.sleep(1000)
            }
        }.start()
        Thread{
            Thread.sleep(100)
            StartListen()
        }.start()


    }
}