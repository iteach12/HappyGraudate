package com.chodingcoding.happygraudate.navigation.module

data class ContentDTO(var explain : String? = null,
                      var imageUrl : String? = null,
                      var uid : String? = null,
                      var userId : String? = null,
                      var timestamp : Long? = null,
                      var fileName:String? = null, //파일 이름을 넣어줌. 삭제할 때 찾아 쓰려고.
                      var favoriteCount : Int = 0,
                      var favorites : MutableMap<String, Boolean> = HashMap()){

    data class Comment(var uid : String? = null,
                       var userId : String? = null,
                       var comment : String? = null,
                       var timestamp : Long? = null)
}