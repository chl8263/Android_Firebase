package com.example.gyun_home.android_firebase

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_database_create.*

class DatabaseCreateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_create)
        button_database_create_input.setOnClickListener {
            createData()
        }
    }

    fun createData() {
        var userDTO = UserDTO(editText_database_name.text.toString(), editText_database_age.text.toString().toInt(), editText_database_city.text.toString())

        FirebaseFirestore.getInstance().collection("users").document().set(userDTO).addOnCompleteListener {      //document 값을 없애줘야 엎어쓰지 않고 게속 만든다.
            Toast.makeText(this, "데이터 베이스 만들기 성공", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {exception->
            Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
