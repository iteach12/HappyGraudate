package com.chodingcoding.happygraudate

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class LoginActivity : AppCompatActivity() {


    private var auth:FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener {
            signinAndSignup()
        }


    }

    fun signinAndSignup(){
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())?.addOnCompleteListener {
            task ->
                if(task.isSuccessful){
                    //creating a user account
                    moveMainPage(task.result.user)

                }else if(task.exception?.message.isNullOrEmpty()){

                    //Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()

                }else{

                    //Login if you have account
                    signinEmail()
                }
        }

    }

    fun signinEmail(){
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())?.
                addOnCompleteListener {
                    task ->
                        if(task.isSuccessful){
                            //Login
                            moveMainPage(task.result.user)
                        }else{
                            //Show the error message
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                }
    }

    fun moveMainPage(user:FirebaseUser?){
        if(user!=null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}