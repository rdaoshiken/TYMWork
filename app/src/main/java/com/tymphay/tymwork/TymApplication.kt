package com.tymphay.tymwork

import android.app.Application
import android.content.Context
import com.tymphay.tymwork.bean.ConnectDevice

//维护应用全局状态的基类
//app的入口
class TymApplication: Application() {
    companion object {

        var context : Application? =null

        //连接设备列表
        var connectList : MutableList<ConnectDevice>? =ArrayList()

        fun getContext():Context {
            return context!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        context=this
    }
}