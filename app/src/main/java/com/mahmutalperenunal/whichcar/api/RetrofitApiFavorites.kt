package com.mahmutalperenunal.whichcar.api

import com.mahmutalperenunal.whichcar.model.CarDetail
import com.mahmutalperenunal.whichcar.model.CarSuggestion
import retrofit2.Call
import retrofit2.http.*

interface RetrofitApiFavorites {

    @GET("api/v1/car/favorites/list/")
    fun getCarDetailList(
        @Header("Authorization") auth: String
    ): Call<List<CarDetail>>

    @Multipart
    @POST("api/v1/car/favorites/post/{id}/")
    fun postFavorite(
        @Header("Authorization") auth: String,
        @Part("id") id: Int
    ): Call<CarDetail>

}