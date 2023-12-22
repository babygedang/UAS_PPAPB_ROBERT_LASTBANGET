package com.example.bisa.Login


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bisa.MainActivity
import com.example.bisa.admin.AdminMakananActivity
import com.example.bisa.databinding.ActivityLoginBinding
import com.example.bisa.model.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

/*
 * LoginActivity class responsible for handling user login and redirecting to the appropriate pages based on user role.
 */
/*
 * Kelas LoginActivity bertanggung jawab untuk menangani login pengguna dan mengalihkan ke halaman yang sesuai berdasarkan peran pengguna.
 */
class LoginActivity : AppCompatActivity() {

    // Binding untuk layout login
    private lateinit var binding: ActivityLoginBinding

    // Instance Firebase Authentication
    private lateinit var auth: FirebaseAuth

    // SessionManager untuk menangani sesi pengguna
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding layout
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Authentication
        auth = Firebase.auth

        // Inisialisasi SessionManager
        sessionManager = SessionManager(this)

        // Jika sudah masuk, alihkan ke halaman utama
        if (sessionManager.isLoggedIn()) {
            redirectToAppropriatePage()
        }

        // Implementasi login
        binding.loginBtLogin.setOnClickListener {
            val email = binding.loginEtEmail.text.toString()
            val password = binding.loginEtPassword.text.toString()
            loginUser(email, password)
        }

        // Alihkan ke halaman pendaftaran
        binding.loginBtToSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // Fungsi untuk menangani login pengguna
    private fun loginUser(email: String, password: String) {
        // Periksa apakah email dan password tidak kosong
        if (email.isNotBlank() && password.isNotBlank()) {
            // Otentikasi pengguna dengan Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login berhasil, perbarui UI dengan informasi pengguna yang masuk
                        showToast("signInWithEmail: berhasil")
                        onLoginSuccess(email, password)
                    } else {
                        // Jika login gagal, tampilkan pesan kepada pengguna.
                        showToast("signInWithEmail: gagal ${task.exception?.message}")
                    }
                }
        } else {
            showToast("Email atau kata sandi kosong")
        }
    }

    // Fungsi untuk menangani keberhasilan login
    private fun onLoginSuccess(email: String, password: String) {
        // Simpan data pengguna ke SharedPreferences menggunakan SessionManager
        sessionManager.setLogin(true)
        sessionManager.saveUserData(email, password)

        // Alihkan ke halaman yang sesuai
        redirectToAppropriatePage()
    }

    // Fungsi untuk mengalihkan ke halaman yang sesuai berdasarkan peran pengguna
    private fun redirectToAppropriatePage() {
        // Dapatkan pengguna saat ini dari Firebase
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Periksa apakah pengguna adalah admin
            checkAdminAccess(currentUser.uid)
        } else {
            showToast("Gagal mendapatkan informasi pengguna")
        }
    }

    // Fungsi untuk memeriksa apakah pengguna adalah admin dan mengalihkan sesuai
    private fun checkAdminAccess(uid: String) {
        // Dapatkan dokumen pengguna dari Firestore
        val userRef = FirebaseFirestore.getInstance().collection("users").document(uid)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Periksa peran pengguna
                    val role = document.getString("role")

                    if (role == "admin") {
                        // Pengguna adalah admin, alihkan ke AdminMakananActivity
                        val intent = Intent(this@LoginActivity, AdminMakananActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Pengguna bukan admin, alihkan ke MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    showToast("Gagal memeriksa peran admin")
                }
            }
            .addOnFailureListener { e ->
                showToast("Gagal memeriksa peran admin")
                e.printStackTrace()
            }
    }

    // Fungsi untuk menampilkan pesan Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
