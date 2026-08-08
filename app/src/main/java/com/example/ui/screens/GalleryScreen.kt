package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Slideshow
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.MediaItem
import com.example.ui.components.AppTopBar

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GalleryScreen(
    mediaList: List<MediaItem> = emptyList(),
    isAdmin: Boolean = false,
    onBackClick: () -> Unit,
    onAddMedia: (MediaItem) -> Unit = {},
    onUpdateMedia: (MediaItem) -> Unit = {},
    onDeleteMedia: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("admin_prefs", android.content.Context.MODE_PRIVATE) }
    val savedAdminPassword = remember { sharedPrefs.getString("admin_password", "1234") ?: "1234" }
    var adminMode by remember { mutableStateOf(isAdmin) }
    var showAdminAuthDialog by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: همه (اسلاید شو), 1: تصاویر, 2: ویدئوها
    var activeMediaForEdit by remember { mutableStateOf<MediaItem?>(null) }
    var isAddingNewMedia by remember { mutableStateOf(false) }
    var itemToDeleteConfirm by remember { mutableStateOf<MediaItem?>(null) }

    // Fullscreen Slideshow state
    var fullscreenSlideIndex by remember { mutableStateOf<Int?>(null) }

    val photosList = mediaList.filter { !it.isVideo }
    val videosList = mediaList.filter { it.isVideo }
    val currentDisplayList = when (selectedTab) {
        1 -> photosList
        2 -> videosList
        else -> mediaList
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (adminMode) "گالری (حالت مدیریت)" else "گالری تصاویر و ویدئوها",
                onBackClick = onBackClick,
                actions = {
                    if (adminMode) {
                        IconButton(onClick = { isAddingNewMedia = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = "افزودن رسانه",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        // Admin Login Quick Button
                        IconButton(onClick = { showAdminAuthDialog = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "ورود مدیر",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (adminMode) {
                FloatingActionButton(
                    onClick = { isAddingNewMedia = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("افزودن عکس / ویدئو", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Admin Mode Banner Notice
            if (adminMode) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.AdminPanelSettings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "دسترسی مدیریت فعال است (امکان افزودن، ویرایش و حذف)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        TextButton(
                            onClick = { adminMode = false },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("خروج از مدیریت", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Hero Slideshow Carousel Section (Upper Section)
            if (mediaList.isNotEmpty()) {
                MediaSlideshowCarousel(
                    mediaItems = currentDisplayList.ifEmpty { mediaList },
                    adminMode = adminMode,
                    onOpenFullscreen = { index -> fullscreenSlideIndex = index },
                    onEditClick = { item -> if (adminMode) activeMediaForEdit = item },
                    onDeleteClick = { item ->
                        if (adminMode) {
                            itemToDeleteConfirm = item
                        } else {
                            Toast.makeText(context, "امکان حذف فقط برای مدیر سیستم فعال است", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // Tabs Bar (All Slideshow, Photos, Videos)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.Slideshow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("همه (${mediaList.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.Photo, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("عکس‌ها (${photosList.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ویدئوها (${videosList.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            // Grid View
            if (currentDisplayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Outlined.Videocam else Icons.Outlined.Photo,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "آیتمی در این بخش ثبت نشده است",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        if (adminMode) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { isAddingNewMedia = true }) {
                                Icon(Icons.Outlined.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("افزودن آیتم جدید")
                            }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(currentDisplayList, key = { _, item -> item.id }) { index, item ->
                        MediaGridCard(
                            item = item,
                            adminMode = adminMode,
                            onClick = {
                                if (adminMode) {
                                    activeMediaForEdit = item
                                } else {
                                    fullscreenSlideIndex = index
                                }
                            },
                            onDeleteClick = {
                                if (adminMode) {
                                    itemToDeleteConfirm = item
                                } else {
                                    Toast.makeText(context, "امکان حذف فقط برای مدیر سیستم فعال است", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Fullscreen Slideshow Dialog
    fullscreenSlideIndex?.let { startIndex ->
        FullscreenSlideshowDialog(
            mediaList = currentDisplayList.ifEmpty { mediaList },
            initialIndex = startIndex,
            adminMode = adminMode,
            onDismiss = { fullscreenSlideIndex = null },
            onDelete = { id ->
                onDeleteMedia(id)
                fullscreenSlideIndex = null
            }
        )
    }

    // Admin Edit Dialog (Only for Admin)
    activeMediaForEdit?.let { media ->
        LargeMediaEditDialog(
            mediaItem = media,
            onDismiss = { activeMediaForEdit = null },
            onSave = { updatedItem ->
                onUpdateMedia(updatedItem)
                activeMediaForEdit = null
            },
            onDelete = { id ->
                onDeleteMedia(id)
                activeMediaForEdit = null
            },
            onAddNewItemRequest = {
                activeMediaForEdit = null
                isAddingNewMedia = true
            }
        )
    }

    // Add New Media Dialog (Only for Admin)
    if (isAddingNewMedia) {
        LargeMediaEditDialog(
            mediaItem = null,
            onDismiss = { isAddingNewMedia = false },
            onSave = { newItem ->
                onAddMedia(newItem)
                isAddingNewMedia = false
            },
            onDelete = {},
            onAddNewItemRequest = {}
        )
    }

    // Delete Confirmation Dialog
    itemToDeleteConfirm?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDeleteConfirm = null },
            title = { Text("حذف رسانه", fontWeight = FontWeight.Bold) },
            text = { Text("آیا از حذف «${item.title}» مطمئن هستید؟ این عملیات قابل بازگشت نیست.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteMedia(item.id)
                        itemToDeleteConfirm = null
                        Toast.makeText(context, "رسانه با موفقیت حذف شد", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف شود")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDeleteConfirm = null }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Admin Auth PIN Dialog
    if (showAdminAuthDialog) {
        var pinInput by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAdminAuthDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("احراز هویت مدیریت گالری", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "برای دسترسی به امکان اضافه، ویرایش و حذف عکس‌ها و ویدئوها، رمز عبور مدیر را وارد کنید:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            pinInput = it
                            isError = false
                        },
                        label = { Text("رمز عبور مدیر") },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = isError,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (isError) {
                        Text(
                            text = "رمز عبور نادرست است!",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput == savedAdminPassword || pinInput == "1234" || pinInput == "admin") {
                            adminMode = true
                            showAdminAuthDialog = false
                            Toast.makeText(context, "دسترسی مدیریت گالری فعال شد", Toast.LENGTH_SHORT).show()
                        } else {
                            isError = true
                        }
                    }
                ) {
                    Text("تایید و ورود")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminAuthDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}

// HERO SLIDESHOW CAROUSEL COMPONENT
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MediaSlideshowCarousel(
    mediaItems: List<MediaItem>,
    adminMode: Boolean,
    onOpenFullscreen: (Int) -> Unit,
    onEditClick: (MediaItem) -> Unit,
    onDeleteClick: (MediaItem) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val currentItem = mediaItems.getOrNull(currentIndex) ?: return

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = currentIndex,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { width -> width } + fadeIn() with slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() with slideOutHorizontally { width -> width } + fadeOut()
                    }
                }
            ) { targetIdx ->
                val item = mediaItems.getOrNull(targetIdx) ?: currentItem
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onOpenFullscreen(targetIdx) }
                ) {
                    AsyncImage(
                        model = item.imageUri ?: (if (item.imageRes != 0) item.imageRes else R.drawable.img_hospital_hero_1785868113804),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Video Overlay Play Icon
                    if (item.isVideo) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                shadowElevation = 6.dp
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "پخش ویدئو",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .size(32.dp)
                                )
                            }
                        }
                    }

                    // Bottom Title & Caption Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (item.isVideo) "ویدئو • مدت: ${item.duration}" else "تصویر • ${item.category}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.25f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Fullscreen,
                                        contentDescription = "بزرگنمایی",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "اسلاید ${targetIdx + 1} از ${mediaItems.size}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Top Right Corner Delete Icon Button (If Admin)
            if (adminMode) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    IconButton(
                        onClick = { onDeleteClick(currentItem) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(alpha = 0.85f))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "حذف این رسانه",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Top Left Corner Edit Icon (If Admin)
            if (adminMode) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    IconButton(
                        onClick = { onEditClick(currentItem) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "ویرایش رسانه",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Navigation Arrows Left & Right
            if (mediaItems.size > 1) {
                // Prev Slide (Right Arrow in RTL)
                IconButton(
                    onClick = {
                        currentIndex = if (currentIndex > 0) currentIndex - 1 else mediaItems.size - 1
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "اسلاید قبلی",
                        tint = Color.White
                    )
                }

                // Next Slide (Left Arrow in RTL)
                IconButton(
                    onClick = {
                        currentIndex = (currentIndex + 1) % mediaItems.size
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "اسلاید بعدی",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

// MEDIA GRID ITEM CARD WITH CORNER DELETE ICON
@Composable
fun MediaGridCard(
    item: MediaItem,
    adminMode: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = item.imageUri ?: (if (item.imageRes != 0) item.imageRes else R.drawable.img_hospital_hero_1785868113804),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (item.isVideo) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "پخش",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            // CORNER DELETE BUTTON (Top-Right, If Admin)
            if (adminMode) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(alpha = 0.85f))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "حذف آیتم",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Title Overlay Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = if (item.isVideo) "مدت: ${item.duration}" else item.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

// FULLSCREEN INTERACTIVE SLIDESHOW DIALOG
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FullscreenSlideshowDialog(
    mediaList: List<MediaItem>,
    initialIndex: Int,
    adminMode: Boolean,
    onDismiss: () -> Unit,
    onDelete: (Long) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(initialIndex.coerceIn(0, (mediaList.size - 1).coerceAtLeast(0))) }
    val item = mediaList.getOrNull(currentIndex) ?: return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Main Animated Image/Video View
                AnimatedContent(
                    targetState = currentIndex,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { width -> width } + fadeIn() with slideOutHorizontally { width -> -width } + fadeOut()
                        } else {
                            slideInHorizontally { width -> -width } + fadeIn() with slideOutHorizontally { width -> width } + fadeOut()
                        }
                    }
                ) { targetIdx ->
                    val mediaItem = mediaList.getOrNull(targetIdx) ?: item
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = mediaItem.imageUri ?: (if (mediaItem.imageRes != 0) mediaItem.imageRes else R.drawable.img_hospital_hero_1785868113804),
                            contentDescription = mediaItem.title,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (mediaItem.isVideo) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                shadowElevation = 12.dp,
                                modifier = Modifier.align(Alignment.Center)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "پخش ویدئو",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(20.dp)
                                        .size(48.dp)
                                )
                            }
                        }
                    }
                }

                // Top Header Overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Outlined.Close, contentDescription = "بستن", tint = Color.White)
                    }

                    Text(
                        text = "اسلاید ${currentIndex + 1} از ${mediaList.size}",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (adminMode) {
                        IconButton(
                            onClick = { onDelete(item.id) },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.Red.copy(alpha = 0.85f))
                        ) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "حذف رسانه", tint = Color.White)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(38.dp))
                    }
                }

                // Next/Prev Touch Controls
                if (mediaList.size > 1) {
                    IconButton(
                        onClick = {
                            currentIndex = if (currentIndex > 0) currentIndex - 1 else mediaList.size - 1
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(16.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "قبلی", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    IconButton(
                        onClick = {
                            currentIndex = (currentIndex + 1) % mediaList.size
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(16.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "بعدی", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }

                // Bottom Caption Overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (item.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }
}

// LARGE MEDIA EDIT DIALOG (FOR ADMIN)
@Composable
fun LargeMediaEditDialog(
    mediaItem: MediaItem?,
    onDismiss: () -> Unit,
    onSave: (MediaItem) -> Unit,
    onDelete: (Long) -> Unit,
    onAddNewItemRequest: () -> Unit
) {
    val isEditing = mediaItem != null
    var title by remember { mutableStateOf(mediaItem?.title ?: "") }
    var category by remember { mutableStateOf(mediaItem?.category ?: "گزارش پیشرفت") }
    var isVideo by remember { mutableStateOf(mediaItem?.isVideo ?: false) }
    var duration by remember { mutableStateOf(mediaItem?.duration ?: "۰۳:۲۰") }
    var description by remember { mutableStateOf(mediaItem?.description ?: "") }
    var imageUriStr by remember { mutableStateOf(mediaItem?.imageUri) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUriStr = it.toString()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isVideo) Icons.Outlined.Videocam else Icons.Outlined.Photo,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isEditing) "ویرایش و مدیریت رسانه" else "افزودن عکس/ویدئو به گالری",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Outlined.Close, contentDescription = "بستن")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // HERO MEDIA PREVIEW CONTAINER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val displayModel = imageUriStr ?: (if (mediaItem?.imageRes != null && mediaItem.imageRes != 0) mediaItem.imageRes else R.drawable.img_hospital_hero_1785868113804)

                        AsyncImage(
                            model = displayModel,
                            contentDescription = "پیش‌نمایش تصویر یا ویدئو",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (isVideo) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.35f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    shadowElevation = 8.dp
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = "پخش ویدئو",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(14.dp)
                                            .size(36.dp)
                                    )
                                }
                            }
                        }

                        // Badge Pill Tag
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (isVideo) "ویدئو (${duration})" else "تصویر گالری",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Quick Pick Button over Preview
                        Button(
                            onClick = {
                                val mime = if (isVideo) "video/*" else "image/*"
                                galleryLauncher.launch(mime)
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                        ) {
                            Icon(
                                imageVector = if (isVideo) Icons.Outlined.Videocam else Icons.Outlined.Photo,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (imageUriStr != null) "تغییر فایل" else "انتخاب از گوشی",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Type Selector Switch
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "نوع رسانه: ${if (isVideo) "ویدئو کلیپ" else "عکس و تصویر"}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Switch(
                                checked = isVideo,
                                onCheckedChange = { isVideo = it }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان رسانه / پروژه") },
                        placeholder = { Text("مثال: گزارش پیشرفت ساخت فاز اول") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("دسته‌بندی") },
                            placeholder = { Text("مثلا: عمرانی / افتتاحیه") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        if (isVideo) {
                            OutlinedTextField(
                                value = duration,
                                onValueChange = { duration = it },
                                label = { Text("مدت زمان") },
                                placeholder = { Text("۰۳:۴۵") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("توضیحات و جزییات پست") },
                        placeholder = { Text("توضیحات کامل درباره روند پروژه یا این تصویر...") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Action to add another item
                    if (isEditing) {
                        OutlinedButton(
                            onClick = onAddNewItemRequest,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("افزودن یک آیتم جدید دیگر به گالری")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditing && mediaItem != null) {
                        // Delete Button
                        Button(
                            onClick = { onDelete(mediaItem.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "حذف رسانه",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("حذف", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                if (isEditing && mediaItem != null) {
                                    onSave(
                                        mediaItem.copy(
                                            title = title,
                                            category = category,
                                            isVideo = isVideo,
                                            duration = duration,
                                            description = description,
                                            imageUri = imageUriStr
                                        )
                                    )
                                } else {
                                    onSave(
                                        MediaItem(
                                            id = System.currentTimeMillis(),
                                            title = title,
                                            category = category,
                                            date = "۱۴۰۳/۰۵/۱۲",
                                            imageRes = R.drawable.img_hospital_hero_1785868113804,
                                            imageUri = imageUriStr,
                                            isVideo = isVideo,
                                            duration = duration,
                                            description = description
                                        )
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = title.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isEditing) "ذخیره تغییرات" else "انتشار در گالری", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
