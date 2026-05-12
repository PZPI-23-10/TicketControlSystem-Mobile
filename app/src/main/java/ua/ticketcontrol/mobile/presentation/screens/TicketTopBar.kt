package ua.ticketcontrol.mobile.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ua.ticketcontrol.mobile.data.model.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketTopBar(state: UiState, onRefresh: () -> Unit, onLogout: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text("Контроль квитків")
                Text(
                    "Користувач #${state.userId ?: "-"} ${state.roles.joinToString(prefix = "(", postfix = ")")}",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0D4F4A),
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        actions = {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Outlined.Refresh, contentDescription = "Оновити")
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.Outlined.Logout, contentDescription = "Вийти")
            }
        }
    )
}
