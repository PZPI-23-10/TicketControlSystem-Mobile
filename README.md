# Ticket Control Mobile

Android/Kotlin застосунок для системи контролю квитків. Серверний проєкт не змінюється: застосунок використовує його тільки як REST API.

## Основні сценарії

- Вхід контролера через `POST /api/Users/login`.
- Завантаження подій через `GET /api/Events`.
- Вибір пристрою контролю через `GET /api/Device?eventId=...`.
- Надсилання heartbeat пристрою через `POST /api/Device/heartbeat/{deviceId}`.
- Сканування QR-коду камерою та перевірка квитка через `POST /api/Validation/check`.
- Перегляд журналу перевірок через `GET /api/Validation?eventId=...`.
- Перегляд квитків, тарифів і статистики події через `Ticket`, `Tariff`, `Statistics`.

## Запуск backend

Запустіть сервер окремо з кореня репозиторію:

```powershell
dotnet run --project .\TicketControlSystem\TicketControlSystem.csproj --launch-profile http
```

За поточним `launchSettings.json` сервер стартує на `http://localhost:5229`.

## Запуск Android

1. В Android Studio відкрийте саме папку `mobile-android`, а не корінь репозиторію.
2. Переконайтесь, що Gradle використовує JDK 17+. Найпростіший варіант на Windows:

   ```powershell
   $env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
   $env:Path="$env:JAVA_HOME\bin;$env:Path"
   ```

3. Якщо запускаєте збірку з терміналу без Android Studio, задайте шлях до Android SDK:

   ```powershell
   $env:ANDROID_HOME='C:\Users\<your-user>\AppData\Local\Android\Sdk'
   $env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
   ```

4. Для перевірки збірки виконайте:

   ```powershell
   .\gradlew.bat :app:assembleDebug
   ```

5. Запустіть конфігурацію `app` на емуляторі або фізичному пристрої.

Для Android-емулятора стандартна адреса сервера в застосунку вже встановлена як `http://10.0.2.2:5229/`.
Для фізичного Android-пристрою замініть адресу сервера на IP вашого комп'ютера в локальній мережі, наприклад `http://192.168.1.20:5229/`.

## Важливі відповідності backend

- Backend віддає JSON у camelCase, тому мобільні DTO використовують ті самі назви полів.
- JWT автоматично додається в заголовок `Authorization: Bearer ...` після входу.
- `Device.Status` на сервері є enum і може серіалізуватись числом: `0` означає активний, `1` означає неактивний.
- QR payload у серверному `GET /api/Ticket/{id}/qr` дорівнює `TicketUid`, тому сканер передає саме UID у `Validation/check`.
