package com.tymphay.tymwork.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tymphay.tymwork.callback.BleCallback
import com.tymphay.tymwork.databinding.ActivityDeviceDetailBinding
import kotlinx.android.synthetic.main.activity_device_detail.*

class DeviceDetailActivity :AppCompatActivity(), BleCallback.UiCallback{

    //绑定视图
    private lateinit var binding: ActivityDeviceDetailBinding
    //Ble回调
    private val bleCallback= BleCallback()
    //状态缓存
    private var stringBuffer= StringBuffer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDeviceDetailBinding.inflate(layoutInflater)  //绑定视图
        setContentView(binding.root)

        //页面初始化
        initView()
    }

    //页面视图的初始化，同时接收传递过来的device
    @SuppressLint("MissingPermission")
    private fun initView() {
        //获取从上个页面传递过来的设备
        val device = intent.getParcelableExtra<BluetoothDevice>("device")
        //将详细信息显示在UI上:
        //Device
        binding.tvDeviceId.text= device?.uuids.toString()  //设备ID
        binding.tvDeviceName1.text= device?.name    //设备名称
        binding.tvDeviceAddress.text=device?.address   //Mac地址
        //Service
        //binding.tvServiceUuid.text=device.

    }

    override fun state(state: String) {
        stringBuffer.append(state).append("\n")
       // binding.tvState.text = stringBuffer.toString()
    }
}