package ua.ticketcontrol.mobile.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ua.ticketcontrol.mobile.data.local.normalizedBaseUrl
import ua.ticketcontrol.mobile.data.model.EventDto
import ua.ticketcontrol.mobile.data.model.TicketDto
import ua.ticketcontrol.mobile.data.model.UiState
import ua.ticketcontrol.mobile.presentation.TicketControlViewModel
import ua.ticketcontrol.mobile.presentation.canManageEvents
import ua.ticketcontrol.mobile.presentation.canManageSelectedEvent
import ua.ticketcontrol.mobile.presentation.components.EventPicker
import ua.ticketcontrol.mobile.presentation.components.SectionCard
import ua.ticketcontrol.mobile.presentation.components.TariffPicker
import ua.ticketcontrol.mobile.presentation.components.UserPicker
import ua.ticketcontrol.mobile.presentation.components.displayName
import ua.ticketcontrol.mobile.presentation.components.eventTypeLabel
import ua.ticketcontrol.mobile.presentation.components.shortDate
import ua.ticketcontrol.mobile.presentation.components.statusLabel

@Composable
fun EventTicketsScreen(state: UiState, viewModel: TicketControlViewModel) {
    if (state.canManageEvents) {
        OwnerEventTicketsScreen(state, viewModel)
    } else {
        UserEventTicketsScreen(state)
    }
}

@Composable
private fun OwnerEventTicketsScreen(state: UiState, viewModel: TicketControlViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            SectionCard {
                Text("Подія", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                EventPicker(state.events, state.selectedEventId, viewModel::selectEvent)
                val selected = state.events.firstOrNull { it.id == state.selectedEventId }
                selected?.let { EventSummary(it) }
            }
        }
        if (state.canManageSelectedEvent) {
            item { IssueTicketCard(state, viewModel) }
        }
        if (state.tickets.isEmpty()) {
            item { Text("Для цієї події ще немає квитків.") }
        }
        items(state.tickets) { ticket -> TicketItem(ticket, state.baseUrl) }
    }
}

@Composable
private fun UserEventTicketsScreen(state: UiState) {
    val groups = state.myTickets.groupBy { it.eventName }.filterValues { it.isNotEmpty() }
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Мої події та квитки", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }
        if (groups.isEmpty()) {
            item { Text("У вас поки немає квитків.") }
        }
        groups.forEach { (eventName, tickets) ->
            item {
                SectionCard {
                    Text(eventName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Квитків: ${tickets.size}", color = Color(0xFF52605B))
                }
            }
            items(tickets) { ticket -> TicketItem(ticket, state.baseUrl) }
        }
    }
}

@Composable
fun MyTicketsScreen(state: UiState) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Мої квитки", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }
        if (state.myTickets.isEmpty()) {
            item { Text("У вас поки немає прив'язаних квитків.") }
        }
        items(state.myTickets) { ticket -> TicketItem(ticket, state.baseUrl) }
    }
}

@Composable
private fun EventSummary(event: EventDto) {
    Spacer(Modifier.height(10.dp))
    Text(event.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    Text("${eventTypeLabel(event.eventType)} • ${shortDate(event.startTime)} - ${shortDate(event.endTime)}")
}

@Composable
private fun IssueTicketCard(state: UiState, viewModel: TicketControlViewModel) {
    var selectedTariffId by remember(state.selectedEventId, state.tariffs) { mutableStateOf(state.tariffs.firstOrNull()?.id) }
    var selectedUserId by remember { mutableStateOf<Int?>(null) }
    var ownerName by remember { mutableStateOf("") }
    var maxUses by remember { mutableStateOf("1") }

    SectionCard {
        Text("Видати квиток", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        TariffPicker(state.tariffs, selectedTariffId) { selectedTariffId = it }
        UserPicker(state.users, selectedUserId) { user ->
            selectedUserId = user?.id
            ownerName = user?.displayName ?: ownerName
        }
        OutlinedTextField(
            value = ownerName,
            onValueChange = { ownerName = it },
            label = { Text("Ім'я власника") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text
            )
        )
        OutlinedTextField(
            value = maxUses,
            onValueChange = { maxUses = it },
            label = { Text("Кількість використань") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(
            onClick = { viewModel.createTicket(selectedUserId, ownerName, selectedTariffId, maxUses) },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        ) {
            Text("Видати квиток")
        }
    }
}

@Composable
private fun TicketItem(ticket: TicketDto, baseUrl: String) {
    var showQr by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current

    SectionCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ticket.ownerName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(ticket.eventName)
                Text("${ticket.tariffName} • ${ticket.currentUses}/${ticket.maxUses} • до ${shortDate(ticket.validTo)}")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(ticket.ticketUid, style = MaterialTheme.typography.labelMedium, color = Color(0xFF52605B), modifier = Modifier.weight(1f))
                    CopyUidButton(ticket.ticketUid)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                AssistChip(onClick = {}, label = { Text(statusLabel(ticket.status)) })
                TextButton(onClick = { showQr = true }) { Text("QR") }
            }
        }
    }
    if (showQr) {
        AlertDialog(
            onDismissRequest = { showQr = false },
            confirmButton = { TextButton(onClick = { showQr = false }) { Text("Закрити") } },
            title = { Text("QR-код квитка") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = "${baseUrl.normalizedBaseUrl()}api/Ticket/${ticket.id}/qr",
                        contentDescription = "QR-код квитка",
                        modifier = Modifier.size(240.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(ticket.ticketUid, style = MaterialTheme.typography.labelMedium)
                        IconButton(onClick = { clipboard.setText(AnnotatedString(ticket.ticketUid)) }) {
                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Копіювати UID")
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun CopyUidButton(uid: String) {
    val clipboard = LocalClipboardManager.current
    IconButton(onClick = { clipboard.setText(AnnotatedString(uid)) }) {
        Icon(Icons.Outlined.ContentCopy, contentDescription = "Копіювати UID")
    }
}
