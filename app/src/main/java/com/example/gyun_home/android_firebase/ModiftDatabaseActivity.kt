package com.example.gyun_home.android_firebase

import android.app.AlertDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_modift_database.*

class ModiftDatabaseActivity : AppCompatActivity() {

    var getList = ArrayList<UserDTO>()
    var getkeyList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modift_database)

        modify_database_recyclerview.adapter = ModifyRecyclerCiewAdapter(getList)
        modify_database_recyclerview.layoutManager = LinearLayoutManager(this)

        FirebaseFirestore.getInstance().collection("users").addSnapshotListener{
            querySnapshot, firebaseFirestoreException ->

            getList.clear()
            getkeyList.clear()
            for(item in querySnapshot!!.documents){
                getList.add(item.toObject(UserDTO::class.java)!!)
                getkeyList.add(item.id)
            }

            modify_database_recyclerview.adapter.notifyDataSetChanged()
        }
    }

    inner class ModifyRecyclerCiewAdapter(initList: ArrayList<UserDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            customViewHolder.itemView.setOnClickListener {
                modiftitme(position)
            }
            customViewHolder.itemView.setOnLongClickListener {
                deleteItem(position)
                true
            }
        }

        inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view) {
            var textview_name = view!!.findViewById<TextView>(R.id.textView_name)
            var textview_age = view!!.findViewById<TextView>(R.id.textView_age)
            var textview_city = view!!.findViewById<TextView>(R.id.textView_city)
        }

        fun deleteItem(position : Int) : Unit{
            var alertDialog = AlertDialog.Builder(this@ModiftDatabaseActivity)
                    .setTitle("회원정보 삭제")
                    .setMessage("회원정보를 삭제 하시겠습니까?")
                    .setPositiveButton("확인",{dialog, which ->

                        FirebaseFirestore.getInstance().collection("users").document(getkeyList[position]).delete()

                    })
                    .setNegativeButton("취소",{dialog, which ->  })
            alertDialog.show()
        }


        fun modiftitme(position: Int){

            var inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            var view = inflater.inflate(R.layout.dialog_modify,null)

            var editText_name = view.findViewById<EditText>(R.id.dialog_editText_name)
            var editText_age = view.findViewById<EditText>(R.id.dialog_editText_age)
            var editText_city = view.findViewById<EditText>(R.id.dialog_editText_city)

            var alertDialog = AlertDialog.Builder(this@ModiftDatabaseActivity)
                    .setTitle("회원정보 수정")
                    .setMessage("회원정보 수정을 할 값을 입력해 주세요")
                    .setView(view)
                    .setPositiveButton("확인",{dialog, which ->
                        var map = HashMap<String , Any>()
                        map["name"] = editText_name.text.toString()
                        map["age"] = editText_age.text.toString().toInt()
                        map["city"] = editText_city.text.toString()
                        map["profile_image_url"] = "스토리지 시간에 입력할 예정"

                        FirebaseFirestore.getInstance().collection("users").document(getkeyList[position]).update(map)

                    })
                    .setNegativeButton("취소",{dialog, which ->  })

            alertDialog.show()

        }
    }



}
