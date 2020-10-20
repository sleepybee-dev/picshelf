package com.gmail.sleepybee410.picshelf

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class PicItem(
    var idx : Int,
    var widgetId : Int,
    var originUri: Uri,
    var uri: Uri,
    var label: String,
    var color: String,
    var frame: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idx)
        parcel.writeInt(widgetId)
        parcel.writeParcelable(originUri, flags)
        parcel.writeParcelable(uri, flags)
        parcel.writeString(label)
        parcel.writeString(color)
        parcel.writeString(frame)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PicItem> {
        override fun createFromParcel(parcel: Parcel): PicItem {
            return PicItem(parcel)
        }

        override fun newArray(size: Int): Array<PicItem?> {
            return arrayOfNulls(size)
        }
    }
}