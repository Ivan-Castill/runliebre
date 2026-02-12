# RunLiebre 🐇💨

**RunLiebre** es una aplicación nativa de Android desarrollada en Kotlin para el monitoreo y gestión de corredores en tiempo real. Utiliza OpenStreetMap (OSM) para el rastreo GPS y Firebase como backend para la autenticación y sincronización de datos.

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple) ![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore-orange) ![Platform](https://img.shields.io/badge/Platform-Android-green) ![License](https://img.shields.io/badge/License-MIT-blue)

## 📱 Características Principales

* **Rastreo GPS en Tiempo Real:** Visualización de corredores en el mapa usando `osmdroid` (OpenStreetMap).
* **Roles de Usuario:**
    * **Corredor:** Comparte su ubicación en segundo plano mientras está activo.
    * **Admin:** Panel de control para visualizar a todos los corredores simultáneamente.
* **Seguridad:** Autenticación mediante correo/contraseña y gestión de sesiones.
* **Interfaz Moderna:** Diseño "Glass & Card" con componentes Material Design.

---

## 📸 Capturas de Pantalla

### 1️⃣ Pantalla de Login
Interfaz de autenticación con email y contraseña.

| Pantalla de Login | 
|:---:|
| Pantalla de Registro | 
| Pantalla de recuperacion de contraseña |

<img width="200" alt="image" src="https://github.com/user-attachments/assets/b7457d3d-77d4-4898-a23d-73a84bc520cb" />
<img width="200" alt="image" src="https://github.com/user-attachments/assets/22281e41-1858-4cf7-92ee-0d780404bd3c" />
<img width="200"  alt="image" src="https://github.com/user-attachments/assets/32754748-0025-4382-90df-0e87ff23eed0" />


---

### 2️⃣ Mapa del Admin
Vista del administrador con todos los corredores en tiempo real.

| Mapa Admin |
|:---:|
| Gestion de Usuarios |
| Perfil admin |
<img width="200" alt="image" src="https://github.com/user-attachments/assets/1283d9f2-b6da-466f-8f89-0a8b09a01a6f" />
<img width="200" alt="image" src="https://github.com/user-attachments/assets/e3630d76-039b-4bf9-9056-015806818a2e" />
<img width="200" alt="image" src="https://github.com/user-attachments/assets/f13bd30e-29b9-416a-8d5f-e393d1df29ea" />



---

### 3️⃣ Perfil del Corredor
Interfaz personal del corredor con información de actividad.

| Mapa del  Corredor |
|:---:|
| Perfil del Corredor | 
<img width="200" alt="image" src="https://github.com/user-attachments/assets/1e9d21d2-cc8c-4ff4-a147-9e0c21a28d2b" />
<img width="200" alt="image" src="https://github.com/user-attachments/assets/81848627-8385-41ac-9b98-e1a6e25101e1" />


---

## 🎥 Video de Demostración

Mira una demostración completa de todas las funcionalidades de la aplicación:

[![Ver Video de Demostración](https://img.shields.io/badge/📹%20Ver%20Video-YouTube-red?style=for-the-badge)]([https://youtube.com/tu_video_aqui](https://youtu.be/pcjo43Vh_-I?si=tMBYzx90_rjEHIyv))


---

## ⚙️ Configuración del Proyecto (Importante)

Este proyecto utiliza **Firebase** para el backend. Por razones de seguridad, el archivo de configuración `google-services.json` no está incluido en este repositorio. Para ejecutar la app, debes configurar tu propio proyecto de Firebase.

### Paso 1: Crear Proyecto en Firebase
1.  Ve a la [Consola de Firebase](https://console.firebase.google.com/).
2.  Crea un nuevo proyecto llamado `RunLiebre` (o el nombre que prefieras).
3.  Desactiva Google Analytics (no es necesario para este demo).

### Paso 2: Registrar la App Android
1.  Dentro del proyecto, haz clic en el icono de **Android**.
2.  En **Nombre del paquete**, asegúrate de poner exactamente el mismo que está en el `build.gradle` del proyecto (por defecto suele ser):
    ```
    com.example.runliebre
    ```
3.  (Opcional) El certificado SHA-1 es necesario solo si vas a usar Google Sign-In (este proyecto usa Email/Pass, así que puedes saltarlo).
4.  Haz clic en **Registrar app**.

### Paso 3: El archivo `google-services.json` 📄
1.  Descarga el archivo **`google-services.json`** que te genera Firebase.
2.  En tu ordenador, mueve ese archivo a la carpeta `app` dentro del proyecto:
    ```text
    RunLiebre/
    ├── app/
    │   ├── google-services.json  <-- ¡PÉGALO AQUÍ!
    │   ├── src/
    │   └── build.gradle
    ├── build.gradle
    └── ...
    ```

### Paso 4: Activar Servicios en la Consola
Para que la app no se cierre inesperadamente, debes activar estos dos servicios en la consola de Firebase:

1.  **Authentication:**
    * Ve al menú "Authentication" > "Sign-in method".
    * Habilita el proveedor **Correo electrónico/Contraseña**.
2.  **Firestore Database:**
    * Ve al menú "Firestore Database" > "Crear base de datos".
    * Selecciona el modo de **Prueba** (Test Mode) para empezar rápido.
    * Elige la ubicación del servidor (ej. `nam5` o la más cercana).

---

## 🚀 Instalación y Ejecución

1.  Clona este repositorio:
    ```bash
    git clone https://github.com/Ivan-Castill/RunLiebre.git
    ```
2.  Abre **Android Studio**.
3.  Selecciona **Open** y busca la carpeta clonada.
4.  Espera a que Gradle sincronice las dependencias (puede tardar unos minutos).
5.  Asegúrate de haber completado la sección de **Configuración** (añadir el `json`).
6.  Conecta tu dispositivo Android o usa un emulador.
7.  Presiona el botón **Run (▶️)**.

## 🛠️ Tecnologías Usadas

* **Lenguaje:** Kotlin
* **Mapas:** osmdroid (OpenStreetMap) - *¡No requiere API Key de Google Maps!*
* **Backend:** Firebase (Firestore & Auth)
* **Diseño:** XML, Material Components, CardView.

## 📄 Licencia

Este proyecto es de código abierto.

---

*Desarrollado con ❤️ por Ivan Castillo - Estudiante de Electromecánica ESFOT-EPN*
