# WeatherApp – Consulta del Clima y Pronóstico
---
![Static Badge](https://img.shields.io/badge/Kotlin-Language-7F52FF?style=for-the-badge&logo=Kotlin)
![Static Badge](https://img.shields.io/badge/Jetpack_compose-Android-green?style=for-the-badge&logo=jetpackcompose)
![Static Badge](https://img.shields.io/badge/Clean-architecture-CB2D29?style=for-the-badge&logo=ccleaner)
![Static Badge](https://img.shields.io/badge/MVVM-Patter%20Arch-%232C4F7C?style=for-the-badge&logo=ccleaner)
![Static Badge](https://img.shields.io/badge/SOLID-Principle-232C4F7C?style=for-the-badge&logo=solid)

Aplicación Android diseñada para consultar el clima actual y el pronóstico de los próximos días utilizando la API de WeatherAPI. El proyecto sigue principios de Clean Architecture, MVVM y buenas prácticas modernas en Kotlin para garantizar escalabilidad, mantenibilidad y calidad de código.

---
| Splash app | Real-time consult weather | Detail location weather |
|---------------------------------|------------------------------------------|-------------------------------------------|
| <img width="300" src="https://github.com/user-attachments/assets/d3c54203-253b-4a8e-9cf9-a926f505bc02" />|<img width="300" src="https://github.com/user-attachments/assets/e17a4f4d-6be7-4626-b870-92769d85ad61"/>|<img width="300" src="https://github.com/user-attachments/assets/8f83824d-9e05-470d-b79f-b76736587978"/>|---

## 🚀 Funcionalidad Principal

### Búsqueda de Ubicaciones
- Búsqueda de ubicaciones en tiempo real mientras el usuario escribe.
- **Debounce de 500ms** para optimizar las llamadas a la API.
- Validación de mínimo 3 caracteres antes de realizar búsquedas.
- Visualización del nombre, región y país en los resultados.

### Detalle del Clima
- **Clima actual**: Temperatura, sensación térmica, condición e icono.
- **Pronóstico de 3 días**: Temperaturas máximas y mínimas con iconos.
- **Temperatura promedio**: Cálculo automático del promedio de temperaturas máximas con 1 decimal de precisión.
- **Carga de imágenes**: Integración con Coil para cargar iconos del clima desde URLs.
- **Formateo de fechas**: Conversión automática de fechas ISO a formato DD/MM.

### Características Generales
- Splash screen inicial con el identificador del app.
- Navegación type-safe con Navigation 3 y serialización.
- Soporte completo para cambio de orientación.
- Manejo robusto de errores y estados inesperados.
- Retry automático en caso de errores de red.

---

## 🧱 Arquitectura
La aplicación está desarrollada siguiendo **Clean Architecture**, separando las capas en:

### **Domain**
- Casos de uso
- Entidades
- Lógica de negocio

### **Data**
- Repositorios
- Data sources
- Modelos para la API (DTO)
- Mappers

### **Presentation**
- ViewModels
- Estados de UI
- Navegación

Se aplican principios SOLID, programación funcional cuando corresponde, funciones pequeñas y componentes reutilizables.

---

## 🌐 API Utilizada (WeatherAPI)

**Búsqueda:**  
`https://api.weatherapi.com/v1/search.json?key=ApiKey&q=Text`

**Pronóstico:**  
`https://api.weatherapi.com/v1/forecast.json?key=ApiKey&q=Name&days=3`

Documentación oficial: https://www.weatherapi.com/docs/

---

## 🧩 Requisitos Técnicos
- **Lenguaje:** Kotlin
- **Arquitectura:** Clean Architecture + MVVM
- **Min SDK:** 23
- **Target SDK:** Última versión estable
- **Dependencias:** AndroidX, Coroutines, Retrofit, entre otras.

---

## ❗ ¿Por qué Min SDK 23 y no 21?

Aunque la prueba sugería usar API 21, se eligió **minSdkVersion 23** por razones técnicas y estratégicas:

1. **Compatibilidad con librerías modernas:**  
   AndroidX, Navigation, y otros componentes actuales ya no brindan soporte completo para API 21. Usar una versión tan antigua genera errores de compilación y fallos en tiempo de ejecución.

2. **Seguridad y rendimiento:**  
   A partir de API 23 se introdujeron mejoras críticas en permisos de ejecución, seguridad del sistema, optimizaciones de batería y APIs más robustas. Mantener compatibilidad con API 21 implica renunciar a estas garantías.

3. **Evitar código complejo y propenso a errores:**  
   Mantener compatibilidad con versiones antiguas requiere condicionales constantes (`if (Build.VERSION >= ...)`), dificultando las pruebas y el mantenimiento.

4. **Impacto mínimo en usuarios reales:**  
   Los dispositivos con API < 23 representan **menos del 1% del mercado actual**. El costo de soportar esa minoría supera ampliamente el beneficio.

**En resumen:**  
Usar Min SDK 23 permite una app más segura, moderna, estable y fácil de mantener, sin afectar significativamente a los usuarios.

---

## 🔄 Debounce y Gestión de Estados

### ¿Qué es Debounce?
El **debounce** es una técnica que retrasa la ejecución de una función hasta que haya pasado un tiempo determinado sin que se vuelva a invocar. En esta app, se usa para optimizar las búsquedas:

```kotlin
_searchQuery
    .debounce(500L)  // Espera 500ms sin cambios
    .distinctUntilChanged()  // Solo si el valor cambió
    .filter { it.trim().length >= 3 || it.isEmpty() }
    .onEach { query -> searchLocations(query) }
    .launchIn(viewModelScope)
```

**Ventajas:**
- **Reduce llamadas a la API**: Si el usuario escribe "Mosquera", en lugar de hacer 8 llamadas (una por letra), solo hace 1.
- **Mejora el rendimiento**: Menos procesamiento y consumo de red.
- **Mejor experiencia de usuario**: Evita resultados intermedios confusos.

**Ejemplo práctico:**
```
Usuario escribe: M -> o -> s -> q -> u -> e -> r -> a
Sin debounce: 8 llamadas API ❌
Con debounce: 1 llamada API (después de 500ms de inactividad) ✅
```

### Estados de la UI

La aplicación maneja 5 estados diferentes para la búsqueda:

| Estado | Descripción | Cuándo se muestra |
|--------|-------------|-------------------|
| **Idle** | Estado inicial, sin búsqueda activa | Al iniciar o limpiar la búsqueda |
| **Loading** | Búsqueda en progreso | Mientras se consulta la API |
| **Success** | Resultados encontrados | Cuando la API retorna ubicaciones |
| **Empty** | Sin resultados | Cuando la búsqueda no encuentra ubicaciones |
| **Error** | Error en la búsqueda | Cuando falla la conexión o la API |

```kotlin
sealed class SearchUiState {
    data object Idle : SearchUiState()
    data object Loading : SearchUiState()
    data class Success(val locations: List<Location>) : SearchUiState()
    data class Empty(val query: String) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}
```

Cada estado renderiza una UI diferente, proporcionando feedback claro al usuario en todo momento.

---

## 🧪 Pruebas

### Cobertura de Tests

El proyecto cuenta con **cobertura completa de tests unitarios** en todas las capas:

#### **Tests Implementados (Total: 60+ tests)**

| Componente | Tests | Descripción |
|------------|-------|-------------|
| **LocationMapperTest** | 6 tests | Mapeo de DTOs a modelos de dominio |
| **CurrentWeatherMapperTest** | 5 tests | Mapeo de clima actual, URLs de iconos |
| **ForecastDayMapperTest** | 7 tests | Mapeo de pronóstico diario, listas |
| **WeatherForecastMapperTest** | 6 tests | Integración de mappers, verificación de llamadas |
| **WeatherRepositoryImplTest** | 12 tests | Búsqueda y pronóstico, manejo de errores HTTP/IO |
| **SearchLocationsUseCaseTest** | 3 tests | Validación de query, casos exitosos y fallidos |
| **GetWeatherForecastUseCaseTest** | 9 tests | Validación de ubicación, trim, manejo de errores |
| **CalculateAverageTemperatureUseCaseTest** | 12 tests | Cálculo de promedios, redondeo, temperaturas negativas |
| **WeatherSearchViewModelTest** | 8+ tests | Debounce, estados de UI, eventos |
| **WeatherDetailViewModelTest** | 10 tests | Carga de pronóstico, retry, estados |
| **StringExtensionsTest** | 6 tests | Formateo de fechas, edge cases |

### Estrategia de Testing
- **Pruebas unitarias** para ViewModels, UseCases, Repositories y Mappers.
- **Mocks con MockK** para aislar dependencias.
- **Coroutines Test** para probar código asíncrono con `StandardTestDispatcher`.
- **Turbine** para testing de Flows.
- **Given-When-Then** como patrón estándar en todos los tests.

### Patrón Provider en Tests

Todos los tests siguen el **patrón Provider** para crear instancias de forma consistente y reutilizable:

```kotlin
// ❌ Forma incorrecta: Crear instancias directamente
@Test
fun test() {
    val mapper = LocationMapper()
    val apiService = mockk<WeatherApiService>()
    val repository = WeatherRepositoryImpl(apiService, "key", mapper)
    // ...
}

// ✅ Forma correcta: Usar funciones provider
@Test
fun test() {
    val mapper = providesMapperMock()
    val apiService = providesApiServiceMock()
    val sut = providesSut(apiService, mapper)
    // ...
}
```

**Ventajas del patrón Provider:**

1. **Reutilización**: Las funciones provider se usan en múltiples tests, evitando duplicación.
   ```kotlin
   private fun providesSut(useCase: SearchLocationsUseCase) = 
       WeatherSearchViewModel(useCase)
   ```

2. **Configuración centralizada**: Los mocks se configuran en un solo lugar.
   ```kotlin
   private fun providesUseCaseMock(
       returnEmptyList: Boolean = false,
       returnError: Boolean = false
   ) = mockk<SearchLocationsUseCase>().apply {
       // Configuración del mock
   }
   ```

3. **Legibilidad**: Los tests son más claros y expresivos.
   ```kotlin
   // Given
   val useCase = providesUseCaseMock(returnError = true)
   val sut = providesSut(useCase)
   ```

4. **Mantenibilidad**: Si cambia el constructor, solo actualizas la función provider.

5. **Flexibilidad**: Parámetros opcionales permiten diferentes escenarios.
   ```kotlin
   providesUseCaseMock()  // Caso exitoso por defecto
   providesUseCaseMock(returnEmptyList = true)  // Lista vacía
   providesUseCaseMock(returnError = true)  // Error
   ```

6. **Consistencia**: Todos los tests siguen el mismo patrón, facilitando la comprensión.

**Ejemplo completo:**
```kotlin
@Test
fun `searchLocations should return success when API call succeeds`() = runTest {
    // Given - Usando providers
    val apiService = providesApiServiceMock()
    val mapper = providesMapperMock()
    val sut = providesSut(apiService, mapper)
    
    // When
    sut.searchLocations("Mosquera").collect { result ->
        // Then
        assert(result.isSuccess)
        assert(result.getOrNull()?.size == 3)
    }
}
```

---

---

## 📦 Cómo Ejecutar el Proyecto

### Requisitos Previos
- **Android Studio**: Hedgehog (2023.1.1) o superior
- **JDK**: 11 o superior
- **Gradle**: 8.13.1 (incluido en el proyecto)
- **Kotlin**: 2.2.21
- **API Key de WeatherAPI**: Obtén una gratis en [weatherapi.com](https://www.weatherapi.com/)

### Pasos para Ejecutar

1. **Clonar el repositorio**
   ```bash
   git clone <url-del-repositorio>
   cd weather_app
   ```

2. **Abrir el proyecto en Android Studio**
   - Abre Android Studio
   - Selecciona "Open" y navega hasta la carpeta del proyecto
   - Espera a que Android Studio indexe el proyecto

3. **Configurar la API Key** (⚠️ IMPORTANTE)
   
   El proyecto usa `BuildConfig` para manejar la API Key de forma segura. La clave se lee desde `local.properties` que **NO está versionado en Git** por seguridad.
   
   **Pasos:**
   - Crea un archivo llamado `local.properties` en la raíz del proyecto (al mismo nivel que `build.gradle.kts`)
   - Agrega la siguiente línea con tu API Key:
     ```properties
     WEATHER_API_KEY=tu_api_key_aqui
     ```
   - Obtén tu API Key gratis en [weatherapi.com](https://www.weatherapi.com/)
   
   **¿Cómo funciona?**
   - El archivo `app/build.gradle.kts` lee la API Key desde `local.properties`
   - La inyecta en `BuildConfig.WEATHER_API_KEY` durante la compilación
   - El `NetworkModule` usa `BuildConfig.WEATHER_API_KEY` para las peticiones HTTP
   - El archivo `local.properties` está en `.gitignore` y **nunca se sube a Git**
   
   **Nota**: Si no configuras la API Key, la app compilará pero las búsquedas fallarán con error de autenticación.

4. **Sincronizar dependencias**
   - Android Studio detectará automáticamente los archivos Gradle
   - Haz clic en "Sync Now" cuando aparezca la notificación
   - Espera a que se descarguen todas las dependencias (puede tardar unos minutos la primera vez)

5. **Ejecutar la aplicación**
   
   **Opción A: Dispositivo físico**
   - Habilita las opciones de desarrollador en tu dispositivo Android
   - Activa la depuración USB
   - Conecta el dispositivo por USB
   - Selecciona tu dispositivo en el menú desplegable de Android Studio
   - Haz clic en el botón "Run" (▶️)
   
   **Opción B: Emulador**
   - Abre el AVD Manager (Tools > Device Manager)
   - Crea un nuevo dispositivo virtual o selecciona uno existente
   - Recomendado: Pixel 6 con API 34 (Android 14)
   - Inicia el emulador
   - Haz clic en el botón "Run" (▶️)

6. **Verificar la instalación**
   - La app mostrará un Splash Screen durante 2 segundos
   - Luego navegará automáticamente a la pantalla de búsqueda
   - Escribe al menos 3 caracteres para buscar una ubicación
   - Deberías ver resultados después de 500ms (debounce)

### Ejecutar Tests Unitarios

**Desde Android Studio:**
1. Navega a la carpeta `app/src/test/java`
2. Haz clic derecho en la carpeta o en un archivo de test específico
3. Selecciona "Run Tests"

**Desde terminal:**
```bash
./gradlew test
```

**Tests disponibles:**

**Mappers:**
- `LocationMapperTest`: Mapeo de ubicaciones (6 tests)
- `CurrentWeatherMapperTest`: Mapeo de clima actual (5 tests)
- `ForecastDayMapperTest`: Mapeo de pronóstico diario (7 tests)
- `WeatherForecastMapperTest`: Integración de mappers (6 tests)

**Repositorios:**
- `WeatherRepositoryImplTest`: Búsqueda y pronóstico, errores HTTP/IO (12 tests)

**Casos de Uso:**
- `SearchLocationsUseCaseTest`: Validación de búsqueda (3 tests)
- `GetWeatherForecastUseCaseTest`: Obtención de pronóstico (9 tests)
- `CalculateAverageTemperatureUseCaseTest`: Cálculo de promedios (12 tests)

**ViewModels:**
- `WeatherSearchViewModelTest`: Búsqueda con debounce (8+ tests)
- `WeatherDetailViewModelTest`: Detalle del clima (10 tests)

**Utilidades:**
- `StringExtensionsTest`: Formateo de fechas (6 tests)

### Solución de Problemas Comunes

**Error: "WEATHER_API_KEY not found"**
- Verifica que creaste el archivo `local.properties`
- Asegúrate de que la API Key esté correctamente configurada
- Sincroniza el proyecto nuevamente (File > Sync Project with Gradle Files)

**Error: "Module with the Main dispatcher is missing"**
- Este error solo ocurre en tests unitarios
- Asegúrate de usar `Dispatchers.setMain(testDispatcher)` en el `@Before`

**La app compila pero no busca ubicaciones**
- Verifica tu conexión a internet
- Confirma que la API Key sea válida
- Revisa los logs en Logcat para ver errores de red

**Gradle sync falla**
- Verifica tu conexión a internet
- Limpia el proyecto: Build > Clean Project
- Invalida cachés: File > Invalidate Caches / Restart

### Estructura del Proyecto
```
weather_app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/yei/dev/weather_app/
│   │   │   │   ├── data/          # Capa de datos
│   │   │   │   ├── domain/        # Capa de dominio
│   │   │   │   ├── presentation/  # Capa de presentación
│   │   │   │   └── di/            # Inyección de dependencias
│   │   │   └── res/               # Recursos (layouts, strings, etc.)
│   │   └── test/                  # Tests unitarios
│   └── build.gradle.kts           # Configuración de Gradle del módulo
├── gradle/                        # Wrapper de Gradle
├── local.properties              # API Key (no versionado)
├── local.properties.example      # Plantilla para API Key
└── build.gradle.kts              # Configuración de Gradle del proyecto
```

---

## 📄 Licencia
Proyecto de uso educativo y demostrativo. Puedes extenderlo y adaptarlo según tus necesidades.
