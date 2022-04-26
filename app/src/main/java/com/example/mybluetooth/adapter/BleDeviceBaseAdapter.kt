package com.example.mybluetooth.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.mybluetooth.bean.BleDevice

class BleDeviceBaseAdapter(layoutResId: Int, data: MutableList<BleDevice>?) :
    BaseQuickAdapter<BleDevice, BaseViewHolder>(layoutResId, data) {

    override fun convert(holder: BaseViewHolder, item: BleDevice) {

    }
}
