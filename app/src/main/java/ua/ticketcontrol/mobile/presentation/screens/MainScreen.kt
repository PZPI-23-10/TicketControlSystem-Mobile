package ua.ticketcontrol.mobile.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ua.ticketcontrol.mobile.data.model.UiState
import ua.ticketcontrol.mobile.presentation.TicketControlViewModel
import ua.ticketcontrol.mobile.presentation.canManageEvents
import ua.ticketcontrol.mobile.presentation.components.ErrorText
import ua.ticketcontrol.mobile.presentation.components.LoadingLine
import ua.ticketcontrol.mobile.presentation.navigation.AppDestination

@Composable
fun MainScreen(
    state: UiState,
    viewModel: TicketControlViewModel,
    onOpenScanner: () -> Unit
) {
    val items = if (state.canManageEvents) {
        listOf(
            AppDestination.Validation,
            AppDestination.EventTickets,
            AppDestination.MyTickets,
            AppDestination.More
        )
    } else {
        listOf(AppDestination.EventTickets)
    }
    var destination by remember { mutableStateOf(items.first()) }

    LaunchedEffect(items) {
        if (destination !in items) destination = items.first()
    }

    Scaffold(
        topBar = {
            TicketTopBar(
                state = state,
                onRefresh = viewModel::refresh,
                onLogout = viewModel::logout
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = destination == item,
                        onClick = { destination = item },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF7F8FA))) {
            state.error?.let { ErrorText(it) }
            if (state.isLoading) LoadingLine()
            when (destination) {
                AppDestination.Validation -> ValidationScreen(state, viewModel, onOpenScanner)
                AppDestination.EventTickets -> EventTicketsScreen(state, viewModel)
                AppDestination.MyTickets -> MyTicketsScreen(state)
                AppDestination.More -> MoreScreen(state, viewModel)
            }
        }
    }
}
