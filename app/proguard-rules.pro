# MohandesCalc ProGuard Rules
# Keep model classes used by Compose
-keep class ir.mohandesan.calc.data.** { *; }
-keep class ir.mohandesan.calc.model.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Kotlin metadata
-keep class kotlin.Metadata { *; }
