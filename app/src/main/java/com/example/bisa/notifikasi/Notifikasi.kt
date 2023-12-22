package com.example.bisa.notifikasi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * Kelas Notifikasi adalah BroadcastReceiver yang menangani penerimaan notifikasi.
 */
class Notifikasi : BroadcastReceiver() {

    /**
     * Fungsi onReceive dipanggil ketika terjadi penerimaan notifikasi.
     *
     * @param context Konteks aplikasi.
     * @param intent Intent yang dikirim oleh sistem.
     */
    override fun onReceive(context: Context, intent: Intent) {
        // Mengambil pesan notifikasi dari intent
        val msg = intent?.getStringExtra("MESSAGE")
        if (msg != null) {
            // Menampilkan pesan notifikasi ke log dan tampilan Toast
            Log.d("Notiif-Receiver", "Membaca notifikasi setelah diklik")
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }
}
