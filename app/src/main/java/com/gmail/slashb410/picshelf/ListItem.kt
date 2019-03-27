package com.gmail.slashb410.picshelf

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class ListItem(
    var originUri: Uri,
    var uri: Uri,
    var label: String,
    var color: String
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readParcelable<Uri>(Uri::class.java.classLoader),
        source.readParcelable<Uri>(Uri::class.java.classLoader),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(originUri, 0)
        writeParcelable(uri, 0)
        writeString(label)
        writeString(color)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ListItem> = object : Parcelable.Creator<ListItem> {
            override fun createFromParcel(source: Parcel): ListItem = ListItem(source)
            override fun newArray(size: Int): Array<ListItem?> = arrayOfNulls(size)
        }
    }
}