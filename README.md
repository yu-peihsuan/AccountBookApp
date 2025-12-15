# AccountBook App (記帳本)

這是一個基於 Android Jetpack Compose 開發的個人記帳應用程式。採用MVVM架構夠進行開發，將UI層和資料層分離，增加可讀性和可維護性。

## 主要功能

* **記帳管理**
    * 支援「支出」與「收入」紀錄。
    * 內建計算機功能，輸入金額更方便。
    * 提供多種預設分類（如：早餐、交通、娛樂、薪水等），並支援 **自訂分類**。

* **記帳紀錄(首頁)**
    * 即時顯示當月預算剩餘百分比（圓環圖）。
    * 顯示當月總收入、總支出與結餘。
    * 以日期分組顯示每日交易清單。
    * 快速切換月份查看歷史紀錄。

* **圖表分析**
    * 提供圓餅圖分析各類別收支占比。
    * 提供長條圖查看每日或每月的收支趨勢。
    * 可依照「年」、「月」或「自訂區間」篩選資料。

* **個人化設定**
    * **預算設定**：設定每月預算目標，首頁即時監控。
    * **貨幣單位**：支援切換台幣 (NT$)、美金 ($) 或日幣 (¥)。
    * **每日提醒**：可設定固定時間通知，養成記帳習慣。
    * **資料匯出**：支援將交易紀錄匯出為 CSV 檔案，方便於 Excel 查看。
    * **帳號管理**：支援使用者註冊、登入、修改頭像與暱稱。

## 技術堆疊 (Tech Stack)

* **語言**: Kotlin
* **UI 框架**: Jetpack Compose(Material3 Design)
* **架構**: MVVM (Model-View-ViewModel)
* **資料儲存**: SQLite (Local Database)
* **非同步處理**: Kotlin Coroutines
* **導航**: Jetpack Navigation Compose
* **依賴注入/其他**: AndroidX Lifecycle, ViewModel

## 應用程式截圖 (Screenshots)

| 首頁 (Home) | 新增交易 (Add) | 圖表分析 (Chart) | 設定 (Setting) |
|:---:|:---:|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/6b0478e7-dbd6-4dda-9145-6e1614dbf99f" width="300" /> | <img src="https://github.com/user-attachments/assets/608d33de-0067-4f9f-a174-35fc15b5304e" width="300" /> | <img src="https://github.com/user-attachments/assets/21e9d5d9-ea2a-4c6f-b1a5-2abade3eb1a6" width="300" /> | <img src="https://github.com/user-attachments/assets/042639df-bdb0-4f5b-9d00-585718830575" width="300" /> |

## 專案結構

```text
com.example.accountbook
├── data            # 資料庫操作 (DBHandler)
├── receiver        # 廣播接收器 (如：鬧鐘提醒)
├── screens         # UI 畫面 (Compose Screens: Home, Add, Chart, Setting...)
├── viewmodel       # 狀態管理 (TransactionViewModel)
├── ui.theme        # 主題與顏色設定
└── MainActivity.kt # 程式進入點
