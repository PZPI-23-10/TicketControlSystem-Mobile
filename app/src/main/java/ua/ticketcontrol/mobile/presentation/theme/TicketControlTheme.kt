package ua.ticketcontrol.mobile.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun TicketControlTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF0D6E66),
            secondary = Color(0xFFB65C32),
            tertiary = Color(0xFF4D6386),
            background = Color(0xFFF7F8FA),
            surface = Color.White
        ),
        content = content
    )
}
