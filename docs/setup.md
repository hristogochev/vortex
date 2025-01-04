# Setup

1.  Add Maven Central to your repositories if needed

    ```kotlin
    repositories {
        mavenCentral()
    }
    ```
    
2. Add the desired dependencies to your module's `build.gradle` file

=== "Dependencies"

    ```kotlin
  
    val vortexVersion = "0.2.0"
        
    // Main library
    implementation("io.github.hristogochev:vortex:$vortexVersion")

    // Koin integration
    implementation("io.github.hristogochev:vortex-koin:$vortexVersion")

    // Kodein integration
    implementation("io.github.hristogochev:vortex-kodein:$vortexVersion")
    
    ```

=== "Version Catalog"

    ```toml
    [versions]
    vortex = "0.2.0"
    
    [libraries]
    // Main library
    vortex = { module = "io.github.hristogochev:vortex", version.ref = "vortex" }

    // Koin integration
    vortex-koin = { module = "io.github.hristogochev:vortex-koin", version.ref = "vortex" }

    // Kodein integration
    vortex-kodein = { module = "io.github.hristogochev:vortex-kodein", version.ref = "vortex" }
    ```

!!! note "Current version [here](https://github.com/hristogochev/vortex/releases)."




