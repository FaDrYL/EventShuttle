[![](https://jitpack.io/v/FaDrYL/EventShuttle.svg)](https://jitpack.io/#FaDrYL/EventShuttle)
# EventShuttle
A simplified EventBus with abilities of event subscribe and distribution.

## Use
```
dependencies {
    implementation 'com.github.FaDrYL:EventShuttle:{version}'
}
```

## Features
- Function/method annotation for subscriber registration
- Custom subscription channel
- Async subscription
- Multiprocessing event distribution

### Multiprocessing
```
implementation "com.github.FaDrYL.eventshuttle:eventshuttle-mesh:{version}"
```

Remember to register strategy to use the multiprocessing feature:
```kotlin
val meshStrategy = MeshStrategy().apply {
    addRemoteSubscriber(application, "{target_package_name}")
}
EventShuttle.registerFlightStrategy(meshStrategy)
```

Also, add this to Apps' manifest:
```xml
<queries>
    <package android:name="{target_package_name}" />
</queries>
```
