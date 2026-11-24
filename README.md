# 🐷 MyLife

**MyLife** es una aplicación Android para organizar tu vida diaria en un solo lugar: tareas escolares, hábitos y metas personales. Está hecha en **Java** con **Android Studio** y guarda toda la información en una base de datos **SQLite** local. Su mascota, **Mochi**, la acompaña con un diseño en tonos pastel.

> Proyecto escolar de desarrollo de aplicaciones móviles con Android Studio.

## ✨ Funciones

### 🔐 Cuenta de usuario
- Registro e inicio de sesión con correo y contraseña.
- Las contraseñas se guardan cifradas (hash SHA-256 con sal), nunca en texto plano.
- La sesión se mantiene al cerrar la app y se puede cerrar desde el perfil o el menú.
- Cada usuario ve únicamente sus propios datos.

### 🏠 Inicio
- Saludo personalizado con el nombre del usuario.
- Resumen de tareas pendientes y realizadas.
- Racha de hábitos: días seguidos cumpliendo al menos un hábito.
- Calendario mensual que marca los días con tareas, metas y hábitos, y muestra las actividades del día seleccionado.

### 📚 Tareas
- Agregar, editar, eliminar y marcar tareas como hechas.
- Cada tarea lleva materia y fecha de entrega, elegida con un selector de fecha.
- Filtros desde el menú de opciones: todas, pendientes y hechas, y eliminar las hechas.

### 🌱 Hábitos
- Hábitos con ícono propio y contador de días cumplidos.
- Marcar "Cumplí hoy" (una vez al día), deshacerlo o reiniciar el contador.

### 🎯 Metas
- Metas con descripción y fecha objetivo opcional.
- Marcar como cumplida (se tacha y cambia a 🏆) y filtrar por en progreso o cumplidas.

### 👤 Perfil
- Datos de la cuenta y edición del nombre.
- Estadísticas: tareas completadas, hábitos cumplidos y progreso semanal.
- Acceso a "Acerca de" y cierre de sesión.

## 🧭 Navegación
- **Barra inferior** con Inicio, Tareas, Hábitos, Metas y Perfil.
- **Menú lateral a la derecha** (botón ☰) con las mismas secciones, además de Acerca de, Cerrar sesión y Salir.

## 📱 Temas del curso aplicados

| Tema | Dónde se usa |
|---|---|
| Fragments | Una pantalla por sección, dentro de `MenuPrincipalActivity` |
| Barras de navegación | Barra inferior y menú lateral (`NavigationView` + `DrawerLayout`) |
| Menús | Menú de opciones en Tareas y Metas |
| ListView | Listas de tareas, hábitos y metas con adaptadores personalizados |
| Imágenes | Mascota Mochi, logotipo e íconos |
| SQLite | CRUD completo con `SQLiteOpenHelper` |

## 🛠️ Tecnologías
- Java
- Android Studio
- SQLite
- Material Components
- Android 7.0 (API 24) o superior

## 🗂️ Estructura del proyecto

```
app/src/main/java/com/example/mylife/
├── activities/    Pantallas y fragments (Login, Registro, Menú principal, Inicio,
│                  Tareas, Hábitos, Metas, Perfil, Acerca de)
├── adaptadores/   Adaptadores de los ListView
└── db/            Base de datos (Base), modelos (Tarea, Habito, Meta) y sesión
```

## 🚀 Cómo ejecutarlo
1. Clona el repositorio:
   ```
   git clone https://github.com/Pancito00/Mylife.git
   ```
2. Ábrelo con **Android Studio** (File → Open, eligiendo la carpeta que contiene `settings.gradle.kts`).
3. Espera a que termine el Gradle Sync.
4. Ejecuta la app en un emulador o en un celular Android.

## 👩‍💻 Autor
**[Tu nombre]**
