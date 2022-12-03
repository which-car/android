package com.mahmutalperenunal.whichcar.api

import com.mahmutalperenunal.whichcar.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitApiUser {

    @GET("api/v1/user/get/{id}/")
    fun getUser(
        @Header("Authorization") auth: String,
        @Path("id") id: Int
    ): Call<User>

    @Multipart
    @PUT("api/v1/user/get/{id}/")
    fun putUser(
        @Header("Authorization") auth: String,
        @Path("id") id: Int,
        @Part photo: MultipartBody.Part,
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody
    ): Call<User>

}