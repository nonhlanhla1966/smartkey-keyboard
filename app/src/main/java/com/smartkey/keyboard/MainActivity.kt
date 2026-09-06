package com.smartkey.keyboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    companion object {
        const val IME_ID = "com.smartkey.keyboard/com.smartkey.keyboard.SmartKeyIME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<MaterialButton>(R.id.btn_enable_keyboard).setOnClickListener {
            startInputMethodSettings(Settings.ACTION_INPUT_METHOD_SETTINGS)
        }

        findViewById<MaterialButton>(R.id.btn_open_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        updateRegistrationStatus()
    }

    override fun onResume() {
        super.onResume()
        updateRegistrationStatus()
    }

    private fun updateRegistrationStatus() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val allMethods = runCatching { imm.inputMethodList }.getOrDefault(emptyList())
        val enabledMethods = runCatching { imm.enabledInputMethodList }.getOrDefault(emptyList())

        val registered: InputMethodInfo? = allMethods.firstOrNull { it.id == IME_ID }
        val enabled: InputMethodInfo? = enabledMethods.firstOrNull { it.id == IME_ID }
        val secureEnabled = runCatching {
            Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_INPUT_METHODS) ?: ""
        }.getOrDefault("")
        val secureHasIme = secureEnabled.contains(IME_ID)

        findViewById<TextView>(R.id.tv_diag_registered).text =
            getString(R.string.main_diag_registered, if (registered != null) "YES" else "NO")
        findViewById<TextView>(R.id.tv_diag_imm).text =
            getString(R.string.main_diag_imm, if (registered != null) "YES" else "NO")
        findViewById<TextView>(R.id.tv_diag_enabled).text =
            getString(R.string.main_diag_enabled, if (enabled != null) "YES" else "NO")
        findViewById<TextView>(R.id.tv_diag_secure).text =
            getString(R.string.main_diag_secure, if (secureHasIme) "YES" else "NO")

        val statusLabel = findViewById<TextView>(R.id.tv_ime_status_label)
        val statusText = findViewById<TextView>(R.id.tv_ime_status_text)

        statusLabel.text = if (registered != null && enabled != null) {
            getString(R.string.main_ime_status_enabled)
        } else if (registered != null) {
            getString(R.string.main_ime_status_registered)
        } else {
            getString(R.string.main_ime_status_not_registered)
        }

        if (registered != null && enabled != null) {
            statusText.text = getString(R.string.main_ime_status_enabled_body)
        } else if (registered != null) {
            statusText.text = getString(R.string.main_ime_status_registered_body)
        } else {
            statusText.text = getString(R.string.main_ime_status_not_registered_body)
        }
    }

    private fun startInputMethodSettings(action: String) {
        try {
            startActivity(Intent(action))
        } catch (e: Exception) {
            Toast.makeText(this, R.string.main_settings_unavailable, Toast.LENGTH_LONG).show()
        }
    }
}
