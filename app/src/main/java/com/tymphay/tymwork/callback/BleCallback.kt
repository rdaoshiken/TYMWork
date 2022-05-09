package com.tymphay.tymwork.callback

import android.annotation.SuppressLint
import android.bluetooth.*
import android.util.Log
import com.tymphay.tymwork.utils.BleConstant
import com.tymphay.tymwork.utils.BleHelper

//管理回调
class BleCallback:BluetoothGattCallback() {

    private lateinit var uiCallback: UiCallback
    private lateinit var mBluetoothGatt: BluetoothGatt

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
            return
        }
        uiCallback.state(
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {   //连接成功
                    //连接成功后，发现服务,触发onServicesDiscovered回调
                    gatt.discoverServices()
                    Log.e("111","连接成功")
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
            /*if (!BleHelper.enableIndicateNotification(gatt)) {
            gatt.disconnect()
            "开启通知属性异常"
        } else {
            //gattServices用来存放所有获取到的服务
            var gattServices: List<BluetoothGattService> = mBluetoothGatt.services
            for (gattService in gattServices){
                var serviceUUID=gattService.uuid.toString()
                Log.e("ServiceUUID:","$serviceUUID")
            }
            "发现了服务 $status"
        }*/

            if (status == BluetoothGatt.GATT_SUCCESS) {

                
                gatt.disconnect()
                "开启通知属性异常"
            } else {
                //gattServices用来存放所有获取到的服务
                var gattServices: List<BluetoothGattService> = mBluetoothGatt.services
                for (gattService in gattServices){
                    var serviceUUID=gattService.uuid.toString()
                    Log.e("ServiceUUID:","$serviceUUID")
                }
                "发现了服务 $status"
            }

        )

    }
}