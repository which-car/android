package com.mahmutalperenunal.whichcar.model

import com.google.gson.annotations.SerializedName

data class CarDetail (
    @SerializedName("id") var id: Int,
    @SerializedName("brand") var brand: String,
    @SerializedName("model") var model: String,
    @SerializedName("recommended") var recommended: Int,
    @SerializedName("not_recommended") var notRecommended: Int,
    @SerializedName("baggage_size")val baggageSize: String,
    @SerializedName("fuel_efficiency")val fuelEfficiency: String,
    @SerializedName("engine") var engine: String,
    @SerializedName("acceleration") var acceleration: String,
    @SerializedName("safety") var safety: String,
    @SerializedName("gearbox") var gearbox: String,
    @SerializedName("chassis_type") var chassisType: String,
    @SerializedName("wheel_drive") var wheelDrive: String,
    @SerializedName("price") var price: String,
    @SerializedName("emission_result") var emissionResult: String,
    @SerializedName("segment") var segment: String,
    @SerializedName("like") var like: String,
    @SerializedName("unlike") var unlike: String,
    @SerializedName("photo") var carPhoto: String? = null
        )