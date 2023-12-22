package com.example.bisa.Adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.bisa.R
import com.example.bisa.model.Makanan
import com.example.bisa.user.UserAddMakananActivity
import com.google.firebase.firestore.FirebaseFirestore

// Adapter untuk RecyclerView yang menampilkan daftar makanan
class MakananAdapter(originalMakanan: List<Makanan>) : RecyclerView.Adapter<MakananAdapter.MakananViewHolder>(), Filterable {

    // Daftar makanan yang akan ditampilkan dalam RecyclerView
    private var makanan: List<Makanan> = listOf()

    // Instance Firestore untuk mengakses database Firestore
    private val firestore = FirebaseFirestore.getInstance()

    // Referensi koleksi 'makanan' di Firestore
    private val makananCollection = firestore.collection("makanan")

    // Membuat ViewHolder untuk setiap item dalam RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakananViewHolder {
        // Inflate layout untuk setiap item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_makanan, parent, false)
        return MakananViewHolder(view)
    }

    // Mengikat data makanan ke ViewHolder
    override fun onBindViewHolder(holder: MakananViewHolder, position: Int) {
        // Mendapatkan makanan pada posisi tertentu
        val currentMakanan = makanan[position]

        // Menetapkan teks untuk TextView pada ViewHolder
        holder.textViewMakanan.text = currentMakanan.makanan
        holder.textViewKalori.text = currentMakanan.kalori

        // Menangani klik pada tombol hapus
        holder.btDelete.setOnClickListener {
            // Menampilkan dialog konfirmasi penghapusan
            showYesNoAlertDialog(
                holder.itemView.context,
                "Apakah anda yakin akan menghapus ${currentMakanan.makanan}",
                DialogInterface.OnClickListener { _, _ ->
                    // Menjalankan fungsi penghapusan makanan
                    deleteMakanan(currentMakanan.id, holder)
                }
            )
        }

        // Menangani klik pada item RecyclerView untuk melihat detail makanan
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, UserAddMakananActivity::class.java)
            // Menyertakan data makanan ke intent untuk dilihat detailnya
            intent.putExtra("nama_makanan", currentMakanan.makanan)
            intent.putExtra("jumlah_kalori", currentMakanan.kalori)
            // Memulai aktivitas untuk melihat detail makanan
            holder.itemView.context.startActivity(intent)
        }

        // ... (kode lainnya)
    }

    // Fungsi untuk menghapus makanan dari Firestore
    private fun deleteMakanan(id: String, holder: MakananViewHolder) {
        makananCollection.document(id)
            .delete()
            .addOnSuccessListener {
                showToast("Makanan berhasil dihapus", holder)
            }
            .addOnFailureListener { e ->
                showToast("Error deleting document: $e", holder)
            }
    }

    // Mendapatkan jumlah item dalam RecyclerView
    override fun getItemCount(): Int {
        return makanan.size
    }

    // Mengatur daftar makanan yang akan ditampilkan dalam RecyclerView
    fun setMakanan(makanan: MutableList<Makanan>) {
        this.makanan = makanan
        notifyDataSetChanged()
    }

    // ViewHolder untuk setiap item dalam RecyclerView
    inner class MakananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewMakanan: TextView = itemView.findViewById(R.id.makananTextView)
        val textViewKalori: TextView = itemView.findViewById(R.id.kaloriTextView)
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
    private fun showToast(message: String, holder: MakananViewHolder) {
        Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()
    }

    // Fungsi untuk memfilter daftar makanan berdasarkan input pengguna
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = ArrayList<Makanan>()

                if (constraint == null || constraint.isEmpty()) {
                    // Jika input kosong, menampilkan semua makanan
                    filteredList.addAll(makanan)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim()

                    for (item in makanan) {
                        // Memeriksa apakah nama makanan mengandung pola filter
                        if (item.makanan.toLowerCase().contains(filterPattern)) {
                            filteredList.add(item)
                        }
                    }
                }

                // Mengembalikan hasil filter
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // Mengatur ulang daftar makanan setelah filter diterapkan
                @Suppress("UNCHECKED_CAST")
                makanan = results?.values as MutableList<Makanan>
                notifyDataSetChanged()
            }
        }
    }
}
