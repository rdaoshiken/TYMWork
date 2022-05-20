package com.tymphay.tymwork.view

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_connect_result.view.*

class DeviceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    //已连接设备名称
    var deviceName: TextView? = itemView.connect_result_name
}
