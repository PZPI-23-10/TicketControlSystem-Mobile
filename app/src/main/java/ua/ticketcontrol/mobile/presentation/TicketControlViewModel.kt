package ua.ticketcontrol.mobile.presentation

import android.app.Application
import android.util.Base64
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ua.ticketcontrol.mobile.data.local.SessionStore
import ua.ticketcontrol.mobile.data.local.normalizedBaseUrl
import ua.ticketcontrol.mobile.data.model.DeviceCreateRequest
import ua.ticketcontrol.mobile.data.model.EventCreateRequest
import ua.ticketcontrol.mobile.data.model.LoginRequest
import ua.ticketcontrol.mobile.data.model.TicketCreateRequest
import ua.ticketcontrol.mobile.data.model.UiState
import ua.ticketcontrol.mobile.data.model.ValidationRequest
import ua.ticketcontrol.mobile.data.remote.ApiFactory

class TicketControlViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionStore = SessionStore(application)
    private val apiFactory = ApiFactory(sessionStore)

    var state = mutableStateOf(
        UiState(
            baseUrl = sessionStore.baseUrl,
            token = sessionStore.token,
            userId = sessionStore.userId.takeIf { it > 0 },
            roles = sessionStore.token?.extractRoles().orEmpty()
        )
    )
        private set

    private val api get() = apiFactory.create(state.value.baseUrl)

    init {
        if (state.value.token != null) refresh()
    }

    fun updateBaseUrl(value: String) {
        state.value = state.value.copy(baseUrl = value)
    }

    fun updateTicketUid(value: String) {
        state.value = state.value.copy(ticketUid = value, error = null)
    }

    fun selectEvent(eventId: Int) {
        state.value = state.value.copy(selectedEventId = eventId, selectedDeviceId = null)
        refreshForEvent(eventId)
    }

    fun selectDevice(deviceId: Int) {
        state.value = state.value.copy(selectedDeviceId = deviceId)
        viewModelScope.launch {
            runCatching { api.heartbeat(deviceId) }
        }
    }

    fun login(email: String, password: String, rememberMe: Boolean) = runTask {
        val baseUrl = state.value.baseUrl.normalizedBaseUrl()
        sessionStore.baseUrl = baseUrl
        val result = apiFactory.create(baseUrl).login(LoginRequest(email, password, rememberMe))
        sessionStore.token = result.accessToken
        sessionStore.userId = result.userId
        state.value = state.value.copy(
            token = result.accessToken,
            userId = result.userId,
            roles = result.accessToken.extractRoles(),
            baseUrl = baseUrl
        )
        refresh()
    }

    fun logout() {
        sessionStore.clearAuth()
        state.value = UiState(baseUrl = sessionStore.baseUrl)
    }

    fun refresh() = runTask {
        val myTickets = runCatching { api.getMyTickets() }.getOrDefault(emptyList())
        val events = if (state.value.canManageEvents) {
            runCatching { api.getEvents() }
                .getOrDefault(emptyList())
                .filter { it.ownerId == state.value.userId }
        } else {
            emptyList()
        }
        val selected = state.value.selectedEventId
            ?.takeIf { selectedId -> events.any { it.id == selectedId } }
            ?: events.firstOrNull()?.id
        val users = if (state.value.canManageEvents) runCatching { api.getUsers() }.getOrDefault(emptyList()) else emptyList()

        state.value = state.value.copy(
            events = events,
            users = users,
            myTickets = myTickets,
            selectedEventId = selected
        )
        if (state.value.canManageEvents && selected != null) loadEventData(selected)
    }

    fun refreshForEvent(eventId: Int) = runTask {
        require(state.value.canManageEvents) { "Перегляд подій доступний лише Owner або Admin." }
        require(state.value.events.any { it.id == eventId }) { "Ця подія недоступна поточному користувачу." }
        loadEventData(eventId)
    }

    fun validateTicket() = runTask {
        val ticketUid = state.value.ticketUid.trim()
        val deviceId = state.value.selectedDeviceId
        require(state.value.canManageEvents) { "Сканування доступне лише власникам подій та адміністраторам." }
        require(ticketUid.isNotBlank()) { "Введіть або відскануйте QR-код квитка." }
        require(deviceId != null) { "Оберіть пристрій контролю." }
        val response = api.validate(ValidationRequest(ticketUid = ticketUid, deviceId = deviceId))
        state.value = state.value.copy(
            lastValidation = response,
            validations = listOf(response) + state.value.validations,
            ticketUid = ""
        )
        state.value.selectedEventId?.let { loadEventData(it) }
    }

    fun createEvent(name: String, eventType: Int, startTime: String, endTime: String) = runTask {
        require(state.value.canManageEvents) { "Створювати події можуть лише Owner або Admin." }
        require(name.isNotBlank()) { "Введіть назву події." }
        require(startTime.isNotBlank() && endTime.isNotBlank()) { "Вкажіть час початку та завершення." }
        val created = api.createEvent(
            EventCreateRequest(
                name = name.trim(),
                eventType = eventType,
                startTime = startTime.trim(),
                endTime = endTime.trim()
            )
        )
        state.value = state.value.copy(selectedEventId = created.id)
        refresh()
    }

    fun createDevice(name: String, location: String) = runTask {
        val eventId = state.value.selectedEventId
        require(eventId != null) { "Оберіть подію для пристрою." }
        require(state.value.canManageSelectedEvent) { "Реєструвати пристрої може лише власник події." }
        require(name.isNotBlank()) { "Введіть назву пристрою." }
        api.createDevice(DeviceCreateRequest(eventId = eventId, name = name.trim(), location = location.trim()))
        loadEventData(eventId)
    }

    fun createTicket(ownerUserId: Int?, ownerName: String, tariffId: Int?, maxUses: String) = runTask {
        val eventId = state.value.selectedEventId
        require(eventId != null) { "Оберіть подію." }
        require(state.value.canManageSelectedEvent) { "Видавати квитки може лише власник події." }
        require(tariffId != null) { "Оберіть тариф." }
        val uses = maxUses.toIntOrNull()
        require(uses != null && uses > 0) { "Кількість використань має бути більшою за 0." }
        api.createTicket(
            TicketCreateRequest(
                tariffId = tariffId,
                ownerUserId = ownerUserId,
                ticketOwnerName = ownerName.trim(),
                maxUses = uses
            )
        )
        val myTickets = runCatching { api.getMyTickets() }.getOrDefault(state.value.myTickets)
        state.value = state.value.copy(myTickets = myTickets)
        loadEventData(eventId)
    }

    private suspend fun loadEventData(eventId: Int) {
        val devices = api.getDevices(eventId)
        val tariffs = api.getTariffs(eventId)
        val tickets = api.getTickets(eventId)
        val validations = api.getValidations(eventId)
        val statistics = runCatching { api.getStatistics(eventId) }.getOrNull()
        state.value = state.value.copy(
            devices = devices,
            tariffs = tariffs,
            tickets = tickets,
            validations = validations,
            statistics = statistics,
            selectedDeviceId = state.value.selectedDeviceId ?: devices.firstOrNull()?.id
        )
    }

    private fun runTask(block: suspend () -> Unit) {
        viewModelScope.launch {
            state.value = state.value.copy(isLoading = true, error = null)
            try {
                block()
            } catch (error: Throwable) {
                state.value = state.value.copy(error = error.message ?: "Невідома помилка")
            } finally {
                state.value = state.value.copy(isLoading = false)
            }
        }
    }
}

val UiState.canManageEvents: Boolean
    get() = roles.any { it.equals("Owner", ignoreCase = true) || it.equals("Admin", ignoreCase = true) }

val UiState.canManageSelectedEvent: Boolean
    get() {
        val currentUserId = userId ?: return false
        val event = events.firstOrNull { it.id == selectedEventId } ?: return false
        return event.ownerId == currentUserId
    }

private fun String.extractRoles(): List<String> = runCatching {
    val payload = split(".").getOrNull(1) ?: return emptyList()
    val decoded = String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING))
    val claims = Json.parseToJsonElement(decoded).jsonObject
    val roleKey = "http://schemas.microsoft.com/ws/2008/06/identity/claims/role"
    val claim = claims[roleKey] ?: claims["role"] ?: return emptyList()
    runCatching { claim.jsonArray.map { it.jsonPrimitive.content } }
        .getOrElse { listOf(claim.jsonPrimitive.content) }
}.getOrDefault(emptyList())
