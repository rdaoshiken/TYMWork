package com.tymphay.tymwork.view

import android.annotation.SuppressLint
import android.bluetooth.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tymphay.tymwork.R
import com.tymphay.tymwork.TymApplication
import com.tymphay.tymwork.adapter.ConnectDeviceAdapter
import com.tymphay.tymwork.model.ConnectDevice
import kotlinx.android.synthetic.main.activity_connect_bluetooth.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ConnectBluetoothActivity :AppCompatActivity() {

    //状态缓存
    private var stringBuffer= StringBuffer()
    //连接设备适配器
    private var connectDeviceAdapter : ConnectDeviceAdapter? =null
    //Key:service name      Value:service uuid
    private val uuids : HashMap<String,String> = HashMap<String,String>()
    lateinit var gatt: BluetoothGatt

    @RequiresApi(Build.VERSION_CODES.N)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_bluetooth)

        //初始化UUID
        initUUID()
        //页面初始化
        initView()
    }

    //初始化UUID
    private fun initUUID(){
        //service uuid:
        uuids.put("00001800-0000-1000-8000-00805f9b34fb","Generic Access Profile")
    }

    //蓝牙回调函数
    private val bluetoothGattCallback = object : BluetoothGattCallback(){
        //连接状态改变时回调
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                state(getString(R.string.connect_fail))
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
                        bt_connect.text=getString(R.string.connect_close)
                        getString(R.string.connect_success)
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {   //断开连接
                        getString(R.string.connect_close)
                    }
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
                        for (key in uuids.keys) {
                            if (serviceUUID.equals(key)) {
                                //服务的详细信息:
                                tv_service_name.text = getString(R.string.service_name)+"${uuids[key]}"
                                tv_service_uuid.text = getString(R.string.service_uuid)+"$serviceUUID"
                                return
                            }else{
                                tv_service_name.text = getString(R.string.service_name)+getString(R.string.unknown)
                                tv_service_uuid.text= getString(R.string.service_uuid)+ "$serviceUUID"
                            }
                        }
                    }
                    getString(R.string.service_discovered)
                } else getString(R.string.service_covered)
            )
        }
    }

    //页面视图的初始化，同时接收传递过来的device
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    private fun initView(){
        supportActionBar?.apply {
            title = getString(R.string.title)
            setDisplayHomeAsUpEnabled(true)
        }

        //获取从MainActivity传递过来的设备
        val device = intent.getParcelableExtra<BluetoothDevice>(getString(R.string.device))

        //设备的详细信息:
        if (device?.uuids == null)  tv_detail_device_id.text= (getString(R.string.device_id_none) ) //设备ID
        else tv_detail_device_id.text= (getString(R.string.device_id)+device?.uuids.toString() ) //设备ID
        tv_detail_device_name.text= (getString(R.string.device_name)+device?.name )   //设备名称
        tv_detail_device_address.text=(getString(R.string.device_address)+device?.address )  //MAC地址

        //连接按钮的点击事件
        //连接
        bt_connect.setOnClickListener {
            if (bt_connect.text == getString(R.string.device_connect)){
                //gatt连接,设置gatt回调
                gatt = device?.connectGatt(this, false, bluetoothGattCallback) ?: gatt
                //断开连接
                bt_connect.setOnClickListener {
                    if(bt_connect.text == getString(R.string.connect_close)){
                        gatt.disconnect()   //断开连接
                        //将设备从已连接的列表中移除
                        TymApplication.connectList?.removeIf {
                            it.device.address == device?.address
                        }
                    }
                }
            }
        }

        //已连接设备的适配器
        connectDeviceAdapter = ConnectDeviceAdapter(TymApplication.connectList as ArrayList<ConnectDevice>?)
        //刷新数据
        connectDeviceAdapter?.notifyDataSetChanged()
        //RecyclerView:
        rv_device_connect.apply {
            layoutManager = LinearLayoutManager(this@ConnectBluetoothActivity)
            adapter = connectDeviceAdapter
        }
    }

    fun state(state: String)=runOnUiThread {
        stringBuffer.append(state).append(getString(R.string.line_feed))
        tv_state.text = stringBuffer.toString()
    }

    //页面返回
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (item.itemId== android.R.id.home){
            onBackPressed()
            true
        } else  false
}