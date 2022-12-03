package com.mahmutalperenunal.whichcar.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class RetrofitInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request: Request = chain.request()
            .newBuilder()
            .build()
        return chain.proceed(request)

    }

}