package com.tymphay.tymwork.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tymphay.tymwork.R
import com.tymphay.tymwork.model.BleDevice
import kotlinx.android.synthetic.main.item_bluetooth.view.*

//扫描列表的适配器
class ScanListAdapter (var list: MutableList<BleDevice>?): RecyclerView.Adapter<DeviceViewHolderScan>() {

    //列表的点击事件
    lateinit var onItemClick : (Int) -> Unit

    fun setOnItemClickListener(onItemClick : (Int) -> Unit){
        this.onItemClick=onItemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolderScan {
        return DeviceViewHolderScan(LayoutInflater.from(parent.context).inflate(R.layout.item_bluetooth,parent,false))
    }

    override fun onBindViewHolder(holder: DeviceViewHolderScan, position: Int) {
        //获取蓝牙设备名称
        holder.deviceName?.text= list?.get(position)?.name
        //获取蓝牙设备地址
        holder.deviceAdress?.text=list?.get(position)?.device?.address
        //获取蓝牙设备信号强度
        holder.deviceRssi?.text=list?.get(position)?.rssi.toString()
        //获取上次扫描的时间
        holder.scanTime?.text=list?.get(position)?.scanTime

        //item的点击事件的处理
        holder.itemView.setOnClickListener {
            onItemClick.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
}

class DeviceViewHolderScan(itemView: View): RecyclerView.ViewHolder(itemView) {
    //已扫描设备名称
    var deviceName: TextView? = itemView.tv_device_name
    //已扫描设备MAC地址
    var deviceAdress: TextView? = itemView.tv_mac_address
    //已扫描设备的信号强度
    var deviceRssi: TextView? = itemView.tv_rssi
    //上次扫描的时间
    var scanTime: TextView? = itemView.tv_scan_time
}

