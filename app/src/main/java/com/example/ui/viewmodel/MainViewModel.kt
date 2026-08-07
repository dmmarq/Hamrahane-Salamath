package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Donation
import com.example.data.model.Pledge
import com.example.data.model.UserProfile
import com.example.repository.AppRepository
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
            onSuccess(donation)
        }
    }

    fun clearLatestReceipt() {
        _latestReceipt.value = null
    }

    fun createPledge(monthlyAmount: Long, dayOfMonth: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.addPledge(monthlyAmount, dayOfMonth)
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
            onSuccess()
        }
    }

    fun registerVolunteer(name: String, phone: String, expertise: String, description: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.registerVolunteer(name, phone, expertise, description)
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

    fun getNewsList() = repository.getNewsList()
    fun getNoticesList() = repository.getNoticesList()
    fun getBoardMembers() = repository.getBoardMembers()
    fun getCampaigns() = repository.getCampaigns()
    fun getFaqList() = repository.getFaqList()
}
