package com.tymphay.tymwork

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tymphay.tymwork.model.OperatorNumber
import kotlinx.android.synthetic.main.result_item.view.*

class RecyclerViewAdapter : RecyclerView.Adapter<ExamViewHolder>() {

    var list: List<OperatorNumber> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()  //刷新数据
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        return ExamViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.result_item, parent, false)
        )
    }

    //数据处理
    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        val operatorNumber = list[position]
        holder.mText.text =
            "${operatorNumber.num1} ${operatorNumber.operator} ${operatorNumber.num2} = ${operatorNumber.result}"
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class ExamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mText: TextView = itemView.result_item
}