package com.mahmutalperenunal.whichcar.model

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("id") var id: Int,
    @SerializedName("username")val username: String,
    @SerializedName("email")val email: String,
    @SerializedName("photo") var profilePhoto: String? = null
)