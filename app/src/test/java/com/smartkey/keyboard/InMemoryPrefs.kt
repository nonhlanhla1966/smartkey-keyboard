package com.smartkey.keyboard

import android.content.SharedPreferences

/** In-memory SharedPreferences for JVM unit tests (no Android runtime needed). */
class InMemoryPrefs : SharedPreferences {
    val map = HashMap<String, Any>()

    override fun getAll(): MutableMap<String, *> = map
    override fun getString(key: String, def: String?): String? = map[key] as? String ?: def
    override fun getStringSet(key: String, def: MutableSet<String>?): MutableSet<String>? = map[key] as? MutableSet<String> ?: def
    override fun getInt(key: String, def: Int): Int = map[key] as? Int ?: def
    override fun getLong(key: String, def: Long): Long = map[key] as? Long ?: def
    override fun getFloat(key: String, def: Float): Float = map[key] as? Float ?: def
    override fun getBoolean(key: String, def: Boolean): Boolean = map[key] as? Boolean ?: def
    override fun contains(key: String): Boolean = map.containsKey(key)

    override fun edit(): SharedPreferences.Editor {
        val backing = map
        return object : SharedPreferences.Editor {
            override fun putString(key: String, value: String) = apply { backing[key] = value }
            override fun putStringSet(key: String, values: MutableSet<String>?) = apply { backing[key] = values }
            override fun putInt(key: String, value: Int) = apply { backing[key] = value }
            override fun putLong(key: String, value: Long) = apply { backing[key] = value }
            override fun putFloat(key: String, value: Float) = apply { backing[key] = value }
            override fun putBoolean(key: String, value: Boolean) = apply { backing[key] = value }
            override fun remove(key: String) = apply { backing.remove(key) }
            override fun clear() = apply { backing.clear() }
            override fun commit(): Boolean = true
            override fun apply() {}
            private fun apply(f: () -> Unit): SharedPreferences.Editor {
                f()
                return this
            }
        }
    }

    override fun registerOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}
}