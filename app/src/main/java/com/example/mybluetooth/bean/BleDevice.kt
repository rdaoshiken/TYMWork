package com.example.mybluetooth.bean

import android.bluetooth.BluetoothDevice

//设备类，用来存放扫描到的结果
data class BleDevice(var device: BluetoothDevice, var rssi:Int, var name:String?)
