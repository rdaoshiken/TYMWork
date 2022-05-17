package com.tymphay.tymwork.model

import android.bluetooth.BluetoothDevice

//设备类，用来存放扫描到的结果   蓝牙设备，信号强度，名称
data class BleDevice(var device: BluetoothDevice, var rssi:Int, var name:String? , var scanTime:String)
