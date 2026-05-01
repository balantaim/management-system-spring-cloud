# Desktop client

## Description

This is a desktop client that can completely replace the web application used for connecting to microservices. It is built using Swing UI in Java, along with several libraries such as FlatLaf, IntelliJ Themes, and MigLayout. The application uses the Micronaut/Spring framework to leverage core functionalities like dependency injection, configuration, and a web client, without starting a web server or using a port. The application supports various color schemes, as well as both dark and light theme options.

### Preview

![Demo](images/video-demo.gif)

### Software

**Tools/libraries:** Java (Swing UI), Spring, Flatlaf (Themes for Swing components), MigLayout (New layout manager for Swing), Lombok, Assertj, Micronaut/Spring (Depending on the branch), Maven

### Default Configuration

- **Default app.theme-variant:** `system`
- **Default app.theme-name:** `material`
- **Default flat.inspector.enabled:** `true`
    - Use `Ctrl` + `Alt` + `Shift` + `X` to activete/decativate inspect mode when the app is running

### Supported themes

**Theme variants:**

- `light`
- `dark`
- `system` (tested on Windows and Linux - Ubuntu)

**Theme names:** `Flat`, `macOS`, `IntelliJ`, `Cyan-Purple`, `Material`, `Solarized-Carbon`, `Orange-Ocean`

Theme variants and names are not case-sensitive!

## Frameworks Comparison: Micronaut vs Spring (Desktop Application Focus)

| Feature / Aspect              | Micronaut Application                              | Spring Boot Application                           |
|------------------------------|----------------------------------------------------|---------------------------------------------------|
| **Primary Use Case**          | Desktop / lightweight applications                 | Desktop (less typical, web-oriented by design)    |
| **Startup Time**             | Fast (no heavy runtime scanning)                  | Slower (reflection & runtime scanning)            |
| **Memory Usage**             | Lower                                             | Higher                                            |
| **JAR Size**                 | ~15 MB                                            | 40+ MB                                            |
| **Dependency Injection**     | Compile-time (no reflection required)             | Runtime (reflection-based)                        |
| **Reflection Usage**         | Minimal                                           | Heavy                                             |
| **Build Tool**               | Maven                                             | Maven                                             |
| **Runtime from Source**      | Supported                                         | Supported                                         |
| **Run as JAR**               | Supported                                         | Supported                                         |
| **Installable App (jpackage)**| Easy (works well without reflection issues)       | Difficult (reflection complicates packaging)      |
| **AOT Usage**                | Disabled (not used in this project)               | Optional (not used here)                          |
| **Native Image Support**     | Available but not used                            | Available but complex and not used                |
| **Cold Start Performance**   | Fast                                              | Slower                                            |
| **Configuration Processing** | Mostly compile-time                               | Runtime                                           |
| **Ecosystem & Libraries**    | Smaller but sufficient                            | Very large and mature                             |
| **Suitability for Desktop**  | High (lightweight, easy packaging)                | Moderate (heavier footprint, packaging harder)    |

### Useful guides

- [Flatlaf docs](https://www.formdev.com/flatlaf/)
- [Flatlaf source](https://github.com/JFormDesigner/FlatLaf)
- [Flatlaf IntelliJ themes](https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-intellij-themes)
- [JFormDesigner](https://github.com/JFormDesigner/FlatLaf#demo)
- [MigLayout](http://www.miglayout.com/)

## Set up the application

Micronaut variant - master branch:

```bash
./mvnw mn:run
```

Spring variant - [Spring archive branch](https://github.com/balantaim/management-system-spring-cloud/tree/spring-archive/ManagementSystemDesktopClient):

```bash
./mvnw  spring-boot:run
```

Build via Maven wrapper:

```bash
./mvnw package
```

Run it via jar (Change x.x.x with your version number. Environment variable is optional):

```bash
java -Dmicronaut.environments=prod -jar management-system-desktop-client-x.x.x.jar
```

### Create cross-platform installable app via jpackage (Optional)

It is mandatory to build the application jar before running jpackage!
Change the version of the application if needed.
Change or remove `--java-options` if needed.

- Linux: apt

    ```bash
    jpackage \
    --type deb \
    --name "ManagementSystem" \
    --app-version "1.0.0" \
    --vendor "Martin Atanasov" \
    --input target/ \
    --main-jar management-system-desktop-client-1.0.0.jar \
    --main-class com.martinatanasov.ManagementSystemDesktopClientApplication \
    --dest dist/ \
    --icon src/main/resources/static/images/logo/logo.png \
    --java-options "-Dmicronaut.environments=prod" \
    --license-file ../LICENSE
    --linux-shortcut \
    --linux-menu-group "Applications" \
    --linux-app-category "Utility"
    ```

- Linux: rpm

    ```bash
    jpackage \
    --type rpm \
    --name "ManagementSystem" \
    --app-version "1.0.0" \
    --vendor "Martin Atanasov" \
    --input target/ \
    --main-jar management-system-desktop-client-1.0.0.jar \
    --main-class com.martinatanasov.ManagementSystemDesktopClientApplication \
    --dest dist/ \
    --icon src/main/resources/static/images/logo/logo.png \
    --license-file ../LICENSE \
    --java-options "-Dmicronaut.environments=prod" \
    --linux-shortcut \
    --linux-menu-group "Applications" \
    --linux-app-category "Utility"
    ```

- Windows: exe

    ```bash
    jpackage \
    --type exe \
    --name "ManagementSystem" \
    --app-version "1.0.0" \
    --vendor "Martin Atanasov" \
    --input target/ \
    --main-jar management-system-desktop-client-1.0.0.jar \
    --main-class com.martinatanasov.ManagementSystemDesktopClientApplication \
    --dest dist/ \
    --icon src/main/resources/static/images/logo/logo.ico \
    --license-file ../LICENSE \
    --java-options "-Dmicronaut.environments=prod" \
    --win-shortcut \
    --win-menu \
    --win-menu-group "Applications" \
    --win-dir-chooser
    ```

- Windows: msi

    ```bash
    jpackage \
    --type msi \
    --name "ManagementSystem" \
    --app-version "1.0.0" \
    --vendor "Martin Atanasov" \
    --input target/ \
    --main-jar management-system-desktop-client-1.0.0.jar \
    --main-class com.martinatanasov.ManagementSystemDesktopClientApplication \
    --dest dist/ \
    --icon src/main/resources/static/images/logo/logo.ico \
    --license-file ../LICENSE \
    --java-options "-Dmicronaut.environments=prod" \
    --win-shortcut \
    --win-menu \
    --win-menu-group "Applications" \
    --win-dir-chooser \
    --win-upgrade-uuid "<YOUR_UUID_HERE>"
    ```

- macOS: dmg

    ```bash
    jpackage \
    --type dmg \
    --name "ManagementSystem" \
    --app-version "1.0.0" \
    --vendor "Martin Atanasov" \
    --input target/ \
    --main-jar management-system-desktop-client-1.0.0.jar \
    --main-class com.martinatanasov.ManagementSystemDesktopClientApplication \
    --dest dist/ \
    --icon src/main/resources/static/images/logo/logo.icns \
    --license-file ../LICENSE \
    --java-options "-Dmicronaut.environments=prod" \
    --mac-package-name "ManagementSystem"
    ```

## Gallery

![Login default](images/dark_material.png)
![Register default](images/dark_material_register.png)

**Themes**

![Theme dark carbon](images/dark_carbon.png)
![Theme dark cyan](images/dark_cyan.png)
![Theme dark flat](images/dark_flat.png)
![Theme dark ocen](images/dark_ocean.png)
![Theme dark purple](images/dark_purple.png)
![Theme light blue](images/light_blue.png)
![Theme light cyan](images/light_cyan.png)
![Theme light macOS](images/light_macos.png)
![Theme light material](images/light_material.png)
![Theme light orange](images/light_orange.png)

### Upgrade Maven wrapper guide (Optional)

1. Get the latest version from [Maven central](https://mvnrepository.com/artifact/org.apache.maven/maven-core)
2. Add latest version instead of `x.x.x`

    ```bash
    ./mvnw -N wrapper:wrapper -Dmaven=x.x.x
    ```

### Create desktop icons guide (Optional)

Guide for Linux (Debian/Ununtu):

1. Install dependencies:

    ```bash
    sudo apt install graphicsmagick-imagemagick-compat
    ```

2. Prepare you base png logo with preferred size 512 or 1024 pixels and generate .ico and .icns variants.
    ```bash
    convert logo.png -resize 512x512 logo.icns
    convert logo.png -resize 512x512 logo.ico
    ```
3. Resize the image for different resolutions if needed (Optional):
    ```bash
    convert logo.png -resize 16x16 logo_16.png
    convert logo.png -resize 32x32 logo_32.png
    convert logo.png -resize 48x48 logo_48.png
    ```
