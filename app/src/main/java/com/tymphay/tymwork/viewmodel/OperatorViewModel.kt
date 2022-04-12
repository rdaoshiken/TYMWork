package com.tymphay.tymwork.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tymphay.tymwork.model.OperatorNumber

class OperatorViewModel : ViewModel() {

    var output = MutableLiveData<List<OperatorNumber>>()
    private val result = mutableListOf<OperatorNumber>()

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
}