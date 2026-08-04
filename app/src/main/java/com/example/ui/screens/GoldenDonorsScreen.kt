package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AppTopBar
import com.example.ui.components.PersianFormatters

data class BadgeTier(
    val title: String,
    val iconStr: String,
    val minAmountText: String,
    val description: String
)

@Composable
fun GoldenDonorsScreen(
    totalUserDonated: Long,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    val tiers = listOf(
        BadgeTier("🥉 خیر نیک‌اندیش", "🥉", "تا ۵۰۰ هزار تومان", "نشان قدردانی برای آغاز همراهی خیرخواهانه"),
        BadgeTier("🥈 یاور سلامت", "🥈", "۵۰۰ هزار تا ۲ میلیون تومان", "همیاری ارزشمند در روند احداث بیمارستان"),
        BadgeTier("🥇 سفیر سلامت", "🥇", "۲ میلیون تا ۱۰ میلیون تومان", "نشان افتخار سفیر درمان و نوعدوستی"),
        BadgeTier("💎 خیر طلایی", "💎", "۱۰ میلیون تا ۵۰ میلیون تومان", "حکاکی نام خیر در لوح ورودی بیمارستان"),
        BadgeTier("🏆 حامی بیمارستان فردوسیه", "🏆", "بالای ۵۰ میلیون تومان", "نام‌گذاری بخش‌های تخصصی به نام خیر یا درگذشتگان")
    )

    val userCurrentBadge = PersianFormatters.getBadgeForAmount(totalUserDonated)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "نشان‌های افتخار و خیرین طلایی",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // User Badge Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_badge_golden_1785868137398),
                        contentDescription = "نشان خیرین",
                        modifier = Modifier.size(100.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "نشان افتخار فعلی شما",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = userCurrentBadge,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "مجموع کمک‌های شما: ${PersianFormatters.formatToman(totalUserDonated)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Text(
                text = "سطوح و نشان‌های افتخار خیرین 🏆",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            tiers.forEach { tier ->
                val isAchieved = userCurrentBadge.contains(tier.title.takeLast(5))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAchieved) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tier.iconStr,
                            fontSize = 32.sp
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tier.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = tier.minAmountText,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = tier.description,
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
