# 🔥 Configuración de Firebase - Sprint 2

## Paso 1: Crear Proyecto en Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Click en **"Add project"** o **"Agregar proyecto"**
3. Nombre del proyecto: **`Restaurandes`**
4. Desactiva Google Analytics si no lo necesitas (o déjalo activado)
5. Click **"Create project"**

## Paso 2: Registrar la App Android

1. En la página principal del proyecto, click en el ícono de **Android** 
2. Llena los campos:
   - **Android package name**: `com.restaurandes` ⚠️ **Debe ser exacto**
   - **App nickname**: `Restaurandes` (opcional)
   - **Debug signing certificate SHA-1**: (opcional por ahora)
3. Click **"Register app"**

## Paso 3: Descargar google-services.json

1. Click en **"Download google-services.json"**
2. **Guarda el archivo** descargado
3. **Mueve el archivo** a la carpeta:
   ```
   Restaurandes-Kotlin/app/google-services.json
   ```
   ⚠️ **Debe estar dentro de la carpeta `app`, NO en la raíz del proyecto**

## Paso 4: Habilitar Firebase Authentication

1. En el menú lateral, ve a **"Build" → "Authentication"**
2. Click **"Get started"**
3. En la pestaña **"Sign-in method"**, habilita:
   - ✅ **Email/Password** → Click, toggle "Enable", Save
4. (Opcional) En la pestaña **"Users"**, puedes crear usuarios de prueba

## Paso 5: Habilitar Cloud Firestore

1. En el menú lateral, ve a **"Build" → "Firestore Database"**
2. Click **"Create database"**
3. Selecciona:
   - **Start in test mode** (para desarrollo)
   - **Location**: `us-central` o el más cercano
4. Click **"Enable"**

## Paso 6: Habilitar Firebase Analytics

1. En el menú lateral, ve a **"Build" → "Analytics"**
2. Si no está habilitado, click **"Get started"**
3. Analytics ya está configurado con el SDK

## Paso 7: Descomentar el Plugin en el Código

Después de agregar `google-services.json`, necesitas descomentar esta línea en `build.gradle.kts`:

```kotlin
// En build.gradle.kts (root)
plugins {
    // ... otros plugins
    id("com.google.gms.google-services") version "4.4.2" apply false // Descomentar esta línea
}

// En app/build.gradle.kts
plugins {
    // ... otros plugins
    id("com.google.gms.google-services") // Descomentar esta línea
}
```

## Paso 8: Sync y Build

1. En Android Studio: **File → Sync Project with Gradle Files**
2. Espera a que termine la sincronización
3. Build el proyecto: `./gradlew clean build`

## ✅ Verificación

Después de configurar, la app podrá:
- ✅ Registrar usuarios con email/password
- ✅ Iniciar sesión
- ✅ Guardar datos de usuario en Firestore
- ✅ Trackear eventos de analytics (BQ1, BQ2, BQ3)

## 📊 Ver Analytics en Firebase

1. Ve a **"Analytics" → "Events"** en Firebase Console
2. Deberías ver eventos como:
   - `user_session_start` (BQ1: Usuarios activos)
   - `screen_view` (BQ2: Interacciones con secciones)
   - `restaurant_view`, `restaurant_favorited` (BQ3: Conversión view→favorite)

## 🚨 Troubleshooting

**Error: "google-services.json not found"**
- Verifica que el archivo esté en `app/google-services.json`
- El nombre debe ser exacto (minúsculas, con guión)

**Error: "Default FirebaseApp is not initialized"**
- Verifica que el plugin esté descomentado
- Sync Gradle y rebuil

**Error: "FirebaseAuth not found"**
- Verifica que las dependencias de Firebase estén en `app/build.gradle.kts`
- Ya están agregadas en el código

## 📝 Reglas de Firestore (Producción)

Para producción, cambia las reglas de Firestore:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection: solo el dueño puede leer/escribir
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

---

## 🎯 Sprint 2 Requirements Check

Con Firebase configurado, cumples:
- ✅ **Autenticación**: Firebase Authentication (Email/Password)
- ✅ **Analytics**: Firebase Analytics con tracking de 3 BQs
- ✅ **Persistencia**: Cloud Firestore para datos de usuario
- ✅ **Servicio Externo**: Firebase Cloud Services
