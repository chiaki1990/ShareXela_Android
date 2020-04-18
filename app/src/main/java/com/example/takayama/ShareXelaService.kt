package com.example.takayama

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


    //ログインを行う
    @FormUrlEncoded
    @POST("rest-auth/login/")
    fun login(@Field("email") email: String, @Field("password") password: String) : Call<AuthModel>


    //AuthTokenで認証を行う
    @GET("api/tokencheck/")
    fun loginWithAuthtoken(@Header("Authorization") authToken: String ): Call<AuthModel>


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

    // api.views.ItemDonarListAPIView#getに連結する
    @GET("api/items/donar_list/")
    fun getItemDonarListAPIView():Call<ItemUniversalListAPIView>

    // api.views.ItemDonarLocalListAPIView#getに連結する
    @GET("api/items/donar_local_list/")
    fun getItemDonarLocalListAPIView(@Header("Authorization") authTokenHeader: String):Call<ItemUniversalListAPIView>

    // api.views.ItemAyudaListAPIView#getに連結する
    @GET("api/items/ayuda_list/")
    fun getItemAyudaListAPIView():Call<ItemUniversalListAPIView>

    // api.views.ItemDonarLocalListAPIView#getに連結する
    @GET("api/items/ayuda_local_list/")
    fun getItemAyudaLocalListAPIView(@Header("Authorization") authTokenHeader: String):Call<ItemUniversalListAPIView>

    // api.views.ItemAnuncioListAPIView#getに連結する
    @GET("api/items/anuncio_list/")
    fun getItemAnuncioListAPIView():Call<ItemUniversalListAPIView>

    // api.views.ItemAnuncioLocalListAPIView#getに連結する
    @GET("api/items/anuncio_local_list/")
    fun getItemAnuncioLocalListAPIView(@Header("Authorization") authTokenHeader: String):Call<ItemUniversalListAPIView>




    //Itemオブジェクトを取得する
    // api.views.ItemDetailSerializerAPIView#getに連結する
    @GET("api/items/{id}/")
    fun getItemDetailSerializerAPIView(@Path("id") itemId: String, @Header("Authorization") authTokenHeader: String?):Call<ItemDetailSerializerAPIViewModel>


    //Itemオブジェクトを生成する
    //認証ユーザーのみリクエスト送れる仕組みが必要
    //@Headers("Content-Type:application/json")
    @POST("api/item_create/")
    fun postItemCreateAPIView(@Header("Authorization") authTokenHeader: String, @Body itemObj:ItemSerializerModel):Call<ResultModel>


    //Itemオブジェクトを生成する
    //認証ユーザーのみリクエスト送れる仕組みが必要
    //@Headers("Content-Type:application/json")
    @Multipart
    @POST("api/item_create_1/")
    fun postItemCreateAPIViewMultiPart(@Header("Authorization") authTokenHeader: String, @Part file1:MultipartBody.Part?,@Part file2:MultipartBody.Part?,@Part file3:MultipartBody.Part?, @Part("jsonData") requestBody: RequestBody):Call<ResultModel>



    /* Itemオブジェクトの一種 自分の記事一覧  */

    @GET("api/mylist/")
    fun getMyItemListAPIView(@Header("Authorization") authTokenHeader: String):Call<MyItemListSerializerAPIView>




    /*  django: profiles.models.Profile           */


    //userのプロフィール情報を取得する
    @GET("api/profiles/")
    fun readProfile(@Header("Authorization") authToken: String ): Call<ProfileSerializerModel>


    //userプロフィール情報を更新する
    // api.views.ProfileAPIView#patch に連絡する
    @Headers("Content-Type:application/json")
    @PATCH("api/profiles/")
    fun patchProfile(@Header("Authorization") authTokenHeader: String, @Body profile:ProfileSerializerModel): Call<ResultModel>



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
    @Headers("Content-Type:application/json")
    @GET("api/area_setting/")

    fun getRegionList():Call<RegionListSet>


    //なにこれ？
    //AreaSettingデータを取得する
    @GET("api/area_setting/")
    fun getAreaSettingsAPIView():Call<RegionListSet>


    //userのPasswordを変更する
    @POST("rest-auth/password/change/")
    fun changePassword(@Header("Authorization") authToken: String,  @Body changePasswordModel:ChangePasswordModel):Call<ChangePasswordResponseModel>



    /*  django: contacts.models.Contact      */

    //ContactModelに関するデータを送信する
    @POST("api/contacts/")
    fun postContactInstance(@Header("Authorization") authToken: String?, @Body contact:ContactSerializerModel): Call<ResultModel>




    /* django: direct_messages/models.py DirectMessage, DirectMessageContent  */


    // api/views.DirectMessageContentListAPIView#getに連結する
    @GET("api/direct_message/{id}/")
    fun getDirectMessageContentListAPIView(@Header("Authorization") authTokenHeader: String?, @Path("id") itemObjId: Int): Call<DirectMessageContentListAPIView>


    // api/views.DirectMessageContentAPIView#postに連結する
    @POST("api/direct_message_content/{id}/")
    fun postDirectMessageContentAPIView(@Header("Authorization") authTokenHeader: String?, @Path("id") itemObjId: Int, @Body directMessageContent:DirectMessageContentSerializerModel): Call<ResultModel>


    // api/views.SolicitudAPIView#patchに連結する
    @PATCH("api/solicitud/{id}/")
    fun patchSolicitudAPIView(@Header("Authorization") authTokenHeader: String?, @Path("id") solicitudObjId: Int): Call<ResultModel>



}