package com.example.mybluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //蓝牙适配器
    private lateinit var bluetoothAdapter:BluetoothAdapter
    //蓝牙展示列表的适配器
    private lateinit var mAdapter: DeviceAdapter
    //蓝牙广播接收器
    private lateinit var bluetoothReceiver:BluetoothReceiver
    //展示列表
    private var list:MutableList<BluetoothDevice> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //检查版本
        checkVersion()

    }

    //检查Android版本
    private fun checkVersion(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6.0或6.0以上
            permissionsRequest() //动态权限申请
        } else {
            //6.0以下
            initBlueTooth() //初始化蓝牙配置
        }
    }

    //权限申请
    private fun permissionsRequest() {
        val rxPermissions = RxPermissions(this)
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe {
                if (it) {
                    initBlueTooth()
                } else {
                    Toast.makeText(this, "权限未开启", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun initBlueTooth() {
        var intentFilter=IntentFilter()
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND) //获得扫描结果
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)  //绑定状态变化
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)  //开始扫描
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) //扫描结束
        //实例化广播接收器
        bluetoothReceiver=BluetoothReceiver()
    }

    //内部类
    //广播接收器
    inner class BluetoothReceiver:BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            when (p1?.action) {
                //显示蓝牙设备
                BluetoothDevice.ACTION_FOUND -> showDeviceDtata(p0, p1)
                //当有蓝牙绑定状态发生改变时，刷新列表数据
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> mAdapter?.changeBondDevice()
                //开始扫描
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> loading_lay.visibility = View.VISIBLE
                //结束扫描
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> loading_lay.visibility = View.GONE
                else -> Toast.makeText(this@MainActivity, "未知", Toast.LENGTH_LONG).show()
            }
        }
    }

    //显示蓝牙设备信息
    private fun showDeviceDtata(p0: Context?, p1: Intent) {
        //获取已绑定的设备
        getBondedDevice()

        //获取周围蓝牙设备
        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        if (list.indexOf(device) == -1) { //防止重复添加
            if (device.name != null) { //过滤掉设备名称为null的设备
                list.add(device)
            }
        }
        mAdapter = DeviceAdapter( list)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = mAdapter
        }

    //获取已绑定的设备
    private fun getBondedDevice() {

    }


}


