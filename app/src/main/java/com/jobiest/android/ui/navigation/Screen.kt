package com.jobiest.android.ui.navigation

sealed class Screen(val route: String) {
    // Auth routes
    data object Login : Screen("login")
    data object Signup : Screen("signup")
    data object VerifyEmail : Screen("verify_email/{email}") {
        fun createRoute(email: String) = "verify_email/${android.net.Uri.encode(email)}"
    }
    data object ForgotPassword : Screen("forgot_password")

    // Main bottom navigation routes (Matching Jobiest Web AppShell)
    data object Dashboard : Screen("dashboard") // Today
    data object Applications : Screen("applications") // Applications
    data object CareerTools : Screen("career_tools") // Create / Tools
    data object Jobs : Screen("jobs") // Job discovery
    data object Profile : Screen("profile") // Career profile

    // Sub-screens
    data object Onboarding : Screen("onboarding")
    data object ResumeStudio : Screen("resume_studio")
    data object Agent : Screen("agent")
    data object NotificationSettings : Screen("notification_settings")
    data object SavedJobs : Screen("saved_jobs")
    data object StartApplication : Screen("start_application")
    data object AtsScanner : Screen("ats_scanner")
    data object Billing : Screen("billing")

    // Dynamic routes
    data object JobDetail : Screen("job_detail/{jobId}") {
        fun createRoute(jobId: String) = "job_detail/$jobId"
    }

    data object ApplicationWizard : Screen("application_wizard/{company}/{role}") {
        fun createRoute(company: String, role: String) =
            "application_wizard/${android.net.Uri.encode(company)}/${android.net.Uri.encode(role)}"
    }

    data object ToolExecution : Screen("tool_execution/{toolId}/{toolTitle}") {
        fun createRoute(toolId: String, toolTitle: String) = "tool_execution/$toolId/${android.net.Uri.encode(toolTitle)}"
    }
}

