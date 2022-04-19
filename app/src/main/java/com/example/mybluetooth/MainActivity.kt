package com.example.mybluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    //蓝牙广播接收器
    private var bluetoothReceiver: BluetoothReceiver? = null
    //蓝牙适配器
    private var bluetoothAdapter: BluetoothAdapter? = null
    //蓝牙设备适配器
    private var mAdapter: DeviceAdapter? = null
    //可变列表
    private var list: MutableList<BluetoothDevice> = mutableListOf()
    //请求码
    private val REQUEST_ENABLE_BLUETOOTH = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //检查版本
        checkVersion()
    }

    //检查Android版本
    private fun checkVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6.0或6.0以上
            permissionsRequest() //动态权限申请
        } else {
            //6.0以下
            initBlueTooth() //初始化蓝牙配置
        }
    }

    //动态权限申请
    private fun permissionsRequest() {
        val rxPermissions = RxPermissions(this)
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe {
                //it就代表这个请求的结果,即为权限请求的结果：同意true或者不同意false
                if (it) {
                    initBlueTooth()
                } else {
                    showMsg("权限未开启")
                }
            }
    }

    //初始化蓝牙
    private fun initBlueTooth() {
        var intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)  //获得扫描结果
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)  //绑定状态变化
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)  //扫描结果
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)  //扫描结束
        bluetoothReceiver = BluetoothReceiver() //实例化广播接收器
        registerReceiver(bluetoothReceiver, intentFilter) //注册广播接收器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() //获取蓝牙适配器
    }

    //显示提示信息
    private fun showMsg(llw: String) {
        Toast.makeText(this, llw, Toast.LENGTH_SHORT).show()
    }

    inner class BluetoothReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            when (p1?.action) {
                //显示蓝牙设备
                BluetoothDevice.ACTION_FOUND -> showDevicesData(p0, p1)
                //当有蓝牙绑定状态发生改变时，刷新列表数据
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> mAdapter?.changeBondDevice()
                //开始扫描
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> loading_lay.visibility = View.VISIBLE
                //停止扫描
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> loading_lay.visibility = View.GONE
                else -> showMsg("未知")
            }
        }
    }

    //显示蓝牙设备信息
    private fun showDevicesData(p0: Context?, p1: Intent) {
        //获取已绑定的设备
        getBondedDevice()

        //获取周围蓝牙设备
        val device = p1.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        if (list.indexOf(device)==-1){   //防止重复添加
            if(device.name!=null){   //过滤掉设备名称为null的设备
                list.add(device)
            }
            mAdapter= DeviceAdapter(R.layout.item_device_list,list)
            rv.layoutManager=LinearLayoutManager(p0)
            rv.adapter=mAdapter

            //item的点击事件
            mAdapter!!.setOnItemChildClickListener{_,_,position ->
            //点击时获取状态，如果已经配对过了就不需要再配对
                if(list[position].bondState==BluetoothDevice.BOND_NONE){
                    createOrRemoveBond(2,list[position])   //取消匹配
                }
            }

        }
    }

    //获取已绑定的设备
    private fun getBondedDevice() {
        val pairedDevices=bluetoothAdapter!!.bondedDevices
        if (pairedDevices.size>0){    //如果获得的结果大于0，则开始逐个解析
            for (device in pairedDevices){
                if (list.indexOf(device)==-1){   //防止重复添加
                    if (device.name!=null){   //过滤掉设备名称为null的设备
                            list.add(device)
                    }
                }
            }
        }
    }

    //创建或者取消匹配
    //type:1匹配  2取消匹配         device:设备
    private fun createOrRemoveBond(type:Int,device:BluetoothDevice) {
        var method:Method?=null
        try {
            when(type){
                1->{
                    method=BluetoothDevice::class.java.getMethod("createBond")
                    method.invoke(device)
                }
                2->{
                    method=BluetoothDevice::class.java.getMethod("removeBond")
                    method.invoke(device)
                    list.remove(device)   //清除列表中已经取消了配对的设备
                }
            }
        }catch (e: NoSuchMethodException){
            e.printStackTrace()
        }catch (e: InvocationTargetException){
            e.printStackTrace()
        }catch (e: IllegalAccessException){
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //卸载广播接收器
        unregisterReceiver(bluetoothReceiver)
    }

    //扫描蓝牙
    fun scanBluetooth(view: View) {
        if(bluetoothAdapter!=null){   //是否支持蓝牙
            if (bluetoothAdapter!!.isEnabled){   //打开
                //开始扫描周围的蓝牙设备，如果扫描到蓝牙设备，通过广播接收器发送广播
                if (mAdapter!=null){  //当适配器不为空时，说明已经有数据了，则清除列表，再进行扫描
                    list.clear()
                    mAdapter!!.notifyDataSetChanged()
                }
                bluetoothAdapter!!.startDiscovery()
            }else{   //未打开
                val intent=Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent,REQUEST_ENABLE_BLUETOOTH)
            }
    }else{
        showMsg("你的设备不支持蓝牙")
        }
   }
}