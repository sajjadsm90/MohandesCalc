package ir.mohandesan.calc

import android.app.Application

/**
 * Application class. Currently a no-op marker.
 *
 * If you want to add Hilt/Dagger for DI later:
 *   1. Add `dagger.hilt.android.plugin` to build.gradle.kts
 *   2. Annotate this class with @HiltAndroidApp
 *   3. Use @HiltViewModel on CalculatorViewModel
 *
 * For now we keep it simple — no DI framework needed.
 */
class MohandesCalcApp : Application()

