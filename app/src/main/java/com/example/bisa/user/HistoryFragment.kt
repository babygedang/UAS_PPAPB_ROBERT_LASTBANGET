package com.example.bisa.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bisa.Adapter.KaloriAdapter
import com.example.bisa.databinding.FragmentHistoryBinding
import com.example.bisa.makanan.MakananActivity
import com.example.bisa.model.Kalori
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment untuk menampilkan riwayat konsumsi kalori pengguna.
 */
class HistoryFragment : Fragment() {

    // Binding untuk layout fragment history
    private lateinit var binding: FragmentHistoryBinding

    // Adapter untuk data kalori
    private lateinit var kaloriAdapter: KaloriAdapter

    // Firebase Firestore instance
    private lateinit var firestore: FirebaseFirestore

    // SharedPreferences untuk menyimpan data terakhir reset
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inisialisasi binding
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi instance Firestore
        firestore = FirebaseFirestore.getInstance()

        // Inisialisasi RecyclerView dan adapter
        val recyclerView = binding.recyclerViewKalori
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        kaloriAdapter = KaloriAdapter()
        recyclerView.adapter = kaloriAdapter

        // Ambil dan amati data kalori dari Firestore
        fetchDataAndObserve()

        // Implementasi aksi tombol tambah makanan
        binding.btnAddMakanan.setOnClickListener {
            navigateToAddMakanan()
        }

        // Panggil metode reset data kalori jika perlu
        resetDataKaloriJikaPerlu()
    }

    // Fungsi untuk navigasi ke halaman tambah makanan
    private fun navigateToAddMakanan() {
        val intent = Intent(requireContext(), MakananActivity::class.java)
        startActivity(intent)
    }

    // Fungsi untuk mengambil dan mengamati data kalori dari Firestore
    private fun fetchDataAndObserve() {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userUid = currentUser?.uid

            if (userUid != null) {
                val kaloriCollection = firestore.collection("kalori")

                // Tambahkan filter berdasarkan UID pengguna
                val query = kaloriCollection.whereEqualTo("userId", userUid)

                // Amati perubahan Firestore
                query.addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        showToast("Error fetching data from Firestore")
                        return@addSnapshotListener
                    }

                    snapshot?.let { documents ->
                        val kaloris = mutableListOf<Kalori>()
                        for (document in documents) {
                            val kaloriId = document.id
                            val kalori = document.toObject(Kalori::class.java).copy(id = kaloriId)
                            kaloris.add(kalori)
                        }

                        // Update UI dengan data Firestore
                        kaloriAdapter.setKalori(kaloris)
                    }
                }
            } else {
                showToast("User not logged in")
            }
        } catch (e: Exception) {
            showToast(e.toString())
            Log.d("ERRORKU", e.toString())
        }
    }

    // Fungsi untuk reset data kalori jika perlu
    private fun resetDataKaloriJikaPerlu() {
        val currentDate =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

        // Dapatkan tanggal terakhir kali reset dari SharedPreferences
        val lastResetDate = sharedPreferences.getString(LAST_RESET_DATE_KEY, "") ?: ""

        // Jika tanggal terakhir kali reset tidak sama dengan tanggal hari ini, reset data
        if (lastResetDate != currentDate) {
            resetDataKalori()
            // Simpan tanggal terakhir kali reset ke SharedPreferences
            with(sharedPreferences.edit()) {
                putString(LAST_RESET_DATE_KEY, currentDate)
                apply()
            }
        }
    }

    // Fungsi untuk reset data kalori
    private fun resetDataKalori() {
        // Reset atau hapus semua data kalori dari hari sebelumnya
        // Implementasikan sesuai kebutuhan aplikasi Anda
        // ...
    }

    // Fungsi untuk menampilkan pesan Toast
    private fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(requireContext(), message, duration).show()
    }

    companion object {
        private const val LAST_RESET_DATE_KEY = "last_reset_date"
    }
}
