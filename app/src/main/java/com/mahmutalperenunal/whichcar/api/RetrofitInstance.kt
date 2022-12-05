package com.mahmutalperenunal.whichcar.api

import com.google.gson.GsonBuilder
import com.mahmutalperenunal.whichcar.api.auth.RetrofitApiLogin
import com.mahmutalperenunal.whichcar.api.auth.RetrofitApiLogout
import com.mahmutalperenunal.whichcar.api.auth.RetrofitApiRegister
import com.mahmutalperenunal.whichcar.util.Constant.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val gson by lazy {
        GsonBuilder().setLenient().create()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiLogin: RetrofitApiLogin by lazy {
        retrofit.create(RetrofitApiLogin::class.java)
    }

    val apiRegister: RetrofitApiRegister by lazy {
        retrofit.create(RetrofitApiRegister::class.java)
    }

    val apiLogout: RetrofitApiLogout by lazy {
        retrofit.create(RetrofitApiLogout::class.java)
    }

    val apiCarDetail: RetrofitApiCarDetail by lazy {
        retrofit.create(RetrofitApiCarDetail::class.java)
    }

    val apiCarSuggestion: RetrofitApiCarSuggestion by lazy {
        retrofit.create(RetrofitApiCarSuggestion::class.java)
    }

    val apiUser: RetrofitApiUser by lazy {
        retrofit.create(RetrofitApiUser::class.java)
    }

    val apiFavorites: RetrofitApiFavorites by lazy {
        retrofit.create(RetrofitApiFavorites::class.java)
    }

}