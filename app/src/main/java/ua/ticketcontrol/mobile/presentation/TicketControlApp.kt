package ua.ticketcontrol.mobile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ua.ticketcontrol.mobile.presentation.scanner.QrScannerScreen
import ua.ticketcontrol.mobile.presentation.screens.LoginScreen
import ua.ticketcontrol.mobile.presentation.screens.MainScreen

@Composable
fun TicketControlApp(viewModel: TicketControlViewModel) {
    val state by viewModel.state
    var scannerOpen by remember { mutableStateOf(false) }

    when {
        scannerOpen -> QrScannerScreen(
            onCode = {
                viewModel.updateTicketUid(it)
                scannerOpen = false
            },
            onClose = { scannerOpen = false }
        )
        state.token == null -> LoginScreen(state = state, viewModel = viewModel)
        else -> MainScreen(
            state = state,
            viewModel = viewModel,
            onOpenScanner = { scannerOpen = true }
        )
    }
}
