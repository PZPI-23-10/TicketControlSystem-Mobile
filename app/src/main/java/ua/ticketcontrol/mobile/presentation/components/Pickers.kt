package ua.ticketcontrol.mobile.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ua.ticketcontrol.mobile.data.model.DeviceDto
import ua.ticketcontrol.mobile.data.model.EventDto
import ua.ticketcontrol.mobile.data.model.TariffDto
import ua.ticketcontrol.mobile.data.model.UserDto

data class EventTypeOption(val id: Int, val title: String)

val eventTypeOptions = listOf(
    EventTypeOption(0, "Концерт"),
    EventTypeOption(1, "Конференція"),
    EventTypeOption(2, "Спорт"),
    EventTypeOption(3, "Театр"),
    EventTypeOption(4, "Інше")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventPicker(events: List<EventDto>, selectedId: Int?, onSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selected = events.firstOrNull { it.id == selectedId }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected?.name ?: "Оберіть подію",
            onValueChange = {},
            readOnly = true,
            label = { Text("Подія") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            events.forEach { event ->
                DropdownMenuItem(text = { Text(event.name) }, onClick = {
                    expanded = false
                    onSelected(event.id)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicePicker(devices: List<DeviceDto>, selectedId: Int?, onSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selected = devices.firstOrNull { it.id == selectedId }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected?.let(::deviceLabel) ?: "Оберіть пристрій",
            onValueChange = {},
            readOnly = true,
            label = { Text("Пристрій контролю") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            devices.forEach { device ->
                DropdownMenuItem(text = { Text(deviceLabel(device)) }, onClick = {
                    expanded = false
                    onSelected(device.id)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTypePicker(selected: Int, onSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTitle = eventTypeOptions.firstOrNull { it.id == selected }?.title ?: "Інше"
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedTitle,
            onValueChange = {},
            readOnly = true,
            label = { Text("Тип події") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth().padding(top = 8.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            eventTypeOptions.forEach { type ->
                DropdownMenuItem(text = { Text(type.title) }, onClick = {
                    expanded = false
                    onSelected(type.id)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TariffPicker(tariffs: List<TariffDto>, selectedId: Int?, onSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selected = tariffs.firstOrNull { it.id == selectedId }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected?.let { "${it.name} • %.2f".format(it.price) } ?: "Оберіть тариф",
            onValueChange = {},
            readOnly = true,
            label = { Text("Тариф") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth().padding(top = 8.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            tariffs.forEach { tariff ->
                DropdownMenuItem(text = { Text("${tariff.name} • %.2f".format(tariff.price)) }, onClick = {
                    expanded = false
                    onSelected(tariff.id)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPicker(users: List<UserDto>, selectedId: Int?, onSelected: (UserDto?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selected = users.firstOrNull { it.id == selectedId }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected?.displayName ?: "Без прив'язки до користувача",
            onValueChange = {},
            readOnly = true,
            label = { Text("Користувач-власник") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth().padding(top = 8.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Без прив'язки") }, onClick = {
                expanded = false
                onSelected(null)
            })
            users.forEach { user ->
                DropdownMenuItem(text = { Text(user.displayName) }, onClick = {
                    expanded = false
                    onSelected(user)
                })
            }
        }
    }
}
