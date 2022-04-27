package com.example.mybluetooth.ui

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mybluetooth.databinding.ActivityConnectBluetoothBinding

class ConnectBluetoothActivity :AppCompatActivity() {

    //绑定视图
    private lateinit var binding: ActivityConnectBluetoothBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityConnectBluetoothBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    //Gatt
    private lateinit var gatt: BluetoothGatt
    //页面视图的初始化，同时接收传递过来的device
    private fun initView(){
        supportActionBar?.apply {
            title = "Data Exchange"
            setDisplayHomeAsUpEnabled(true)
        }
        val device = intent.getParcelableExtra<BluetoothDevice>("device")
        //gatt连接
        gatt = device!!.connectGatt(this, false, bleCallback)
    }




}