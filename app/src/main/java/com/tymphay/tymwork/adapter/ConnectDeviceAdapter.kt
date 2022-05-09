package com.tymphay.tymwork.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tymphay.tymwork.R
import com.tymphay.tymwork.bean.ConnectDevice
import kotlinx.android.synthetic.main.item_connect_result.view.*

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
        return list!!.size
    }
}

class DeviceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    //已连接设备名称
    var deviceName: TextView? = itemView.connect_result_name
}

