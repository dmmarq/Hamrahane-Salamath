package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppNotification
import com.example.data.model.BoardMember
import com.example.data.model.CampaignItem
import com.example.data.model.Donation
import com.example.data.model.MediaItem
import com.example.data.model.NewsItem
import com.example.data.model.NoticeItem
import com.example.data.model.Pledge
import com.example.data.model.UserProfile
import com.example.repository.AppRepository
import com.example.util.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppFontSize { SMALL, NORMAL, LARGE }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(AppDatabase.getDatabase(application))

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val donations: StateFlow<List<Donation>> = repository.allDonations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalDonated: StateFlow<Long?> = repository.totalDonated
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val donationCount: StateFlow<Int> = repository.donationCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pledges: StateFlow<List<Pledge>> = repository.allPledges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wallMessages = repository.wallMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Settings
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    private val _fontSize = MutableStateFlow(AppFontSize.NORMAL)
    val fontSize = _fontSize.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

    // Notification State
    private val _notifications = MutableStateFlow<List<AppNotification>>(repository.getInitialNotifications())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    val unreadNotificationsCount: StateFlow<Int> = _notifications.combine(_notifications) { list, _ ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2)

    fun postNotification(title: String, message: String, category: String, targetRoute: String? = null) {
        val dateFormatted = SimpleDateFormat("HH:mm - yyyy/MM/dd", Locale.getDefault()).format(Date())
        val formattedDatePersian = AppRepository.formatPersianNumberString(dateFormatted)
        val newNotification = AppNotification(
            id = System.currentTimeMillis(),
            title = title,
            message = message,
            category = category,
            dateFormatted = formattedDatePersian,
            isRead = false,
            targetRoute = targetRoute
        )
        _notifications.value = listOf(newNotification) + _notifications.value

        if (_notificationsEnabled.value) {
            NotificationHelper.sendSystemNotification(
                context = getApplication(),
                notificationId = (System.currentTimeMillis() % 10000).toInt(),
                title = title,
                message = message
            )
        }
    }

    fun markNotificationAsRead(id: Long) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun deleteNotification(id: Long) {
        _notifications.value = _notifications.value.filterNot { it.id == id }
    }

    // Last generated receipt for thank you dialog
    private val _latestReceipt = MutableStateFlow<Donation?>(null)
    val latestReceipt = _latestReceipt.asStateFlow()

    // Base Public Stats (Starts clean at zero)
    val basePublicRaised: Long = 0L
    val basePublicDonorsCount: Int = 0

    val combinedRaisedAmount: StateFlow<Long> = totalDonated.combine(MutableStateFlow(basePublicRaised)) { local, base ->
        (local ?: 0L) + base
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), basePublicRaised)

    val combinedDonorsCount: StateFlow<Int> = donationCount.combine(MutableStateFlow(basePublicDonorsCount)) { local, base ->
        local + base
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), basePublicDonorsCount)

    fun registerUser(name: String, age: String, gender: String) {
        viewModelScope.launch {
            repository.saveUserProfile(name, age, gender)
        }
    }

    fun makeDonation(
        amount: Long,
        campaignName: String = "ساخت بیمارستان ۶۴ تختخوابی",
        paymentMethod: String = "درگاه آنلاین",
        isAnonymous: Boolean = false,
        message: String = "",
        onSuccess: (Donation) -> Unit
    ) {
        viewModelScope.launch {
            val donation = repository.addDonation(amount, campaignName, paymentMethod, isAnonymous, message)
            _latestReceipt.value = donation

            val amountStr = AppRepository.formatPersianNumber(amount)
            postNotification(
                title = "پرداخت جدید ${amountStr} تومان",
                message = "پرداخت مبلغ ${amountStr} تومان برای '${campaignName}' با موفقیت به سیستم اضافه شد.",
                category = "پرداخت",
                targetRoute = "donation_history"
            )

            onSuccess(donation)
        }
    }

    fun clearLatestReceipt() {
        _latestReceipt.value = null
    }

    fun createPledge(monthlyAmount: Long, dayOfMonth: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.addPledge(monthlyAmount, dayOfMonth)

            val amountStr = AppRepository.formatPersianNumber(monthlyAmount)
            postNotification(
                title = "ثبت نذر سلامت جدید",
                message = "تعهد ماهانه به مبلغ ${amountStr} تومان در روز ${AppRepository.formatPersianNumber(dayOfMonth.toLong())} هر ماه ثبت گردید.",
                category = "پرداخت",
                targetRoute = "pledge"
            )

            onSuccess()
        }
    }

    fun removePledge(id: Long) {
        viewModelScope.launch {
            repository.deletePledge(id)
        }
    }

    fun postWallMessage(message: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.addWallMessage(message)

            postNotification(
                title = "پیام جدید در دیوار مهربانی",
                message = "پیام جدید ثبت شد: '${message}'",
                category = "دیوار مهربانی",
                targetRoute = "wall_of_kindness"
            )

            onSuccess()
        }
    }

    fun registerVolunteer(name: String, phone: String, expertise: String, description: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.registerVolunteer(name, phone, expertise, description)

            postNotification(
                title = "ثبت نام داوطلب جدید",
                message = "ثبت‌نام داوطلب گرامی ${name} در زمینه ${expertise} انجام گردید.",
                category = "عمومی",
                targetRoute = "volunteers"
            )

            onSuccess()
        }
    }

    fun toggleTheme(dark: Boolean) {
        _isDarkTheme.value = dark
    }

    fun setFontSize(size: AppFontSize) {
        _fontSize.value = size
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    // Dynamic Content StateFlows
    private val _newsList = MutableStateFlow<List<NewsItem>>(repository.getNewsList())
    val newsList: StateFlow<List<NewsItem>> = _newsList.asStateFlow()

    private val _noticesList = MutableStateFlow<List<NoticeItem>>(repository.getNoticesList())
    val noticesList: StateFlow<List<NoticeItem>> = _noticesList.asStateFlow()

    private val _boardMembers = MutableStateFlow<List<BoardMember>>(repository.getBoardMembers())
    val boardMembers: StateFlow<List<BoardMember>> = _boardMembers.asStateFlow()

    private val _campaigns = MutableStateFlow<List<CampaignItem>>(repository.getCampaigns())
    val campaigns: StateFlow<List<CampaignItem>> = _campaigns.asStateFlow()

    private val _mediaList = MutableStateFlow<List<MediaItem>>(repository.getMediaList())
    val mediaList: StateFlow<List<MediaItem>> = _mediaList.asStateFlow()

    fun addMediaItem(item: MediaItem) {
        _mediaList.value = listOf(item) + _mediaList.value
        val mediaType = if (item.isVideo) "ویدئو" else "تصویر"
        postNotification(
            title = "${mediaType} جدید در گالری",
            message = "پست جدید '${item.title}' در گالری بیمارستان قرار گرفت.",
            category = "گالری",
            targetRoute = "gallery"
        )
    }

    fun updateMediaItem(updated: MediaItem) {
        _mediaList.value = _mediaList.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteMediaItem(id: Long) {
        _mediaList.value = _mediaList.value.filterNot { it.id == id }
    }

    fun addNewsItem(item: NewsItem) {
        _newsList.value = listOf(item) + _newsList.value
        postNotification(
            title = "خبر جدید: ${item.title}",
            message = item.excerpt.ifBlank { item.title },
            category = "اخبار",
            targetRoute = "news_and_notices"
        )
    }

    fun updateNewsItem(updated: NewsItem) {
        _newsList.value = _newsList.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteNewsItem(id: Long) {
        _newsList.value = _newsList.value.filterNot { it.id == id }
    }

    fun addNoticeItem(item: NoticeItem) {
        _noticesList.value = listOf(item) + _noticesList.value
        postNotification(
            title = "اطلاعیه جدید: ${item.title}",
            message = item.summary.ifBlank { item.title },
            category = "اطلاعیه",
            targetRoute = "news_and_notices"
        )
    }

    fun updateNoticeItem(updated: NoticeItem) {
        _noticesList.value = _noticesList.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteNoticeItem(id: Long) {
        _noticesList.value = _noticesList.value.filterNot { it.id == id }
    }

    fun addBoardMember(member: BoardMember) {
        _boardMembers.value = _boardMembers.value + member
    }

    fun updateBoardMember(updated: BoardMember) {
        _boardMembers.value = _boardMembers.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteBoardMember(id: Long) {
        _boardMembers.value = _boardMembers.value.filterNot { it.id == id }
    }

    fun addCampaignItem(campaign: CampaignItem) {
        _campaigns.value = _campaigns.value + campaign
        postNotification(
            title = "کمپین جدید: ${campaign.title}",
            message = campaign.description.ifBlank { campaign.title },
            category = "کمپین",
            targetRoute = "donation"
        )
    }

    fun updateCampaignItem(updated: CampaignItem) {
        _campaigns.value = _campaigns.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteCampaignItem(id: Long) {
        _campaigns.value = _campaigns.value.filterNot { it.id == id }
    }

    fun getNewsList() = repository.getNewsList()
    fun getNoticesList() = repository.getNoticesList()
    fun getBoardMembers() = repository.getBoardMembers()
    fun getCampaigns() = repository.getCampaigns()
    fun getFaqList() = repository.getFaqList()
}
