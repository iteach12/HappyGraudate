package com.chodingcoding.happygraudate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    var auth:FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
    var callbackManager:CallbackManager? = null

    val GOOGLE_LOGIN_CODE = 9001



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //firebase 로그인 통합 관리하는 Object만들기
        auth = FirebaseAuth.getInstance()

        //구글 로그인 옵션
        var gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        //구글 로그인 클래스 만듦
        googleSignInClient = GoogleSignIn.getClient(this, gso)







    }
}
