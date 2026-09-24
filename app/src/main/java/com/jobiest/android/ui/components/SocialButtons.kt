package com.jobiest.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobiest.android.ui.theme.*

/**
 * "Continue with Google" matching web components/site/GoogleButton.tsx.
 * Styled with light background, border #E2E6ED, deep petrol ink text #17223B.
 */
@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String = "Continue with Google"
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = JobiestBg,
            contentColor = JobiestInk
        ),
        border = BorderStroke(1.dp, JobiestBorder)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            GoogleIcon(modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = JobiestInk
            )
        }
    }
}

/**
 * "Continue with LinkedIn" matching web components/site/LinkedInButton.tsx.
 */
@Composable
fun LinkedInSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String = "Continue with LinkedIn"
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = JobiestBg,
            contentColor = JobiestInk
        ),
        border = BorderStroke(1.dp, JobiestBorder)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            LinkedInIcon(modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = JobiestInk
            )
        }
    }
}

@Composable
fun GoogleIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val scale = w / 18f

        // Draw Google 'G' colors (Blue, Green, Yellow, Red)
        drawCircle(color = Color(0xFF4285F4), radius = 4f * scale, center = Offset(w - 4f * scale, h / 2f))
        drawCircle(color = Color(0xFF34A853), radius = 4f * scale, center = Offset(w / 2f, h - 3f * scale))
        drawCircle(color = Color(0xFFFBBC05), radius = 4f * scale, center = Offset(3f * scale, h / 2f))
        drawCircle(color = Color(0xFFEA4335), radius = 4f * scale, center = Offset(w / 2f, 3f * scale))
    }
}

@Composable
fun LinkedInIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val scale = w / 24f

        // LinkedIn brand blue background with 'in' shape
        drawRoundRect(
            color = Color(0xFF0A66C2),
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * scale, 4f * scale)
        )
        // Draw standard White 'in' text representation
        drawCircle(color = Color.White, radius = 1.8f * scale, center = Offset(7f * scale, 7f * scale))
        val barPath = Path().apply {
            moveTo(5.5f * scale, 10f * scale)
            lineTo(8.5f * scale, 10f * scale)
            lineTo(8.5f * scale, 19f * scale)
            lineTo(5.5f * scale, 19f * scale)
            close()
        }
        drawPath(barPath, color = Color.White)

        val nPath = Path().apply {
            moveTo(11.5f * scale, 10f * scale)
            lineTo(14.5f * scale, 10f * scale)
            lineTo(14.5f * scale, 12f * scale)
            lineTo(18.5f * scale, 12f * scale)
            lineTo(18.5f * scale, 19f * scale)
            lineTo(15.5f * scale, 19f * scale)
            lineTo(15.5f * scale, 14.5f * scale)
            lineTo(14.5f * scale, 14.5f * scale)
            lineTo(14.5f * scale, 19f * scale)
            lineTo(11.5f * scale, 19f * scale)
            close()
        }
        drawPath(nPath, color = Color.White)
    }
}
