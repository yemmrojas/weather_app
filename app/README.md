# WeatherApp – Consulta del Clima y Pronóstico
---
![Static Badge](https://img.shields.io/badge/Kotlin-Language-7F52FF?style=for-the-badge&logo=Kotlin)
![Static Badge](https://img.shields.io/badge/Java-Language-orange?style=for-the-badge&logo=java)
![Static Badge](https://img.shields.io/badge/Jetpack_compose-Android-green?style=for-the-badge&logo=jetpackcompose)
![Static Badge](https://img.shields.io/badge/Clean-architecture-CB2D29?style=for-the-badge&logo=ccleaner)
![Static Badge](https://img.shields.io/badge/MVVM-Patter%20Arch-%232C4F7C?style=for-the-badge&logo=ccleaner)
![Static Badge](https://img.shields.io/badge/SOLID-Principle-232C4F7C?style=for-the-badge&logo=solid)

Aplicación Android diseñada para consultar el clima actual y el pronóstico de los próximos días utilizando la API de WeatherAPI. El proyecto sigue principios de Clean Architecture, MVVM y buenas prácticas modernas en Kotlin para garantizar escalabilidad, mantenibilidad y calidad de código.

---

## 🚀 Funcionalidad Principal
- Búsqueda de ubicaciones en tiempo real mientras el usuario escribe.
- Visualización del nombre y país en los resultados.
- Detalle del clima actual con icono, descripción y temperatura.
- Pronóstico de 3 días (incluyendo el actual).
- Splash screen inicial.
- Soporte completo para cambio de orientación.
- Manejo de errores y estados inesperados.

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

## 🧪 Pruebas
- Pruebas unitarias para casos de uso.
- Pruebas instrumentadas para vistas y flujos.
- Pruebas E2E opcionales.

---

## 📦 Cómo Ejecutar el Proyecto
1. Clonar el repositorio.
2. Abrir en Android Studio (versión Flamingo o superior).
3. Insertar tu API Key en el archivo correspondiente.
4. Ejecutar en dispositivo físico o emulador.

---

## 📄 Licencia
Proyecto de uso educativo y demostrativo. Puedes extenderlo y adaptarlo según tus necesidades.
