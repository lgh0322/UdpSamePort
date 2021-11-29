package com.vaca.udpsameport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import java.io.IOException
import java.lang.Exception
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

class MainActivity : AppCompatActivity() {
  lateinit  var channel:DatagramChannel
    private val buf: ByteBuffer = ByteBuffer.allocate(600)
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
            channel.socket().bind(InetSocketAddress(8888));
        } catch (e: IOException) {

            e.printStackTrace();
        }
    }

    fun StartListen() {
        while (true) {
            try {
                channel.receive(buf)
                val gg=bytebuffer2ByteArray(buf)
                if (gg != null) {
                    Log.e("receive", gg.size.toString()+"   "+String(gg))
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUdp()
        Thread{
            Thread.sleep(2000)
            StartListen()
        }.start()

        Thread{
            while (true){
                Thread.sleep(1000)
                send("fut45546456tck")
            }
        }.start()


    }
}