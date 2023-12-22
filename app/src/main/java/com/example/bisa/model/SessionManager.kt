package com.example.bisa.model

import android.content.Context
import android.content.SharedPreferences

/**
 * Kelas SessionManager bertanggung jawab untuk mengelola sesi pengguna seperti login, logout, dan menyimpan data pengguna.
 *
 * @param context Konteks aplikasi.
 */
class SessionManager(context: Context) {
    private val PRIVATE_MODE = 0
    private val PREF_NAME = "app-pref"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        private const val PREF_NAME = "app-pref"
        private const val PRIVATE_MODE = 0
        private const val KEY_IS_LOGIN = "isLoggedIn"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        // Tambahkan kunci lainnya sesuai kebutuhan
    }

    /**
     * Fungsi untuk menyimpan status login pengguna.
     *
     * @param isLoggedIn Status login pengguna.
     */
    fun setLogin(isLoggedIn: Boolean) {
        editor.putBoolean(KEY_IS_LOGIN, isLoggedIn)
        editor.apply()
    }

    /**
     * Fungsi untuk memeriksa apakah pengguna sudah login atau belum.
     *
     * @return True jika pengguna sudah login, false jika tidak.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGIN, false)
    }

    /**
     * Fungsi untuk menyimpan data pengguna setelah berhasil login.
     *
     * @param username Nama pengguna.
     * @param email Alamat email pengguna.
     */
    fun saveUserData(username: String, email: String) {
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_EMAIL, email)
        // Tambahkan penyimpanan data lainnya sesuai kebutuhan
        editor.apply()
    }

    /**
     * Fungsi untuk mendapatkan nama pengguna yang sudah disimpan.
     *
     * @return Nama pengguna atau null jika tidak ada data.
     */
    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    /**
     * Fungsi untuk mendapatkan alamat email pengguna yang sudah disimpan.
     *
     * @return Alamat email pengguna atau null jika tidak ada data.
     */
    fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    /**
     * Fungsi untuk melakukan logout dengan menghapus semua data sesi pengguna.
     */
    fun logout() {
        editor.clear()
        editor.apply()
    }
}
