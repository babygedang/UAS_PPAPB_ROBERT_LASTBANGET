package com.example.bisa.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bisa.Adapter.MakananAdapter
import com.example.bisa.Login.LoginActivity
import com.example.bisa.databinding.ActivityAdminMakananBinding
import com.example.bisa.model.Makanan
import com.example.bisa.model.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Activity untuk manajemen makanan oleh admin
class AdminMakananActivity : AppCompatActivity() {

    // Binding untuk layout
    private lateinit var binding: ActivityAdminMakananBinding

    // Adapter untuk RecyclerView yang menampilkan daftar makanan
    private lateinit var makananAdapter: MakananAdapter

    // Firebase Firestore sebagai database
    private lateinit var firestore: FirebaseFirestore

    // Daftar makanan yang belum difilter
    private var originalMakanan: List<Makanan> = listOf()

    // Manajer sesi untuk mengelola sesi pengguna
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menggunakan layout binding untuk ActivityAdminMakanan
        binding = ActivityAdminMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        // Memeriksa dan memberikan akses admin
        checkAdminAccess()

        // Konfigurasi RecyclerView dan adapter untuk daftar makanan
        val recyclerView = binding.recyclerViewMakanan
        recyclerView.layoutManager = LinearLayoutManager(this)
        makananAdapter = MakananAdapter(originalMakanan)
        recyclerView.adapter = makananAdapter

        // Ambil dan amati data makanan dari Firestore
        fetchDataAndObserve()

        with(binding) {
            // Menangani klik pada tombol "Tambah Makanan Kustom"
            btnCustomMakanan.setOnClickListener {
                val intent = Intent(this@AdminMakananActivity, AdminAddMakananActivity::class.java)
                startActivity(intent)
            }

            // Menangani perubahan teks pada kolom pencarian
            searchMakanan.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    makananAdapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable) {}
            })

            // Inisialisasi manajer sesi untuk pengelolaan sesi pengguna
            sessionManager = SessionManager(this@AdminMakananActivity)

            // Menangani klik pada tombol "Logout"
            btnLogoutAdmin.setOnClickListener {
                // Melakukan logout
                sessionManager.logout()

                // Mengarahkan ke halaman login setelah logout
                val intent = Intent(this@AdminMakananActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    // Mendapatkan dan mengamati data makanan dari Firebase Firestore
    private fun fetchDataAndObserve() {
        try {
            val makananCollection = firestore.collection("makanan")
            // Mengamati perubahan Firestore pada koleksi makanan
            makananCollection.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    showToast(this@AdminMakananActivity, "Error mengambil data dari Firestore")
                    return@addSnapshotListener
                }

                snapshot?.let { documents ->
                    val makanans = mutableListOf<Makanan>()
                    for (document in documents) {
                        val makananId = document.id
                        val makanan = document.toObject(Makanan::class.java).copy(id = makananId)
                        makanans.add(makanan)
                    }
                    // Menyimpan dataset makanan yang belum difilter
                    originalMakanan = makanans.toList()
                    // Memperbarui UI dengan data Firestore
                    makananAdapter.setMakanan(makanans)
                }
            }
        } catch (e: Exception) {
            showToast(this@AdminMakananActivity, e.toString())
            e.printStackTrace()
        }
    }

    // Memeriksa hak akses admin berdasarkan status pengguna
    private fun checkAdminAccess() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // Mengambil informasi peran pengguna dari Firebase Firestore
            val userRef = FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val role = document.getString("role")

                        if (role == "admin") {
                            // Pengguna adalah admin, mengizinkan akses ke halaman ini
                            setupAdminAccess()
                        } else {
                            // Pengguna bukan admin, menampilkan pesan atau mengambil tindakan sesuai
                            showToast(this@AdminMakananActivity, "Anda bukan admin")
                            finish() // Tidak mengizinkan akses, kembali ke halaman sebelumnya atau keluar dari halaman ini
                        }
                    }
                }
                .addOnFailureListener { e ->
                    showToast(this@AdminMakananActivity, "Gagal memeriksa peran admin")
                    e.printStackTrace()
                }
        } else {
            // Pengguna belum masuk, mengarahkan atau melakukan tindakan sesuai
            showToast(this@AdminMakananActivity, "Anda belum masuk")
            finish() // Tidak mengizinkan akses, kembali ke halaman sebelumnya atau keluar dari halaman ini
        }
    }

    // Menyiapkan akses admin dengan mengkonfigurasi UI atau hak akses
    private fun setupAdminAccess() {
        // Melakukan tindakan untuk menyiapkan UI atau hak akses admin
        binding.btnCustomMakanan.visibility = View.VISIBLE
    }

    // Menampilkan pesan Toast dengan durasi default singkat
    private fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}
