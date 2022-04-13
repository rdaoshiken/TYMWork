package com.tymphay.tymwork.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tymphay.tymwork.R
import com.tymphay.tymwork.RecyclerViewAdapter
import com.tymphay.tymwork.viewmodel.OperatorViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_navbar.*


class MainActivity : AppCompatActivity() {

    private lateinit var operatorViewModel: OperatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        operatorViewModel = ViewModelProvider(this)[OperatorViewModel::class.java]

        //Fragment:
        //隐藏标题栏
        supportActionBar?.hide()

        bt_add.setOnClickListener{
            operatorViewModel.add(et_input1.text.toString().toInt(),et_input2.text.toString().toInt())
            replace(AddFragment())
        }

        bt_sub.setOnClickListener{
            operatorViewModel.subtraction(et_input1.text.toString().toInt(),et_input2.text.toString().toInt())
            replace(SubtractionFragment())
        }

        bt_mul.setOnClickListener{
            operatorViewModel.multiply(et_input1.text.toString().toInt(),et_input2.text.toString().toInt())
            replace(MultiplyFragment())
        }

        bt_div.setOnClickListener{
            operatorViewModel.divide(et_input1.text.toString().toInt(),et_input2.text.toString().toInt())
            replace(DivideFragment())
        }
    }

    private fun replace(fragment: Fragment){
        val fragmentManager=supportFragmentManager
        val transaction=fragmentManager.beginTransaction()
        transaction.replace(R.id.content,fragment)
        transaction.commit()
    }
}