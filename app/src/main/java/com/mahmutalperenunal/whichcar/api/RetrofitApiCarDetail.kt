package com.mahmutalperenunal.whichcar.api

import com.mahmutalperenunal.whichcar.model.CarDetail
import retrofit2.Call
import retrofit2.http.*

interface RetrofitApiCarDetail {

    @GET("api/v1/car/detail/")
    fun getCarDetail(
        @Header("Authorization") auth: String,
        @Path("id") id: Int
    ): Call<CarDetail>

}