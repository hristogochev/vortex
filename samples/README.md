# Sample

### Building Android App
```shell
./gradlew :samples:sample:assembleDebug
```

### Running iOS
- Open Xcode project with: `open samples/sample-iosApp/iosApp.xcodeproj`
- Run/Build in Xcode

### Running JVM Native app (Desktop)
```shell
./gradlew :samples:sample:run
```

### Running JS
```shell
./gradlew :samples:sample:jsBrowserDevelopmentRun
```

### Running WasmJS
```shell
./gradlew :samples:sample:wasmJsBrowserDevelopmentRun
```


### Running Arm64 MacOS Native app (Desktop using Kotlin Native)
```shell
./gradlew :samples:sample:runNativeMacosArm64Debug
```

### Running x86_64 MacOS Native app (Desktop using Kotlin Native)
```shell
./gradlew :samples:sample:runNativeMacosX64Debug
```

If you want to run Android sample in the emulator, you can open the project and run the application configuration `samples.sample` on Android Studio.
