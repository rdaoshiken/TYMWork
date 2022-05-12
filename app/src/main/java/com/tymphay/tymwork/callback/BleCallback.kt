package com.tymphay.tymwork.callback

import android.annotation.SuppressLint
import android.bluetooth.*
import android.util.Log
import com.tymphay.tymwork.TymApplication
import com.tymphay.tymwork.adapter.ConnectDeviceAdapter
import com.tymphay.tymwork.bean.ConnectDevice

//管理回调
class BleCallback:BluetoothGattCallback() {

    private lateinit var uiCallback: UiCallback
    private lateinit var mBluetoothGatt: BluetoothGatt
    //连接设备适配器
    var connectDeviceAdapter : ConnectDeviceAdapter? =null

    fun setUiCallback(uiCallback: UiCallback) {
        this.uiCallback = uiCallback
    }

    //UI回调
    interface UiCallback{
        //当前Ble的状态信息
        fun state(state:String)
    }

    //连接状态回调
    @SuppressLint("MissingPermission")
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.e("status", "onConnectionStateChange: $status")
            uiCallback.state("连接失败")
            return
            }
        uiCallback.state(
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {   //连接成功
                    //获取已连接到的设备
                    val device = gatt.device
                    //添加已连接的设备到列表中
                    TymApplication.connectList?.add(ConnectDevice(device,device.name))
                    //连接成功后，发现服务,触发onServicesDiscovered回调
                    gatt.discoverServices()
                    "连接成功"
                }
                BluetoothProfile.STATE_DISCONNECTED -> "断开连接"    //断开连接
                else -> "onConnectionStateChange: $status"
            }
        )
    }

    //发现服务回调,打开通知开关
    @SuppressLint("MissingPermission")
    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        uiCallback.state(
            if (status == BluetoothGatt.GATT_SUCCESS){
                //gattServices用来存放所有获取到的服务
                var gattServices: MutableList<BluetoothGattService> = gatt.services as MutableList<BluetoothGattService>
                for (gattService in gattServices) {
                    var serviceUUID = gattService.uuid.toString()
                    Log.e("service", "UUID:$serviceUUID")
                }
                "发现服务"
            } else "未发现服务"
        )
    }
}