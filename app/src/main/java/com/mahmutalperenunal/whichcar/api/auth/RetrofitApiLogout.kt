package com.mahmutalperenunal.whichcar.api.auth

import com.mahmutalperenunal.whichcar.model.auth.Logout
import retrofit2.Call
import retrofit2.http.POST

interface RetrofitApiLogout {

    @POST("api/v1/dj-rest-auth/logout/")
    fun postLogout(): Call<Logout>

}