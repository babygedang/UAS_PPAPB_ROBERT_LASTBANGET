package com.example.bisa.makanan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bisa.Adapter.FoodAdapterRoom
import com.example.bisa.Adapter.MakananAdapter
import com.example.bisa.Database.AppDatabase
import com.example.bisa.Database.FoodDao
import com.example.bisa.Database.FoodEntity
import com.example.bisa.databinding.ActivityMakananBinding
import com.example.bisa.model.Makanan
import com.example.bisa.user.UserAddMakananActivity
import com.google.firebase.firestore.FirebaseFirestore

/**
 * MakananActivity bertanggung jawab untuk menampilkan daftar makanan dari Firestore dan Room Database.
 */
class MakananActivity : AppCompatActivity() {

    // Binding untuk layout MakananActivity
    private lateinit var binding: ActivityMakananBinding

    // Adapter untuk daftar makanan dari Firestore
    private lateinit var makananAdapter: MakananAdapter

    // Instance Firestore untuk mengakses data dari Firebase Firestore
    private lateinit var firestore: FirebaseFirestore

    // Dao untuk mengakses data dari Room Database
    private lateinit var foodDao: FoodDao

    // Adapter untuk daftar makanan dari Room Database
    private lateinit var foodAdapterRoom: FoodAdapterRoom

    // Dataset awal daftar makanan dari Firestore
    private var originalMakanan: List<Makanan> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi layout binding
        binding = ActivityMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance()

        // Inisialisasi Room Database
        val database = AppDatabase.getDatabase(this@MakananActivity)
        foodDao = database!!.foodDao()!!

        // Inisialisasi adapter untuk Room Database
        foodAdapterRoom = FoodAdapterRoom(this@MakananActivity)

        // Inisialisasi RecyclerView dan layout manager
        val recyclerView = binding.recyclerViewMakanan
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter untuk Firestore
        makananAdapter = MakananAdapter(originalMakanan)
        recyclerView.adapter = makananAdapter

        // Ambil dan amati data makanan dari Firestore
        fetchDataAndObserve()

        // Ambil dan amati data makanan dari Room Database
        fetchRoomDataAndObserve()

        with(binding) {
            // Aksi saat tombol tambah makanan ditekan
            btnCustomMakanan.setOnClickListener {
                val intent = Intent(this@MakananActivity, UserAddMakananActivity::class.java)
                startActivity(intent)
            }

            // Implementasi pencarian makanan
            searchMakanan.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // Memfilter daftar makanan dari Firestore
                    makananAdapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable) {}
            })
        }
    }

    /**
     * Fungsi untuk mengambil dan mengamati data makanan dari Firebase Firestore.
     */
    private fun fetchDataAndObserve() {
        try {
            val makananCollection = firestore.collection("makanan")
            // Amati perubahan Firestore
            makananCollection.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    showToast(this@MakananActivity, "Error mengambil data dari Firestore")
                    return@addSnapshotListener
                }

                snapshot?.let { documents ->
                    val makanans = mutableListOf<Makanan>()
                    for (document in documents) {
                        val makananId = document.id
                        val makanan = document.toObject(Makanan::class.java).copy(id = makananId)
                        makanans.add(makanan)
                    }
                    // Simpan dataset asli
                    originalMakanan = makanans.toList()
                    // Perbarui UI dengan data Firestore
                    makananAdapter.setMakanan(makanans)
                }
            }
        } catch (e: Exception) {
            showToast(this@MakananActivity, e.toString())
            e.printStackTrace()
        }
    }

    /**
     * Fungsi untuk mengambil dan mengamati data makanan dari Room Database.
     */
    private fun fetchRoomDataAndObserve() {
        try {
            // Amati perubahan LiveData dari Room Database
            var dataList = mutableListOf<FoodEntity>()
            foodDao.getAllMakanan.observe(this, Observer { makananList ->

                for (data in makananList){
                    dataList.add(data)
                }

            })
            foodAdapterRoom.setMakanans(dataList)
        } catch (e: Exception) {
            showToast(this@MakananActivity, "Error mengambil data dari Room Database")
            e.printStackTrace()
        }
    }

    /**
     * Fungsi untuk menampilkan pesan Toast.
     */
    private fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}
