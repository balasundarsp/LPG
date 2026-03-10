# LPG Agency Accounting App 📦

A native Android app for managing an LPG gas agency's accounting — sales, expenses, and cylinder inventory, with full database backup & sync between devices.

---

## Features

| Module | Details |
|---|---|
| **Dashboard** | Today's sales, expenses, profit, outstanding balances, full/empty cylinder count |
| **Sales** | Record refills, new cylinder sales, deposits. Auto-calculates balance. Search by customer. Edit/delete. |
| **Expenses** | Log expenses by category (Delivery, Staff, Rent, etc). Track payment method. |
| **Inventory** | Track full & empty cylinders per type (6kg, 12kg, 50kg). Stock-in with supplier receipts. |
| **Backup & Sync** | Export DB file → share via WhatsApp/email → import on another device with same app |

---

## How to Build

### Requirements
- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 34
- Kotlin 1.9.x
- Java 8+

### Steps
1. Open Android Studio
2. **File → Open** → select the `lpg_agency_app` folder
3. Wait for Gradle sync to complete
4. Connect an Android device (API 26+) or start an emulator
5. Click **Run ▶**

> **Note:** The MPAndroidChart library requires the JitPack repository. Add this to your `settings.gradle`:
> ```gradle
> dependencyResolutionManagement {
>     repositories {
>         google()
>         mavenCentral()
>         maven { url 'https://jitpack.io' }  // Add this line
>     }
> }
> ```

---

## How Database Sync Works

### Sending to another device:
1. Open app → Dashboard → **Backup**
2. Tap **Export & Share Database**
3. Share the `.db` file via WhatsApp, email, Bluetooth, etc.

### Receiving on another device:
1. Open the received `.db` file
2. Open app → Dashboard → **Backup**
3. Tap **Import Database from File**
4. Select the `.db` file → Confirm
5. Restart the app

> ⚠️ Import **replaces** all data. Always export first before importing on a device with existing records.

---

## Project Structure

```
app/src/main/
├── java/com/lpgagency/accounting/
│   ├── MainActivity.kt
│   ├── data/
│   │   ├── models/       Models.kt (Sale, Expense, InventoryItem, StockMovement)
│   │   ├── dao/          Daos.kt
│   │   └── db/           AppDatabase.kt, AppRepository.kt
│   ├── ui/
│   │   ├── dashboard/    DashboardFragment, BackupFragment, RecentSalesAdapter
│   │   ├── sales/        SalesFragment, AddSaleFragment, SalesAdapter
│   │   ├── expenses/     ExpensesFragment, AddExpenseFragment, ExpensesAdapter
│   │   └── inventory/    InventoryFragment, InventoryAdapter
│   └── utils/
│       ├── DatabaseBackupUtil.kt
│       └── FormatUtil.kt
└── res/
    ├── layout/           All XML layouts
    ├── navigation/       nav_graph.xml
    ├── menu/             bottom_nav_menu.xml
    └── xml/              file_paths.xml (FileProvider)
```

---

## Cylinder Types Supported
- 6kg, 12kg, 50kg, Other

## Sale Types
- Refill, New Cylinder, Deposit Return, Other

## Expense Categories
- Delivery, Maintenance, Staff, Rent, Utilities, Purchases, Other
