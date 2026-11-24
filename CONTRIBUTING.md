# 🤝 Guía de contribución – MyLife

¡Gracias por tu interés en colaborar con **MyLife**! 🐷💗

MyLife es una aplicación Android diseñada para ayudar a organizar la vida diaria mediante la gestión de tareas escolares, hábitos y metas personales. Este documento establece las pautas para contribuir al desarrollo del proyecto y mantener una colaboración organizada y respetuosa.

## 📌 ¿Cómo puedes contribuir?

Puedes colaborar de diferentes maneras:

- **Reportar errores:** identificar y comunicar fallos en el funcionamiento de la aplicación.
- **Proponer mejoras:** sugerir nuevas funcionalidades o cambios en las existentes.
- **Mejorar la documentación:** corregir, actualizar o complementar los documentos del proyecto.
- **Contribuir al código:** desarrollar nuevas funciones, corregir errores u optimizar el código existente.

## 🚀 Proceso de contribución

Para colaborar con MyLife, sigue estos pasos:

### 1. Realizar un Fork

Crea una copia del repositorio en tu cuenta de GitHub mediante la opción **Fork**.

### 2. Clonar el repositorio

Descarga tu copia del proyecto en tu computadora:

```bash
git clone https://github.com/TU-USUARIO/Mylife.git
```

### 3. Crear una rama

Antes de realizar cambios, crea una rama independiente para trabajar en tu modificación.

```bash
git checkout -b tipo/descripcion-del-cambio
```

Ejemplos:

- `feat/new-habit`: agregar una nueva funcionalidad.
- `fix/login-error`: corregir un error de inicio de sesión.
- `docs/update-readme`: actualizar la documentación.

### 4. Realizar los cambios

Desarrolla las modificaciones procurando mantener la organización y el estilo del código existente.

Antes de continuar:

- Verifica que el código compile correctamente.
- Comprueba que las funcionalidades modificadas trabajen adecuadamente.
- Evita modificar archivos que no estén relacionados con tu contribución.
- No incluyas contraseñas, claves privadas ni información personal.

### 5. Realizar un commit

Utiliza mensajes claros que describan los cambios realizados.

Se recomienda seguir el formato **Conventional Commits**:

| Tipo | Descripción |
|---|---|
| `feat` | Nueva funcionalidad. |
| `fix` | Corrección de errores. |
| `docs` | Cambios en la documentación. |
| `style` | Cambios de formato o presentación. |
| `refactor` | Mejoras en el código sin cambiar su comportamiento. |
| `test` | Incorporación o modificación de pruebas. |

Ejemplo:

```bash
git add .
git commit -m "feat: add habit management"
```

### 6. Enviar los cambios

Sube tu rama a GitHub:

```bash
git push origin tipo/descripcion-del-cambio
```

Posteriormente, crea un **Pull Request** hacia la rama principal `main` del repositorio original.

Describe brevemente:
- Qué cambios realizaste.
- Qué problema solucionan o qué funcionalidad agregan.
- Cómo comprobaste que funcionan correctamente.

## 💻 Tecnologías del proyecto

Para trabajar con MyLife se recomienda contar con:

- Android Studio.
- Java.
- Android SDK.
- Gradle.
- SQLite.
- Material Components.

## 📋 Recomendaciones de desarrollo

Para mantener la calidad del proyecto:

- Utiliza nombres descriptivos para clases, métodos y variables.
- Mantén una estructura de código organizada.
- Evita duplicar código cuando pueda reutilizarse.
- Conserva la compatibilidad con la versión mínima de Android establecida en el proyecto.
- Procura que los cambios mantengan la identidad visual de MyLife.
- Documenta las modificaciones importantes.

## 🔒 Seguridad

Si encuentras una vulnerabilidad o un problema relacionado con la seguridad, evita publicar información sensible o detalles que puedan comprometer a los usuarios. Comunica el problema de manera responsable a los responsables del proyecto.

## 💗 Código de conducta

Todas las personas que participen en MyLife deben mantener una comunicación respetuosa, inclusiva y profesional, siguiendo las disposiciones establecidas en nuestro archivo `CODE_OF_CONDUCT.md`.

## 🐷 ¡Gracias por contribuir!

Cada sugerencia, corrección y mejora ayuda a que MyLife siga creciendo.

**¡Gracias por formar parte de MyLife y ayudarnos a construir una aplicación mejor!** 💗
