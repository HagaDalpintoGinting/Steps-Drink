Step & Drink
Aplikasi Android untuk tracking langkah harian dan kebutuhan air minum menggunakan Kotlin dan Jetpack Compose.

📋 Deskripsi
Step & Drink adalah aplikasi mobile yang membantu pengguna untuk:

Melacak langkah harian menggunakan sensor step counter bawaan smartphone
Mencatat asupan air minum untuk memenuhi kebutuhan hidrasi harian
Menetapkan dan memantau target langkah dan air minum yang dapat disesuaikan
Melihat riwayat aktivitas untuk evaluasi kebiasaan sehat

Aplikasi ini dibuat sebagai tugas akhir mata kuliah Pemrograman Bergerak dengan fokus pada penggunaan sensor hardware, database lokal, dan multi-halaman navigation.

✨ Fitur
1. Home Screen

Dashboard dengan ringkasan aktivitas hari ini
Card interaktif untuk langkah dan air minum
Progress bar visual untuk tracking target
Greeting dengan nama pengguna

2. Step Tracker

Real-time tracking langkah menggunakan sensor TYPE_STEP_COUNTER
Start/Stop tracking dengan tombol floating action button
Riwayat langkah 7 hari terakhir
Target langkah yang dapat disesuaikan

3. Water Tracker

Quick add buttons (250ml, 500ml, 1000ml)
Input custom untuk jumlah air
Riwayat minum harian dengan timestamp
Hapus record jika salah input
Target air minum yang dapat disesuaikan

4. Profile & Settings

Edit nama pengguna
Ubah target langkah harian
Ubah target air minum harian
Informasi aplikasi


🛠️ Teknologi
Bahasa & Framework

Kotlin - Bahasa pemrograman
Jetpack Compose - UI Framework
Material Design 3 - Design system

Architecture

MVVM (Model-View-ViewModel)
Repository Pattern
Clean Architecture

Database & Storage

Room Database - Local database (SQLite)
DataStore Preferences - Settings storage

Components

Navigation Compose - Multi-halaman navigation
Sensor Manager - Akses hardware sensor (Step Counter)
ViewModel - State management
Kotlin Flow - Reactive data stream
Coroutines - Asynchronous operations


📦 Struktur Project
stepdrink/
├── data/
│   ├── local/
│   │   ├── entity/         # StepRecord, WaterRecord
│   │   ├── dao/            # StepDao, WaterDao
│   │   ├── database/       # AppDatabase
│   │   └── PreferencesManager.kt
│   └── repository/         # StepRepository, WaterRepository
├── ui/
│   ├── screen/             # HomeScreen, StepsScreen, WaterScreen, ProfileScreen
│   ├── navigation/         # Navigation setup
│   ├── components/         # Reusable UI components
│   └── theme/              # App theming
├── viewmodel/              # StepViewModel, WaterViewModel, ProfileViewModel
├── sensor/                 # StepCounterManager
├── util/                   # DateUtils
└── MainActivity.kt

🗄️ Database
step_records
Menyimpan data langkah harian

id - Primary key
date - Tanggal (yyyy-MM-dd)
steps - Jumlah langkah
timestamp - Waktu pencatatan

water_records
Menyimpan data minum air

id - Primary key
date - Tanggal (yyyy-MM-dd)
amount - Jumlah air (ml)
timestamp - Waktu pencatatan

Preferences (DataStore)
Menyimpan pengaturan pengguna

user_name - Nama pengguna
step_goal - Target langkah harian
water_goal - Target air minum harian (ml)


🚀 Instalasi
Requirements

Android Studio (versi terbaru)
Minimum SDK: API 26 (Android 8.0)
Kotlin 1.9.22

Cara Menjalankan

Clone atau download project
Buka di Android Studio
Sync Gradle (File → Sync Project with Gradle Files)
Build project (Build → Make Project)
Run di device atau emulator
Izinkan permission ACTIVITY_RECOGNITION saat diminta


📱 Cara Penggunaan

Tracking Langkah

Buka Step Tracker
Klik tombol Play
Izinkan permission
Mulai berjalan
Data otomatis tersimpan


Catat Air Minum

Buka Water Tracker
Klik quick add atau tombol +
Input jumlah air
Data tersimpan dengan timestamp


Ubah Target

Buka Profile
Klik card yang ingin diubah
Input nilai baru
Simpan




🎯 Tujuan Project
Project ini dibuat untuk memenuhi tugas akhir mata kuliah Pemrograman Bergerak dengan requirements:

✅ Multi-halaman (4 screens)
✅ Penggunaan sensor (Step Counter)
✅ Database lokal (Room)
✅ IDE Android Studio
✅ Bahasa Kotlin
✅ UI Framework Jetpack Compose


👨‍💻 Developer
Nama: Haga Dalpinto Ginting

📝 Catatan

Aplikasi memerlukan device dengan sensor step counter untuk fitur tracking langkah
Emulator Android biasanya tidak memiliki sensor step counter
Data disimpan secara lokal di device
Aplikasi masih dalam tahap pengembangan dan dapat dikembangkan lebih lanjut


📄 License
MIT License - Copyright (c) 2025

Made with Kotlin & Jetpack Compose
