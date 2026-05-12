# UML та ER-діаграми мобільного застосунку Ticket Control

## UML Діаграма Прецедентів

```mermaid
flowchart LR
    User["Звичайний користувач"]
    Owner["Власник події"]
    Admin["Адміністратор"]

    UC_Login(("Авторизуватися"))
    UC_ViewOwnTickets(("Переглянути власні квитки"))
    UC_ViewTicketQr(("Відкрити QR-код квитка"))
    UC_CopyUid(("Скопіювати UID квитка"))
    UC_ViewOwnedEvents(("Переглянути власні події"))
    UC_ViewEventTickets(("Переглянути квитки події"))
    UC_IssueTicket(("Видати квиток"))
    UC_CreateEvent(("Створити подію"))
    UC_RegisterDevice(("Зареєструвати пристрій"))
    UC_SendHeartbeat(("Оновити стан пристрою"))
    UC_ScanQr(("Сканувати QR-код"))
    UC_ValidateTicket(("Перевірити квиток"))
    UC_ViewStats(("Переглянути статистику події"))
    UC_Logout(("Вийти із системи"))

    User --> UC_Login
    User --> UC_ViewOwnTickets
    User --> UC_ViewTicketQr
    User --> UC_CopyUid
    User --> UC_Logout

    Owner --> UC_Login
    Owner --> UC_ViewOwnTickets
    Owner --> UC_ViewTicketQr
    Owner --> UC_CopyUid
    Owner --> UC_ViewOwnedEvents
    Owner --> UC_ViewEventTickets
    Owner --> UC_IssueTicket
    Owner --> UC_CreateEvent
    Owner --> UC_RegisterDevice
    Owner --> UC_SendHeartbeat
    Owner --> UC_ScanQr
    Owner --> UC_ValidateTicket
    Owner --> UC_ViewStats
    Owner --> UC_Logout

    Admin --> UC_Login
    Admin --> UC_ViewOwnTickets
    Admin --> UC_ViewTicketQr
    Admin --> UC_CopyUid
    Admin --> UC_ViewOwnedEvents
    Admin --> UC_ViewEventTickets
    Admin --> UC_IssueTicket
    Admin --> UC_CreateEvent
    Admin --> UC_RegisterDevice
    Admin --> UC_SendHeartbeat
    Admin --> UC_ScanQr
    Admin --> UC_ValidateTicket
    Admin --> UC_ViewStats
    Admin --> UC_Logout

    UC_ScanQr -. "include" .-> UC_ValidateTicket
    UC_ValidateTicket -. "include" .-> UC_SendHeartbeat
    UC_ViewOwnTickets -. "include" .-> UC_ViewTicketQr
    UC_ViewEventTickets -. "extend" .-> UC_IssueTicket
```

## UML Діаграма Компонентів

```mermaid
flowchart TB
    subgraph Android["Мобільний застосунок Android"]
        UI["Presentation Layer\nJetpack Compose Screens"]
        VM["TicketControlViewModel\nстан, ролі, бізнес-дії"]
        Scanner["QR Scanner\nCameraX + ML Kit"]
        Components["Reusable UI Components\nPickers, Cards, Navigation"]
        Models["Data Models\nDTO / UI State"]
        Api["Remote API Client\nRetrofit + OkHttp"]
        Session["Local Session Store\nSharedPreferences"]
        Coil["QR Image Loader\nCoil"]
    end

    subgraph Server["Backend API"]
        AuthApi["Users API\nlogin, users"]
        EventsApi["Events API"]
        TicketsApi["Ticket API"]
        DevicesApi["Device API"]
        ValidationApi["Validation API"]
        StatsApi["Statistics API"]
    end

    Camera["Камера пристрою"]
    Storage["Локальне сховище Android"]

    UI --> Components
    UI --> VM
    UI --> Scanner
    Scanner --> Camera
    Scanner --> VM
    VM --> Models
    VM --> Api
    VM --> Session
    Session --> Storage
    Coil --> TicketsApi
    UI --> Coil

    Api --> AuthApi
    Api --> EventsApi
    Api --> TicketsApi
    Api --> DevicesApi
    Api --> ValidationApi
    Api --> StatsApi
```

## ER-Модель Даних Мобільної Платформи

Мобільний застосунок не має власної реляційної БД. Постійно локально зберігається лише сесія користувача, а доменні сутності синхронізуються через REST API та живуть у стані застосунку.

```mermaid
erDiagram
    LOCAL_SESSION {
        string baseUrl
        string accessToken
        int userId
    }

    USER {
        int id
        string username
        string email
        string[] roles
    }

    EVENT {
        int id
        string name
        string eventType
        datetime startTime
        datetime endTime
        int ownerId
    }

    DEVICE {
        int id
        int eventId
        string name
        string location
        int status
        datetime lastHeartbeat
    }

    TARIFF {
        int id
        int eventId
        string name
        double price
    }

    TICKET {
        int id
        int ownerUserId
        string ticketUid
        string tariffName
        string eventName
        string ownerName
        string status
        int maxUses
        int currentUses
        datetime validTo
    }

    VALIDATION {
        bool isSuccess
        string message
        datetime validationTime
        string ticketOwner
        int currentUses
        int maxUses
    }

    EVENT_STATISTICS {
        int eventId
        string eventName
        double totalRevenue
        double potentialRevenue
        int totalTicketsIssued
        int ticketsUsed
        double attendancePercentage
    }

    TARIFF_STATISTICS {
        string tariffName
        int ticketsSold
        int ticketsUsed
        double revenueGenerated
    }

    LOCAL_SESSION ||--o| USER : "поточний користувач"
    USER ||--o{ EVENT : "володіє"
    USER ||--o{ TICKET : "має"
    EVENT ||--o{ DEVICE : "містить"
    EVENT ||--o{ TARIFF : "має"
    EVENT ||--o{ TICKET : "відображається у"
    TARIFF ||--o{ TICKET : "визначає"
    DEVICE ||--o{ VALIDATION : "виконує"
    TICKET ||--o{ VALIDATION : "перевіряється"
    EVENT ||--o| EVENT_STATISTICS : "агрегує"
    EVENT_STATISTICS ||--o{ TARIFF_STATISTICS : "містить"
```

## UML Діаграма Діяльності

```mermaid
flowchart TD
    Start([Старт застосунку])
    LoadSession["Завантажити baseUrl, token, userId із SessionStore"]
    HasToken{"Є accessToken?"}
    LoginScreen["Показати екран входу"]
    Login["Ввести сервер, email, пароль"]
    AuthRequest["Надіслати login-запит"]
    AuthOk{"Авторизація успішна?"}
    SaveSession["Зберегти token, userId, baseUrl"]
    LoadData["Завантажити дані користувача"]
    RoleCheck{"Роль Owner/Admin?"}

    UserTickets["Показати тільки власні квитки,\nзгруповані за подіями"]
    ManagerHome["Показати навігацію:\nСкан, Події, Мої, Ще"]

    SelectEvent["Обрати власну подію"]
    LoadEventData["Завантажити пристрої, тарифи,\nквитки, статистику"]
    OwnerAction{"Обрана дія"}

    Scan["Сканувати QR-код або ввести UID"]
    Heartbeat["Надіслати heartbeat пристрою"]
    Validate["Надіслати запит перевірки квитка"]
    ValidationOk{"isSuccess == true?"}
    Granted["Показати доступ дозволено"]
    Denied["Показати причину відмови"]

    IssueTicket["Заповнити власника, тариф,\nкількість використань"]
    CreateTicket["Надіслати запит видачі квитка"]
    RefreshTickets["Оновити список квитків"]

    CreateEvent["Створити нову подію"]
    RegisterDevice["Зареєструвати пристрій події"]
    Stats["Переглянути статистику обраної події"]
    MyTickets["Переглянути власні квитки\nта QR-коди"]
    Logout["Вийти із системи"]
    End([Кінець])

    Start --> LoadSession --> HasToken
    HasToken -- "ні" --> LoginScreen --> Login --> AuthRequest --> AuthOk
    AuthOk -- "ні" --> LoginScreen
    AuthOk -- "так" --> SaveSession --> LoadData
    HasToken -- "так" --> LoadData
    LoadData --> RoleCheck
    RoleCheck -- "ні" --> UserTickets
    RoleCheck -- "так" --> ManagerHome

    ManagerHome --> SelectEvent --> LoadEventData --> OwnerAction
    OwnerAction -- "Скан" --> Scan --> Heartbeat --> Validate --> ValidationOk
    ValidationOk -- "так" --> Granted --> RefreshTickets
    ValidationOk -- "ні" --> Denied --> RefreshTickets
    OwnerAction -- "Видати квиток" --> IssueTicket --> CreateTicket --> RefreshTickets
    OwnerAction -- "Створити подію" --> CreateEvent --> LoadData
    OwnerAction -- "Зареєструвати пристрій" --> RegisterDevice --> LoadEventData
    OwnerAction -- "Статистика" --> Stats
    OwnerAction -- "Мої квитки" --> MyTickets

    UserTickets --> MyTickets
    ManagerHome --> Logout --> End
    UserTickets --> Logout --> End
```

## Опис Прийнятих Інженерних Рішень

Мобільний застосунок реалізовано на стеку `Kotlin + Jetpack Compose` з поділом на шари даних, віддаленої взаємодії та представлення. Для роботи з API використано `Retrofit` і `OkHttp`, для керування станом — `ViewModel` та єдиний `UiState`, а для QR-сканування — `CameraX` разом із `ML Kit`. Така структура дозволила відокремити мережеву логіку від інтерфейсу, спростити підтримку коду та зробити поведінку екранів передбачуваною.

Інтерфейс побудовано з урахуванням ролей користувачів: звичайний користувач бачить лише власні квитки та QR-коди, тоді як Owner і Admin отримують доступ до сканування, керування власними подіями, пристроями, видачі квитків і статистики. Для зручності мобільної взаємодії події та квитки об’єднано в один сценарій перегляду, а широку верхню навігацію замінено нижньою панеллю. Додатково враховано серверну модель heartbeat для пристроїв контролю та окрему обробку бізнес-результату `isSuccess`, що дає змогу коректно відображати як успішні перевірки, так і причини відмови.
