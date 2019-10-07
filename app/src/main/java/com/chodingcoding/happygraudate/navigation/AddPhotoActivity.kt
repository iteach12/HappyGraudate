package com.chodingcoding.happygraudate.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.chodingcoding.happygraudate.MainActivity


import com.chodingcoding.happygraudate.R
import com.chodingcoding.happygraudate.navigation.module.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

import kotlinx.android.synthetic.main.activity_add_photo.*
import kotlinx.android.synthetic.main.activity_add_photo.view.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage:FirebaseStorage? = null
    private var auth:FirebaseAuth? = null
    var photoUri: Uri? = null
    var firestore:FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        storage = FirebaseStorage.getInstance("gs://happygraudate.appspot.com")
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        loading_lottie.setAnimation("loading_progress_lottie.json")
        loading_lottie.scale = 0.2f
        loading_lottie.playAnimation()
        loading_lottie.loop(true)



        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                //This is path to the selected image
                photoUri = data?.data
                addphoto_image.setImageURI(photoUri)

            }else{
                //Exit the addPhotoAcitivity if you leave the album without selecting it
                finish()
            }


        }


    }

    fun contentUpload(){
        //Make filename

        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val userName = auth?.currentUser?.email!!.split("@".toRegex())[0]
        var imageFileName = "$userName _ $timestamp _.png"
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)


        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener {uri ->


            var contentDTO = ContentDTO()

            //Insert downloadUrl of image
            contentDTO.imageUrl = uri.toString()

            //Insert uid of user
            contentDTO.uid = auth?.currentUser?.uid

            //Insert userId
            contentDTO.userId = auth?.currentUser?.email

            //Insert explain of content
            contentDTO.explain = addphoto_image_content.text.toString()

            //Insert timestamp
            contentDTO.timestamp = System.currentTimeMillis()

            //File name
            contentDTO.fileName = imageFileName

            firestore?.collection("images")?.document(imageFileName)?.set(contentDTO)

            setResult(Activity.RESULT_OK)




            finish()
        }





        }

    }







