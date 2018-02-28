# TestLook

## Language
Kotlin. Android extension for Kotlin is used to get rid of findViewById() and annoying casts.

Extensions are used to extend Android SDK classes: 
[Context.kt](https://github.com/shuhart/TestLook/blob/master/app/src/main/java/com/shuhart/testlook/utils/Context.kt) and 
[SharedPreferences.kt](https://github.com/shuhart/TestLook/blob/master/app/src/main/java/com/shuhart/testlook/utils/SharedPreferences.kt)


## Architecture
MVP + Dagger 2, RxJava2 for Network and Ui layer, LiveData to handle configuration changes. 

MVP module: [Search an aiport screen](https://github.com/shuhart/TestLook/tree/master/app/src/main/java/com/shuhart/testlook/modules/flight/search/airport)

## Plane animation
[MapsActivity.kt](https://github.com/shuhart/TestLook/blob/master/app/src/main/java/com/shuhart/testlook/modules/flight/search/map/MapsActivity.kt)

Bezier curves are applied using Android Path and PathMeasure classes. The most difficult part is calculation of control points. This process is extremely simple. Control points are positioned according to the y-axis of starting and ending points. Some correction is applied when control points are getting too close to the each other.

<img src="/images/demo.gif" alt="Sample" width="300px" />
