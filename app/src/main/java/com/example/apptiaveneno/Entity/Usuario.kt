package com.example.apptiaveneno.Entity

import android.os.Parcel
import android.os.Parcelable

data class Usuario(
    val idUsuario: Int,
    val nombreCompleto: String,
    val correo: String,
    val clave: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idUsuario)
        parcel.writeString(nombreCompleto)
        parcel.writeString(correo)
        parcel.writeString(clave)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Usuario> {
        override fun createFromParcel(parcel: Parcel): Usuario {
            return Usuario(parcel)
        }

        override fun newArray(size: Int): Array<Usuario?> {
            return arrayOfNulls(size)
        }
    }
}

