package ua.ticketcontrol.mobile.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ua.ticketcontrol.mobile.data.model.AuthResponse
import ua.ticketcontrol.mobile.data.model.DeviceCreateRequest
import ua.ticketcontrol.mobile.data.model.DeviceDto
import ua.ticketcontrol.mobile.data.model.EventCreateRequest
import ua.ticketcontrol.mobile.data.model.EventDto
import ua.ticketcontrol.mobile.data.model.EventStatisticsDto
import ua.ticketcontrol.mobile.data.model.LoginRequest
import ua.ticketcontrol.mobile.data.model.TariffDto
import ua.ticketcontrol.mobile.data.model.TicketCreateRequest
import ua.ticketcontrol.mobile.data.model.TicketDto
import ua.ticketcontrol.mobile.data.model.UserDto
import ua.ticketcontrol.mobile.data.model.ValidationRequest
import ua.ticketcontrol.mobile.data.model.ValidationResponse

interface TicketControlApi {
    @POST("api/Users/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/Events")
    suspend fun getEvents(): List<EventDto>

    @POST("api/Events")
    suspend fun createEvent(@Body request: EventCreateRequest): EventDto

    @GET("api/Device")
    suspend fun getDevices(@Query("eventId") eventId: Int? = null): List<DeviceDto>

    @POST("api/Device")
    suspend fun createDevice(@Body request: DeviceCreateRequest): DeviceDto

    @POST("api/Device/heartbeat/{deviceId}")
    suspend fun heartbeat(@Path("deviceId") deviceId: Int)

    @GET("api/Tariff")
    suspend fun getTariffs(@Query("eventId") eventId: Int? = null): List<TariffDto>

    @GET("api/Ticket")
    suspend fun getTickets(@Query("eventId") eventId: Int? = null): List<TicketDto>

    @GET("api/Ticket/my")
    suspend fun getMyTickets(): List<TicketDto>

    @POST("api/Ticket")
    suspend fun createTicket(@Body request: TicketCreateRequest): TicketDto

    @GET("api/Users")
    suspend fun getUsers(): List<UserDto>

    @POST("api/Validation/check")
    suspend fun validate(@Body request: ValidationRequest): ValidationResponse

    @GET("api/Validation")
    suspend fun getValidations(@Query("eventId") eventId: Int? = null): List<ValidationResponse>

    @GET("api/Statistics/{eventId}")
    suspend fun getStatistics(@Path("eventId") eventId: Int): EventStatisticsDto
}
