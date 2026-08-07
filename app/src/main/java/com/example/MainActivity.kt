package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.RtlContainer
import com.example.ui.screens.AboutAndTrusteesScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.ContactUsScreen
import com.example.ui.screens.DonationHistoryScreen
import com.example.ui.screens.DonationScreen
import com.example.ui.screens.FaqScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.GoldenDonorsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NewsAndNoticesScreen
import com.example.ui.screens.PledgeScreen
import com.example.ui.screens.ProjectOverviewScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.UserRegisterScreen
import com.example.ui.screens.VolunteersAndPartnersScreen
import com.example.ui.screens.WallOfKindnessScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val fontSize by viewModel.fontSize.collectAsState()
            val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

            val userProfile by viewModel.userProfile.collectAsState()
            val totalDonated by viewModel.combinedRaisedAmount.collectAsState()
            val donorsCount by viewModel.combinedDonorsCount.collectAsState()
            val userDonations by viewModel.donations.collectAsState()
            val userPledges by viewModel.pledges.collectAsState()
            val wallMessages by viewModel.wallMessages.collectAsState()
            val latestReceipt by viewModel.latestReceipt.collectAsState()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RtlContainer {
                        val navController = rememberNavController()

                        // Determine Start Destination
                        val startRoute = "home"

                        NavHost(
                            navController = navController,
                            startDestination = startRoute
                        ) {
                            composable("splash") {
                                SplashScreen(
                                    onSplashFinished = {
                                        if (userProfile?.isRegistered == true) {
                                            navController.navigate("home") {
                                                popUpTo("splash") { inclusive = true }
                                            }
                                        } else {
                                            navController.navigate("welcome") {
                                                popUpTo("splash") { inclusive = true }
                                            }
                                        }
                                    }
                                )
                            }

                            composable("welcome") {
                                WelcomeScreen(
                                    onStartClick = {
                                        navController.navigate("register")
                                    }
                                )
                            }

                            composable("register") {
                                UserRegisterScreen(
                                    onRegisterComplete = { name, age, gender ->
                                        viewModel.registerUser(name, age, gender)
                                        navController.navigate("home") {
                                            popUpTo("welcome") { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable("home") {
                                HomeScreen(
                                    userProfile = userProfile,
                                    totalRaisedAmount = totalDonated,
                                    totalDonorsCount = donorsCount,
                                    onNavigate = { route ->
                                        navController.navigate(route)
                                    }
                                )
                            }

                            composable("project_overview") {
                                ProjectOverviewScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onDonateClick = { navController.navigate("donation") }
                                )
                            }

                            composable("donation") {
                                DonationScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onDonationSubmitted = { amount, campaign, method, isAnonymous, message ->
                                        viewModel.makeDonation(
                                            amount = amount,
                                            campaignName = campaign,
                                            paymentMethod = method,
                                            isAnonymous = isAnonymous,
                                            message = message,
                                            onSuccess = { donation ->
                                                if (message.isNotBlank()) {
                                                    viewModel.postWallMessage(message) {}
                                                }
                                            }
                                        )
                                    }
                                )
                            }

                            composable("donation_history") {
                                DonationHistoryScreen(
                                    donations = userDonations,
                                    totalDonated = userProfile?.totalDonatedAmount ?: 0L,
                                    donationCount = userDonations.size,
                                    onBackClick = { navController.popBackStack() },
                                    onNewDonationClick = { navController.navigate("donation") }
                                )
                            }

                            composable("pledge") {
                                PledgeScreen(
                                    pledges = userPledges,
                                    onBackClick = { navController.popBackStack() },
                                    onSavePledge = { amount, day ->
                                        viewModel.createPledge(amount, day) {}
                                    },
                                    onDeletePledge = { id ->
                                        viewModel.removePledge(id)
                                    }
                                )
                            }

                            composable("wall") {
                                WallOfKindnessScreen(
                                    messages = wallMessages,
                                    onBackClick = { navController.popBackStack() },
                                    onSubmitMessage = { msg ->
                                        viewModel.postWallMessage(msg) {}
                                    }
                                )
                            }

                            composable("golden_donors") {
                                GoldenDonorsScreen(
                                    totalUserDonated = userProfile?.totalDonatedAmount ?: 0L,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable("news_notices") {
                                NewsAndNoticesScreen(
                                    newsList = viewModel.getNewsList(),
                                    noticesList = viewModel.getNoticesList(),
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable("gallery") {
                                GalleryScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable("about_trustees") {
                                AboutAndTrusteesScreen(
                                    boardMembers = viewModel.getBoardMembers(),
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable("volunteers") {
                                VolunteersAndPartnersScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onSubmitVolunteer = { name, phone, exp, desc ->
                                        viewModel.registerVolunteer(name, phone, exp, desc) {}
                                    }
                                )
                            }

                            composable("contact_us") {
                                ContactUsScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable("faq") {
                                FaqScreen(
                                    faqList = viewModel.getFaqList(),
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable("settings") {
                                SettingsScreen(
                                    isDarkTheme = isDarkTheme,
                                    fontSize = fontSize,
                                    notificationsEnabled = notificationsEnabled,
                                    onBackClick = { navController.popBackStack() },
                                    onAdminClick = { navController.navigate("admin") },
                                    onToggleDarkTheme = { viewModel.toggleTheme(it) },
                                    onSetFontSize = { viewModel.setFontSize(it) },
                                    onToggleNotifications = { viewModel.toggleNotifications(it) }
                                )
                            }

                            composable("admin") {
                                AdminScreen(
                                    donations = userDonations,
                                    wallMessages = wallMessages,
                                    totalRaised = totalDonated,
                                    donorsCount = donorsCount,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }

                        // Digital Receipt Dialog Popup when donation is made
                        latestReceipt?.let { donation ->
                            ReceiptDialog(
                                donation = donation,
                                onDismiss = { viewModel.clearLatestReceipt() }
                            )
                        }
                    }
                }
            }
        }
    }
}
