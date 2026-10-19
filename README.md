# مهندس‌حساب (MohandesCalc)

<div dir="rtl">

ماشین حساب مهندسی، آفلاین و متن‌باز برای اندروید — نوشته‌شده با **Kotlin + Jetpack Compose**.

این پروژه طوری طراحی شده که به‌سادگی در GitHub منتشر و سپس در **فروشگاه Iranian apps market (Cafe Bazaar)** بارگذاری شود.

## ✨ امکانات

### ماشین حساب اصلی
- ارزیابی عبارت با Shunting-Yard و RPN (پشتیبانی از پرانتزهای تودرتو، ضرب ضمنی مثل `2π` یا `3sin(x)`)
- توابع مثلثاتیتی: `sin`, `cos`, `tan`, `asin`, `acos`, `atan`
- توابع هذلولی: `sinh`, `cosh`, `tanh`, `asinh`, `acosh`, `atanh`
- توابع نمایی/لگاریتمی: `ln` (طبیعی), `log` (پایه ۱۰), `exp`, `logbase(x, b)`, `root(x, n)`
- قدر مطلق، کف، سقف، گردکردن، علامت
- فاکتوریل `!` (با پشتیبانی از اعداد اعشاری از طریق تابع گامای لانکزوش)
- ترکیب و جایگوین `comb(n,k)`, `perm(n,k)`
- بزرگ‌ترین مقسوم‌علیه و کوچک‌ترین مضرب `gcd`, `lcm`
- ثابت‌ها: `π` (`pi`), `e`, `φ` (`phi`), `τ` (`tau`)
- سه حالت زاویه: **DEG / RAD / GRAD** با کلید چرخش
- کلید **2nd** برای دسترسی به توابع ثانویه (مثل `asin`, `acos`, `eˣ`)
- پیش‌نمایش زنده‌ی نتیجه در حین تایپ
- ذخیره خودکار در تاریخچه (۵۰ مورد آخر)

### تبدیل واحد (۹ دسته)
- طول، جرم، دما، سرعت، مساحت، حجم، انرژی، فشار، زمان
- بیش از ۹۰ واحد مختلف از جمله واحدهای باستانی و علمی (سال نوری، الکترون‌ولت، پاسکال، …)

### محاسبات ماتریسی
- ماتریس‌های ۲×۲ تا ۴×۴
- عملیات: جمع، تفریق، ضرب، دترمینان، معکوس، ترانهاده
- الگوریتم حذف گاوسی با محوردهی جزئی برای پایداری عددی

### رسم نمودار تابع
- ورودی تابع به‌صورت رشته (`sin(x) * x`, `x^2 - 2*x + 1`, …)
- تعیین بازه‌ی x
- رسم شبکه، محورها و منحنی با Canvas
- تشخیص خودکار بازه‌ی y

### تاریخچه
- ذخیره‌ی عبارت، نتیجه، حالت زاویه و زمان
- با کلیک روی هر مورد، عبارت در ماشین حساب بارگذاری می‌شود

### تنظیمات
- تم: تیره، روشن، سیستم
- لرزش هنگام لمس (روشن/خاموش)
- نمایش اطلاعات برنامه

## 🛠 تکنولوژی‌ها

| لایه | تکنولوژی |
|------|----------|
| زبان | Kotlin 2.0.20 |
| UI | Jetpack Compose + Material 3 |
| Architecture | Single-Activity + Navigation Compose + ViewModel |
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 34 (Android 14) |
| Build | Gradle 8.7 + AGP 8.5.2 |
| Java | 17 |

## 📁 ساختار پروژه

```
MohandesCalc/
├── app/
│   ├── build.gradle.kts              # تنظیمات ماژول app
│   ├── proguard-rules.pro            # قوانین ProGuard
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/ir/mohandesan/calc/
│       │   ├── MainActivity.kt       # اکتیویتی اصلی + Navigation
│       │   ├── MohandesCalcApp.kt    # کلاس Application
│       │   ├── data/                  # لایه‌ی منطق محاسباتی
│       │   │   ├── CalculatorEngine.kt     # موتور ارزیابی عبارت
│       │   │   ├── ComplexNumber.kt       # عدد مختلط
│       │   │   ├── FunctionEvaluator.kt   # ارزیابی برای نمودار
│       │   │   ├── Matrix.kt              # عملیات ماتریسی
│       │   │   └── UnitConverter.kt       # تبدیل واحدها
│       │   ├── model/                # مدل‌های داده
│       │   │   ├── CalculatorModels.kt
│       │   │   └── KeypadLayout.kt        # چیدمان کلیدها
│       │   ├── viewmodel/
│       │   │   └── CalculatorViewModel.kt
│       │   ├── ui/
│       │   │   ├── theme/             # Material 3 theme
│       │   │   ├── components/        # دکمه، نمایشگر
│       │   │   ├── screens/           # ۶ صفحه اصلی
│       │   │   └── navigation/
│       │   └── res/                   # strings, themes, icons
│       └── ...
├── gradle/libs.versions.toml         # نسخه‌ها به‌صورت متمرکز
├── settings.gradle.kts
├── build.gradle.kts
└── README.md
```

## 🚀 شروع به کار

### پیش‌نیازها

- [Android Studio Ladybug یا جدیدتر](https://developer.android.com/studio)
- JDK 17
- (اختیاری) Gradle 8.7 نصب‌شده روی سیستم — ولی پروژه از Gradle Wrapper استفاده می‌کند و نیازی به نصب جداگانه نیست.

### مراحل اجرا

1. پروژه را clone کنید:
   ```bash
   git clone https://github.com/your-username/MohandesCalc.git
   cd MohandesCalc
   ```

2. پروژه را در Android Studio باز کنید: `File → Open` و پوشه‌ی پروژه را انتخاب کنید.

3. صبر کنید تا Gradle Sync کامل شود (اولین بار ممکن است چند دقیقه طول بکشد).

4. یک دستگاه یا شبیه‌ساز (Emulator) با API 24 یا بالاتر انتخاب کنید و دکمه‌ی **Run** را بزنید.

### build از خط فرمان

برای ساخت APK دیباگ:
```bash
# در پوشه‌ی ریشه‌ی پروژه
./gradlew assembleDebug
```
خروجی: `app/build/outputs/apk/debug/app-debug.apk`

برای ساخت AAB (بارگذاری در بازار):
```bash
./gradlew bundleRelease
```
خروجی: `app/build/outputs/bundle/release/app-release.aab`

> ⚠️ برای ساخت نسخه‌ی Release امضا‌شده، ابتدا بخش **انتشار در بازار** را مطالعه کنید.

## 📦 انتشار در بازار (Cafe Bazaar)

بازار فقط فایل **APK امضا‌شده‌ی Release** را قبول می‌کند. مراحل زیر را دنبال کنید:

### گام ۱ — ساخت Keystore (یک‌بار برای همیشه)

```bash
keytool -genkeypair -v \
  -keystore mohandescalc.keystore \
  -alias mohandescalc \
  -keyalg RSA -keysize 2048 \
  -validity 10000 \
  -storepass YOUR_STORE_PASSWORD \
  -keypass YOUR_KEY_PASSWORD
```

سوالات خواسته‌شده (نام، سازمان، …) را به دلخواه پر کنید. فایل `mohandescalc.keystore` را در یک جای امن نگه دارید — **اگر آن را گم کنید، دیگر نمی‌توانید آپدیت برای برنامه منتشر کنید!**

### گام ۲ — پیکربندی امضا در پروژه

فایل `app/build.gradle.kts` را باز کنید و بلوک `signingConfigs` را در `android { }` اضافه کنید:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../mohandescalc.keystore")
            storePassword = "YOUR_STORE_PASSWORD"
            keyAlias = "mohandescalc"
            keyPassword = "YOUR_KEY_PASSWORD"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... تنظیمات قبلی
        }
    }
}
```

> 🔐 **مهم:** فایل keystore و پسوردها را هرگز در Git کامیت نکنید. فایل `.gitignore` پروژه الREADY این موارد را نادیده می‌گیرد. بهتر است پسوردها را در فایل `local.properties` یا متغیرهای محیطی نگه دارید.

### گام ۳ — ساخت APK Release

```bash
./gradlew assembleRelease
```

خروجی: `app/build/outputs/apk/release/app-release.apk`

### گام ۴ — ثبت‌نام در پنل توسعه‌دهندگان بازار

1. به [developer.bazaar.ir](https://developer.bazaar.ir) بروید.
2. با شماره‌ی موبایل ایرانی ثبت‌نام کنید.
3. روی «برنامه جدید» کلیک کنید.
4. اطلاعات برنامه (نام، توضیح، دسته‌بندی، آیکون، اسکرین‌شات) را پر کنید.
5. فایل APK را آپلود کنید.
6. پس از تأیید بازار (معمولاً ۱ تا ۳ روز کاری)، برنامه منتشر می‌شود.

### نکات مهم برای انتشار در بازار

- آیکون ۵۱۲×۵۱۲ پیکسل، اسکرین‌شات حداقل ۲ عدد، توضیح فارسی حداقل ۳۰۰ کاراکتر.
- برنامه نباید وابستگی به سرویس‌های گوگل (FCM، Maps، …) داشته باشد. این پروژه کاملاً آفلاین و بدون وابستگی به Google Play Services است. ✅
- نسخه‌ی اولیه را با `versionCode = 1` و `versionName = "1.0.0"` منتشر کنید. برای آپدیت‌های بعدی، هر بار `versionCode` را حداقل یکی اضافه کنید.

## 🧪 تست

پروژه شامل یک مجموعه‌ی پایه‌ی تست در `app/src/test/` است. برای اجرای تست‌ها:

```bash
./gradlew test
```

## 🌍 ترجمه و سفارشی‌سازی

### افزودن فونت دلخواه (مثل Vazirmatn)

1. فایل‌های فونت (TTF) را در `app/src/main/res/font/` قرار دهید.
2. در `Theme.kt` خط `AppFontFamily` را به این شکل تغییر دهید:
   ```kotlin
   private val AppFontFamily = FontFamily(
       Font(R.font.vazirmatn_regular, FontWeight.Normal),
       Font(R.font.vazirmatn_medium, FontWeight.Medium),
       Font(R.font.vazirmatn_bold, FontWeight.Bold)
   )
   ```

### افزودن زبان جدید

یک پوشه‌ی `values-XX/` با `strings.xml` بسازید (مثلاً `values-ar/strings.xml` برای عربی) و تمام مقادیر را ترجمه کنید.

## 📜 مجوز

این پروژه تحت مجوز **MIT** منتشر شده است. برای جزئیات بیشتر به فایل [LICENSE](LICENSE) مراجعه کنید.

## 🤝 مشارکت

Pull request و issue با کمال میل پذیرفته می‌شود. لطفاً قبل از ارسال PR:

1. کد را با `./gradlew lint` بررسی کنید.
2. تست‌ها را با `./gradlew test` اجرا کنید.
3. فرمت کد را با `./gradlew ktlintFormat` (در صورت نصب) اصلاح کنید.

## 📞 تماس

سؤال یا پیشنهاد؟ در GitHub issue باز کنید.

ساخته‌شده با ❤️ برای جامعه‌ی فارسی‌زبان اندروید.

</div>
