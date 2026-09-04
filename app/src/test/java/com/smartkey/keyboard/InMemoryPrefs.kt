package com.smartkey.keyboard

import android.content.SharedPreferences

/** In-memory SharedPreferences for JVM unit tests (no Android runtime needed). */
class InMemoryPrefs : SharedPreferences {
    val map = HashMap<String, Any?>()

    override fun getAll(): MutableMap<String, *> = map
    override fun getString(key: String, def: String?): String? = map[key] as? String ?: def
    override fun getStringSet(key: String, def: Set<String>?): Set<String>? = map[key] as? Set<String> ?: def
    override fun getInt(key: String, def: Int): Int = map[key] as? Int ?: def
    override fun getLong(key: String, def: Long): Long = map[key] as? Long ?: def
    override fun getFloat(key: String, def: Float): Float = map[key] as? Float ?: def
    override fun getBoolean(key: String, def: Boolean): Boolean = map[key] as? Boolean ?: def
    override fun contains(key: String): Boolean = map.containsKey(key)

    override fun edit(): SharedPreferences.Editor = EditorImpl()

    private inner class EditorImpl : SharedPreferences.Editor {
        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun putStringSet(key: String, values: Set<String>?): SharedPreferences.Editor {
            map[key] = values
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            map.remove(key)
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            map.clear()
            return this
        }

        override fun commit(): Boolean = true

        override fun apply() {}
    }

    override fun registerOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}
}