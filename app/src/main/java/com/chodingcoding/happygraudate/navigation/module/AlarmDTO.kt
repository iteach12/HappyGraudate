package com.chodingcoding.happygraudate.navigation.module


data class AlarmDTO(
    var destinationUid : String? = null,
    var userId : String? = null,
    var uid : String? = null,
    var kind : Int? = null,
    var message : String? = null,
    var timestamp : Long? = null,
    var alreadyRead: Boolean? = null
) //alreadyRead 는 이미 읽은 알람을 구분하기 위함임. 기본은 false로 해서. true가 되면 로드되지 않도록