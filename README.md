# TestLook

## Language
Kotlin. Extensions, lambdas, inlined functions are used where appropriate.
kotlin-android-extensions is used to get rid of findViewById() and annoying casts.

## Architecture
MVP + Dagger 2, RxJava2 for Network layer

## Plane animation
Bezier curves using Android Path and PathMeasure classes. The most difficult part is calculation of control points. This process is extremely simple. Control points are positioned according to the y-axis of starting and ending points. Some corrections is applied when control points are getting too close to the each other.
