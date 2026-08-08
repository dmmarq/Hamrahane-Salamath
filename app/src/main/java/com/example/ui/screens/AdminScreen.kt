package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Announcement
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.BoardMember
import com.example.data.model.CampaignItem
import com.example.data.model.Donation
import com.example.data.model.MediaItem
import com.example.data.model.NewsItem
import com.example.data.model.NoticeItem
import com.example.data.model.Pledge
import com.example.data.model.WallMessage
import com.example.ui.components.AppTopBar
import com.example.ui.components.PersianFormatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    donations: List<Donation>,
    wallMessages: List<WallMessage>,
    totalRaised: Long,
    donorsCount: Int,
    newsList: List<NewsItem> = emptyList(),
    noticesList: List<NoticeItem> = emptyList(),
    boardMembers: List<BoardMember> = emptyList(),
    campaigns: List<CampaignItem> = emptyList(),
    mediaList: List<MediaItem> = emptyList(),
    onAddNews: (NewsItem) -> Unit = {},
    onUpdateNews: (NewsItem) -> Unit = {},
    onDeleteNews: (Long) -> Unit = {},
    onAddNotice: (NoticeItem) -> Unit = {},
    onUpdateNotice: (NoticeItem) -> Unit = {},
    onDeleteNotice: (Long) -> Unit = {},
    onAddBoardMember: (BoardMember) -> Unit = {},
    onUpdateBoardMember: (BoardMember) -> Unit = {},
    onDeleteBoardMember: (Long) -> Unit = {},
    onAddCampaign: (CampaignItem) -> Unit = {},
    onUpdateCampaign: (CampaignItem) -> Unit = {},
    onDeleteCampaign: (Long) -> Unit = {},
    onAddMedia: (MediaItem) -> Unit = {},
    onUpdateMedia: (MediaItem) -> Unit = {},
    onDeleteMedia: (Long) -> Unit = {},
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("admin_prefs", android.content.Context.MODE_PRIVATE) }
    var currentPassword by remember { mutableStateOf(sharedPrefs.getString("admin_password", "1234") ?: "1234") }

    var isAuthenticated by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    var showEditBankDialog by remember { mutableStateOf(false) }
    var editBankCard by remember { mutableStateOf(sharedPrefs.getString("bank_card_number", "۶۰۳۷-۹۹۷۹-۱۲۳۴-۵۶۷۸") ?: "۶۰۳۷-۹۹۷۹-۱۲۳۴-۵۶۷۸") }
    var editBankIban by remember { mutableStateOf(sharedPrefs.getString("bank_iban_number", "IR۱۲-۰۱۷۰-۰۰۰۰-۰۱۲۳-۴۵۶۷-۸۹۰۱-۲۳") ?: "IR۱۲-۰۱۷۰-۰۰۰۰-۰۱۲۳-۴۵۶۷-۸۹۰۱-۲۳") }
    var editAccountHolder by remember { mutableStateOf(sharedPrefs.getString("bank_account_holder", "مؤسسه خیریه همراهان سلامت فردوسیه") ?: "مؤسسه خیریه همراهان سلامت فردوسیه") }

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var changePasswordError by remember { mutableStateOf<String?>(null) }

    // Dialog state for Content Management
    var editingNews by remember { mutableStateOf<NewsItem?>(null) }
    var isAddingNews by remember { mutableStateOf(false) }

    var editingNotice by remember { mutableStateOf<NoticeItem?>(null) }
    var isAddingNotice by remember { mutableStateOf(false) }

    var editingCampaign by remember { mutableStateOf<CampaignItem?>(null) }
    var isAddingCampaign by remember { mutableStateOf(false) }

    var editingBoardMember by remember { mutableStateOf<BoardMember?>(null) }
    var isAddingBoardMember by remember { mutableStateOf(false) }

    var editingMedia by remember { mutableStateOf<MediaItem?>(null) }
    var isAddingMedia by remember { mutableStateOf(false) }

    // If not authenticated, display Security Lock Screen
    if (!isAuthenticated) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "ورود به بخش مدیریت",
                    onBackClick = onBackClick
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AdminPanelSettings,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "پنل مدیریت مجمع خیرین سلامت",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "جهت دسترسی به گزارشات مالی، پیام‌های کاربران و تنظیمات پایگاه داده، رمز عبور مدیریت را وارد نمایید.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = {
                                pinInput = it
                                pinError = false
                            },
                            label = { Text("رمز عبور مدیر") },
                            leadingIcon = { Icon(Icons.Outlined.Key, contentDescription = null) },
                            singleLine = true,
                            isError = pinError,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        if (pinError) {
                            Text(
                                text = "رمز عبور وارد شده نادرست است.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                if (pinInput == currentPassword || pinInput == "admin") {
                                    isAuthenticated = true
                                    Toast.makeText(context, "ورود موفقیت‌آمیز به پنل مدیریت", Toast.LENGTH_SHORT).show()
                                } else {
                                    pinError = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("ورود به پنل", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        return
    }

    // Authenticated Admin Dashboard
    Scaffold(
        topBar = {
            AppTopBar(
                title = "داشبورد مدیریت بیمارستان",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Summary Cards Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "وضعیت زنده سیستم و پایگاه داده",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("متصل به آنلاین", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AdminStatItem(
                            title = "کل مبالغ",
                            value = PersianFormatters.formatToman(totalRaised),
                            icon = Icons.Outlined.Payments
                        )

                        AdminStatItem(
                            title = "تعداد تراکنش‌ها",
                            value = "${donations.size} تراکنش",
                            icon = Icons.Outlined.Favorite
                        )

                        AdminStatItem(
                            title = "پیام‌های دیوار",
                            value = "${wallMessages.size} پیام",
                            icon = Icons.Outlined.Message
                        )
                    }
                }
            }

            // Tab Selection Row
            val tabs = listOf("مالی", "اخبار و اطلاعیه‌ها", "کمپین‌ها و هیئت امنا", "گالری و رسانه‌ها", "دیوار مهربانی", "تنظیمات")
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Donations List Tab
                    if (donations.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "هنوز هیچ تراکنش مالی ثبت نشده است.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(donations) { donation ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (donation.isAnonymous) "خیر گمنام" else "پرداخت‌کننده محترم",
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Text(
                                                text = PersianFormatters.formatToman(donation.amount),
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("کد پیگیری: ${donation.trackingCode}", style = MaterialTheme.typography.bodySmall)
                                            Text("تاریخ: ${donation.dateFormatted}", style = MaterialTheme.typography.bodySmall)
                                        }

                                        if (donation.message.isNotBlank()) {
                                            Text(
                                                text = "پیام: \"${donation.message}\"",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Manage News & Notices Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { isAddingNews = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("افزودن خبر")
                                }

                                Button(
                                    onClick = { isAddingNotice = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("افزودن اطلاعیه")
                                }
                            }
                        }

                        // News Section
                        item {
                            Text(
                                text = "مدیریت اخبار بیمارستان (${newsList.size} خبر)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(newsList) { news ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(news.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                        Row {
                                            TextButton(onClick = { editingNews = news }) {
                                                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                            }
                                            TextButton(onClick = { onDeleteNews(news.id) }) {
                                                Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                    Text("${news.category} | ${news.date}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    Text(news.excerpt, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                }
                            }
                        }

                        // Notices Section
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "مدیریت اطلاعیه‌ها (${noticesList.size} اطلاعیه)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(noticesList) { notice ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(notice.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                        Row {
                                            TextButton(onClick = { editingNotice = notice }) {
                                                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                            }
                                            TextButton(onClick = { onDeleteNotice(notice.id) }) {
                                                Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                    Text("${notice.category} | ${notice.date}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                    Text(notice.summary, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Manage Campaigns & Board Members Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { isAddingCampaign = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Outlined.Campaign, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("افزودن کمپین")
                                }

                                Button(
                                    onClick = { isAddingBoardMember = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Outlined.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("افزودن عضو امنا")
                                }
                            }
                        }

                        // Campaigns Section
                        item {
                            Text(
                                text = "کمپین‌های مالی فعال (${campaigns.size} کمپین)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(campaigns) { campaign ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(campaign.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                        Row {
                                            TextButton(onClick = { editingCampaign = campaign }) {
                                                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                            }
                                            TextButton(onClick = { onDeleteCampaign(campaign.id) }) {
                                                Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                    Text("مبلغ هدف: ${PersianFormatters.formatToman(campaign.targetAmount)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    Text(campaign.description, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        // Board Members Section
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "اعضای هیئت امنا و مدیران (${boardMembers.size} عضو)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(boardMembers) { member ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(member.name, fontWeight = FontWeight.Bold)
                                        Row {
                                            TextButton(onClick = { editingBoardMember = member }) {
                                                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                            }
                                            TextButton(onClick = { onDeleteBoardMember(member.id) }) {
                                                Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                    Text("سمت: ${member.role}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    Text(member.bio, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Manage Gallery & Media Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Button(
                                onClick = { isAddingMedia = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("افزودن پست تصویری / ویدئویی به گالری")
                            }
                        }

                        item {
                            Text(
                                text = "مدیریت پست‌های گالری و ویدئوها (${mediaList.size} رسانه)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(mediaList) { media ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AsyncImage(
                                                    model = media.imageUri ?: (if (media.imageRes != 0) media.imageRes else R.drawable.img_hospital_hero_1785868113804),
                                                    contentDescription = media.title,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                                if (media.isVideo) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .background(Color.Black.copy(alpha = 0.3f)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Outlined.Videocam,
                                                            contentDescription = null,
                                                            tint = Color.White,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(media.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                                if (media.imageUri != null) {
                                                    Text("فایل آپلود شده از گالری", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                        }

                                        Row {
                                            TextButton(onClick = { editingMedia = media }) {
                                                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                            }
                                            TextButton(onClick = { onDeleteMedia(media.id) }) {
                                                Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    ) {
                                        AsyncImage(
                                            model = media.imageUri ?: (if (media.imageRes != 0) media.imageRes else R.drawable.img_hospital_hero_1785868113804),
                                            contentDescription = media.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Text(
                                        text = "نوع: ${if (media.isVideo) "ویدئو (مدت: ${media.duration})" else "تصویر"} | دسته: ${media.category} | تاریخ: ${media.date}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )

                                    if (media.description.isNotBlank()) {
                                        Text(media.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                    }
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // Wall Messages Tab
                    if (wallMessages.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "هیچ پیامی روی دیوار مهربانی ثبت نشده است.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(wallMessages) { msg ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(msg.senderName, fontWeight = FontWeight.Bold)
                                            Text(msg.dateFormatted, style = MaterialTheme.typography.bodySmall)
                                        }
                                        Text(msg.message, style = MaterialTheme.typography.bodyMedium)
                                        Text("نوع مشارکت: ${msg.amountText}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }

                5 -> {
                    // Settings & System Info
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.CloudDone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("مشخصات اتصال به پروژه Firebase", fontWeight = FontWeight.Bold)
                                }

                                Text("پروژه فایربیس: ferdousieh-hospital", style = MaterialTheme.typography.bodyMedium)
                                Text("بسته اپلیکیشن (Package Name): com.ferdousieh.hospital", style = MaterialTheme.typography.bodyMedium)
                                Text("فایل پیکربندی: google-services.json (موجود و فعال)", style = MaterialTheme.typography.bodyMedium)
                                Text("پایگاه داده: Cloud Firestore (همگام‌سازی دوطرفه)", style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Payments, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ویرایش مشخصات حساب بانکی خیریه", fontWeight = FontWeight.Bold)
                                }

                                Text("صاحب حساب: $editAccountHolder", style = MaterialTheme.typography.bodySmall)
                                Text("شماره کارت: $editBankCard", style = MaterialTheme.typography.bodySmall)
                                Text("شماره شبا: $editBankIban", style = MaterialTheme.typography.bodySmall)

                                Button(
                                    onClick = { showEditBankDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("ویرایش شماره کارت و شبا")
                                }
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("تغییر رمز عبور پنل مدیریت", fontWeight = FontWeight.Bold)
                                }

                                Text(
                                    "جهت ارتقاء امنیت، می‌توانید رمز عبور ورود به پنل مدیریت را در این بخش تغییر دهید.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Button(
                                    onClick = {
                                        newPasswordInput = ""
                                        confirmPasswordInput = ""
                                        changePasswordError = null
                                        showChangePasswordDialog = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("تغییر رمز عبور مدیریت")
                                }
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("دستورالعمل‌ها و وضعیت دسترسی", fontWeight = FontWeight.Bold)
                                }

                                Text(
                                    "اطلاعات پرداختی و پیام‌های ثبت شده همزمان در دیتابیس داخلی گوشی و Cloud Firestore ثبت می‌گردند و به طور زنده در کلیه دستگاه‌های همگام‌سازی شده به نمایش در می‌آیند.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Bank Account Dialog
    if (showEditBankDialog) {
        var tempCard by remember { mutableStateOf(editBankCard) }
        var tempIban by remember { mutableStateOf(editBankIban) }
        var tempHolder by remember { mutableStateOf(editAccountHolder) }

        AlertDialog(
            onDismissRequest = { showEditBankDialog = false },
            title = {
                Text("ویرایش مشخصات حساب بانکی", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = tempHolder,
                        onValueChange = { tempHolder = it },
                        label = { Text("نام صاحب حساب / موسسه") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tempCard,
                        onValueChange = { tempCard = it },
                        label = { Text("شماره کارت بانکی (۱۶ رقمی)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tempIban,
                        onValueChange = { tempIban = it },
                        label = { Text("شماره شبا (IBAN)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        sharedPrefs.edit()
                            .putString("bank_card_number", tempCard)
                            .putString("bank_iban_number", tempIban)
                            .putString("bank_account_holder", tempHolder)
                            .apply()

                        editBankCard = tempCard
                        editBankIban = tempIban
                        editAccountHolder = tempHolder

                        showEditBankDialog = false
                        Toast.makeText(context, "اطلاعات حساب بانکی با موفقیت به‌روزرسانی شد.", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("ذخیره تغییرات")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBankDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Change Password Dialog
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = {
                Text("تغییر رمز عبور مدیریت", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("رمز عبور جدید پنل مدیریت را وارد نمایید:", style = MaterialTheme.typography.bodySmall)

                    OutlinedTextField(
                        value = newPasswordInput,
                        onValueChange = {
                            newPasswordInput = it
                            changePasswordError = null
                        },
                        label = { Text("رمز عبور جدید") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = confirmPasswordInput,
                        onValueChange = {
                            confirmPasswordInput = it
                            changePasswordError = null
                        },
                        label = { Text("تکرار رمز عبور جدید") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (changePasswordError != null) {
                        Text(
                            text = changePasswordError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPasswordInput.isBlank()) {
                            changePasswordError = "رمز عبور جدید نمی‌تواند خالی باشد."
                        } else if (newPasswordInput != confirmPasswordInput) {
                            changePasswordError = "تکرار رمز عبور با رمز جدید مطابقت ندارد."
                        } else {
                            sharedPrefs.edit().putString("admin_password", newPasswordInput).apply()
                            currentPassword = newPasswordInput
                            showChangePasswordDialog = false
                            Toast.makeText(context, "رمز عبور مدیریت با موفقیت تغییر یافت.", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("ذخیره رمز جدید")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Add/Edit News Dialog
    if (isAddingNews || editingNews != null) {
        val newsToEdit = editingNews
        var title by remember { mutableStateOf(newsToEdit?.title ?: "") }
        var category by remember { mutableStateOf(newsToEdit?.category ?: "اخبار عمومی") }
        var excerpt by remember { mutableStateOf(newsToEdit?.excerpt ?: "") }
        var content by remember { mutableStateOf(newsToEdit?.content ?: "") }

        AlertDialog(
            onDismissRequest = {
                isAddingNews = false
                editingNews = null
            },
            title = { Text(if (newsToEdit == null) "افزودن خبر جدید" else "ویرایش خبر", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان خبر") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("دسته‌بندی (مثلا: گزارش پیشرفت)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = excerpt,
                        onValueChange = { excerpt = it },
                        label = { Text("خلاصه کوتاه") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("متن کامل خبر") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            if (newsToEdit == null) {
                                onAddNews(
                                    NewsItem(
                                        id = System.currentTimeMillis(),
                                        title = title,
                                        category = category,
                                        date = "۱۴۰۳/۰۵/۱۲",
                                        excerpt = excerpt,
                                        content = content
                                    )
                                )
                            } else {
                                onUpdateNews(
                                    newsToEdit.copy(
                                        title = title,
                                        category = category,
                                        excerpt = excerpt,
                                        content = content
                                    )
                                )
                            }
                            isAddingNews = false
                            editingNews = null
                        }
                    }
                ) {
                    Text("ذخیره")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddingNews = false; editingNews = null }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Add/Edit Notice Dialog
    if (isAddingNotice || editingNotice != null) {
        val noticeToEdit = editingNotice
        var title by remember { mutableStateOf(noticeToEdit?.title ?: "") }
        var category by remember { mutableStateOf(noticeToEdit?.category ?: "اطلاعیه رسمی") }
        var summary by remember { mutableStateOf(noticeToEdit?.summary ?: "") }
        var content by remember { mutableStateOf(noticeToEdit?.content ?: "") }

        AlertDialog(
            onDismissRequest = {
                isAddingNotice = false
                editingNotice = null
            },
            title = { Text(if (noticeToEdit == null) "افزودن اطلاعیه جدید" else "ویرایش اطلاعیه", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان اطلاعیه") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("دسته‌بندی (مثلا: فراخوان)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text("خلاصه اطلاعیه") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("متن کامل اطلاعیه") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            if (noticeToEdit == null) {
                                onAddNotice(
                                    NoticeItem(
                                        id = System.currentTimeMillis(),
                                        title = title,
                                        category = category,
                                        summary = summary,
                                        content = content,
                                        date = "۱۴۰۳/۰۵/۱۲",
                                        isImportant = true
                                    )
                                )
                            } else {
                                onUpdateNotice(
                                    noticeToEdit.copy(
                                        title = title,
                                        category = category,
                                        summary = summary,
                                        content = content
                                    )
                                )
                            }
                            isAddingNotice = false
                            editingNotice = null
                        }
                    }
                ) {
                    Text("ذخیره")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddingNotice = false; editingNotice = null }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Add/Edit Campaign Dialog
    if (isAddingCampaign || editingCampaign != null) {
        val campaignToEdit = editingCampaign
        var title by remember { mutableStateOf(campaignToEdit?.title ?: "") }
        var targetStr by remember { mutableStateOf(campaignToEdit?.targetAmount?.toString() ?: "5000000000") }
        var description by remember { mutableStateOf(campaignToEdit?.description ?: "") }

        AlertDialog(
            onDismissRequest = {
                isAddingCampaign = false
                editingCampaign = null
            },
            title = { Text(if (campaignToEdit == null) "افزودن کمپین مالی جدید" else "ویرایش کمپین", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان کمپین") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = targetStr,
                        onValueChange = { targetStr = it },
                        label = { Text("مبلغ هدف (تومان)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("توضیحات کمپین") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val targetAmt = targetStr.toLongOrNull() ?: 5000000000L
                            if (campaignToEdit == null) {
                                onAddCampaign(
                                    CampaignItem(
                                        id = System.currentTimeMillis(),
                                        title = title,
                                        targetAmount = targetAmt,
                                        raisedAmount = 0L,
                                        description = description,
                                        endDate = "پایان پروژه"
                                    )
                                )
                            } else {
                                onUpdateCampaign(
                                    campaignToEdit.copy(
                                        title = title,
                                        targetAmount = targetAmt,
                                        description = description
                                    )
                                )
                            }
                            isAddingCampaign = false
                            editingCampaign = null
                        }
                    }
                ) {
                    Text("ذخیره")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddingCampaign = false; editingCampaign = null }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Add/Edit Board Member Dialog
    if (isAddingBoardMember || editingBoardMember != null) {
        val memberToEdit = editingBoardMember
        var name by remember { mutableStateOf(memberToEdit?.name ?: "") }
        var role by remember { mutableStateOf(memberToEdit?.role ?: "") }
        var bio by remember { mutableStateOf(memberToEdit?.bio ?: "") }

        AlertDialog(
            onDismissRequest = {
                isAddingBoardMember = false
                editingBoardMember = null
            },
            title = { Text(if (memberToEdit == null) "افزودن عضو هیئت امنا" else "ویرایش مشخصات عضو", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("نام و نام خانوادگی") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("سمت (مثلا: رئیس هیئت امنا)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("بیوگرافی و سوابق") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            if (memberToEdit == null) {
                                onAddBoardMember(
                                    BoardMember(
                                        id = System.currentTimeMillis(),
                                        name = name,
                                        role = role,
                                        bio = bio
                                    )
                                )
                            } else {
                                onUpdateBoardMember(
                                    memberToEdit.copy(
                                        name = name,
                                        role = role,
                                        bio = bio
                                    )
                                )
                            }
                            isAddingBoardMember = false
                            editingBoardMember = null
                        }
                    }
                ) {
                    Text("ذخیره")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddingBoardMember = false; editingBoardMember = null }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Add/Edit Media Post Dialog
    if (isAddingMedia || editingMedia != null) {
        LargeMediaEditDialog(
            mediaItem = editingMedia,
            onDismiss = {
                isAddingMedia = false
                editingMedia = null
            },
            onSave = { item ->
                if (editingMedia == null) {
                    onAddMedia(item)
                } else {
                    onUpdateMedia(item)
                }
                isAddingMedia = false
                editingMedia = null
            },
            onDelete = { id ->
                onDeleteMedia(id)
                isAddingMedia = false
                editingMedia = null
            },
            onAddNewItemRequest = {
                editingMedia = null
                isAddingMedia = true
            }
        )
    }
}

@Composable
private fun AdminStatItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}
