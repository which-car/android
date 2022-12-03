package com.mahmutalperenunal.whichcar.model

import com.google.gson.annotations.SerializedName

data class CarSuggestion (
        @SerializedName("id") var id: Int,
        @SerializedName("baggage_size")val baggageSize: Int,
        @SerializedName("fuel_efficiency")val fuelEfficiency: Int,
        @SerializedName("performance") var performance: Int,
        @SerializedName("safety") var safety: Int,
        @SerializedName("gearbox") var gearbox: String,
        @SerializedName("chassis_type") var chassisType: String,
        @SerializedName("wheel_drive") var wheelDrive: String,
        @SerializedName("min_price") var minPrice: String,
        @SerializedName("max_price") var maxPrice: String
)