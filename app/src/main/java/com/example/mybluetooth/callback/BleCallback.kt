package com.example.mybluetooth.callback

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.util.Log
import com.example.mybluetooth.utils.BleHelper

//管理回调
class BleCallback:BluetoothGattCallback() {

    private val TAG = BleCallback::class.java.simpleName
    private lateinit var uiCallback: UiCallback

    fun setUiCallback(uiCallback: UiCallback) {
        this.uiCallback = uiCallback
    }

    //连接状态回调
    @SuppressLint("MissingPermission")
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.e(TAG, "onConnectionStateChange: $status")
            return
        }
        uiCallback.state(
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    //获取MtuSize
                    gatt.requestMtu(512)
                    "连接成功"
                }
                BluetoothProfile.STATE_DISCONNECTED -> "断开连接"
                else -> "onConnectionStateChange: $status"
            }
        )
    }

    //UI回调
    interface UiCallback{
        //当前Ble的状态信息
        fun state(state:String)
    }

}