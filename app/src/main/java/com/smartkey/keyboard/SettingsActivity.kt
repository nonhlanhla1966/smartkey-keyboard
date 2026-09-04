package com.smartkey.keyboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

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
        val switchAutocorrect = findViewById<MaterialSwitch>(R.id.switch_autocorrect)
        val switchLearning = findViewById<MaterialSwitch>(R.id.switch_learning)
        val switchNumberRow = findViewById<MaterialSwitch>(R.id.switch_numberrow)
        val spinnerKeyHeight = findViewById<Spinner>(R.id.spinner_keyheight)
        val spinnerOneHanded = findViewById<Spinner>(R.id.spinner_onehanded)
        val switchClipboard = findViewById<MaterialSwitch>(R.id.switch_clipboard)
        val spinnerHours = findViewById<Spinner>(R.id.spinner_clipboard_hours)
        val btnClear = findViewById<Button>(R.id.btn_clear_learning)
        val btnClearClipboard = findViewById<Button>(R.id.btn_clear_clipboard)
        val btnKeyboardSettings = findViewById<MaterialButton>(R.id.btn_open_keyboard_settings)

        btnKeyboardSettings.setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "Open System Settings → Languages & input", Toast.LENGTH_LONG).show()
            }
        }

        switchHaptics.isChecked = prefs.getBoolean(SmartPrefs.KEY_HAPTICS, true)
        switchSounds.isChecked = prefs.getBoolean(SmartPrefs.KEY_SOUNDS, true)
        switchSuggestions.isChecked = prefs.getBoolean(SmartPrefs.KEY_SUGGESTIONS, true)
        switchAutocap.isChecked = prefs.getBoolean(SmartPrefs.KEY_AUTOCAP, true)
        switchAutocorrect.isChecked = prefs.getBoolean(SmartPrefs.KEY_AUTOCORRECT, true)
        switchLearning.isChecked = prefs.getBoolean(SmartPrefs.KEY_LEARNING, true)
        switchNumberRow.isChecked = prefs.getBoolean(SmartPrefs.KEY_NUMBER_ROW, false)
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

        val keySizes = arrayOf("small", "medium", "large")
        val keySizeLabels = arrayOf(
            getString(R.string.key_size_small),
            getString(R.string.key_size_medium),
            getString(R.string.key_size_large)
        )
        val keyHeightAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, keySizeLabels)
        keyHeightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKeyHeight.adapter = keyHeightAdapter
        val currentSize = prefs.getString(SmartPrefs.KEY_KEY_HEIGHT) ?: "medium"
        spinnerKeyHeight.setSelection(keySizes.indexOf(currentSize).coerceAtLeast(0))

        val oneHandedValues = intArrayOf(0, -1, 1)
        val oneHandedLabels = arrayOf(
            getString(R.string.one_handed_off),
            getString(R.string.one_handed_left),
            getString(R.string.one_handed_right)
        )
        val oneHandedAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, oneHandedLabels)
        oneHandedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOneHanded.adapter = oneHandedAdapter
        val currentHand = prefs.getInt(SmartPrefs.KEY_ONE_HANDED, 0)
        val handIndex = oneHandedValues.indexOf(currentHand).coerceAtLeast(0)
        spinnerOneHanded.setSelection(handIndex)

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
        switchAutocorrect.setOnCheckedChangeListener { _, checked -> prefs.putBoolean(SmartPrefs.KEY_AUTOCORRECT, checked) }
        switchLearning.setOnCheckedChangeListener { _, checked -> prefs.putBoolean(SmartPrefs.KEY_LEARNING, checked) }
        switchNumberRow.setOnCheckedChangeListener { _, checked -> prefs.putBoolean(SmartPrefs.KEY_NUMBER_ROW, checked) }
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

        spinnerKeyHeight.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                prefs.putString(SmartPrefs.KEY_KEY_HEIGHT, keySizes[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                prefs.putString(SmartPrefs.KEY_KEY_HEIGHT, "medium")
            }
        }

        spinnerOneHanded.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                prefs.putInt(SmartPrefs.KEY_ONE_HANDED, oneHandedValues[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                prefs.putInt(SmartPrefs.KEY_ONE_HANDED, 0)
            }
        }

        btnClear.setOnClickListener {
            SuggestionsEngine(prefs).clear()
            Toast.makeText(this, R.string.setting_privacy_clear_done, Toast.LENGTH_SHORT).show()
        }

        btnClearClipboard.setOnClickListener {
            SmartClipboard(this, prefs).clearAll()
            Toast.makeText(this, R.string.setting_clipboard_cleared, Toast.LENGTH_SHORT).show()
        }
    }
}