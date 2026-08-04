package com.example.ui.components

import androidx.compose.ui.graphics.Color
import com.example.repository.AppRepository

object PersianFormatters {
    fun formatNumber(number: Long): String {
        return AppRepository.formatPersianNumber(number)
    }

    fun formatNumber(number: Int): String {
        return AppRepository.formatPersianNumber(number.toLong())
    }

    fun formatToman(amount: Long): String {
        return "${AppRepository.formatPersianNumber(amount)} تومان"
    }

    fun getBadgeForAmount(totalAmount: Long): String {
        return when {
            totalAmount >= 50000000L -> "🏆 حامی بیمارستان فردوسیه"
            totalAmount >= 10000000L -> "💎 خیر طلایی"
            totalAmount >= 2000000L -> "🥇 سفیر سلامت"
            totalAmount >= 500000L -> "🥈 یاور سلامت"
            else -> "🥉 خیر نیک‌اندیش"
        }
    }

    fun getBadgeColor(totalAmount: Long): Color {
        return when {
            totalAmount >= 50000000L -> Color(0xFFD4AF37)
            totalAmount >= 10000000L -> Color(0xFF00ACC1)
            totalAmount >= 2000000L -> Color(0xFFFFB300)
            totalAmount >= 500000L -> Color(0xFF78909C)
            else -> Color(0xFFA1887F)
        }
    }
}
