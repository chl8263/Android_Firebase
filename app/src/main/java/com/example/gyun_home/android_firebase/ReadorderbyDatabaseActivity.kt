package com.example.gyun_home.android_firebase

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_readorderby_database.*
import java.util.*
import kotlin.collections.ArrayList

class ReadorderbyDatabaseActivity : AppCompatActivity() {

    var direction: Query.Direction? = null
    var startAge: String? = null
    var endAge: String? = null
    var limit: String? = null
    var getList: ArrayList<UserDTO> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_readorderby_database)

        read_orderby_database_spiner_direction.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var selectString = parent!!.getItemAtPosition(position) as String
                if (selectString == "ASC") {
                    direction = Query.Direction.ASCENDING

                } else {
                    direction = Query.Direction.DESCENDING
                }
            }

        }
        read_orderby_database_spiner_age_start.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                startAge = parent!!.getItemAtPosition(position) as String
            }

        }
        read_orderby_database_spiner_age_end.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                endAge = parent!!.getItemAtPosition(position) as String
            }

        }
        read_orderby_database_spiner_limit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                limit = parent!!.getItemAtPosition(position) as String
            }

        }
    }

    fun getListFromFireStore() {
        FirebaseFirestore.getInstance().collection("users")
                .orderBy("age", direction!!)
                .startAt(startAge!!.toInt())
                .endAt(endAge!!.toInt())
                .limit(limit!!.toLong())
                .get()
                .addOnSuccessListener { querySnapshot ->
                    getList.clear()
                    for (item in querySnapshot.documents) {
                        var userDTO = item.toObject(UserDTO::class.java)
                        getList.add(userDTO!!)
                    }
                    read_orderby_database_recyclerView.adapter.notifyDataSetChanged()
                }
    }
}
