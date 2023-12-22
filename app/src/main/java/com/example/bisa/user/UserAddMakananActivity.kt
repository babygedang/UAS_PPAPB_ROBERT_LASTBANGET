package com.example.bisa.user

import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bisa.MainActivity
import com.example.bisa.databinding.ActivityUserAddMakananBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Activity untuk menambahkan data makanan ke Firestore oleh pengguna.
 */
class UserAddMakananActivity : AppCompatActivity() {

    // Binding untuk layout activity_user_add_makanan
    private lateinit var binding: ActivityUserAddMakananBinding

    // Instance Firestore
    private val db = FirebaseFirestore.getInstance()

    // Waktu yang dipilih
    private var selectedTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserAddMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan nilai dari intent
        val namaMakanan = intent.getStringExtra("nama_makanan")
        val jumlahKalori = intent.getStringExtra("jumlah_kalori")

        // Menetapkan nilai ke dalam EditText
        binding.addETMakanan.setText(namaMakanan)
        binding.addETKalori.setText(jumlahKalori)

        // Setting time listener
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            // Handle waktu yang dipilih di sini
            selectedTime = "$hourOfDay:$minute"
            binding.addTVWaktu.text = selectedTime
        }

        // Menambahkan OnClickListener ke TextView untuk memilih waktu
        binding.addTVWaktu.setOnClickListener {
            // Membuat instance dari Calendar untuk mendapatkan waktu saat ini
            val c = Calendar.getInstance()
            val currentHour = c.get(Calendar.HOUR_OF_DAY)
            val currentMinute = c.get(Calendar.MINUTE)

            // Membuat TimePickerDialog
            val timePickerDialog = TimePickerDialog(
                this,
                timeSetListener,
                currentHour,
                currentMinute,
                false
            )

            // Menampilkan dialog pemilih waktu
            timePickerDialog.show()
        }

        // Menambahkan OnClickListener ke tombol Tambah
        binding.addBTTambah.setOnClickListener {
            // Simpan data ke Firestore
            simpanDataFirestore(namaMakanan, jumlahKalori, selectedTime)

            // Kembali ke MainActivity setelah menambahkan data
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Fungsi untuk menyimpan data makanan ke Firestore
    private fun simpanDataFirestore(namaMakanan: String?, jumlahKalori: String?, waktu: String) {
        // Mendapatkan referensi ke koleksi "kalori"
        val collectionRef = db.collection("kalori")
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        val userUid = currentUser?.uid

        if (userUid != null) {
            // Menyimpan data ke dalam Firestore dengan menyertakan userId
            val dataMakanan = hashMapOf(
                "userId" to userUid,
                "nama_makanan" to namaMakanan,
                "jumlah_kalori" to jumlahKalori,
                "waktu" to waktu,
                "tanggal" to currentDate // Tambahkan tanggal saat menyimpan data
            )

            // Menambahkan data ke Firestore
            collectionRef
                .add(dataMakanan)
                .addOnSuccessListener { documentReference ->
                    // Berhasil menyimpan data
                    val pesan = "Data berhasil disimpan dengan ID: ${documentReference.id}"
                    showToast(pesan)
                }
                .addOnFailureListener { e ->
                    // Gagal menyimpan data
                    val pesan = "Data gagal disimpan. Error: $e"
                    showToast(pesan)
                    Log.e("TAG", "Gagal menyimpan data", e)
                }
        } else {
            showToast("User not logged in")
        }
    }

    // Fungsi untuk menampilkan pesan Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
