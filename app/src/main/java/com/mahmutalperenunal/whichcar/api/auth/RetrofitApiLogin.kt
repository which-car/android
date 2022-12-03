package com.mahmutalperenunal.whichcar.api.auth

import com.mahmutalperenunal.whichcar.model.auth.AuthToken
import com.mahmutalperenunal.whichcar.model.auth.Login
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitApiLogin {

    @POST("api/v1/dj-rest-auth/login/")
    fun postLogin(
        @Body logIn: Login
    ): Call<AuthToken>

}