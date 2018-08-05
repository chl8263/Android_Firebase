package com.example.gyun_home.android_firebase

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_read_data_base.*

class ReadDataBaseActivity : AppCompatActivity() {

    var realTimeArrayList = arrayListOf<UserDTO>()

    var realTimeKeyArrayList = arrayListOf<String>()

    var getarrayList = arrayListOf<UserDTO>()

    var city: String? = null

    var age: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_data_base)

        var userDTO = UserDTO("원균", 30, "의정부")

        getarrayList.add(userDTO)
        getarrayList.add(userDTO)

        recyclerView_read_database.adapter = ReadRecyclerCiewAdapter(getarrayList)
        recyclerView_read_database.layoutManager = LinearLayoutManager(this)

        FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener { querySnapshot ->
            //동기방식
            for (item in querySnapshot.documents) {   //document 로 받으면 문서 하나씩 들어가서 본다
                var userDTO = item.toObject(UserDTO::class.java)
                getarrayList.add(userDTO!!)
            }
            recyclerView_read_database.adapter.notifyDataSetChanged()

        }


        recyclerView_read_database_realtime.adapter = ReadRecyclerCiewAdapter(realTimeArrayList)
        recyclerView_read_database_realtime.layoutManager = LinearLayoutManager(this)

        /*FirebaseFirestore.getInstance().collection("users").addSnapshotListener { querySnapshot, firebaseFirestoreException ->  //비동기 방식
            realTimeArrayList.clear()
            for(item in querySnapshot!!.documents){   //document 로 받으면 문서 하나씩 들어가서 본다
                var userDTO = item.toObject(UserDTO::class.java)
                realTimeArrayList.add(userDTO!!)
            }
            recyclerView_read_database_realtime.adapter.notifyDataSetChanged()    //메모리 누수 방지

        }*/

        FirebaseFirestore.getInstance().collection("users").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            //비동기 방식

            for (item in querySnapshot!!.documentChanges) {   //document 로 받으면 문서 하나씩 들어가서 본다
                when (item.type) {       // 각의 타입을 예외처리하여 리소스 최소화
                    DocumentChange.Type.ADDED -> {
                        realTimeArrayList.add(item.document.toObject(userDTO::class.java))
                        realTimeKeyArrayList.add(item.document.id)

                    }
                    DocumentChange.Type.MODIFIED -> modeifyItem(item.document.id, item.document.toObject(UserDTO::class.java))
                    DocumentChange.Type.REMOVED -> deleteId(item.document.id)


                }
            }
            recyclerView_read_database_realtime.adapter.notifyDataSetChanged()    //메모리 누수 방지

        }
        read_database_activity_spiner_age.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                age = parent!!.getItemAtPosition(position) as String
                listBySpiner()
            }

        }
    }

    fun listBySpiner() {
        if (city != null && age != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .whereEqualTo("city", city)
                    .whereGreaterThan("age", age!!.toInt())
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        getarrayList.clear()
                        for (item in querySnapshot.documents) {
                            var userDTO = item.toObject(UserDTO::class.java)
                            getarrayList.add(userDTO!!)
                        }
                        recyclerView_read_database.adapter.notifyDataSetChanged()
                    }.addOnFailureListener { exception ->
                        Log.e("execption",exception.toString())
                    }

        }

        read_database_activity_spiner_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                city = parent!!.getItemAtPosition(position) as String
                listBySpiner()
            }

        }
    }

    fun modeifyItem(modifyItem: String, userDTO: UserDTO) {
        for ((position, item) in realTimeKeyArrayList.withIndex()) {
            if (item == modifyItem) {
                realTimeArrayList[position] = userDTO
            }
        }
    }

    fun deleteId(deleteKey: String) {
        for ((position, item) in realTimeKeyArrayList.withIndex())
            if (deleteKey == item) {
                realTimeKeyArrayList.removeAt(position)
            }

    }


    class ReadRecyclerCiewAdapter(initList: ArrayList<UserDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var list: ArrayList<UserDTO> = initList

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_recyclerview, parent, false)
            return CustomViewHolder(view)
        }


        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            var customViewHolder = holder as CustomViewHolder
            customViewHolder.textview_name.text = list!!.get(position).name
            customViewHolder.textview_age.text = list!!.get(position).age.toString()
            customViewHolder.textview_city.text = list!!.get(position).city
        }

        class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view) {
            var textview_name = view!!.findViewById<TextView>(R.id.textView_name)
            var textview_age = view!!.findViewById<TextView>(R.id.textView_age)
            var textview_city = view!!.findViewById<TextView>(R.id.textView_city)
        }

    }
}
