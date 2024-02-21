package com.example.myapplication.Pedido

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pedido(
    val id: String?=null,
    val cartaId: String?=null,
    val userId: String?=null,
    val cartaNombre: String?=null,
    val cartaColor: String?=null,
    val precio: Double?=null,
    var estado: String?=null,
    val fecha: String?=null,
    val urlCarta: String?=null,
    val notificacionEstado: Int?=null,
    val notificacionUsuario: String?=null
) : Parcelable