package com.example.gyun_home.android_firebase

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_find_passwd.*

class FindPasswdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_passwd)

        btn_find_passwd.setOnClickListener {
            findPasswd()
        }
    }

    fun findPasswd(){
        FirebaseAuth.getInstance().sendPasswordResetEmail(editText_find_email.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this,"재설정 메일을 보냄", Toast.LENGTH_SHORT).show()
                    }else {
                        Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
    }
}
