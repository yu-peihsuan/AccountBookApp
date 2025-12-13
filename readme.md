# AccountBook 記帳本 APP 💰

## 專案簡介
這是一個使用 **Android (Kotlin)** 與 **Jetpack Compose** 開發的個人記帳應用程式。採用 MVVM 架構與 Material Design 3 設計風格，提供直觀的操作介面，幫助使用者輕鬆記錄每日收支，並透過圖表分析消費習慣，達成理財目標。

## ✨ 主要功能

### 1. 帳戶與安全
* **註冊/登入系統**：基於本地 SQLite 資料庫的使用者管理。
* **個人化設定**：
    * 支援更換大頭貼（讀取裝置相簿）。
    * 修改使用者暱稱。
* **帳號管理**：支援刪除帳號功能，連動清除所有相關交易紀錄。

### 2. 記帳功能
* **收支記錄**：支援「支出」與「收入」兩種類型。
* **分類管理**：
    * 內建豐富的圖示分類（如：早餐、交通、購物、薪水、發票等）。
    * **自訂分類**：使用者可自行新增專屬的類別名稱。
* **智慧輸入**：
    * **內建計算機**：輸入金額時可直接進行加減乘除運算。
    * 日期選擇器：可補記過去日期的帳務。
    * 備註功能：紀錄詳細消費內容。

### 3. 首頁概覽
* **預算儀表板**：環形圖顯示本月預算使用百分比與剩餘額度。
* **即時統計**：顯示當月總收入、總支出與結餘。
* **近期紀錄**：條列式顯示每日交易明細，支援點擊編輯。

### 4. 圖表分析 📊
* **多維度檢視**：支援「月檢視」、「年檢視」與「自訂日期區間」篩選。
* **趨勢分析**：長條圖 (Bar Chart) 呈現每日/每月的收支變化趨勢。
* **佔比分析**：圓餅圖 (Pie Chart) 清晰呈現各分類的支出/收入比例。
* **分類明細**：點擊圖表下方的分類，可查看該類別在選定區間內的所有交易列表。

### 5. 實用工具與設定
* **每日提醒**：可設定固定時間發送通知，養成記帳習慣（使用 `AlarmManager`）。
* **多國貨幣**：支援切換 台幣 (NT$)、美金 ($)、日幣 (¥)。
* **資料匯出**：支援將完整交易紀錄匯出為 **CSV** 檔案，方便在 Excel 中整理。

## 🛠️ 技術架構

* **開發語言**：Kotlin
* **UI 框架**：Jetpack Compose (Material Design 3)
* **架構模式**：MVVM (Model-View-ViewModel)
* **資料庫**：SQLite (`SQLiteOpenHelper`)
* **圖片加載**：Coil
* **導航**：Jetpack Navigation Compose
* **非同步處理**：Coroutines
* **關鍵 Android API**：
    * `AlarmManager` & `BroadcastReceiver`：實作每日定時提醒。
    * `FileProvider`：實作安全的檔案分享 (CSV 匯出)。
    * `ActivityResultContracts`：處理動態權限請求與照片選取。

## 📂 專案結構

```text
com.example.accountbook
├── data
│   └── DBHandler.kt          # SQLite 資料庫操作 (CRUD)
├── receiver
│   └── ReminderReceiver.kt   # 廣播接收器 (處理鬧鐘與通知)
├── screens                   # Compose UI 畫面
│   ├── LoginScreen.kt        # 登入/註冊
│   ├── HomeScreen.kt         # 首頁 (預算、列表)
│   ├── AddTransactionScreen.kt # 新增/編輯交易 (含計算機邏輯)
│   ├── ChartScreen.kt        # 圖表分析 (Canvas 繪圖)
│   ├── CategoryDetailScreen.kt # 分類詳細列表
│   └── SettingScreen.kt      # 設定頁面
├── ui.theme                  # 主題、顏色與字型定義
├── viewmodel
│   └── TransactionViewModel.kt # 資料邏輯層 (State管理)
└── MainActivity.kt           # 程式進入點與 Navigation Host 設定