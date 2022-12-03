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
        @Header("Authorization") auth: String,
        @Path("id") id: Int
    ): Call<List<CarDetail>>

    @Multipart
    @POST("api/v1/car/suggestion_criteria/")
    fun postSuggestionCriteria(
        @Header("Authorization") auth: String,
        @Part photo: MultipartBody.Part,
        @Part("baggage_size") baggageSize: RequestBody,
        @Part("fuel_efficiency") fuelEfficiency: RequestBody,
        @Part("performance") performance: RequestBody,
        @Part("safety") safety: RequestBody,
        @Part("gearbox") gearbox: RequestBody,
        @Part("chassis_type") chassisType: RequestBody,
        @Part("wheel_drive") wheelDrive: RequestBody,
        @Part("min_price") minPrice: RequestBody,
        @Part("max_price") maxPrice: RequestBody,
    ): Call<CarSuggestion>

}