# 🏋️‍♂️ SportSync: Plataforma de Gestión Deportiva

> **Proyecto TFG** | Android Studio + Realm/MongoDB Atlas

**SportsGO** es una solución integral para la gestión de entrenamientos personalizados. Conecta a entrenadores con sus pupilos mediante una arquitectura **Offline-First**, garantizando que los datos de rendimiento estén siempre disponibles, con o sin conexión.

---

## 🚀 Tecnologías Clave
* **Android Studio**: Entorno de desarrollo nativo.
* **Realm SDK**: Base de datos local de alto rendimiento.
* **MongoDB Atlas**: Sincronización en la nube y persistencia global.
* **Atlas Device Sync**: Gestión automática de datos entre dispositivos.

---

## 📱 Roles de Usuario

El sistema se divide en tres perfiles con funcionalidades específicas:

### 1. 👑 Vista Administrador
* **Gestión de Cuentas**: Alta, baja y modificación de entrenadores y pupilos.
* **Monitorización Global**: Panel de estadísticas sobre el uso de la aplicación.
* **Seguridad**: Control de accesos y mantenimiento de la base de datos MongoDB.

### 2. 👟 Vista Trainer (Entrenador)
* **Planificación**: Creación de rutinas y dietas personalizadas para cada pupilo.
* **Seguimiento**: Visualización en tiempo real del progreso (pesos, marcas, tiempos).
* **Feedback**: Chat o sistema de comentarios sobre los ejercicios realizados.

### 3. 🦾 Vista Pupilo (Atleta)
* **Mi Diario**: Visualización de la rutina diaria asignada por el entrenador.
* **Registro de Cargas**: Introducción de datos durante el entrenamiento (series, repeticiones).
* **Evolución**: Gráficas de progreso personal almacenadas de forma local y sincronizadas.

---

## 🛠️ Estructura de Datos (Modelos Realm)

La arquitectura de datos se basa en los siguientes objetos principales:

* **User**: Perfil con roles (`Admin`, `Trainer`, `Pupil`).
* **TrainingPlan**: Lista de ejercicios, series y descansos.
* **WorkoutLog**: Registro histórico de entrenamientos completados.

---

## 🔧 Configuración del Proyecto

1. **Clonar el repositorio:**
   ```bash
   git clone (https://github.com/juaangr/TFG_AdrianPeter_JuanGomez.git)
