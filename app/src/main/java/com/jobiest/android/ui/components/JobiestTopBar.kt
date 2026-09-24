package com.jobiest.android.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobiest.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobiestTopBar(
    title: String,
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onSavedJobsClick: (() -> Unit)? = null,
    onBillingClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            if (title == "Today" || title == "Dashboard") {
                JobiestBrandHeader(markSize = 22.dp, fontSizeSp = 20)
            } else {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        letterSpacing = (-0.3).sp
                    ),
                    color = JobiestInk
                )
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = JobiestInk
                    )
                }
            }
        },
        actions = {
            if (onSavedJobsClick != null) {
                IconButton(onClick = onSavedJobsClick) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Saved Jobs",
                        tint = JobiestInk
                    )
                }
            }
            if (onBillingClick != null) {
                IconButton(onClick = onBillingClick) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = "Billing",
                        tint = JobiestInk
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = JobiestBg,
            scrolledContainerColor = JobiestBg,
            navigationIconContentColor = JobiestInk,
            titleContentColor = JobiestInk,
            actionIconContentColor = JobiestInk
        )
    )
}
