package com.example.bisa.model

import com.google.firebase.database.Exclude

data class Kalori(
    @set:Exclude @get:Exclude @Exclude var id : String = "",
    var nama_makanan: String = "",
    var jumlah_kalori: String = "",
    var waktu: String,
    var tanggal: String = ""
) {
    // Konstruktor tanpa argumen diperlukan oleh Firebase Firestore
    constructor() : this("", "", "", "", "")
}
