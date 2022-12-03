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

    //create retrofit
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    //create retrofit api for login proceed
    val apiLogin: RetrofitApiLogin by lazy {
        retrofit.create(RetrofitApiLogin::class.java)
    }

    //create retrofit api for register proceed
    val apiRegister: RetrofitApiRegister by lazy {
        retrofit.create(RetrofitApiRegister::class.java)
    }

    //create retrofit api for log out proceed
    val apiLogout: RetrofitApiLogout by lazy {
        retrofit.create(RetrofitApiLogout::class.java)
    }

    //create retrofit api for log out proceed
    val apiCarDetail: RetrofitApiCarDetail by lazy {
        retrofit.create(RetrofitApiCarDetail::class.java)
    }

    //create retrofit api for log out proceed
    val apiCarSuggestion: RetrofitApiCarSuggestion by lazy {
        retrofit.create(RetrofitApiCarSuggestion::class.java)
    }

    //create retrofit api for log out proceed
    val apiUser: RetrofitApiUser by lazy {
        retrofit.create(RetrofitApiUser::class.java)
    }

}