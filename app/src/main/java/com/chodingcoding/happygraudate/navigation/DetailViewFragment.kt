package com.chodingcoding.happygraudate.navigation

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chodingcoding.happygraudate.FcmPush
import com.chodingcoding.happygraudate.R
import com.chodingcoding.happygraudate.navigation.module.AlarmDTO
import com.chodingcoding.happygraudate.navigation.module.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class DetailViewFragment : Fragment(){

    var firestore : FirebaseFirestore? = null
    var firebaseStorage:FirebaseStorage? = null
    var user:FirebaseAuth? = null
    var uid : String? = null
    var fcmPush : FcmPush? = null
    var commentList:ArrayList<ContentDTO.Comment>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        user = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        fcmPush = FcmPush()

        view.detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)




        return view
    }
    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("images")?.orderBy("timestamp",
                Query.Direction.DESCENDING)?.addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()

                if(querySnapshot == null) return@addSnapshotListener

                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                //데이터에 변경이 있을 때 갱신해 줘야 함.
                notifyDataSetChanged()

            }


        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail,parent, false)
            return CustomViewHolder(view)

        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder = (holder as CustomViewHolder).itemView

            //User id
            viewholder.detailviewitem_profile_textview.text = contentDTOs!![position].userId!!.split("@".toRegex())[0]


            //Image
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholder.detailviewitem_imageview_content)


            //Author
            viewholder.detailviewitem_author.text = contentDTOs!![position].userId!!.split("@".toRegex())[0]


            //Explain of content
            viewholder.detailviewitem_explain_textview.text = contentDTOs!![position].explain




            if(contentDTOs[position].userId == user?.currentUser?.email){
                viewholder.detailviewitem_setting_img.visibility = View.VISIBLE
                viewholder.detailviewitem_setting_img.setOnClickListener {

                    val builder = AlertDialog.Builder(ContextThemeWrapper(activity, R.style.Theme_AppCompat_Light_Dialog))
                    builder.setTitle("게시물 삭제")
                    builder.setMessage("정말로 삭제하시겠습니까?")

                    /*builder.setPositiveButton("확인") {dialog, id ->
                    }
                    builder.setNegativeButton("취소") {dialog, id ->
                    }*/
                    builder.setPositiveButton("확인") { _, _ ->
                        var ref = firebaseStorage?.reference?.child("images")?.child(
                            contentDTOs[position].fileName!!)
                        ref?.delete()?.addOnCompleteListener {
                                task ->
                            if(task.isSuccessful){
                                firestore?.collection("images")?.document(contentDTOs[position].fileName!!)?.delete()?.addOnCompleteListener {
                                        task ->
                                    if(task.isSuccessful){
                                        Toast.makeText(activity, "게시물 삭제가 완료되었습니다.", Toast.LENGTH_LONG).show()
                                    }

                                }
                            }
                        }

                    }
                    builder.setNegativeButton("취소") { _, _ ->

                    }

                    builder.show()





                }
            }else{
                viewholder.detailviewitem_setting_img.visibility = View.GONE
            }






            //likes
            viewholder.detailviewitem_favoritecounter_textview.text = "좋아요 " + contentDTOs!![position].favoriteCount +"개"


//            //댓글개수
//            firestore?.collection("images")?.document(contentDTOs[position].uid!!)?.collection("comments")?.document()?.get()?.addOnCompleteListener {
//                task ->
//                if(task.isSuccessful){
//
//                    for(snapshot in task.result?.data!!){
//                        commentList?.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
//                    }
//                }
//            }
//            viewholder.detailviewitem_comment_counter_textview.text = commentList?.size.toString()




            //This code is when the button is clicked
            viewholder.detailviewitem_favorite_imageview.setOnClickListener{
                favoriteEvent(position)

            }

            if(contentDTOs!![position].favorites.containsKey(uid!!)){

                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)

            }else{
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)

            }
            //This code is when the profile image is clicked
            viewholder.detailviewitem_profile_image.setOnClickListener{
                var fragment = UserFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid", contentDTOs[position].uid)
                bundle.putString("userId", contentDTOs[position].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content, fragment)?.commit()

            }
            viewholder.detailviewitem_comment_imageview.setOnClickListener {v->
                var intent = Intent(v.context, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                startActivity(intent)

            }



        }
        fun favoriteEvent(position : Int){
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->

                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
                if(contentDTO!!.favorites.containsKey(uid)){
                    //When the button is clicked
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                    contentDTO?.favorites.remove(uid)

                }else{
                    //When the button is not clicked
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1
                    contentDTO?.favorites[uid!!] = true
                    favoriteAlarm(contentDTOs[position].uid!!)


                }
                transaction.set(tsDoc,contentDTO)


            }
        }


        fun favoriteAlarm(destinationUid : String){

            var alarmDTO = AlarmDTO()
            alarmDTO.destinationUid = destinationUid
            alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
            alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
            alarmDTO.kind = 0
            alarmDTO.timestamp = System.currentTimeMillis()
            alarmDTO.alreadyRead = false
            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

            var message = user?.currentUser?.email + getString(R.string.alarm_favorite)
            fcmPush?.sendMessage(destinationUid, "알림 메세지 입니다.", message)




        }

    }
}