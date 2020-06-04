package ga.sharexela.sharexela
import android.Manifest
import java.text.SimpleDateFormat


enum class IntentKey{
    ItemId,
    ItemObj,
    FragmentTag
}


enum class FragmentTag{
    EDITAR_ARTICULO,
    PROFILE_LIST,
    PROFILE_EDIT_AREA,
    PROFILE_EDIT_AREA_NEW,
    PROFILE_EDIT_AREA_CHANGE,
    FROM_CREAR_ARTICULO_FRAGMENT,
    FROM_EDITAR_ARTICULO_FRAGMENT,
    FROM_EDIT_AREA_INFO_FRAGMENAT,
    TO_CREAR_ARTICULO

}

enum class BtnChoice{
    //api/constants.pyに対応

    //#ユーザー認証されていない場合
    ANONYMOUS_USER_ACCESS,
    //#ユーザー認証され、ユーザーが出品者の場合 && 申請者を選ぶ場合
    SELECT_SOLICITUDES,
    //#ユーザー認証され、ユーザーが出品者の場合 && 申請者がいない場合
    NO_SOLICITUDES,
    //#ユーザー認証され、ユーザーが出品者の場合 && 取引相手が自分に決まっている場合
    GO_TRANSACTION,
    //#ユーザー認証され、ユーザーが出品者以外の場合 && 取引相手が決まっていない場合 && 未申請の場合
    SOLICITAR,
    //#ユーザー認証され、ユーザーが出品者以外の場合 && 取引相手が決まっていない場合 && 申請済みの場合
    SOLICITADO,
    //#ユーザー認証され、ユーザーが出品者以外の場合 && 取引相手が他人に決まっている場合
    CANNOT_TRANSACTION

}


enum class ItemObjectsCategory{
    //どこで使っている？？どう使っている？
    ALL_GUATEMALA,
    DONAR_GUATEMALA,
    DONAR_LOCAL,
    AYUDAR_GUATEMALA,
    AYUDAR_LOCAL,
    ANUNCIO_GUATEMALA,
    ANUNCIO_LOCAL

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

val REQUEST_CODE_IMAGES_ACTIVITY = 5


//パーミッション関係
    //GoogleMapsを記事作成、記事編集、(Profile)で使う。そのためのパーミッション
val REQUIRED_PERMISSIONS_LOCATION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
val REQUEST_CODE_PERMISSIONS_LOCATION = 15

    //写真を記事作成、編集に使うためのパーミッション
val REQUEST_CODE_PERMISSIONS_IMAGES = 10
val REQUIRED_PERMISSIONS_IMAGES = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)

