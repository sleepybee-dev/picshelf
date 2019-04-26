package com.gmail.slashb410.picshelf

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class ListItem(
    var idx : Int,
    var originUri: Uri,
    var uri: Uri,
    var label: String,
    var color: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idx)
        parcel.writeParcelable(originUri, flags)
        parcel.writeParcelable(uri, flags)
        parcel.writeString(label)
        parcel.writeString(color)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ListItem> {
        override fun createFromParcel(parcel: Parcel): ListItem {
            return ListItem(parcel)
        }

        override fun newArray(size: Int): Array<ListItem?> {
            return arrayOfNulls(size)
        }
    }
}