package com.tymphay.tymwork.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
import android.bluetooth.BluetoothGattDescriptor
import java.util.*

object BleHelper {

    //启用指令通知
    fun enableIndicateNotification(gatt: BluetoothGatt): Boolean =
        //设置特征                             获取Gatt服务
        setCharacteristicNotification(gatt, gatt.getService(UUID.fromString(BleConstant.SERVICE_UUID))
            //获取Gatt特征（特性）
            .getCharacteristic(UUID.fromString(BleConstant.CHARACTERISTIC_INDICATE_UUID)))

    //设置特征通知
    @SuppressLint("MissingPermission")
    private fun setCharacteristicNotification(gatt: BluetoothGatt, gattCharacteristic: BluetoothGattCharacteristic): Boolean =
        //如果特征具备Notification功能，返回true就代表设置成功
        if (gatt.setCharacteristicNotification(gattCharacteristic, true))
            gatt.writeDescriptor(gattCharacteristic.getDescriptor(UUID.fromString(BleConstant.DESCRIPTOR_UUID))
                .apply {
                    value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                }) else false

   /* //发送指令  command:命令   isResponse:是否响应
    @SuppressLint("MissingPermission")
    fun sendCommand(gatt: BluetoothGatt, command: String, isResponse: Boolean): Boolean =
        gatt.writeCharacteristic(gatt.getService(UUID.fromString(BleConstant.SERVICE_UUID))
            .getCharacteristic(UUID.fromString(BleConstant.CHARACTERISTIC_WRITE_UUID)).apply {
                writeType = if (isResponse) WRITE_TYPE_DEFAULT else WRITE_TYPE_NO_RESPONSE
                value = ByteUtils.hexStringToBytes(command) })*/
}
