package com.example.mybluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_device_list.view.*

//蓝牙设备列表适配器
class DeviceAdapter(var data: MutableList<BluetoothDevice>?): RecyclerView.Adapter<DeviceViewHolder>(){

    lateinit var onItemClick:(view:View,pos:Int) ->Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_device_list,parent,false))
    }

    //数据处理
    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.nameText?.text=data!![position].name
     //   holder.stateText?.text=data!![position].name

        //蓝牙设备绑定状态判断
        holder.stateText?.text=when(data!![position].bondState){
            10->"未配对"
            11->"正在配对中"
            12->"已配对"
            else->"未配对"
        }

        //添加蓝牙设备点击事件
        holder.itemView.setOnClickListener(){
            onItemClick.invoke(it,position)
        }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    //刷新适配器
    fun changeBondDevice() {
        notifyDataSetChanged()
    }
}

class DeviceViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var nameText:TextView?=itemView.tv_name     //设备名称
        var stateText:TextView?=itemView.tv_bond_state    //设备绑定状态
}
