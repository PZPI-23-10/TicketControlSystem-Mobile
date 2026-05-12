package ua.ticketcontrol.mobile.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ua.ticketcontrol.mobile.data.model.UiState
import ua.ticketcontrol.mobile.data.model.ValidationResponse
import ua.ticketcontrol.mobile.presentation.TicketControlViewModel
import ua.ticketcontrol.mobile.presentation.components.DevicePicker
import ua.ticketcontrol.mobile.presentation.components.EventPicker
import ua.ticketcontrol.mobile.presentation.components.SectionCard
import ua.ticketcontrol.mobile.presentation.components.ValidationListItem
import ua.ticketcontrol.mobile.presentation.components.validationMessageLabel

@Composable
fun ValidationScreen(state: UiState, viewModel: TicketControlViewModel, onOpenScanner: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            EventPicker(state.events, state.selectedEventId, viewModel::selectEvent)
            Spacer(Modifier.height(10.dp))
            DevicePicker(state.devices, state.selectedDeviceId, viewModel::selectDevice)
        }
        item {
            SectionCard {
                Text("Перевірка QR-коду", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = state.ticketUid,
                    onValueChange = viewModel::updateTicketUid,
                    label = { Text("UID квитка") },
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Text
                    )
                )
                Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = onOpenScanner, modifier = Modifier.weight(1f)) { Text("Сканувати") }
                    Button(onClick = viewModel::validateTicket, modifier = Modifier.weight(1f)) { Text("Перевірити") }
                }
            }
        }
        state.lastValidation?.let { item { ValidationResultCard(it) } }
        item {
            Text("Останні перевірки", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        items(state.validations.take(20)) { validation ->
            ValidationListItem(validation)
        }
    }
}

@Composable
private fun ValidationResultCard(validation: ValidationResponse) {
    val color = if (validation.isSuccess) Color(0xFF176B4D) else Color(0xFFB3261E)
    SectionCard {
        AssistChip(onClick = {}, label = { Text(if (validation.isSuccess) "Доступ дозволено" else "Доступ заборонено") })
        Text(validationMessageLabel(validation.message), color = color, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Власник: ${validation.ticketOwner ?: "невідомо"}")
        Text("Використань: ${validation.currentUses}/${validation.maxUses}")
    }
}
