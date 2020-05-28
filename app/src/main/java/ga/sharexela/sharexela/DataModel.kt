package ga.sharexela.sharexela

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable


/*
命名規則:
djangoのAPIViewの返される結果を受けるdataクラスは、
  **APIView + Modelと名付ける。


APIViewの結果を解析するための各Modelに対応するdataクラスは、
  **Serializer + Modelと名付ける



 */

data class SessionData(
    var profileObj: ProfileSerializerModel? = null,
    var logInStatus: Boolean = false,
    var authTokenHeader: String? = null
)




data class ResultModel(
    var result: String,
    var detail: String
)

data class ItemResultModel(
    var result: String,
    var detail: String,
    var ITEM_OBJ: ItemSerializerModel
)


data class profileResultModel(
    var result: String,
    var detail: String,
    var PROFILE_OBJ: ProfileSerializerModel
)

data class CheckTokenResult(
    var result: String,
    var PROFILE_OBJ: ProfileSerializerModel,
    var key: String
)


data class AuthModel(
    var key:String?,
    var non_field_errors: String?,
    var PROFILE_OBJ: ProfileSerializerModel?

    //{ "key": "ebfa00bd84de2b8f319b747636270257ec24601c" }
    //{ "non_field_errors": [ "Unable to log in with provided credentials." ] }
)

/*
email ishiharasatomi@gmail.com
password1 1234tweet
password2 1234tweet
 */


/*
data class AreaSet(

    var ADM0_LIST: ArrayList<String>,
    var ADM1_LIST: ArrayList<String>,
    var ADM2_LIST: ArrayList<String>
)
*/




/*
data class RegionListSet(

    var ADM0_LIST: ArrayList<String>,
    var ADM1_LIST: ArrayList<String>,
    var ADM2_LIST: ArrayList<String>
)
*/


data class RegionListSet(

    var ADM0_LIST: ArrayList<String>,
    var ADM1_LIST: ArrayList<String>,
    var ADM2_LIST: ArrayList<String>,
    var geoJsonData: String,
    var muniGeoJson: String
)


data class RegionListSimpleSet(

    var ADM0_LIST: ArrayList<String>,
    var ADM1_LIST: ArrayList<String>,
    var ADM2_LIST: ArrayList<String>

)





// django: api/seralizers.py ItemSerializerより
data class ItemSerializerModel(
    var id:Int? = null,                     //nullにしておくと記事生成時に都合がいいけど、読み込み時は都合が悪いか？
    var user: UserSerializerModel? = null, //nullにしておくと記事生成時に都合がいいけど、読み込み時は都合が悪いか？
    var title:String = "",
    var description: String = "",
    var category:CategorySerializerModel? = null,
    var adm0: String? = null,
    var adm1: String? = null,
    var adm2: String? = null,
    var created_at: String? = null,      //nullにしておくと記事生成時に都合がいいけど、読み込み時は都合が悪いか？
    var active: Boolean? = true ,
    var deadline: Boolean? = null,      //nullにしておくと記事生成時に都合がいいけど、読み込み時は都合が悪いか？
    var favorite_users: ArrayList<UserSerializerModel>? = null,
    var item_contacts: ArrayList<ItemContactSerializerModel>? = null,
    var solicitudes: ArrayList<SolicitudSerializerModel>? = null,
    var direct_message: DirectMessageSerializerModel? = null,
    var image1: String? = null,
    var image2: String? = null,         //nullにしておくと記事生成時に都合がいいけど、読み込み時は都合が悪いか？
    var image3: String? = null          //nullにしておくと記事生成時に都合がいいけど、読み込み時は都合が悪いか？
    //今後point(geoデータ)を実装する
    //今後priceを実装する

): Serializable




// django: api/serializers.py CategorySerializerより
data class CategorySerializerModel(
    var name: String
): Serializable




// django: api/serializers.py DirectMessageSerializerより
data class DirectMessageSerializerModel(
    var item: ItemSerializerModel,
    var owner: ProfileSerializerModel,
    var participant: ProfileSerializerModel,
    var created_at: String
): Serializable




// django: api/serializers.py DirectMessageContentSerializerより
data class DirectMessageContentSerializerModel(
    var dm: DirectMessageSerializerModel? = null,
    var content: String? = null,
    var profile: ProfileSerializerModel? = null,
    var created_at: String? = null
)


// django: api/serializers.py　FeedbackSerializerより
data class FeedbackSerializerModel(
    var id: Int? = null,
    var evaluator: UserSerializerModel? = null,
    var content: String? = null,
    var level: Int? = null
): Serializable



// django: api/serializers.py　ProfileSerializerより
data class ProfileSerializerModel(
    var user: UserSerializerModel? = null,
    var adm0: String? = null,
    var adm1: String? = null,
    var adm2: String? = null,
    var description: String? = null,
    var feedback: ArrayList<FeedbackSerializerModel>? = null,
    var point: String? = null,                      //"SRID=4326;POINT (-92.53475189208982 16.7240018958819)"
    //var point: ArrayList<Double>? = null,         //これをどのように扱うか。
    var radius: Int? = null,
    var image: String? = null,
    var sex: Int? = null,
    var phoneNumber: String? = null
):Serializable


// django: api/serializers.py UserSerializerより
data class UserSerializerModel(
    var id: Int? = null,
    var username: String? = null,
    var email: String? = null
): Serializable



// django: api/serializers.py ContactSerializerより
data class ContactSerializerModel(
    var title:String,
    var email_address:String,
    var content:String
)



// django: api/views.py MyItemListAPIViewより
data class MyItemListSerializerAPIView(
    //var item_objects_count: Int?,
    var itemSerializer: List<ItemSerializerModel>?
)



// django: api/views.py ItemDarListSerializerViewより
data class ItemDarListSerializerViewModel(
    var item_objects_count: Int?,
    var itemSerializer: List<ItemSerializerModel>?
)



//djangoから結果を受け取る場合のkeyは大文字構成にして
//modelの属性に関しては小文字にすれば良いんじゃないか？？そういう修正していく。


// django: api/views.py ItemListAPIView,
data class ItemListAPIViewModel(
    var ITEM_OBJECTS: List<ItemSerializerModel>?
)


// django: api/views.py ItemListAPIView, ItemDarLocalListAPIView, に対応するリスト結果を受け取る
data class ItemUniversalListAPIView(
    var ITEM_OBJECTS: List<ItemSerializerModel>,
    var result: String
)

//SearchActivityで得た結果をActivityに渡すためのdata class
data class ItemObjectsSerialized(
    var itemObjects: List<ItemSerializerModel>
):Serializable



// django: api/views.py ItemDetailSerializerAPIViewより
data class ItemDetailSerializerAPIViewModel(
    //var ITEM_OBJ_SERIALIZER: ItemSerializerModel これに変えておく時間があったら。
    var item_obj_serializer: ItemSerializerModel,
    var profile_obj_serializer: ProfileSerializerModel,
    //var item_contact_objects_serializer: ArrayList<ItemContactSerializerModel>,
    //var SOLICITUD_OBJECTS_SERIALIZER: ArrayList<SolicitudSerializerModel>,
    var BTN_CHOICE: String = ""

)




// django: api/serializers.py ItemContactSerializerに対応する
data class ItemContactSerializerModel(

    var post_user: ProfileSerializerModel? = null,
    var item: ItemSerializerModel? = null,
    var message: String? = null,
    var timestamp: String? = null
    ):Serializable


// django: api/views.py ItemContactListAPIViewに対応する
data class ItemContactListAPIViewModel(
    var ITEM_CONTACT_OBJECTS: ArrayList<ItemContactSerializerModel>,
    var ITEM_OBJECT: ItemSerializerModel
):Serializable



// django: api/views.py DirectMessageContentListAPIViewに対応する
data class DirectMessageContentListAPIView(
    var DM_CONTENT_OBJECTS_SERIALIZER: ArrayList<DirectMessageContentSerializerModel>,
    var ACCESS_USER_PROFILE_SERIALIZER: ProfileSerializerModel
)


// django: api/views.py ItemObjByDirectMessageContentObjPKAPIViewに対応する
data class ItemObjByDirectMessageContentObjPKAPIView(
    var ITEM_OBJECT: ItemSerializerModel
)




//EditMailPasswordFragmentにて使用
//rest-authでpasswordを変更するためだけのPOSTの内容
data class ChangePasswordModel(
    var new_password1:String,
    var new_password2:String,
    var old_password:String
)

//EditMailPasswordFragmentにて使用
//ChangePasswordModelのレスポンスを規定
data class ChangePasswordResponseModel(
    var detail:String,
    var old_password:String
)



/* Solicitud関連      */

// SolicitarFragmentにて使用
// django: api/serializers.py SolicitudSerializerより
data class SolicitudSerializerModel(
    var id: Int? = null,
    var applicant: ProfileSerializerModel? = null,
    var message: String? = null,
    var timestamp: String? = null,
    var accepted: Boolean? = null
):Serializable



// django: api/views.py SolicitudAPIViewを受ける
data class SolicitudAPIViewModel(
    var SOLICITUD_OBJECT: SolicitudSerializerModel
)


// django: api/views.py SolicitudListAPIViewBySolicitudObjAPIViewを受ける
data class SolicitudListAPIViewBySolicitudObjAPIViewModel(
    var SOLICITUD_OBJECTS: ArrayList<SolicitudSerializerModel>
)




// NotificationFragmentで使用
// django: api/views.py AvisosAllListAPIViewより
data class AvisosAllListAPIViewModel(
    var AVISO_OBJECTS: ArrayList<AvisoSerializerModel>
)

// django: api/serializers.py AvisoSerializerより
data class AvisoSerializerModel(
    var aviso_user: ProfileSerializerModel,
    var content_type: String,
    var object_id: Int,
    var content_object: contentObjectModel, //djangoのAvisoObjectRelatedFieldで便宜的に返り値をstr型に変更している為
    var checked: Boolean,
    var created_at: String
)


data class contentObjectModel(
    var itemName: String,
    var modelName: String
)



data class Multipolygon(
    
    var coordinates:Any
)