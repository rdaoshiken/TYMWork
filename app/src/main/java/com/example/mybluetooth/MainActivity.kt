package com.example.mybluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    //蓝牙适配器
    lateinit var bluetoothAdapter:BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //初始化蓝牙适配器
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter()

        //检查蓝牙是否可用
        if (bluetoothAdapter==null){
            Toast.makeText(this,"您的蓝牙不可用",Toast.LENGTH_LONG).show()
        }else{
            if (!bluetoothAdapter.isEnabled){  //如果蓝牙未开启,则打开蓝牙
                startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1)
            }
        }


    }

    //扫描蓝牙
    fun scanBluetooth(view: View) {
        if(bluetoothAdapter!=null) {
            if (bluetoothAdapter!!.isEnabled){  //打开
                //开始扫描周围的蓝牙设备，如果扫描到蓝牙设备，则通过广播接收器发送广播
                if ()
            }

        }
    }

}
