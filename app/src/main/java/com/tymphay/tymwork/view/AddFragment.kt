package com.tymphay.tymwork.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tymphay.tymwork.R
import com.tymphay.tymwork.RecyclerViewAdapter
import com.tymphay.tymwork.model.OperatorNumber
import com.tymphay.tymwork.viewmodel.OperatorViewModel

class AddFragment : Fragment() {

    var output = ""
    private lateinit var operatorViewModel: OperatorViewModel
    private lateinit var operatorNumber: OperatorNumber

    private var arrList: ArrayList<OperatorViewModel> = ArrayList()

    private lateinit var recyclerView: RecyclerView

    private val adapter by lazy { RecyclerViewAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        operatorViewModel = ViewModelProvider(requireActivity()).get(OperatorViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        //Livedata:
//        //通过ViewModelProvider创建operatorViewModel
//        ViewModelProvider(this)
//        operatorViewModel= ViewModelProvider(requireActivity()).get(OperatorViewModel::class.java)
//        result_item.text=operatorViewModel.output .toString()

        return inflater.inflate(R.layout.add_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.add_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        //设置观察
        operatorViewModel.output.observe(viewLifecycleOwner) {
            adapter.list = it.filter { operatorNumber->
                operatorNumber.operator == "+"
            }
        }
    }

    private fun initResult() {
//        operatorNumber=OperatorNumber(0,0)
//        arrList.add(OperatorViewModel(operatorNumber ))
    }
}