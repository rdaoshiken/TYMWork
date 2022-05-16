package com.tymphay.tymwork.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.tymphay.tymwork.R
import com.tymphay.tymwork.adapter.ScanListAdapter
import com.tymphay.tymwork.bean.BleDevice
import kotlinx.android.synthetic.main.activity_main.*
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult

class MainActivity : AppCompatActivity() {

    //低功耗蓝牙适配器
    private lateinit var scanListAdapter: ScanListAdapter
    //蓝牙列表
    private var mList: MutableList<BleDevice> = ArrayList()
    //地址列表
    private var addressList: MutableList<String> = ArrayList()
    //当前是否扫描
    private var isScanning = false

    //注册开启蓝牙，需要注意在onCreate之前注册
    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) showMsg(if (defaultAdapter.isEnabled) "蓝牙已打开" else "蓝牙未打开")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //检查版本
        checkAndroidVersion()
        //初始化页面
        initView()
    }

    //检查Android版本
    private fun checkAndroidVersion(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //判断权限是否开启
            when (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)) {
                PackageManager.PERMISSION_GRANTED -> openBluetooth()    //权限已打开
                else -> requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)  //权限未打开,请求权限
            }
        }
    }

    //请求权限之后，用户选择的结果
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            1 -> when(grantResults){
                intArrayOf(PackageManager.PERMISSION_GRANTED) -> {
                    openBluetooth()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    //获取蓝牙适配器
    private var defaultAdapter = BluetoothAdapter.getDefaultAdapter()
    //打开蓝牙
    private fun openBluetooth() = defaultAdapter.let {
        //如果蓝牙未打开,则通过Intent去打开系统蓝牙
        if (it.isEnabled) showMsg("蓝牙已打开，可以开始扫描设备了") else activityResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }

    //Toast提示
    private fun showMsg(msg: String = "权限未通过") = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    //扫描结果回调
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val address=result.device.address
            val name=result.device.name ?:"Unknown"
            if (addressList.size==0){
                addressList.add(address)
                addDeviceList(BleDevice(result.device,result.rssi,name))
            }else{
                //检查之前所添加的设备地址是否存在当前地址列表
                if (!addressList.contains(address)){
                    addressList.add(address)
                    addDeviceList(BleDevice(result.device,result.rssi,name))
                }
            }
        }
    }

    private fun addDeviceList(bleDevice: BleDevice) {
        mList.add(bleDevice)
        //无设备UI展示
        lay_no_equipment.visibility = if (mList.size > 0) View.GONE else View.VISIBLE
        //刷新列表适配器
        scanListAdapter.notifyDataSetChanged()
    }

    //页面初始化
    private fun initView() {
        //适配器:
        scanListAdapter = ScanListAdapter(mList).apply {
            //设置列表的点击事件
            setOnItemClickListener { position ->
                stopScan()
                val device = mList[position].device
                //跳转页面,传递数据:
                //将MainActivity中点击的device传递到ConnectBluetoothActivity中
                startActivity(Intent(this@MainActivity,ConnectBluetoothActivity::class.java)
                    .putExtra("device",device))
            }
        }
        //RecyclerView:
        rv_device.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = scanListAdapter
        }
        //浮动按钮的点击事件：
        //扫描蓝牙
        fab_add.setOnClickListener { if (isScanning) stopScan() else scan() }
    }

    //开始扫描蓝牙
    private fun scan() {
        if (!defaultAdapter.isEnabled) {
            showMsg("蓝牙未打开");return
        }
        if (isScanning) {
            showMsg("正在扫描中...");return
        }
        isScanning = true
        addressList.clear()
        mList.clear()
        BluetoothLeScannerCompat.getScanner().startScan(scanCallback)
        progress_bar.visibility = View.VISIBLE
        fab_add.text = "扫描中"
    }

    //停止扫描蓝牙
    private fun stopScan() {
        if (!defaultAdapter.isEnabled) {
            showMsg("蓝牙未打开");return
        }
        if (isScanning) {
            isScanning = false
            BluetoothLeScannerCompat.getScanner().stopScan(scanCallback)
            progress_bar.visibility = View.INVISIBLE
            fab_add.text = "开始扫描"
        }
    }
}


