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
    private val buf: ByteBuffer = ByteBuffer.allocate(60)

    val handler=android.os.Handler()
    public fun initUdp(){
        try {
            channel = DatagramChannel.open();
            channel.socket().bind(InetSocketAddress(23564));
        } catch (e: IOException) {

            e.printStackTrace();
        }
    }

    fun StartListen() {
        while (true) {
            try {
                buf.clear()
                channel.receive(buf)

                //获取ByteBuffer的有效内容
                buf.flip()
                val bytes = ByteArray(buf.limit())
                buf[bytes]
                val msg = Message()
                val bundle = Bundle()

                //把数据放到buddle中
                val configMessage = String(bytes)
                bundle.putString("receive", configMessage)
                //把buddle传递到message
                msg.setData(bundle)
                handler.sendMessage(msg)
            } catch (e: Exception) {
                continue
            }
        }
    }

    var myHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            var bundle = Bundle()
            //从传过来的message数据中取出传过来的绑定数据的bundle对象
            bundle = msg.getData()
            val receive = bundle.getString("receive")
            Log.e("fuck",receive!!)
        }
    }

    fun send(message: String) {
        try {
            val configInfo = message.toByteArray()
            buf.clear()
            buf.put(configInfo)
            buf.flip()
            channel.send(buf, InetSocketAddress(SERVER_IP, SERVER_PORT))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUdp()
        StartListen()
    }
}