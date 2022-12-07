package com.mahmutalperenunal.whichcar.api.auth

import com.mahmutalperenunal.whichcar.model.auth.AuthToken
import com.mahmutalperenunal.whichcar.model.auth.Register
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitApiRegister {

    @POST("api/v1/dj-rest-auth/registration/")
    fun postRegister(
        @Body register: Register
    ): Call<AuthToken>

}