package com.chodingcoding.happygraudate

import android.annotation.SuppressLint

import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import android.view.View
import com.chodingcoding.happygraudate.navigation.module.ContentDTO
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.collections.ArrayList


class LoginActivity : AppCompatActivity() {


    private var auth:FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    var callbackmanager:CallbackManager? = null








    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener {
            signinAndSignup()
        }
        google_sign_in_button.setOnClickListener {
            googleLogin()
        }
        facebook_sign_in_button.setOnClickListener {
            facebookLogin()
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        callbackmanager = CallbackManager.Factory.create()




    }


    override fun onStart() {
        super.onStart()

        //현재 auth가 있으면 바로 보내버리니까.. 좀만 기달려 보자. 여기서 아무것도 없으면 가지 말아야죠..
        moveMainPage(auth?.currentUser)
    }
    //my hashkey
    //3iXLOTJBvOt9oHpOAM3jY6P0JUU=
    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }

    }


    fun googleLogin(){
        progress_bar.visibility = View.VISIBLE
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    fun facebookLogin(){


        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile"))
        LoginManager.getInstance()
            .registerCallback(callbackmanager, object:FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {


                }

                override fun onError(error: FacebookException?) {

                }

            })
    }

    fun handleFacebookAccessToken(token: AccessToken?){
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){

                    //Login
                    moveMainPage(task.result?.user)
                }else{
                    //Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }

            }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackmanager?.onActivityResult(requestCode,resultCode,data)
        if(requestCode==GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess){
                var account = result.signInAccount

                firebaseAuthWithGoogle(account)

                //아래 else 프로그래스바 때문에 넣은거임.
            }else{
                progress_bar.visibility = View.GONE
            }
        }
    }

    fun firebaseAuthWithGoogle(account : GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    //Loginsuccess
                    moveMainPage(task.result?.user)
                }else{
                    //Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signinAndSignup(){
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())?.addOnCompleteListener {
            task ->
                if(task.isSuccessful){
                    //creating a user account
                    moveMainPage(task.result?.user)

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
                            moveMainPage(task.result?.user)
                        }else{
                            //Show the error message
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                }
    }

    fun moveMainPage(user:FirebaseUser?){
        if(user!=null){
            progress_bar.visibility = View.GONE
            startActivity(Intent(this, MainActivity::class.java))
//            startActivity(Intent(this, TestActivity::class.java))
            finish()
        }
    }

}