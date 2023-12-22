package com.example.bisa.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.bisa.Database.AppDatabase
import com.example.bisa.Database.FoodDao
import com.example.bisa.Database.FoodEntity
import com.example.bisa.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Adapter untuk RecyclerView yang menampilkan daftar makanan dari Room Database
class FoodAdapterRoom(private val context: Context) : RecyclerView.Adapter<FoodAdapterRoom.BukuViewHolder>() {

    // Daftar makanan yang akan ditampilkan dalam RecyclerView
    private var makanans: List<FoodEntity> = listOf()

    // Dao untuk mengakses database makanan
    private lateinit var foodDao: FoodDao

    // ExecutorService untuk menjalankan operasi di latar belakang
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    // Membuat ViewHolder untuk setiap item dalam RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BukuViewHolder {
        // Inflate layout untuk setiap item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_makanan, parent, false)
        return BukuViewHolder(view)
    }

    // Mengikat data makanan ke ViewHolder
    override fun onBindViewHolder(holder: BukuViewHolder, position: Int) {
        // Mendapatkan instance database
        val db = AppDatabase.getDatabase(context)
        foodDao = db!!.foodDao()!!

        // Mendapatkan makanan pada posisi tertentu
        val currentMakanan = makanans[position]

        // Menetapkan teks untuk TextView pada ViewHolder
        holder.textViewMakanan.text = currentMakanan.makanan
        holder.textViewKalori.text = currentMakanan.kalori

        // Menangani klik pada tombol hapus
        holder.btDelete.setOnClickListener {
            // Menampilkan dialog konfirmasi penghapusan
            showYesNoAlertDialog(
                "Apakah anda yakin akan menghapus " + currentMakanan.makanan,
                DialogInterface.OnClickListener { _, _ ->
                    // Menampilkan pesan singkat bahwa item akan dihapus
                    Toast.makeText(context, "del" + currentMakanan.id.toString(), Toast.LENGTH_SHORT).show()

                    // Menjalankan operasi penghapusan di latar belakang
                    executorService.execute {
                        try {
                            foodDao.delete(currentMakanan)
                        } catch (e: Exception) {
                            // Menangani exception jika diperlukan
                        }
                    }
                })
        }
    }

    // Mendapatkan jumlah item dalam RecyclerView
    override fun getItemCount(): Int {
        return makanans.size
    }

    // Mengatur daftar makanan yang akan ditampilkan dalam RecyclerView
    fun setMakanans(makanans: MutableList<FoodEntity>) {
        this.makanans = makanans.toList()
        notifyDataSetChanged()
    }

    // ViewHolder untuk setiap item dalam RecyclerView
    inner class BukuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewMakanan: TextView = itemView.findViewById(R.id.makananTextView)
        val textViewKalori: TextView = itemView.findViewById(R.id.kaloriTextView)
        val btDelete: Button = itemView.findViewById(R.id.itemBtDelete)

        // Menambahkan tampilan lain jika diperlukan
    }

    // Menampilkan dialog konfirmasi dengan opsi Ya dan Tidak
    private fun showYesNoAlertDialog(message: String, onYesClickListener: DialogInterface.OnClickListener) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton("Yes", onYesClickListener)
        alertDialogBuilder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
