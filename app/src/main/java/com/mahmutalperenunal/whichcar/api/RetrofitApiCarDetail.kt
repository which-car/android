package com.mahmutalperenunal.whichcar.api

import com.mahmutalperenunal.whichcar.model.CarDetail
import retrofit2.Call
import retrofit2.http.*

interface RetrofitApiCarDetail {

    @GET("api/v1/car/detail/list/{brand}/")
    fun getCarDetailList(
        @Header("Authorization") auth: String,
        @Path("brand") brand: String
    ): Call<List<CarDetail>>

    @GET("api/v1/car/detail/{brand}/{model}")
    fun getCarDetail(
        @Header("Authorization") auth: String,
        @Path("brand") brand: String,
        @Path("model") model: String
    ): Call<CarDetail>

}