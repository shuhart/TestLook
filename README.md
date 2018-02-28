# TestLook

## Language
Kotlin. Android extension for Kotlin is used to get rid of findViewById() and annoying casts.

Extensions are used to extend Android SDK classes: 
[Context.kt](https://github.com/shuhart/TestLook/blob/master/app/src/main/java/com/shuhart/testlook/utils/Context.kt) and 
[SharedPreferences.kt](https://github.com/shuhart/TestLook/blob/master/app/src/main/java/com/shuhart/testlook/utils/SharedPreferences.kt)


## Architecture
 * Entirely written in [Kotlin](https://kotlinlang.org/)
 * Uses [RxJava](https://github.com/ReactiveX/RxJava) 2
 * Uses LiveData from the [Architecture Components](https://developer.android.com/topic/libraries/architecture/)
 * Uses [dagger2](https://google.github.io/dagger/android.html) for dependency injection
 * Uses MVP to implement a clean architecture

MVP module: [Search an aiport screen](https://github.com/shuhart/TestLook/tree/master/app/src/main/java/com/shuhart/testlook/modules/flight/search/airport)

## Plane animation
[MapsActivity.kt](https://github.com/shuhart/TestLook/blob/master/app/src/main/java/com/shuhart/testlook/modules/flight/search/map/MapsActivity.kt)

Bezier curves are applied using Android Path and PathMeasure classes. The most difficult part is calculation of control points. This process is extremely simple. Control points are positioned according to the y-axis of starting and ending points. Some correction is applied when control points are getting too close to the each other.

<img src="/images/demo.gif" alt="Sample" width="300px" />
