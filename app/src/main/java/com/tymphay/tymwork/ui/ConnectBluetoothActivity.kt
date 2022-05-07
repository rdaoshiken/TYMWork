package com.tymphay.tymwork.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.tymphay.tymwork.callback.BleCallback
import com.tymphay.tymwork.databinding.ActivityConnectBluetoothBinding
import kotlinx.android.synthetic.main.device_detail.*

class ConnectBluetoothActivity :AppCompatActivity(),BleCallback.UiCallback {

    //绑定视图
    private lateinit var binding: ActivityConnectBluetoothBinding
    //Gatt
    private lateinit var gatt: BluetoothGatt
    //Ble回调
    private val bleCallback=BleCallback()
    //状态缓存
    private var stringBuffer= StringBuffer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityConnectBluetoothBinding.inflate(layoutInflater)  //绑定视图
        setContentView(binding.root)

        //页面初始化
        initView()
    }

    //页面视图的初始化，同时接收传递过来的device
    @SuppressLint("MissingPermission")
    private fun initView(){
        supportActionBar?.apply {
            title = "Connection"
            setDisplayHomeAsUpEnabled(true)
        }
        //获取从上个页面传递过来的设备
        val device = intent.getParcelableExtra<BluetoothDevice>("device")
        //将设备详细信息显示在UI上
        Log.v("Detail", "${device?.name}")
        tv_device_name_1.setText("设备名称：${device?.name}")
        tv_device_address.setText("设备地址：${device?.address}")
        //gatt连接,设置gatt回调
        gatt = device!!.connectGatt(this, false, bleCallback)
        //Ble状态页面UI回调
        bleCallback.setUiCallback(this)
    }

    override fun state(state: String)=runOnUiThread {
        stringBuffer.append(state).append("\n")
        binding.tvState.text = stringBuffer.toString()
    }

    //页面返回
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId== android.R.id.home){ onBackPressed();true }else  false

}