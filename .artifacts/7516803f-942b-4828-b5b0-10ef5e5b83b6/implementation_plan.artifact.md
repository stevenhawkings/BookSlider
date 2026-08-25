# Plan de Implementación: Registro de Usuarios y Almacenamiento Local

Este plan detalla cómo implementar la captura de datos (email y contraseña) en el `RegisterFragment` y su almacenamiento persistente en el dispositivo.

## Resumen de Cambios

1.  **Diseño de Interfaz (UI):** Actualizar `fragment_register.xml` para incluir campos de texto para email, contraseña y un botón de registro, manteniendo el estilo visual de la pantalla de Login.
2.  **Lógica del Fragment:** Configurar `ViewBinding` en `RegisterFragment.kt` para capturar la información ingresada por el usuario al hacer clic en el botón.
3.  **Almacenamiento (Persistence):** Utilizar `SharedPreferences` para guardar los datos de manera local y persistente. La estructura será simple: usaremos el email como clave para guardar la contraseña, simulando un registro básico de usuario.

## Revisión del Usuario Requerida

> [!IMPORTANT]
> Se utilizará **SharedPreferences** para el almacenamiento por ser la opción más directa y sencilla de implementar sin añadir dependencias externas. Sin embargo, para una aplicación real con muchos usuarios y requerimientos de seguridad, se recomendaría usar **Firebase Authentication** o una base de datos **Room**.

## Cambios Propuestos

### Interfaz de Usuario (UI)

#### [MODIFY] [fragment_register.xml](file:///C:/Users/Pc/Desktop/GitGametown/BookSlider/app/src/main/res/layout/fragment_register.xml)
*   Reemplazar el `FrameLayout` actual por un `ConstraintLayout`.
*   Añadir dos `EditText` (Email y Password) con el estilo `edit_text` ya existente en el proyecto.
*   Añadir un `Button` para confirmar el registro.
*   Incluir un título "Registro" para identificar la pantalla.

---

### Lógica de Captura y Almacenamiento

#### [MODIFY] [RegisterFragment.kt](file:///C:/Users/Pc/Desktop/GitGametown/BookSlider/app/src/main/java/com/example/book_slide/fragment/RegisterFragment.kt)
*   Implementar `ViewBinding` para interactuar con los nuevos elementos de la UI.
*   Configurar el `OnClickListener` del botón de registro.
*   Implementar una función `saveUser(email, password)` que:
    1.  Obtenga una instancia de `SharedPreferences`.
    2.  Guarde la relación Email -> Password.
    3.  Muestre un `Toast` confirmando el registro exitoso.
    4.  (Opcional) Navegue de vuelta al Login o al Home.

## Plan de Verificación

### Pruebas Manuales
1.  **Captura de datos:** Ingresar un correo y contraseña en los campos de registro.
2.  **Persistencia:** Al hacer clic en "Registrar", verificar que aparezca el mensaje de éxito.
3.  **Validación:** Intentar registrarse con campos vacíos y verificar que se maneje el error (opcional pero recomendado).
4.  **Recuperación:** (En pasos futuros) Podremos verificar que estos datos se pueden usar en el `LogInFragment` para validar la sesión.
