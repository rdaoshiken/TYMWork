package com.example.mybluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class MainActivity : AppCompatActivity() {

    //蓝牙适配器
    private  var bluetoothAdapter:BluetoothAdapter?=null
    //蓝牙展示列表的适配器
    private var mAdapter: DeviceAdapter?=null
    //蓝牙广播接收器
    private  var bluetoothReceiver:BluetoothReceiver?=null
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

    //初始化蓝牙配置
    private fun initBlueTooth() {
        var intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND) //获得扫描结果
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED) //绑定状态变化
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED) //开始扫描
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) //扫描结束
        bluetoothReceiver = BluetoothReceiver() //实例化广播接收器
        registerReceiver(bluetoothReceiver, intentFilter) //注册广播接收器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() //获取蓝牙适配器
    }

    //内部类
    //广播接收器,接收广播
    inner class BluetoothReceiver:BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            when (p1?.action) {
                //显示蓝牙设备
                BluetoothDevice.ACTION_FOUND -> showDeviceDtata(p0, p1)
                //当有蓝牙绑定状态发生改变时，刷新列表数据
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> mAdapter?.changeBondDevice()
                //开始扫描、显示加载图标
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> loading_lay.visibility = View.VISIBLE
                //结束扫描
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> loading_lay.visibility = View.GONE
                else -> Toast.makeText(this@MainActivity, "未知", Toast.LENGTH_LONG).show()
            }
        }
    }

    //显示蓝牙设备信息
    @SuppressLint("MissingPermission")
    private fun showDeviceDtata(p0: Context?, p1: Intent) {
        //获取已绑定的设备
        getBondedDevice()

        //获取周围蓝牙设备
        val device = p1.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        if (list.indexOf(device) == -1) { //防止重复添加
            if (device?.name != null) { //过滤掉设备名称为null的设备
                list.add(device)
            }
        }

        mAdapter = DeviceAdapter(list)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = mAdapter

        //item的点击事件
        mAdapter!!.onItemClick={ _, pos ->
            //点击时获取动态，如果已经配对过了就不需要再配对
            if (list[pos].bondState==BluetoothDevice.BOND_NONE){
                createOrRemoveBond(1,list[pos])   //开始匹配
            }else{
                showDialog("确定要取消配对吗？",DialogInterface.OnClickListener{dialog,which->
                    //取消配对
                    createOrRemoveBond(2,list[pos])  //取消匹配
                })
            }
        }
    }

    //创建或者取消匹配     i:  1匹配  2取消匹配     bluetoothDevice:设备
    private fun createOrRemoveBond(i: Int, bluetoothDevice: BluetoothDevice) {
        var method: Method? = null
        try {
            when (i) {
                1 -> {
                    method = BluetoothDevice::class.java.getMethod("createBond")
                    method.invoke(bluetoothDevice)
                }
                2 -> {
                    method = BluetoothDevice::class.java.getMethod("removeBond")
                    method.invoke(bluetoothDevice)
                    list.remove(bluetoothDevice) //清除列表中已经取消了配对的设备
                }
            }
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    //获取已绑定的设备
    @SuppressLint("MissingPermission")
    private fun getBondedDevice() {
        val pairedDevices = bluetoothAdapter!!.bondedDevices
        if (pairedDevices.size > 0) { //如果获取的结果大于0，则开始逐个解析
            for (device in pairedDevices) {
                if (list.indexOf(device) == -1) { //防止重复添加
                    if (device.name != null) { //过滤掉设备名称为null的设备
                        list.add(device)
                    }
                }
            }
        }
    }

    //扫描蓝牙设备
    @SuppressLint("MissingPermission")
    fun scanBluetooth(view: View) {
            if (bluetoothAdapter != null) { //若支持蓝牙
                if (bluetoothAdapter!!.isEnabled) { //打开
                    //开始扫描周围的蓝牙设备,如果扫描到蓝牙设备，通过广播接收器发送广播
                    if (mAdapter != null) { //当适配器不为空时，这时就说明已经有数据了，所以清除列表数据，再进行扫描
                        list.clear()
                        mAdapter!!.notifyDataSetChanged()
                    }
                    getBondedDevice()
                    bluetoothAdapter!!.startDiscovery()    //开始扫描
                } else { //未打开
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(intent, 1)   //打开蓝牙
                }
            } else {
                Toast.makeText(this,"你的设备不支持蓝牙",Toast.LENGTH_LONG).show()
            }
    }

    //在界面销毁的时候卸载广播接收器
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothReceiver)
    }

    //弹窗：     Title：弹窗的标题  onClickListener：按钮的点击事件
    private fun showDialog(Title: String, onClickListener: DialogInterface.OnClickListener) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(Title)
        builder.setMessage(Title)
        builder.setPositiveButton("确定") { dialog, which ->
            Log.e("Jater", "YES")
        }
        builder.setNegativeButton("取消") { dialog, which ->
            Log.e("Jater", "Cancel")
        }
        builder.create()
        builder.show()
    }
}


