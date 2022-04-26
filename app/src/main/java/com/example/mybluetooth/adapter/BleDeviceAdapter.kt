package com.example.mybluetooth.adapter

import com.example.mybluetooth.bean.BleDevice
import com.example.mybluetooth.databinding.ItemBluetoothBinding

class BleDeviceAdapter(data: MutableList<BleDevice>? = null) :
    ViewBindingAdapter<ItemBluetoothBinding, BleDevice>(data) {
    override fun convert(holder: ViewBindingHolder<ItemBluetoothBinding>, item: BleDevice) {
        val binding = holder.vb
        binding.tvDeviceName.text = item.name
        binding.tvMacAddress.text = item.device.address
        binding.tvRssi.text = "${item.rssi} dBm"
    }
}
