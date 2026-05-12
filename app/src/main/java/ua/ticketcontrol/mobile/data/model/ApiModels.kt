package ua.ticketcontrol.mobile.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = true
)

@Serializable
data class AuthResponse(
    val userId: Int,
    val accessToken: String
)

@Serializable
data class EventDto(
    val id: Int,
    val name: String,
    val eventType: String,
    val startTime: String,
    val endTime: String,
    val ownerId: Int
)

@Serializable
data class EventCreateRequest(
    val name: String,
    val eventType: Int,
    val startTime: String,
    val endTime: String
)

@Serializable
data class DeviceDto(
    val id: Int,
    val eventId: Int,
    val name: String,
    val location: String = "",
    val status: Int? = null,
    val lastHeartbeat: String? = null
)

@Serializable
data class DeviceCreateRequest(
    val eventId: Int,
    val name: String,
    val location: String = ""
)

@Serializable
data class TariffDto(
    val id: Int,
    val eventId: Int,
    val name: String,
    val price: Double
)

@Serializable
data class TicketDto(
    val id: Int,
    val ownerUserId: Int? = null,
    val ticketUid: String,
    val tariffName: String,
    val eventName: String,
    val ownerName: String,
    val status: String,
    val maxUses: Int,
    val currentUses: Int,
    val validTo: String
)

@Serializable
data class TicketCreateRequest(
    val tariffId: Int,
    val ownerUserId: Int? = null,
    val ticketOwnerName: String = "",
    val validFrom: String? = null,
    val validTo: String? = null,
    val maxUses: Int
)

@Serializable
data class UserDto(
    val id: Int,
    val username: String? = null,
    val email: String? = null,
    val role: List<String> = emptyList(),
    val ownedEvents: List<String> = emptyList()
)

@Serializable
data class ValidationRequest(
    val ticketUid: String,
    val deviceId: Int
)

@Serializable
data class ValidationResponse(
    val isSuccess: Boolean,
    val message: String,
    val validationTime: String,
    val ticketOwner: String? = null,
    val currentUses: Int,
    val maxUses: Int
)

@Serializable
data class EventStatisticsDto(
    val eventId: Int,
    val eventName: String,
    val totalRevenue: Double,
    val potentialRevenue: Double,
    val totalTicketsIssued: Int,
    val ticketsUsed: Int,
    val attendancePercentage: Double,
    val tariffStats: List<TariffStatisticsDto> = emptyList()
)

@Serializable
data class TariffStatisticsDto(
    val tariffName: String,
    val ticketsSold: Int,
    val ticketsUsed: Int,
    val revenueGenerated: Double
)

data class UiState(
    val baseUrl: String = "http://10.0.2.2:5229/",
    val token: String? = null,
    val userId: Int? = null,
    val roles: List<String> = emptyList(),
    val selectedEventId: Int? = null,
    val selectedDeviceId: Int? = null,
    val ticketUid: String = "",
    val events: List<EventDto> = emptyList(),
    val devices: List<DeviceDto> = emptyList(),
    val tariffs: List<TariffDto> = emptyList(),
    val tickets: List<TicketDto> = emptyList(),
    val myTickets: List<TicketDto> = emptyList(),
    val users: List<UserDto> = emptyList(),
    val validations: List<ValidationResponse> = emptyList(),
    val statistics: EventStatisticsDto? = null,
    val lastValidation: ValidationResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
