package ua.ticketcontrol.mobile.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import ua.ticketcontrol.mobile.data.model.UiState
import ua.ticketcontrol.mobile.presentation.TicketControlViewModel
import ua.ticketcontrol.mobile.presentation.canManageEvents
import ua.ticketcontrol.mobile.presentation.canManageSelectedEvent
import ua.ticketcontrol.mobile.presentation.components.EventPicker
import ua.ticketcontrol.mobile.presentation.components.EventTypePicker
import ua.ticketcontrol.mobile.presentation.components.MetricRow
import ua.ticketcontrol.mobile.presentation.components.SectionCard

@Composable
fun MoreScreen(state: UiState, viewModel: TicketControlViewModel) {
    if (!state.canManageEvents) return

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            SectionCard {
                Text("Ще", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                MoreTile(Icons.Outlined.BarChart, "Статистика", "Показники для обраної власної події.")
                MoreTile(Icons.Outlined.EventAvailable, "Події", "Створення подій доступне ролям Owner і Admin.")
                MoreTile(Icons.Outlined.Devices, "Пристрої", "Реєстрація пристроїв доступна власнику події.")
            }
        }
        item { StatisticsBlock(state, viewModel) }
        item { ManagementScreen(state, viewModel) }
    }
}

@Composable
private fun StatisticsBlock(state: UiState, viewModel: TicketControlViewModel) {
    val stats = state.statistics
    SectionCard {
        Text("Статистика", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        EventPicker(state.events, state.selectedEventId, viewModel::selectEvent)
        Spacer(Modifier.height(12.dp))
        if (stats == null) {
            Text("Статистика для обраної події поки недоступна.")
        } else {
            Text(stats.eventName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            MetricRow("Видано квитків", stats.totalTicketsIssued.toString())
            MetricRow("Використано", stats.ticketsUsed.toString())
            MetricRow("Відвідуваність", "%.1f%%".format(stats.attendancePercentage))
            MetricRow("Дохід", "%.2f".format(stats.totalRevenue))
            MetricRow("Потенційний дохід", "%.2f".format(stats.potentialRevenue))
            stats.tariffStats.forEach { tariff ->
                Spacer(Modifier.height(10.dp))
                Text(tariff.tariffName, fontWeight = FontWeight.SemiBold)
                MetricRow("Продано", tariff.ticketsSold.toString())
                MetricRow("Використано", tariff.ticketsUsed.toString())
                MetricRow("Дохід", "%.2f".format(tariff.revenueGenerated))
            }
        }
    }
}

@Composable
private fun MoreTile(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F8FA))
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = title, tint = Color(0xFF0D6E66))
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF52605B))
            }
        }
    }
}

@Composable
private fun ManagementScreen(state: UiState, viewModel: TicketControlViewModel) {
    SectionCard {
        Text("Керування", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        CreateEventCard(viewModel)
        Spacer(Modifier.height(18.dp))
        Text("Пристрої обраної події", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        EventPicker(state.events, state.selectedEventId, viewModel::selectEvent)
        if (state.canManageSelectedEvent) {
            Spacer(Modifier.height(12.dp))
            CreateDeviceCard(viewModel)
        } else {
            Text("Реєструвати пристрої можна тільки для власних подій.", modifier = Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
private fun CreateEventCard(viewModel: TicketControlViewModel) {
    var name by remember { mutableStateOf("") }
    var eventType by remember { mutableIntStateOf(0) }
    var startTime by remember { mutableStateOf("2026-05-10T10:00:00") }
    var endTime by remember { mutableStateOf("2026-05-10T12:00:00") }

    Text("Нова подія", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Назва") },
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    )
    EventTypePicker(eventType, { eventType = it })
    OutlinedTextField(
        value = startTime,
        onValueChange = { startTime = it },
        label = { Text("Початок") },
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        singleLine = true
    )
    OutlinedTextField(
        value = endTime,
        onValueChange = { endTime = it },
        label = { Text("Завершення") },
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        singleLine = true
    )
    Button(
        onClick = { viewModel.createEvent(name, eventType, startTime, endTime) },
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
    ) {
        Text("Створити подію")
    }
}

@Composable
private fun CreateDeviceCard(viewModel: TicketControlViewModel) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    Text("Новий пристрій", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Назва пристрою") },
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    )
    OutlinedTextField(
        value = location,
        onValueChange = { location = it },
        label = { Text("Локація") },
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    )
    Button(
        onClick = { viewModel.createDevice(name, location) },
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
    ) {
        Text("Зареєструвати пристрій")
    }
}
