# TestLook

## Language
Kotlin. Android extension for Kotlin is used to get rid of findViewById() and annoying casts.
- Extensions are used to extend Android SDK classes: 
[Context.kt](https://github.com/shuhart/TestLook/blob/master/app/src/main/java/com/shuhart/testlook/utils/Context.kt)
[SharedPreferences.kt](https://github.com/shuhart/TestLook/blob/master/app/src/main/java/com/shuhart/testlook/utils/SharedPreferences.kt)


## Architecture
MVP + Dagger 2, RxJava2 for Network layer

## Plane animation
Bezier curves are applied using Android Path and PathMeasure classes. The most difficult part is calculation of control points. This process is extremely simple. Control points are positioned according to the y-axis of starting and ending points. Some correction is applied when control points are getting too close to the each other.
[MapsActivity.kt](https://github.com/shuhart/TestLook/blob/master/app/src/main/java/com/shuhart/testlook/modules/flight/search/map/MapsActivity.kt#L146)

<img src="/images/demo.gif" alt="Sample" width="300px" />
