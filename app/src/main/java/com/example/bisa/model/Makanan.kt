package com.example.bisa.model

import com.google.firebase.database.Exclude

data class Makanan(
    @set:Exclude @get:Exclude @Exclude var id : String = "",
    var makanan: String = "",
    var kalori: String = ""
)
