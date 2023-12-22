package com.example.bisa.Adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.bisa.model.Kalori
import com.example.bisa.user.UserAddMakananActivity
import com.example.bisa.R
import com.google.firebase.firestore.FirebaseFirestore

// Adapter untuk RecyclerView yang menampilkan daftar kalori
class KaloriAdapter : RecyclerView.Adapter<KaloriAdapter.KaloriViewHolder>() {

    // Daftar kalori yang akan ditampilkan dalam RecyclerView
    private var kalori: List<Kalori> = listOf()

    // Instance Firestore untuk mengakses database Firestore
    private val firestore = FirebaseFirestore.getInstance()

    // Referensi koleksi 'kalori' di Firestore
    private val kaloriCollection = firestore.collection("kalori")

    // Membuat ViewHolder untuk setiap item dalam RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KaloriViewHolder {
        // Inflate layout untuk setiap item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.kalori_adapter, parent, false)
        return KaloriViewHolder(view)
    }

    // Mengikat data kalori ke ViewHolder
    override fun onBindViewHolder(holder: KaloriViewHolder, position: Int) {
        // Mendapatkan kalori pada posisi tertentu
        val currentKalori = kalori[position]

        // Menetapkan teks untuk TextView pada ViewHolder
        holder.textViewMakanan.text = currentKalori.nama_makanan
        holder.textViewKalori.text = currentKalori.jumlah_kalori
        holder.textViewWaktu.text = currentKalori.waktu

        // Menangani klik pada tombol hapus
        holder.btDelete.setOnClickListener {
            // Menampilkan dialog konfirmasi penghapusan
            showYesNoAlertDialog(
                holder.itemView.context,
                "Apakah anda yakin akan menghapus ${currentKalori.jumlah_kalori}",
                DialogInterface.OnClickListener { _, _ ->
                    // Menjalankan fungsi penghapusan kalori
                    deleteMakanan(currentKalori.id, holder)
                }
            )
        }

        // Menangani klik pada item RecyclerView untuk mengedit kalori
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, UserAddMakananActivity::class.java)
            // Menyertakan data kalori ke intent untuk diedit
            intent.putExtra("id_makanan", currentKalori.id)
            intent.putExtra("nama_makanan", currentKalori.nama_makanan)
            intent.putExtra("jumlah_kalori", currentKalori.jumlah_kalori)
            intent.putExtra("waktu_makan", currentKalori.waktu)
            // Memulai aktivitas untuk mengedit kalori
            holder.itemView.context.startActivity(intent)
        }

        // ... (kode lainnya)
    }

    // Fungsi untuk menghapus kalori dari Firestore
    private fun deleteMakanan(id: String, holder: KaloriViewHolder) {
        kaloriCollection.document(id)
            .delete()
            .addOnSuccessListener {
                showToast("Kalori harian berhasil dihapus", holder)
            }
            .addOnFailureListener { e ->
                showToast("Error deleting document: $e", holder)
            }
    }

    // Mendapatkan jumlah item dalam RecyclerView
    override fun getItemCount(): Int {
        return kalori.size
    }

    // Mengatur daftar kalori yang akan ditampilkan dalam RecyclerView
    fun setKalori(kalori: List<Kalori>) {
        this.kalori = kalori
        notifyDataSetChanged()
    }

    // ViewHolder untuk setiap item dalam RecyclerView
    inner class KaloriViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewMakanan: TextView = itemView.findViewById(R.id.makananTextView)
        val textViewKalori: TextView = itemView.findViewById(R.id.kaloriTextView)
        val textViewWaktu: TextView = itemView.findViewById(R.id.waktuTextView)
        val btDelete: Button = itemView.findViewById(R.id.itemBtDelete)
    }

    // Menampilkan dialog konfirmasi dengan opsi Ya dan Tidak
    fun showYesNoAlertDialog(
        context: Context,
        message: String,
        onYesClickListener: DialogInterface.OnClickListener
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton("Yes", onYesClickListener)
        alertDialogBuilder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    // Menampilkan pesan singkat menggunakan Toast
    private fun showToast(message: String, holder: KaloriViewHolder) {
        Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()
    }
}
