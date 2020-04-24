package com.example.takayama

import java.text.SimpleDateFormat

enum class IntentKey{
    ItemId,
    FragmentKey
}


enum class FragmentTag{
    PROFILE_LIST
}

enum class BtnChoice{
    //api/constants.pyに対応

    //#ユーザー認証されていない場合
    ANONYMOUS_USER_ACCESS,

    //#ユーザー認証され、ユーザーが出品者の場合 && 申請者を選ぶ場合
    SELECT_SOLICITUDES,

    //#ユーザー認証され、ユーザーが出品者の場合 && 申請者がいない場合
    NO_SOLICITUDES,

    //#ユーザー認証され、ユーザーが出品者の場合 && 取引相手が決まっている場合
    GO_TRANSACTION,

    //#ユーザー認証され、ユーザーが出品者以外の場合
    SOLICITAR,
    SOLICITADO,

}


val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
val showDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")

val IMAGE1: String = "IMAGE1"
val IMAGE2: String = "IMAGE2"
val IMAGE3: String = "IMAGE3"


val REQUEST_CODE_IMAGE = 1 //ProfileImageを設定

val REQUEST_CODE_ARTICULO_IMAGE1 = 2
val REQUEST_CODE_ARTICULO_IMAGE2 = 3
val REQUEST_CODE_ARTICULO_IMAGE3 = 4


