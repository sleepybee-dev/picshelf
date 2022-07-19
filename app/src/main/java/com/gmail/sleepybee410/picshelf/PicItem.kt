package com.gmail.sleepybee410.picshelf

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PIC")
data class PicItem(
    @PrimaryKey(autoGenerate = true)
    var idx : Int,

    @ColumnInfo(name = "createDate")
    var createDate : String?,
    @ColumnInfo(name = "widgetId")
    var widgetId : Int,
    @ColumnInfo(name = "originUri")
    var originUri: Uri?,
    @ColumnInfo(name = "uri")
    var uri: Uri?,
    @ColumnInfo(name = "label")
    var label: String?,
    @ColumnInfo(name = "color")
    var color: String?,
    @ColumnInfo(name = "frame")
    var frame: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
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
        parcel.writeString(createDate)
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