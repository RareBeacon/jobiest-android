package com.jobiest.android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobiest.android.ui.theme.*

/**
 * Authentic Jobiest Mark: "A career in ascent: a rising line that peaks at a dot (the goal)."
 * Matches components/site/Logo.tsx geometry: viewBox 0 0 24 24
 * Path: M4 16 L9 9 L13 13 L20 5, Circle: cx 20, cy 5, r 2.1
 */
@Composable
fun JobiestMark(
    modifier: Modifier = Modifier,
    tint: Color = JobiestInk,
    strokeWidthFactor: Float = 2.4f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val scaleX = w / 24f
        val scaleY = h / 24f

        val path = Path().apply {
            moveTo(4f * scaleX, 16f * scaleY)
            lineTo(9f * scaleX, 9f * scaleY)
            lineTo(13f * scaleX, 13f * scaleY)
            lineTo(20f * scaleX, 5f * scaleY)
        }

        drawPath(
            path = path,
            color = tint,
            style = Stroke(
                width = strokeWidthFactor * scaleX,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        drawCircle(
            color = tint,
            radius = 2.1f * scaleX,
            center = Offset(20f * scaleX, 5f * scaleY)
        )
    }
}

/**
 * Official Jobiest App Icon Badge: Rounded Navy container (#062B68) with electric gold mark (#FFD21A).
 * Matches public/favicon.svg and public/icons/icon-1024.png.
 */
@Composable
fun JobiestAppBadge(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(JobiestLogoNavy, RoundedCornerShape(size * 0.22f)),
        contentAlignment = Alignment.Center
    ) {
        JobiestMark(
            modifier = Modifier.size(size * 0.58f),
            tint = JobiestLogoGold,
            strokeWidthFactor = 2.6f
        )
    }
}

/**
 * Official Jobiest Wordmark Header: Rising mark + "Jobiest" brand text.
 */
@Composable
fun JobiestBrandHeader(
    modifier: Modifier = Modifier,
    markSize: Dp = 24.dp,
    fontSizeSp: Int = 24
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JobiestMark(
            modifier = Modifier.size(markSize),
            tint = JobiestInk,
            strokeWidthFactor = 2.6f
        )
        Text(
            text = "Jobiest",
            color = JobiestInk,
            fontSize = fontSizeSp.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
        )
    }
}

/**
 * Alias for JobiestAppBadge.
 */
@Composable
fun JobiestBrandAppBadge(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) = JobiestAppBadge(modifier = modifier, size = size)

/**
 * Official Jobiest Screen Top Header with title, optional subtitle, back navigation, and action buttons.
 */
@Composable
fun JobiestBrandHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    androidx.compose.material3.Surface(
        modifier = modifier.fillMaxWidth(),
        color = JobiestBg,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                androidx.compose.material3.IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = JobiestInk
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            JobiestAppBadge(size = 32.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = JobiestInk,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        color = JobiestMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                actions()
            }
        }
    }
}
