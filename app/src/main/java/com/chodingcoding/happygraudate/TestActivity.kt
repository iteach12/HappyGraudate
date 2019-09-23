package com.chodingcoding.happygraudate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_photo.*
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    data class TestDTO(var name:String? = null, var address:String? = null)


    var auth:FirebaseAuth? = null
    var firebasefiresotre:FirebaseFirestore? = null
    var testDTO:TestDTO? = null
    var testDTOs:ArrayList<TestDTO>? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        auth = FirebaseAuth.getInstance()
        firebasefiresotre = FirebaseFirestore.getInstance()

        testDTO = TestDTO("박유림", "우리집은 LH4단지 푸른숨 아파트")


        //데이터 생성 하기 creation
        test_user_btn.setOnClickListener {
            firebasefiresotre?.collection("coltest")?.document("doctest")?.set(testDTO!!)?.addOnCompleteListener{
                task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "성공", Toast.LENGTH_SHORT).show()
                }
            }

        }

        //데이터 읽어오기 read pull driven 필요할 때 읽어오는 방식임.
        test_user_btn_get.setOnClickListener {
            firebasefiresotre?.collection("coltest")?.document("doctest")?.get()?.addOnCompleteListener{
                task ->
                if(task.isSuccessful){

                    var testDTO1 = task.result?.toObject(TestDTO::class.java)
                    test_textview_second.text = testDTO1?.address

                }

            }
        }

        //데이터 읽어오기 push driven 데이터가 변경될 때마다 뷰를 그려준다. 채팅방 처럼.
        test_user_btn_push_driven.setOnClickListener {
            firebasefiresotre?.collection("coltest")?.document("doctest")?.addSnapshotListener{
                documentSnapshot, firebaseFirestoreException ->
                var testDTO2 = documentSnapshot?.toObject(TestDTO::class.java)
                test_textview_third.text = testDTO2?.name

            }

        }


        //업데이트하기 인데, DTO로 올리는게 아니라 맵으로 올리게 되어 있음. 왜냐하면 이름과 값이 있어야
        //데이터 베이스에 저장을 할거 아니냐. 인정?
        //이건 따로 함수로 만들어서 불러와보자.
        test_user_btn_update.setOnClickListener {
            updateData()
        }

        //delete 걍 도큐먼트를 지우는 것은 되는데, 다른것도 되는지 해보자.
        test_user_btn_delete_doc.setOnClickListener {
            firebasefiresotre?.collection("coltest")?.document("doctest")?.delete()?.addOnCompleteListener{task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "도큐삭제 성공", Toast.LENGTH_SHORT).show()
                }


            }
        }

        //필드 삭제하는 방법
        test_user_btn_delete_field.setOnClickListener{

            var dc = firebasefiresotre?.collection("coltest")?.document("doctest")
            var updates = mutableMapOf<String, Any>(
                "name" to FieldValue.delete()
            )
            dc?.update(updates)?.addOnCompleteListener{
                task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "필드삭제 성공", Toast.LENGTH_SHORT).show()
                }
            }

        }



    }


    //업데이트 함수 만들었음.
    fun updateData(){
        var map = mutableMapOf<String, Any>()
        map["phone"] = "010-7203-9653"
        firebasefiresotre?.collection("coltest")?.document("doctest")?.update(map)?.addOnCompleteListener {
            task ->
            if(task.isSuccessful){

                Toast.makeText(this, "업데이트 성공", Toast.LENGTH_LONG).show()
            }
        }
    }


}
