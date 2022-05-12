package com.tymphay.tymwork.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tymphay.tymwork.TymApplication
import com.tymphay.tymwork.adapter.ConnectDeviceAdapter
import com.tymphay.tymwork.bean.ConnectDevice
import com.tymphay.tymwork.callback.BleCallback
import com.tymphay.tymwork.databinding.ActivityConnectBluetoothBinding
import kotlinx.android.synthetic.main.activity_connect_bluetooth.*

class ConnectBluetoothActivity :AppCompatActivity(),BleCallback.UiCallback {

    //绑定视图
    private lateinit var binding: ActivityConnectBluetoothBinding
    //Gatt
    private lateinit var gatt : BluetoothGatt
    //Ble回调
    private val bleCallback= BleCallback()
    //状态缓存
    private var stringBuffer= StringBuffer()
    //连接设备适配器
    var connectDeviceAdapter : ConnectDeviceAdapter? =null

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
        //获取从MainActivity传递过来的设备
        val device = intent.getParcelableExtra<BluetoothDevice>("device")

        //设备的详细信息:
        if (device?.uuids.toString() == null)  binding.tvDeviceId.text= ("设备ID: None" ) //设备ID
                else binding.tvDeviceId.text= ("设备ID: "+device?.uuids.toString() ) //设备ID
        binding.tvDeviceName1.text= ("设备名称: "+device?.name )   //设备名称
        binding.tvDeviceAddress.text=("MAC地址: "+device?.address )  //MAC地址

            //gatt连接,设置gatt回调
            gatt = device!!.connectGatt(this, false, bleCallback)

        //连接按钮的点击事件
        bt_connect.setOnClickListener {

        }

        //服务的详细信息:
        binding.tvServiceName.text=("服务名称: ")
        binding.tvServiceUuid.text=("服务UUID: " )

        //Ble状态页面UI回调
        bleCallback.setUiCallback(this)

        //已连接设备的适配器
        connectDeviceAdapter = ConnectDeviceAdapter(TymApplication.connectList as ArrayList<ConnectDevice>?)
        //刷新数据
        connectDeviceAdapter!!.notifyDataSetChanged()
        //RecyclerView:
        rv_device_connect.apply {
            layoutManager = LinearLayoutManager(this@ConnectBluetoothActivity)
            adapter = connectDeviceAdapter
        }
    }

    override fun state(state: String)=runOnUiThread {
        stringBuffer.append(state).append("\n")
        binding.tvState.text = stringBuffer.toString()
    }

    //页面返回
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId== android.R.id.home){ onBackPressed();true } else  false

}