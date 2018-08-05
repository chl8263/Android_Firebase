package com.example.gyun_home.android_firebase

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_database.*

class DatabaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)

        button_database_create.setOnClickListener {
            startActivity(Intent(this,DatabaseCreateActivity::class.java))
        }
        button_database_read.setOnClickListener {
            startActivity(Intent(this, ReadDataBaseActivity::class.java))
        }
        button_database_update_delete.setOnClickListener {
            startActivity(Intent(this,ModiftDatabaseActivity::class.java))
        }
    }
}
