package com.jobiest.android.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.jobiest.android.JobiestApp
import com.jobiest.android.data.repository.*
import com.jobiest.android.network.models.JobDto
import com.jobiest.android.notifications.PushTokenManager
import com.jobiest.android.security.AuthState
import com.jobiest.android.ui.components.JobiestBottomBar
import com.jobiest.android.ui.components.JobiestTopBar
import com.jobiest.android.ui.components.LoadingView
import com.jobiest.android.ui.screens.agent.AgentScreen
import com.jobiest.android.ui.screens.applications.ApplicationWizardScreen
import com.jobiest.android.ui.screens.applications.ApplicationsScreen
import com.jobiest.android.ui.screens.applications.StartApplicationScreen
import com.jobiest.android.ui.screens.auth.*
import com.jobiest.android.ui.screens.billing.BillingScreen
import com.jobiest.android.ui.screens.dashboard.DashboardScreen
import com.jobiest.android.ui.screens.jobs.JobDetailScreen
import com.jobiest.android.ui.screens.jobs.JobSearchScreen
import com.jobiest.android.ui.screens.jobs.SavedJobsScreen
import com.jobiest.android.ui.screens.notifications.NotificationSettingsScreen
import com.jobiest.android.ui.screens.profile.ProfileScreen
import com.jobiest.android.ui.screens.resume.ResumeStudioScreen
import com.jobiest.android.ui.screens.tools.AtsScannerScreen
import com.jobiest.android.ui.screens.tools.CareerToolsScreen
import com.jobiest.android.ui.screens.tools.ToolExecutionScreen
import com.jobiest.android.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun JobiestNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val app = JobiestApp.instance
    val sessionManager = app.sessionManager
    val networkClient = app.networkClient
    val scope = rememberCoroutineScope()

    val authRepo = remember {
        AuthRepository(
            authService = networkClient.supabaseAuthService,
            jobiestApiService = networkClient.jobiestApiService,
            sessionManager = sessionManager
        )
    }
    val jobRepo = remember { JobRepository(networkClient.jobiestApiService) }
    val appRepo = remember { ApplicationRepository(networkClient.jobiestApiService) }
    val profileRepo = remember { ProfileRepository(networkClient.jobiestApiService) }
    val toolsRepo = remember { CareerToolsRepository(networkClient.jobiestApiService) }
    val billingRepo = remember { BillingRepository(networkClient.jobiestApiService) }
    val pushTokenManager = remember {
        PushTokenManager(
            context = app.applicationContext,
            apiService = networkClient.jobiestApiService,
            sessionManager = sessionManager
        )
    }

    val authState by authRepo.authState.collectAsState()
    val isOnline by app.connectivityObserver.observe().collectAsState(initial = app.connectivityObserver.isOnline())
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedJob by remember { mutableStateOf<JobDto?>(null) }

    if (authState is AuthState.Loading) {
        LoadingView("Starting Jobiest…")
        return
    }

    val isAuthenticated = authState is AuthState.Authenticated

    // Automatically register push token upon authentication
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            pushTokenManager.registerDeviceToken()
        }
    }

    val isBottomBarVisible = isAuthenticated && currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Jobs.route,
        Screen.Applications.route,
        Screen.ResumeStudio.route,
        Screen.Profile.route
    )

    Scaffold(
        containerColor = JobiestBg,
        topBar = {
            Column {
                if (!isOnline) {
                    Surface(
                        color = JobiestWarningBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, JobiestWarning.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Offline — using cached data",
                            color = JobiestWarning,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                if (isBottomBarVisible) {
                    val title = when (currentRoute) {
                        Screen.Dashboard.route -> "Overview"
                        Screen.Jobs.route -> "Discover"
                        Screen.Applications.route -> "Applications"
                        Screen.ResumeStudio.route -> "Resume Studio"
                        Screen.Profile.route -> "Profile"
                        else -> "Jobiest"
                    }
                    JobiestTopBar(
                        title = title,
                        canNavigateBack = false,
                        onSavedJobsClick = { navController.navigate(Screen.SavedJobs.route) },
                        onBillingClick = { navController.navigate(Screen.Billing.route) }
                    )
                }
            }
        },
        bottomBar = {
            if (isBottomBarVisible) {
                JobiestBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) Screen.Dashboard.route else Screen.Login.route,
            modifier = Modifier
                .padding(innerPadding)
                .background(JobiestBg)
        ) {
            // === AUTHENTICATION & ONBOARDING ===
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinishOnboarding = {
                        navController.navigate(Screen.Signup.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    authRepository = authRepo,
                    onNavigateToSignup = { navController.navigate(Screen.Signup.route) },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToVerify = { email ->
                        navController.navigate(Screen.VerifyEmail.createRoute(email))
                    }
                )
            }

            composable(Screen.Signup.route) {
                SignupScreen(
                    authRepository = authRepo,
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onSignupSubmitted = { email ->
                        navController.navigate(Screen.VerifyEmail.createRoute(email))
                    }
                )
            }

            composable(
                route = Screen.VerifyEmail.route,
                arguments = listOf(navArgument("email") { type = NavType.StringType })
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                VerifyEmailScreen(
                    email = android.net.Uri.decode(email),
                    authRepository = authRepo,
                    onVerificationSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Signup.route) { inclusive = true }
                        }
                    },
                    onBackToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    authRepository = authRepo,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // === CORE WORKSPACE SURFACES (5 Bottom Nav Items + Agent) ===
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    profileRepository = profileRepo,
                    applicationRepository = appRepo,
                    billingRepository = billingRepo,
                    onFindOpportunitiesClick = { navController.navigate(Screen.Jobs.route) },
                    onOpenAgentClick = { navController.navigate(Screen.Agent.route) },
                    onViewApplicationsClick = { navController.navigate(Screen.Applications.route) },
                    onResumeStudioClick = { navController.navigate(Screen.ResumeStudio.route) },
                    onApplyWithAi = { comp, role ->
                        navController.navigate(Screen.ApplicationWizard.createRoute(comp, role))
                    },
                    onBillingClick = { navController.navigate(Screen.Billing.route) }
                )
            }

            composable(Screen.Jobs.route) {
                JobSearchScreen(
                    jobRepository = jobRepo,
                    onJobClick = { job ->
                        selectedJob = job
                        navController.navigate(Screen.JobDetail.createRoute(job.id))
                    },
                    onNavigateToSavedJobs = { navController.navigate(Screen.SavedJobs.route) },
                    onApplyWithAi = { job ->
                        navController.navigate(Screen.ApplicationWizard.createRoute(job.displayCompany, job.displayTitle))
                    }
                )
            }

            composable(Screen.Applications.route) {
                ApplicationsScreen(
                    applicationRepository = appRepo,
                    onStartNewApplication = { navController.navigate(Screen.StartApplication.route) }
                )
            }

            composable(Screen.ResumeStudio.route) {
                ResumeStudioScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onStartTailoring = { role ->
                        navController.navigate(Screen.ApplicationWizard.createRoute("Target Role", role))
                    }
                )
            }

            composable(Screen.Agent.route) {
                AgentScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onActionClick = { route ->
                        when (route) {
                            "resume_studio" -> navController.navigate(Screen.ResumeStudio.route)
                            "jobs" -> navController.navigate(Screen.Jobs.route)
                            "applications" -> navController.navigate(Screen.Applications.route)
                            else -> navController.navigate(route)
                        }
                    }
                )
            }

            composable(
                route = Screen.ApplicationWizard.route,
                arguments = listOf(
                    navArgument("company") { type = NavType.StringType },
                    navArgument("role") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val company = backStackEntry.arguments?.getString("company") ?: "Company"
                val role = backStackEntry.arguments?.getString("role") ?: "Position"
                ApplicationWizardScreen(
                    company = android.net.Uri.decode(company),
                    role = android.net.Uri.decode(role),
                    onNavigateBack = { navController.popBackStack() },
                    onSubmitSuccess = {
                        navController.navigate(Screen.Applications.route) {
                            popUpTo(Screen.Dashboard.route)
                        }
                    }
                )
            }

            composable(Screen.NotificationSettings.route) {
                NotificationSettingsScreen(
                    pushTokenManager = pushTokenManager,
                    apiService = networkClient.jobiestApiService,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    profileRepository = profileRepo,
                    authRepository = authRepo,
                    onNavigateToBilling = { navController.navigate(Screen.Billing.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.NotificationSettings.route) },
                    onSignedOut = {
                        scope.launch {
                            pushTokenManager.unregisterDeviceToken()
                        }
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // === SECONDARY DESTINATIONS ===
            composable(
                route = Screen.JobDetail.route,
                arguments = listOf(navArgument("jobId") { type = NavType.StringType })
            ) {
                val job = selectedJob
                if (job != null) {
                    JobDetailScreen(
                        job = job,
                        jobRepository = jobRepo,
                        applicationRepository = appRepo,
                        onNavigateBack = { navController.popBackStack() },
                        onApplicationCreated = {
                            navController.navigate(Screen.Applications.route) {
                                popUpTo(Screen.Jobs.route)
                            }
                        }
                    )
                } else {
                    navController.popBackStack()
                }
            }

            composable(Screen.SavedJobs.route) {
                SavedJobsScreen(
                    jobRepository = jobRepo,
                    onNavigateBack = { navController.popBackStack() },
                    onJobClick = { job ->
                        selectedJob = job
                        navController.navigate(Screen.JobDetail.createRoute(job.id))
                    }
                )
            }

            composable(Screen.StartApplication.route) {
                StartApplicationScreen(
                    applicationRepository = appRepo,
                    onNavigateBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate(Screen.Applications.route) {
                            popUpTo(Screen.StartApplication.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.CareerTools.route) {
                CareerToolsScreen(
                    careerToolsRepository = toolsRepo,
                    onNavigateToAtsScanner = { navController.navigate(Screen.AtsScanner.route) },
                    onToolClick = { tool ->
                        navController.navigate(Screen.ToolExecution.createRoute(tool.id, tool.title))
                    }
                )
            }

            composable(Screen.AtsScanner.route) {
                AtsScannerScreen(
                    careerToolsRepository = toolsRepo,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.ToolExecution.route,
                arguments = listOf(
                    navArgument("toolId") { type = NavType.StringType },
                    navArgument("toolTitle") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val toolId = backStackEntry.arguments?.getString("toolId") ?: ""
                val toolTitle = backStackEntry.arguments?.getString("toolTitle") ?: ""
                ToolExecutionScreen(
                    toolId = toolId,
                    toolTitle = android.net.Uri.decode(toolTitle),
                    careerToolsRepository = toolsRepo,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Billing.route) {
                BillingScreen(
                    billingRepository = billingRepo,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
