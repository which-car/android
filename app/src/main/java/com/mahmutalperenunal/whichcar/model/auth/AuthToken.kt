package com.mahmutalperenunal.whichcar.model.auth

import com.google.gson.annotations.SerializedName

data class AuthToken (
    @SerializedName("key") val authToken: String
)