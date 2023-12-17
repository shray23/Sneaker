package com.example.sneakersapp.data

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class SneakerDataListResponse(
    @SerializedName("dataList")
    public var sneakerDataList: MutableList<SneakerData>?
)

@Entity
data class SneakerData(
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    public var id: String,

    @SerializedName("brand")
    public var brand: String?,

    @SerializedName("colorway")
    public var colorway: MutableList<String>?,

    @SerializedName("gender")
    public var gender: String?,

    @SerializedName("media")
    public var media: SneakerMedia?,

    @SerializedName("releaseDate")
    public var releaseDate: String?,

    @SerializedName("retailPrice")
    public var retailPrice: Int = 0,

    @SerializedName("styleId")
    public var styleId: String?,

    @SerializedName("shoe")
    public var shoe: String?,

    @SerializedName("name")
    public var name: String?,

    @SerializedName("title")
    public var title: String?,

    @SerializedName("year")
    public var year: Int = 0,

    @SerializedName("size")
    public var size: MutableList<String>? //added to support different sizes
): java.io.Serializable

data class SneakerMedia(
    @SerializedName("imageUrl")
    public var imageUrl: String?,

    @SerializedName("smallImageUrl")
    public var smallImageUrl: String?,

    @SerializedName("thumbUrl")
    public var thumbUrl: String?
) : java.io.Serializable

@Entity
data class SneakerID(
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    public var sneakerId: String,
)
