package com.tymphay.tymwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tymphay.tymwork.R
import com.tymphay.tymwork.model.ConnectDevice
import com.tymphay.tymwork.view.DeviceViewHolder

//显示所有已连接设备适配器
class ConnectDeviceAdapter (var list: ArrayList<ConnectDevice>?): RecyclerView.Adapter<DeviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_connect_result,parent,false))
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        //获取蓝牙设备名称
        holder.deviceName?.text= list?.get(position)?.name
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
}


