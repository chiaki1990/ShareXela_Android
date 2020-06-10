package ga.sharexela.sharexela

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ShareXelaService {


    /* django: django.contrib.auth.models.User */

    //ユーザー登録を行う
    @FormUrlEncoded
    @POST("rest-auth/registration/")
    fun signup(@Field("email") email: String, @Field("password1") password1: String, @Field("password2") password2: String) : Call<AuthModel>


    //Basic認証でログインを行う
    @FormUrlEncoded
    @POST("rest-auth/login/")
    fun login(@Field("email") email: String, @Field("password") password: String) : Call<AuthModel>


    //AuthTokenで認証を行う
    @GET("api/tokencheck/")
    fun loginWithAuthtoken(@Header("Authorization") authToken: String ): Call<CheckTokenResult>


    //ログアウトを行う
    // https://django-rest-auth.readthedocs.io/en/latest/api_endpoints.html
    @POST("rest-auth/logout/")
    fun logout(): Call<ResultModel>



    /* django: items.models.Items  */


    //Itemオブジェクトクエリを取得する

    // api.views.ItemListAPIView#getに連結する
    @GET("api/items/list/")
    //fun getItemListAPIView():Call<List<ItemSerializerModel>>
    fun getItemListAPIView():Call<ItemListAPIViewModel>


    // api.Views.item_views.py ItemCategoryListAPIView#getに連結する
    @GET("api/items/home/list/")
    fun getItemHomeListAPIView():Call<ItemHomeListSerializerViewModel>



    // api.Views.item_views.py ItemCategoryListAPIView#getに連結する
    @GET("api/items/category/{categoryNumber}/items/list/")
    fun getItemCategoryListAPIView(@Path("categoryNumber") categoryName: String):Call<ItemUniversalListAPIView>


    // api.Views.item_views.py ItemCategoryLocalListAPIView#getに連結する
    @GET("api/items/category/{categoryNumber}/items/list/local/")
    fun getItemCategoryLocalListAPIView(@Header("Authorization") authTokenHeader: String, @Path("categoryNumber") categoryName: String):Call<ItemUniversalListAPIView>



    // api.views.ItemAnuncioListAPIView#getに連結する
    @GET("api/items/anuncio_list/")
    fun getItemAnuncioListAPIView():Call<ItemUniversalListAPIView>


    // api.views.ItemAnuncioLocalListAPIView#getに連結する
    @GET("api/items/anuncio_local_list/")
    fun getItemAnuncioLocalListAPIView(@Header("Authorization") authTokenHeader: String):Call<ItemUniversalListAPIView>


    // api.Views.item_views.py ItemFavoriteListAPIVIiew#getに連結する
    @GET("api/items/user/item_favorite_list/")
    fun getItemFavoriteListAPIVIiew(@Header("Authorization") authTokenHeader: String):Call<ItemUniversalListAPIView>



    //Itemオブジェクトを取得する
    // api.views.ItemDetailSerializerAPIView#getに連結する
    @GET("api/items/{id}/")
    fun getItemDetailSerializerAPIView(@Path("id") itemId: String, @Header("Authorization") authTokenHeader: String?):Call<ItemDetailSerializerAPIViewModel>


    @Multipart
    @PATCH("api/items/{id}/")
    fun patchItemDetailSerializerAPIView(@Path("id") itemId: String, @Header("Authorization") authTokenHeader: String, @Part file1:MultipartBody.Part?, @Part file2:MultipartBody.Part?, @Part file3:MultipartBody.Part?, @Part("jsonData") requestBody: RequestBody):Call<ResultModel>


    //Itemオブジェクトを生成する
    //認証ユーザーのみリクエスト送れる仕組みが必要
    //@Headers("Content-Type:application/json")
    //@POST("api/item_create/")
    //fun postItemCreateAPIView(@Header("Authorization") authTokenHeader: String, @Body itemObj:ItemSerializerModel):Call<ResultModel>


    //Itemオブジェクトを生成する
    //認証ユーザーのみリクエスト送れる仕組みが必要
    //@Headers("Content-Type:application/json")
    @Multipart
    @POST("api/item_create_1/")
    fun postItemCreateAPIViewMultiPart(@Header("Authorization") authTokenHeader: String, @Part file1:MultipartBody.Part?, @Part file2:MultipartBody.Part?, @Part file3:MultipartBody.Part?, @Part("jsonData") requestBody: RequestBody):Call<ResultModel>



    //Itemオブジェクトに対してFavoriteを追加する/削除する
    // api.Views.item_views.py ItemFavoriteAPIView#patchに連結する
    @PATCH("api/item/{id}/favorite/")
    fun patchItemFavoriteAPIView(@Path("id") itemObjId: String, @Header("Authorization") authTokenHeader: String?):Call<ResultModel>


    /* Itemオブジェクトの一種 自分の記事一覧  */

    @GET("api/mylist/")
    fun getMyItemListAPIView(@Header("Authorization") authTokenHeader: String):Call<MyItemListSerializerAPIView>




    /* ItemContactオブジェクト */

    // api/views.py ItemContactListAPIView#getに連結する
    @GET("api/item/{id}/item_contacts/list/")
    fun getItemContactListAPIView(@Header("Authorization") authTokenHeader: String, @Path("id") itemObjId:Int): Call<ItemContactListAPIViewModel>


    // api/views.py ItemContactAPIView#postに連結する
    @POST("api/item_contacts/")
    fun postItemContactAPIView(@Header("Authorization") authTokenHeader: String, @Body itemContactObj: ItemContactSerializerModel): Call<ResultModel>


    // api/views.py ItemContactListByContactObjAPIView#getに連結する
    @GET("api/item_contact/{id}/item_contacts/")
    fun getItemContactListByContactObjPKAPIView(@Header("Authorization") authTokenHeader: String, @Path("id") itemContactObjId:Int): Call<ItemContactListAPIViewModel>





    /* Contactオブジェクト */

    // api/views.py ContactAPIView#postに連結する
    @POST("api/contacts/")
    fun postContactInstance(@Header("Authorization") authToken: String?, @Body contact:ContactSerializerModel): Call<ResultModel>






    /*  django: profiles.models.Profile           */


    //userのプロフィール情報を取得する
    @GET("api/profiles/")
    fun readProfile(@Header("Authorization") authToken: String ): Call<ProfileSerializerModel>


    //userプロフィール情報を更新する
    // api.views.ProfileAPIView#patch に連絡する
    @Headers("Content-Type:application/json")
    @PATCH("api/profiles/")
    fun patchProfile(@Header("Authorization") authTokenHeader: String, @Body profile:ProfileSerializerModel): Call<profileResultModel>



    /*
    //userプロフィール画像を変更する
    // api.views.ProfileAPIView#patch に連絡する
    @Multipart
    @PATCH("api/profiles/")
    fun patchProfileImage(@Header("Authorization") authTokenHeader: String, @Part("profileImage") file: RequestBody ): Call<ResultModel>


     */



    //userプロフィール画像を変更する
    // api.views.ProfileAPIView#patch に連結する
    @Multipart
    @PATCH("api/profiles/")
    fun patchProfileImage(@Header("Authorization") authTokenHeader: String, @Part file: MultipartBody.Part ): Call<ResultModel>







    //トークン認証データ必要か考えること
    @GET("api/area_setting_geo/")
    fun getRegionList(@Header("Authorization") authTokenHeader: String):Call<RegionListSet>




    //なにこれ？
    //AreaSettingデータを取得する
    @GET("api/area_setting/")
    fun getAreaSettingsAPIView():Call<RegionListSet>


    //userのPasswordを変更する
    @POST("rest-auth/password/change/")
    fun changePassword(@Header("Authorization") authToken: String,  @Body changePasswordModel:ChangePasswordModel):Call<ChangePasswordResponseModel>





    /* django: direct_messages/models.py DirectMessage, DirectMessageContent  */

    // api/views.DirectMessageContentListAPIView#getに連結する
    @GET("api/item/{id}/direct_message_content_list/")
    fun getDirectMessageContentListAPIView(@Header("Authorization") authTokenHeader: String?, @Path("id") itemObjId: Int): Call<DirectMessageContentListAPIView>

    // api/views.DirectMessageContentAPIView#postに連結する
    @POST("api/direct_message_content/{id}/")
    fun postDirectMessageContentAPIView(@Header("Authorization") authTokenHeader: String?, @Path("id") itemObjId: Int, @Body directMessageContent:DirectMessageContentSerializerModel): Call<ResultModel>

    //
    @GET("api/direct_message_content/{id}/ritem/")
    fun getItemObjByDirectMessageContentObjPKAPIView(@Header("Authorization") authTokenHeader: String?, @Path("id") DirectMessageContentObjId: Int): Call<ItemSerializerModel>


    /*  django: solicitudes.models.Solicitud      */

    // api/views.py SolicitudAPIView#getに連結する
    @GET("api/solicitud/{id}/")
    fun getSolicitudAPIView(@Header("Authorization") authTokenHeader: String?, @Path("id") solicitudObjId: Int):Call<SolicitudAPIViewModel>

    // api/views.SolicitudAPIView#postに連結する
    @POST ("api/solicitudes/item/{id}/")
    fun postSolicitudAPIView(@Header("Authorization") authTokenHeader: String?, @Body solicitudObj: SolicitudSerializerModel, @Path("id") itemObjId: Int): Call<ResultModel>

    // api/views.SolicitudAPIView#patchに連結する
    @PATCH("api/solicitud/{id}/")
    fun patchSolicitudAPIView(@Header("Authorization") authTokenHeader: String?, @Path("id") solicitudObjId: Int): Call<ItemResultModel>

    // api/views.py SolicitudListAPIViewBySolicitudObjAPIView#getに連結する
    @GET("api/solicitudes/solicitud/{id}/solicitud_list/")
    fun getSolicitudListAPIViewBySolicitudObjAPIView(@Header("Authorization") authTokenHeader: String?, @Path("id") solicitudObjId: Int):Call<SolicitudListAPIViewBySolicitudObjAPIViewModel>




    /* django: avisos/models.py Aviso       */

    /* django: avisos/views.AvisosAllListAPIView#getに連結する */
    @GET("api/avisos/list/")
    fun getAvisosAllListAPIView(@Header("Authorization") authTokenHeader: String?):Call<AvisosAllListAPIViewModel>




    /* django: api/models.py DeviceToken     */

    /* django: api/Views.fcm_views DeviceTokenDealAPIVeiw#patchに連結する */
    @FormUrlEncoded
    @PATCH("api/fcm/user/device_token/")
    fun patchDeviceTokenDealAPIVeiw(@Header("Authorization") authTokenHeader: String?, @Field("deviceToken") deviceToken: String):Call<ResultModel>



    @GET("api/multipoly_test/")
    fun getMultipolygon():Call<Multipolygon>


    /* django: api/views.py GetRegionDataByPointAPIView#postに連結する */
    @FormUrlEncoded
    @POST("api/util/region/")
    fun postGetRegionDataByPointAPIView(@Header("Authorization") authTokenHeader: String?, @Field("wkt_point") wkt_point: String):Call<ResultRegionModel>

}

