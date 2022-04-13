package com.tymphay.tymwork.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tymphay.tymwork.model.OperatorNumber

class OperatorViewModel : ViewModel() {

    //LiveData:将OperatorViewModel的数据传给Fragment
    var output = MutableLiveData<List<OperatorNumber>>()
    //存放历史记录
    private val result = mutableListOf<OperatorNumber>()

    //加法
    fun add(num1: Int, num2: Int) {
        result.add(OperatorNumber(num1, num2, num1 + num2, "+"))
        output.value = result
    }

    //减法
    fun subtraction(num1: Int, num2: Int) {
        result.add(OperatorNumber(num1, num2, num1- num2, "-"))
        output.value = result
    }

    //乘法
    fun multiply(num1: Int, num2: Int) {
        result.add(OperatorNumber(num1, num2, num1 * num2, "*"))
        output.value = result
    }

    //除法
    fun divide(num1: Int, num2: Int) {
        result.add(OperatorNumber(num1, num2, num1 / num2, "÷"))
        output.value = result
    }
}