package ua.ticketcontrol.mobile.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
    val label: String,
    val icon: ImageVector
) {
    Validation("Скан", Icons.Outlined.QrCodeScanner),
    EventTickets("Події", Icons.Outlined.ConfirmationNumber),
    MyTickets("Мої", Icons.Outlined.Wallet),
    More("Ще", Icons.Outlined.MoreHoriz)
}
