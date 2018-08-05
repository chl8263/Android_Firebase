package com.example.gyun_home.android_firebase

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_storage.*
import java.io.ByteArrayOutputStream

class StorageActivity : AppCompatActivity() {

    var bitmap:Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)

        storage_imageView_local.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,1)
        }
        storage_button_fileUpload.setOnClickListener {
            if(bitmap != null) {
                uploadImage(bitmap!!)
            }
        }
        storage_button_delete.setOnClickListener {
            deleteImage()
        }
        storage_button_image_read.setOnClickListener {
            imageLoad()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            var imageUrl = data!!.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,imageUrl)
            storage_imageView_local.setImageBitmap(bitmap)
        }
    }

    fun uploadImage(bitmap : Bitmap){
        var baos = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos)
        var data = baos.toByteArray()
        FirebaseStorage.getInstance().reference.child("users").child(storage_editText_fileName.text.toString()).putBytes(data)
                .addOnCompleteListener {
                    task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,"업로드에 성고햇습니다",Toast.LENGTH_SHORT).show()
                        saveUrlToDatabase(task.result.downloadUrl.toString())   //업로드에 성공된 이미지 url 이 날라온당
                    }
                }
    }

    fun saveUrlToDatabase(url: String){

        var map = HashMap<String,Any>()
        map["profile_image_url"]= url
        FirebaseFirestore.getInstance().collection("users").document(storage_editText_documentid.text.toString()).update(map)

    }

    fun deleteImage(){

        FirebaseStorage.getInstance().reference.child("users").child(storage_editText_fileName.text.toString()).delete().addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                Toast.makeText(this,"파일삭제 완료 햇습니당",Toast.LENGTH_SHORT).show()
                var map = HashMap<String,Any>()
                map["profile_image_url"]= FieldValue.delete()   //스키마까지 제거해 준당
                FirebaseFirestore.getInstance().collection("users").document(storage_editText_documentid.text.toString()).update(map)
            }
        }
    }

    fun imageLoad(){

        FirebaseFirestore.getInstance().collection("users").document(storage_editText_documentid.text.toString()).get().addOnCompleteListener {
            task ->
            if(task.isSuccessful) {
                var userDTO = task.result.toObject(UserDTO::class.java)

                Picasso.get().load(userDTO!!.profile_image_url).into(storage_imageView_server)

            }
        }
    }
}
