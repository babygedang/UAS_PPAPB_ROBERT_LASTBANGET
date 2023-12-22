package com.example.bisa.Login

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bisa.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Set up tombol-tombol dan action listener
        with(binding) {
            signupBtSignup.setOnClickListener {
                val email = signupEtEmail.text.toString()
                val password = signupEtPassword.text.toString()
                insert(email, password)
            }

            signupBtToLogin.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    /**
     * Fungsi untuk mendaftarkan pengguna baru menggunakan Firebase Authentication.
     * Jika berhasil, data pengguna ditambahkan ke Firestore dan pengguna diarahkan ke halaman login.
     */
    private fun insert(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Pendaftaran berhasil, perbarui UI dengan informasi pengguna yang berhasil mendaftar
                    Log.d(TAG, "createUserWithEmail: success")
                    showToast("Account Created Successfully!")

                    // Dapatkan UID pengguna baru
                    val userUid = auth.currentUser?.uid

                    // Tambahkan data pengguna ke Firestore
                    if (userUid != null) {
                        addUserToFirestore(userUid, email)
                    }

                    // Alihkan ke halaman login
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)

                } else {
                    // Jika pendaftaran gagal, tampilkan pesan kepada pengguna.
                    Log.w(TAG, "createUserWithEmail: failure", task.exception)
                    showToast("Failed to Create Account!")
                }
            }
    }

    /**
     * Fungsi untuk menambahkan data pengguna ke Firestore setelah berhasil mendaftar.
     */
    private fun addUserToFirestore(userUid: String, userEmail: String) {
        val userCollection = FirebaseFirestore.getInstance().collection("users")

        val userData = hashMapOf(
            "email" to userEmail,
            "role" to "user"
        )

        userCollection.document(userUid).set(userData)
            .addOnSuccessListener {
                // Penambahan data berhasil
                Log.d(TAG, "User successfully added to Firestore")
            }
            .addOnFailureListener { e ->
                // Penambahan data gagal
                Log.w(TAG, "Failed to add user to Firestore", e)
            }
    }

    /**
     * Fungsi untuk menampilkan pesan Toast.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
