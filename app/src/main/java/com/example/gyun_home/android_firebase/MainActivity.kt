package com.example.gyun_home.android_firebase

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var authStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //로그인 세션을 관리하는 부분임  , 이메일 , 페이스북 등등 모든 로그인 을 authStateListener 로 관리해야 깔끔하다
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            var user = firebaseAuth.currentUser
            if (user != null) {   // 파이어 베이스 로그인이 되었을 경우
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }

        //구글 로그인 옵션
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        // 구글 로그인 클래스 만듬
        var googlesignInClient = GoogleSignIn.getClient(this, gso)

        button_signUp.setOnClickListener {
            createEmailId()
        }
        button_login.setOnClickListener {
            loginId()
        }
        button_find_passwd.setOnClickListener {
            startActivity(Intent(this, FindPasswdActivity::class.java))
        }

        btn_google_login.setOnClickListener {
            var signInIntent = googlesignInClient.signInIntent
            startActivityForResult(signInIntent, 1)
        }

    }

    fun createEmailId() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(editText_id.text.toString(), editText3_passwd.text.toString())
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

    }

    fun loginId() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(editText_id.text.toString(), editText3_passwd.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
    }

    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener!!)
    }

    override fun onPause() {
        super.onPause()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener!!)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            var task = GoogleSignIn.getSignedInAccountFromIntent(data)  //구글 로그인에 성공 햇을때 넘어오는 토큰 값을 가지고 있는 task
            var account = task.getResult(ApiException::class.java)  //apiExecption 으로 캐스팅
            var credential = GoogleAuthProvider.getCredential(account.idToken, null) // 구글 로그인에 성공했다는 인증서를 만들어줌
            FirebaseAuth.getInstance().signInWithCredential(credential) //이것을 파이어 베이스에넘겨주는 것이다.
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, " 구글아이디 연동이 성공 했습니당 ", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }
}
