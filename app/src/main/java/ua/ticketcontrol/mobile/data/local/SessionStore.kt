package ua.ticketcontrol.mobile.data.local

import android.content.Context

class SessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("ticket_control_session", Context.MODE_PRIVATE)

    var baseUrl: String
        get() = prefs.getString("base_url", "http://10.0.2.2:5229/") ?: "http://10.0.2.2:5229/"
        set(value) = prefs.edit().putString("base_url", value.normalizedBaseUrl()).apply()

    var token: String?
        get() = prefs.getString("token", null)
        set(value) = prefs.edit().putString("token", value).apply()

    var userId: Int
        get() = prefs.getInt("user_id", 0)
        set(value) = prefs.edit().putInt("user_id", value).apply()

    fun clearAuth() {
        prefs.edit().remove("token").remove("user_id").apply()
    }
}

fun String.normalizedBaseUrl(): String {
    val trimmed = trim()
    return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
}
