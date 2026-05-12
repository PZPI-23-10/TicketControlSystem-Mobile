package ua.ticketcontrol.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import ua.ticketcontrol.mobile.presentation.TicketControlApp
import ua.ticketcontrol.mobile.presentation.TicketControlViewModel
import ua.ticketcontrol.mobile.presentation.theme.TicketControlTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<TicketControlViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicketControlTheme {
                TicketControlApp(viewModel)
            }
        }
    }
}
