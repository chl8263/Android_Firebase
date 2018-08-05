package com.example.gyun_home.android_firebase

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        button_change_email.setOnClickListener {
            var editTextNewId = EditText(this)
            var alertDialog = AlertDialog.Builder(this)
            alertDialog.setView(editTextNewId)
            alertDialog.setTitle("아이디 변경")
            alertDialog.setMessage("변경하고 싶은아이디를 입력해 주세요")
            alertDialog.setPositiveButton("확인",{dialog, which ->  changeId(editTextNewId.text.toString())})
            alertDialog.setNegativeButton("취소",{dialog, which ->  })
            alertDialog.show()
        }


        button_check_id.setOnClickListener {
            sendEmailVerification()
        }

        button_logOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()    // 로그인 세션이 null 이 된다
            finish()
        }

        button_changePasswd.setOnClickListener {
            var editTextNewPassed = EditText(this)
            editTextNewPassed.transformationMethod = PasswordTransformationMethod.getInstance() // 비번 비밀스럽게 만듦
            var alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("패스워드 변경")
            alertDialog.setMessage("변경하고 싶은 패스워드를 입력하세요")
            alertDialog.setView(editTextNewPassed)
            alertDialog.setPositiveButton("변경",{dialog, which -> changePasswd(editTextNewPassed.text.toString()) })
            alertDialog.setNegativeButton("취소",{dialog, which ->  dialog.dismiss() })
            alertDialog.show()
        }

        button_id_delete.setOnClickListener {
            var alertDialog = android.app.AlertDialog.Builder(this)
            alertDialog.setTitle("삭제?")
            alertDialog.setMessage("아이디를 삭제 ㄱ ?")
            alertDialog.setPositiveButton("확인", { dialog, which -> deleteId() })
            alertDialog.setNegativeButton("취소", {dialog, which -> dialog.dismiss() })
            alertDialog.show()
        }

        button_database.setOnClickListener {
            startActivity(Intent(this,DatabaseActivity::class.java))
        }
    }

    fun changePasswd (passwd : String){
        FirebaseAuth.getInstance().currentUser!!.updatePassword(passwd).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this,"비번변경 성공", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendEmailVerification (){   // 현재 이메일 아이디가 유효한지 이메일 보내는 코드

        if(FirebaseAuth.getInstance().currentUser!!.isEmailVerified) {    //이메일을 직접 열어 인증을 한경우
            Toast.makeText(this,"이메일 인증이 완료됨  ", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().currentUser!!.sendEmailVerification().addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this,"확인이메일 보냄 ", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun changeId(email : String){
        FirebaseAuth.getInstance().currentUser!!.updateEmail(email)
                .addOnCompleteListener {task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,"이메일 변경 완료 ", Toast.LENGTH_SHORT).show()
                    }else {
                        Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }

    }
    fun deleteId(){
        FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener {task ->
            if(task.isSuccessful){
                FirebaseAuth.getInstance().signOut()

                Toast.makeText(this,"아이디 삭제 완료 ", Toast.LENGTH_SHORT).show()
                finish()
            }else {
                Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }
}
