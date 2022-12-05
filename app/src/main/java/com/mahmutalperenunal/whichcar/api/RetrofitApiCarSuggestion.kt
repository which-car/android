package com.mahmutalperenunal.whichcar.api

import com.mahmutalperenunal.whichcar.model.CarDetail
import com.mahmutalperenunal.whichcar.model.CarSuggestion
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitApiCarSuggestion {

    @GET("api/v1/car/suggested_car/")
    fun getSuggestedCar(
        @Header("Authorization") auth: String
    ): Call<List<CarDetail>>

    @Multipart
    @POST("api/v1/car/suggestion_criteria/")
    fun postSuggestionCriteria(
        @Header("Authorization") auth: String,
        @Part("baggage_size") baggageSize: Int,
        @Part("fuel_efficiency") fuelEfficiency: Int,
        @Part("performance") performance: Int,
        @Part("safety") safety: Int,
        @Part("gearbox") gearbox: String,
        @Part("chassis_type") chassisType: String,
        @Part("wheel_drive") wheelDrive: String,
        @Part("min_price") minPrice: String,
        @Part("max_price") maxPrice: String,
    ): Call<CarSuggestion>

}