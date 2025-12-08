package com.example.accountbook.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.accountbook.MainActivity
// import com.example.accountbook.R  <-- 暫時註解掉，改用系統圖示

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReminderReceiver", "onReceive: 接收到記帳提醒廣播！")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // ★ 修改 1：改用新的 ID，強迫系統重新抓取設定
        val channelId = "daily_reminder_channel_high"
        val channelName = "每日記帳提醒 (重要)"

        // 建立通知頻道 (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // ★ 修改 2：提升為 IMPORTANCE_HIGH (會彈出視窗 + 有聲音)
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "提醒使用者每日記帳"
                enableVibration(true) // 開啟震動
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 點擊通知後開啟 MainActivity
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 建構通知
        val notification = NotificationCompat.Builder(context, channelId)
            // ★ 修改 3：改用系統內建圖示 (避免大頭照變成白色方塊看不見)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("記帳提醒")
            .setContentText("今天記帳了嗎？花一分鐘記錄一下今天的開支吧！")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 設定高優先級
            .setDefaults(NotificationCompat.DEFAULT_ALL)   // 開啟震動與聲音
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(1001, notification)
            Log.d("ReminderReceiver", "notify: 通知發送指令已執行 (High Priority)")
        } catch (e: SecurityException) {
            Log.e("ReminderReceiver", "notify error: 無法發送通知，權限可能不足", e)
        }
    }
}