package com.mahmutalperenunal.whichcar.model.auth

import com.google.gson.annotations.SerializedName

data class Register (
    @SerializedName("username") var username: String,
    @SerializedName("password1") var password1: String,
    @SerializedName("password2") var password2: String,
    @SerializedName("email") var email: String,
    @SerializedName("photo") var profilePhoto: String? = null
)