package ua.ticketcontrol.mobile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ua.ticketcontrol.mobile.data.model.DeviceDto
import ua.ticketcontrol.mobile.data.model.UserDto
import ua.ticketcontrol.mobile.data.model.ValidationResponse

@Composable
fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun ErrorText(message: String) {
    Text(message, color = Color(0xFFB3261E), modifier = Modifier.padding(16.dp))
}

@Composable
fun LoadingLine() {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color(0xFF52605B))
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ValidationListItem(validation: ValidationResponse) {
    SectionCard {
        Row {
            Box(
                modifier = Modifier.width(8.dp).height(44.dp).background(
                    if (validation.isSuccess) Color(0xFF25A06A) else Color(0xFFE05D44),
                    RoundedCornerShape(6.dp)
                )
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(if (validation.isSuccess) "Успішна перевірка" else "Відмова", fontWeight = FontWeight.SemiBold)
                Text("${validation.ticketOwner ?: "Невідомий власник"} • ${shortDate(validation.validationTime)}")
            }
        }
    }
}

val UserDto.displayName: String
    get() = username ?: email ?: "Користувач #$id"

fun shortDate(value: String): String = value
    .replace("T", " ")
    .substringBefore(".")

fun eventTypeLabel(value: String): String = when (value) {
    "Concert" -> "Концерт"
    "Conference" -> "Конференція"
    "Sport" -> "Спорт"
    "Theater" -> "Театр"
    "Other" -> "Інше"
    else -> value
}

fun deviceLabel(device: DeviceDto): String {
    val status = when (device.status) {
        0 -> "активний"
        1 -> "неактивний"
        else -> "стан невідомий"
    }
    val location = device.location.takeIf { it.isNotBlank() }?.let { " • $it" }.orEmpty()
    return "${device.name}$location • $status"
}

fun statusLabel(value: String): String = when (value) {
    "Valid", "active" -> "Дійсний"
    "Used", "used" -> "Використаний"
    "Expired", "expired" -> "Прострочений"
    "Cancelled", "cancelled" -> "Скасований"
    else -> value
}

fun validationMessageLabel(value: String): String = when {
    value.equals("Welcome!", ignoreCase = true) -> "Ласкаво просимо!"
    value.equals("Access Granted", ignoreCase = true) -> "Доступ дозволено"
    value.equals("Access Denied", ignoreCase = true) -> "Доступ заборонено"
    value.equals("Device unavailable", ignoreCase = true) -> "Пристрій недоступний"
    value.equals("Ticket not found", ignoreCase = true) -> "Квиток не знайдено"
    value.equals("Ticket is for another event", ignoreCase = true) -> "Квиток належить іншій події"
    value.equals("Ticket expired or time not valid", ignoreCase = true) -> "Квиток прострочений або ще не дійсний"
    value.equals("All ticket uses consumed", ignoreCase = true) -> "Усі використання квитка вичерпано"
    value.startsWith("Ticket status is", ignoreCase = true) -> "Статус квитка: ${value.substringAfter("is").trim()}"
    else -> value
}
