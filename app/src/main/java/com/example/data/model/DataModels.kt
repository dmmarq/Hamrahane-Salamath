package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val age: String = "",
    val gender: String = "آقا", // آقا یا خانم
    val isRegistered: Boolean = false,
    val totalDonatedAmount: Long = 0L,
    val totalDonationCount: Int = 0,
    val joinDate: String = "۱۴۰۳/۰۱/۰۱"
)

@Entity(tableName = "donations")
data class Donation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Long,
    val dateFormatted: String,
    val timestamp: Long = System.currentTimeMillis(),
    val campaignName: String = "ساخت بیمارستان ۶۴ تختخوابی",
    val paymentMethod: String = "درگاه آنلاین",
    val trackingCode: String,
    val receiptNo: String,
    val isAnonymous: Boolean = false,
    val message: String = ""
)

@Entity(tableName = "pledges")
data class Pledge(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monthlyAmount: Long,
    val dayOfMonth: Int = 1,
    val isPledgeActive: Boolean = true,
    val createdDate: String,
    val title: String = "نذر سلامت ماهانه"
)

@Entity(tableName = "wall_messages")
data class WallMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderName: String,
    val message: String,
    val amountText: String,
    val dateFormatted: String,
    val isApproved: Boolean = true
)

@Entity(tableName = "volunteers")
data class VolunteerRegistration(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val expertise: String,
    val description: String,
    val dateSubmitted: String
)

data class NoticeItem(
    val id: Long,
    val title: String,
    val category: String,
    val summary: String,
    val content: String,
    val date: String,
    val isImportant: Boolean = false
)

data class NewsItem(
    val id: Long,
    val title: String,
    val category: String,
    val date: String,
    val excerpt: String,
    val content: String,
    val readTime: String = "۳ دقیقه"
)

data class BoardMember(
    val id: Long,
    val name: String,
    val role: String,
    val bio: String
)

data class CampaignItem(
    val id: Long,
    val title: String,
    val targetAmount: Long,
    val raisedAmount: Long,
    val description: String,
    val endDate: String
)

data class MediaItem(
    val id: Long,
    val title: String,
    val category: String,
    val date: String,
    val imageRes: Int = 0,
    val imageUri: String? = null,
    val isVideo: Boolean = false,
    val duration: String = "",
    val description: String = ""
)

data class FaqItem(
    val id: Long,
    val question: String,
    val answer: String
)

data class AppNotification(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String,
    val category: String, // e.g., "پرداخت", "اطلاعیه", "اخبار", "گالری", "دیوار مهربانی", "عمومی"
    val dateFormatted: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val targetRoute: String? = null
)
