package com.example.myapplication.Carta

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Carta(
    val id: String = "",
    val nombreCarta: String = "",
    val precio: Double=0.0,
    val stock: Int = 0,
    var disponibilidad: String="",
    val color: String = "",
    val urlCarta: String = ""
): Parcelable
