package com.example.bisa.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.bisa.Database.AppDatabase
import com.example.bisa.Database.FoodDao
import com.example.bisa.Database.FoodEntity
import com.example.bisa.databinding.ActivityAdminAddMakananBinding
import com.example.bisa.makanan.MakananActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

// Activity untuk menambahkan makanan oleh admin
class AdminAddMakananActivity : AppCompatActivity() {

    // Binding untuk layout
    private lateinit var binding: ActivityAdminAddMakananBinding

    // DAO (Data Access Object) untuk entitas makanan
    private lateinit var foodDao: FoodDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout menggunakan view binding
        binding = ActivityAdminAddMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan instance database menggunakan AppDatabase
        val database = AppDatabase.getDatabase(this@AdminAddMakananActivity)
        // Mendapatkan DAO untuk entitas makanan dari database
        foodDao = database!!.foodDao()!!

        // Menangani klik pada tombol "Tambah"
        with(binding) {
            addBTTambah.setOnClickListener {
                // Mendapatkan nilai inputan pengguna
                val makanan = addETMakanan.text.toString()
                val kalori = addETKalori.text.toString()

                // Memeriksa apakah inputan kosong
                if (makanan.isEmpty() || kalori.isEmpty()) {
                    showToast("Can't Empty Data!")
                } else {
                    // Membuat objek FoodEntity untuk mewakili makanan yang akan ditambahkan
                    val foodEntity = FoodEntity(id = 0, makanan = makanan, kalori = kalori)
                    // Menjalankan fungsi untuk menyimpan makanan ke database
                    insertMakanan(foodEntity)
                    // Menampilkan pesan bahwa data telah dimasukkan
                    showToast("INSERTED!")

                    // Berpindah ke halaman MakananActivity setelah data dimasukkan
                    val intent = Intent(this@AdminAddMakananActivity, MakananActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    // Fungsi untuk menyimpan makanan ke dalam database menggunakan coroutines
    private fun insertMakanan(foodEntity: FoodEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            // Menjalankan fungsi insert pada DAO menggunakan coroutines
            foodDao.insert(foodEntity)
        }
    }

    // Fungsi untuk menampilkan pesan Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
