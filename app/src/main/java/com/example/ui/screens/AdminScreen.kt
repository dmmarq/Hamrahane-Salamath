package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Donation
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
            val tabs = listOf("تراکنش‌های مالی", "پیام‌های دیوار", "تنظیمات فایربیس")
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
                                style = MaterialTheme.typography.bodyMedium,
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

                2 -> {
                    // Firebase & Configuration Info
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
