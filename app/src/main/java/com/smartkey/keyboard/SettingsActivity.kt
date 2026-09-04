package com.smartkey.keyboard

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: SmartPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        prefs = SmartPrefs(this)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val switchHaptics = findViewById<MaterialSwitch>(R.id.switch_haptics)
        val switchSounds = findViewById<MaterialSwitch>(R.id.switch_sounds)
        val spinnerTheme = findViewById<Spinner>(R.id.spinner_theme)
        val switchSuggestions = findViewById<MaterialSwitch>(R.id.switch_suggestions)
        val switchAutocap = findViewById<MaterialSwitch>(R.id.switch_autocap)
        val switchLearning = findViewById<MaterialSwitch>(R.id.switch_learning)
        val switchClipboard = findViewById<MaterialSwitch>(R.id.switch_clipboard)
        val spinnerHours = findViewById<Spinner>(R.id.spinner_clipboard_hours)
        val btnClear = findViewById<Button>(R.id.btn_clear_learning)

        switchHaptics.isChecked = prefs.getBoolean(SmartPrefs.KEY_HAPTICS, true)
        switchSounds.isChecked = prefs.getBoolean(SmartPrefs.KEY_SOUNDS, true)
        switchSuggestions.isChecked = prefs.getBoolean(SmartPrefs.KEY_SUGGESTIONS, true)
        switchAutocap.isChecked = prefs.getBoolean(SmartPrefs.KEY_AUTOCAP, true)
        switchLearning.isChecked = prefs.getBoolean(SmartPrefs.KEY_LEARNING, true)
        switchClipboard.isChecked = prefs.getBoolean(SmartPrefs.KEY_CLIPBOARD_ENABLED, false)

        val themeNames = ThemeConfig.ALL.map { it.name }
        val themeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, themeNames)
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTheme.adapter = themeAdapter
        val currentTheme = prefs.getString(SmartPrefs.KEY_THEME) ?: ThemeConfig.KEY_LIGHT
        spinnerTheme.setSelection(themeNames.indexOf(currentTheme).coerceAtLeast(0))

        val hours = intArrayOf(1, 6, 12, 24, 72)
        val hourAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hours.map { it.toString() })
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHours.adapter = hourAdapter
        val currentHours = prefs.getInt(SmartPrefs.KEY_CLIPBOARD_HOURS, 12)
        spinnerHours.setSelection(hours.indexOf(currentHours).coerceAtLeast(0))

        switchHaptics.setOnCheckedChangeListener { _, checked -> prefs.putBoolean(SmartPrefs.KEY_HAPTICS, checked) }
        switchSounds.setOnCheckedChangeListener { _, checked -> prefs.putBoolean(SmartPrefs.KEY_SOUNDS, checked) }

        spinnerTheme.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                prefs.putString(SmartPrefs.KEY_THEME, themeNames[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                prefs.putString(SmartPrefs.KEY_THEME, ThemeConfig.KEY_LIGHT)
            }
        }

        switchSuggestions.setOnCheckedChangeListener { _, checked -> prefs.putBoolean(SmartPrefs.KEY_SUGGESTIONS, checked) }
        switchAutocap.setOnCheckedChangeListener { _, checked -> prefs.putBoolean(SmartPrefs.KEY_AUTOCAP, checked) }
        switchLearning.setOnCheckedChangeListener { _, checked -> prefs.putBoolean(SmartPrefs.KEY_LEARNING, checked) }
        switchClipboard.setOnCheckedChangeListener { _, checked ->
            prefs.putBoolean(SmartPrefs.KEY_CLIPBOARD_ENABLED, checked)
            if (!checked) {
                SmartClipboard(this, prefs).clearAll()
            }
        }

        spinnerHours.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                prefs.putInt(SmartPrefs.KEY_CLIPBOARD_HOURS, hours[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                prefs.putInt(SmartPrefs.KEY_CLIPBOARD_HOURS, 12)
            }
        }

        btnClear.setOnClickListener {
            SuggestionsEngine(prefs).clear()
            Toast.makeText(this, "Learned words cleared", Toast.LENGTH_SHORT).show()
        }
    }
}