package com.tymphay.tymwork.ui

import android.annotation.SuppressLint
import android.bluetooth.*
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tymphay.tymwork.R
import com.tymphay.tymwork.TymApplication
import com.tymphay.tymwork.adapter.ConnectDeviceAdapter
import com.tymphay.tymwork.bean.ConnectDevice
import kotlinx.android.synthetic.main.activity_connect_bluetooth.*
import kotlinx.android.synthetic.main.item_bluetooth.*
import kotlinx.android.synthetic.main.item_connect_result.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ConnectBluetoothActivity :AppCompatActivity() {

    //状态缓存
    private var stringBuffer= StringBuffer()
    //连接设备适配器
    var connectDeviceAdapter : ConnectDeviceAdapter? =null
    //Key:service name      Value:service uuid
    val uuids : HashMap<String,String> = HashMap<String,String>()
    lateinit var gatt: BluetoothGatt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_bluetooth)

        //service uuid:
        uuids.put("00001800-0000-1000-8000-00805f9b34fb","Generic Access Profile")
//        uuids.put("00001801-0000-1000-8000-00805f9b34fb","Generic Attribute Profile")
//        uuids.put("0000180a-0000-1000-8000-00805f9b34fb","Device Information")
//        uuids.put("0000180f-0000-1000-8000-00805f9b34fb","Battery Service")

        //页面初始化
        initView()
    }

    //蓝牙回调函数
    private val bluetoothGattCallback = object : BluetoothGattCallback(){
        //连接状态改变时回调
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e("status", "onConnectionStateChange: $status")
                state("连接失败")
                return
            }
            state(
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {   //连接成功
                        //获取已连接到的设备
                        val device = gatt?.device
                        //添加已连接的设备到列表中
                        //TymApplication.connectList?.add(ConnectDevice(device,device.name))
                        device?.let { ConnectDevice(it,device.name) }
                            ?.let { TymApplication.connectList?.add(it) }
                        //连接成功后，发现服务,触发onServicesDiscovered回调
                        gatt?.discoverServices()
                        bt_connect.text="断开连接"
                        "连接成功"
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> "断开连接"    //断开连接
                    else -> "onConnectionStateChange: $status"
                }
            )
        }

        //发现服务回调
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            state(
                if (status == BluetoothGatt.GATT_SUCCESS){
                    //gattServices用来存放所有获取到的服务
                    var gattServices: MutableList<BluetoothGattService> =
                        gatt?.services as MutableList<BluetoothGattService>

                    //将获取到的service添加到全局变量
                    for (i in 0 until gattServices.size){
                        TymApplication.gattServices.add(gattServices[i])
                    }

                    //获取Service Detail:
                    //遍历获取到的gattServices
                    for (gattService in  TymApplication.gattServices) {
                        var serviceUUID = gattService.uuid.toString()
                        Log.e("serviceUUID","$serviceUUID")
                        for (key in uuids.keys) {
                            if (serviceUUID.equals(key)) {
                                //服务的详细信息:
                                tv_service_name.text = "服务名称: ${uuids[key]}"
                                tv_service_uuid.text = "服务UUID: $serviceUUID"
                                return
                            }else{
                                tv_service_name.text = "服务名称: Unknown"
                                tv_service_uuid.text="服务UUID: $serviceUUID"
                            }
                        }
                    }
                    "发现服务"
                } else "未发现服务"
            )

        }
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
        if (device?.uuids == null)  tv_device_id.text= ("设备ID: None" ) //设备ID
        else tv_device_id.text= ("设备ID: "+device?.uuids.toString() ) //设备ID
        tv_device_name_1.text= ("设备名称: "+device?.name )   //设备名称
        tv_device_address.text=("MAC地址: "+device?.address )  //MAC地址

        //连接按钮的点击事件
        bt_connect.setOnClickListener {
            if (bt_connect.text == "连接"){
                //gatt连接,设置gatt回调
                gatt = device!!.connectGatt(this, false, bluetoothGattCallback)
            }else{
                gatt.disconnect()     //断开连接
            }
        }

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

    fun state(state: String)=runOnUiThread {
        stringBuffer.append(state).append("\n")
        tv_state.text = stringBuffer.toString()
    }

    //页面返回
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId== android.R.id.home){ onBackPressed();true } else  false

}