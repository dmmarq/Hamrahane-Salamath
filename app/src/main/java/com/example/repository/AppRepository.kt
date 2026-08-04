package com.example.repository

import com.example.data.local.AppDatabase
import com.example.data.model.BoardMember
import com.example.data.model.CampaignItem
import com.example.data.model.Donation
import com.example.data.model.FaqItem
import com.example.data.model.NewsItem
import com.example.data.model.NoticeItem
import com.example.data.model.Pledge
import com.example.data.model.UserProfile
import com.example.data.model.VolunteerRegistration
import com.example.data.model.WallMessage
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppRepository(private val db: AppDatabase) {

    val userProfile: Flow<UserProfile?> = db.userDao().getUserProfile()
    val allDonations: Flow<List<Donation>> = db.donationDao().getAllDonations()
    val totalDonated: Flow<Long?> = db.donationDao().getTotalDonated()
    val donationCount: Flow<Int> = db.donationDao().getDonationCount()
    val allPledges: Flow<List<Pledge>> = db.pledgeDao().getAllPledges()
    val wallMessages: Flow<List<WallMessage>> = db.wallDao().getAllWallMessages()

    suspend fun saveUserProfile(name: String, age: String, gender: String) {
        val current = db.userDao().getUserProfileOnce()
        val updated = (current ?: UserProfile()).copy(
            name = name,
            age = age,
            gender = gender,
            isRegistered = true
        )
        db.userDao().insertOrUpdateUser(updated)
    }

    suspend fun addDonation(
        amount: Long,
        campaignName: String,
        paymentMethod: String,
        isAnonymous: Boolean,
        message: String
    ): Donation {
        val timestamp = System.currentTimeMillis()
        val trackingCode = "HS-${(100000..999999).random()}"
        val receiptNo = "RC-${(1000000..9999999).random()}"
        val dateFormatted = getCurrentPersianDate()

        val donation = Donation(
            amount = amount,
            dateFormatted = dateFormatted,
            timestamp = timestamp,
            campaignName = campaignName,
            paymentMethod = paymentMethod,
            trackingCode = trackingCode,
            receiptNo = receiptNo,
            isAnonymous = isAnonymous,
            message = message
        )
        db.donationDao().insertDonation(donation)

        // Update user stats
        val current = db.userDao().getUserProfileOnce() ?: UserProfile()
        val newTotal = current.totalDonatedAmount + amount
        val newCount = current.totalDonationCount + 1
        db.userDao().insertOrUpdateUser(
            current.copy(
                totalDonatedAmount = newTotal,
                totalDonationCount = newCount
            )
        )

        // If user left a message, add to Wall of Kindness
        if (message.isNotBlank()) {
            val sender = if (isAnonymous) "خیر گمنام" else if (current.name.isNotBlank()) current.name else "یاور سلامت"
            val amountStr = "${formatPersianNumber(amount)} تومان"
            db.wallDao().insertWallMessage(
                WallMessage(
                    senderName = sender,
                    message = message,
                    amountText = amountStr,
                    dateFormatted = dateFormatted
                )
            )
        }

        return donation
    }

    suspend fun addPledge(monthlyAmount: Long, dayOfMonth: Int): Long {
        val pledge = Pledge(
            monthlyAmount = monthlyAmount,
            dayOfMonth = dayOfMonth,
            createdDate = getCurrentPersianDate()
        )
        return db.pledgeDao().insertPledge(pledge)
    }

    suspend fun deletePledge(id: Long) {
        db.pledgeDao().deletePledge(id)
    }

    suspend fun addWallMessage(message: String) {
        val current = db.userDao().getUserProfileOnce()
        val sender = if (current?.name?.isNotBlank() == true) current.name else "همراه نیکی"
        db.wallDao().insertWallMessage(
            WallMessage(
                senderName = sender,
                message = message,
                amountText = "مشارکت معنوی",
                dateFormatted = getCurrentPersianDate()
            )
        )
    }

    suspend fun registerVolunteer(name: String, phone: String, expertise: String, description: String) {
        db.volunteerDao().insertVolunteer(
            VolunteerRegistration(
                name = name,
                phone = phone,
                expertise = expertise,
                description = description,
                dateSubmitted = getCurrentPersianDate()
            )
        )
    }

    // Default static/rich Persian content for news, notices, board members, FAQs & campaigns
    fun getNewsList(): List<NewsItem> = listOf(
        NewsItem(
            id = 1,
            title = "اتمام موفقیت‌آمیز فاز فونداسیون بیمارستان ۶۴ تختخوابی فردوسیه",
            category = "گزارش پیشرفت",
            date = "۱۴۰۳/۰۵/۱۰",
            excerpt = "با همت خیرین محترم و تلاش شبانه‌روزی مهندسان عمران، بتن‌ریزی فونداسیون اصلی بیمارستان به طور کامل پایان یافت.",
            content = "پروژه احداث بیمارستان ۶۴ تختخوابی شهر فردوسیه با مساحت ۵۰۰۰ متر مربع زیربنا وارد مرحله اجرای اسکلت بتنی شده است. این پروژه به منظور ارتقای سطح درمان و پاسخگویی به نیاز ۵۰ هزار نفر از اهالی منطقه طراحی گردیده و شامل بخش‌های اورژانس، آی‌سی‌یو، سی‌سی‌یو، اطاق‌های عمل مجهز و کلینیک‌های تخصصی خواهد بود."
        ),
        NewsItem(
            id = 2,
            title = "بازدید هیئت خیرین کشوری از محل پروژه بیمارستان",
            category = "همایش‌ها و رویدادها",
            date = "۱۴۰۳/۰۴/۲۵",
            excerpt = "جمعی از خیرین برجسته حوزه سلامت کشور با حضور در شهر فردوسیه از مراحل اجرایی پروژه بازدید به عمل آوردند.",
            content = "در این بازدید خیرین گرامی ضمن ابراز خرسندی از شفافیت مالی و سرعت پیشرفت فیزیکی، متعهد به تامین هزینه‌های تجهیز دو باب اتاق عمل جراحی تخصصی گردیدند. اعضای هیئت امنای خیریه «همراهان سلامت» از تمامی نیت‌های خیرخواهانه مردم شریف قدردانی کردند."
        ),
        NewsItem(
            id = 3,
            title = "آغاز کمپین «نذر سلامت» به مناسبت ایام معنوی",
            category = "کمپین خیرخواهانه",
            date = "۱۴۰۳/۰۴/۰۱",
            excerpt = "شهروندان نیکوکار می‌توانند با پرداخت مبالغ دلخواه یا تعهد ماهانه در ثواب جاریه بیمارستان فردوسیه سهیم شوند.",
            content = "طرح نذر سلامت با هدف مشارکت همگانی و مستمر مردم عزیز راه‌اندازی شد. هر فرد می‌تواند با تعهد پرداخت ماهیانه حتی ۵۰ هزار تومان، سهمی بزرگ در احداث تجهیزات حیاتی این مرکز درمانی ایفا نماید."
        )
    )

    fun getNoticesList(): List<NoticeItem> = listOf(
        NoticeItem(
            id = 1,
            title = "جلسه عمومی هیئت امنا با خیرین و مردم شریف فردوسیه",
            category = "اطلاعیه رسمی",
            summary = "ارائه گزارش حسابرسی مالی ۶ ماهه اول و پاسخ به سوالات شهروندان",
            content = "زمان: پنج‌شنبه جاری ساعت ۱۶ - مکان: سالن همایش‌های شهرداری فردوسیه. حضور تمام خیرین و اهالی محترم موجب گرمی بخشیدن به این حرکت جهادی خواهد بود.",
            date = "۱۴۰۳/۰۵/۱۲",
            isImportant = true
        ),
        NoticeItem(
            id = 2,
            title = "فراخوان جذب نیروهای داوطلب فنی و رسانه‌ای",
            category = "فراخوان داوطلب",
            summary = "دعوت از مهندسان عمران، عکاسان، تولیدکنندگان محتوا و داوطلبان اداری",
            content = "از کلیه علاقمندان به همراهی در زمینه‌های تخصص فنی، رسانه‌ای و اجرایی دعوت می‌شود از طریق بخش ثبت‌نام داوطلبان در اپلیکیشن اطلاعات خود را ثبت فرمایند.",
            date = "۱۴۰۳/۰۵/۰۵",
            isImportant = false
        )
    )

    fun getBoardMembers(): List<BoardMember> = listOf(
        BoardMember(
            id = 1,
            name = "حاج محسن فردوسی",
            role = "رئیس هیئت امنا",
            bio = "خیر کشوری و فعال در ساخت بیش از ۱۰ مرکز درمانی و آموزشی در استان تهران"
        ),
        BoardMember(
            id = 2,
            name = "دکتر رضا کریمی",
            role = "مدیرعامل خیریه و متخصص جراحی",
            bio = "استاد دانشگاه علوم پزشکی و سرپرست تیم متخصصین درمان منطقه"
        ),
        BoardMember(
            id = 3,
            name = "مهندس امیرحسین صادقی",
            role = "مسئول فنی و عمرانی پروژه",
            bio = "کارشناس ارشد سازه با ۲۰ سال سابقه در طراحی و نظارت بیمارستان‌های تخصصی"
        ),
        BoardMember(
            id = 4,
            name = "خانم زهرا علوی",
            role = "مدیر امور خیرین و روابط عمومی",
            bio = "فعال اجتماعی و سرپرست کمپین‌های جذب مشارکت‌های مردمی و نوعدوستی"
        )
    )

    fun getCampaigns(): List<CampaignItem> = listOf(
        CampaignItem(
            id = 1,
            title = "کمپین نذر سلامت (تجهیز اورژانس)",
            targetAmount = 5000000000L,
            raisedAmount = 3200000000L,
            description = "تامین هزینه‌های احداث و تجهیز بخش اورژانس پیشرفته بیمارستان فردوسیه",
            endDate = "۱۴۰۳/۰۶/۳۱"
        ),
        CampaignItem(
            id = 2,
            title = "کمپین یادبود اموات و صدقه جاریه",
            targetAmount = 10000000000L,
            raisedAmount = 6800000000L,
            description = "اهداء مبالغ یادبود برای شادی روح درگذشتگان در قالب نام‌گذاری بخش‌های درمانی",
            endDate = "پایان پروژه"
        ),
        CampaignItem(
            id = 3,
            title = "تجهیز ۲ باب اتاق عمل جراحی تخصصی",
            targetAmount = 15000000000L,
            raisedAmount = 8500000000L,
            description = "خرید دستگاه‌های بیهوشی، مانیتورینگ قلب و چراغ‌های سیالیتیک اتاق عمل",
            endDate = "۱۴۰۳/۰۸/۳۰"
        )
    )

    fun getFaqList(): List<FaqItem> = listOf(
        FaqItem(
            id = 1,
            question = "بیمارستان ۶۴ تختخوابی فردوسیه دقیقاً در کجا احداث می‌شود؟",
            answer = "پروژه در زمین اهدایی شهرداری فردوسیه به مساحت ۵۰۰۰ متر مربع در ابتدای بلوار اصلی شهر واقع گردیده است."
        ),
        FaqItem(
            id = 2,
            question = "شفافیت مالی و نظارت بر هزینه‌کرد کمک‌ها چگونه است؟",
            answer = "تمام حساب‌های خیریه تحت نظارت سازمان حسابرسی رسمی و گزارش‌های مالی ماهانه در اپلیکیشن به‌صورت فایل PDF و نموداری منتشر می‌شود."
        ),
        FaqItem(
            id = 3,
            question = "آیا امکان پرداخت کارت به کارت یا نذر ماهیانه وجود دارد؟",
            answer = "بله، در بخش کمک مالی می‌توانید شماره کارت رسمی خیریه را کپی نموده یا در بخش نذر سلامت، تعهد ماهانه خود را فعال فرمایید."
        ),
        FaqItem(
            id = 4,
            question = "چگونه می‌توانم نام خود یا درگذشتگانم را در لوح سپاس بیمارستان ثبت کنم؟",
            answer = "کمک‌های بالای ۱۰ میلیون تومان امکان ثبت نام خیر در لوح افتخار ورودی بیمارستان و بخش‌های تخصصی را خواهند داشت."
        )
    )

    private fun getCurrentPersianDate(): String {
        // Simplified Persian date formatting string
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val englishDate = sdf.format(Date())
        return formatPersianNumberString("۱۴۰۳/${englishDate.substring(5)}")
    }

    companion object {
        fun formatPersianNumber(number: Long): String {
            val formattedStr = String.format(Locale.US, "%,d", number)
            return formatPersianNumberString(formattedStr)
        }

        fun formatPersianNumberString(str: String): String {
            return str.replace('0', '۰')
                .replace('1', '۱')
                .replace('2', '۲')
                .replace('3', '۳')
                .replace('4', '۴')
                .replace('5', '۵')
                .replace('6', '۶')
                .replace('7', '۷')
                .replace('8', '۸')
                .replace('9', '۹')
        }
    }
}
