package com.jobiest.android.ui.theme

import androidx.compose.ui.graphics.Color

// Authentic Jobiest Design Tokens (from web globals.css & ModernJob Design Brief)
// Warm ivory/white canvas + deep petrol ink (never black backgrounds)
val JobiestBg = Color(0xFFFFFFFF)
val JobiestBgSecondary = Color(0xFFF5F6F9)
val JobiestCard = Color(0xFFFFFFFF)
val JobiestBorder = Color(0xFFE2E6ED)
val JobiestBorderSubtle = Color(0xFFECEDF1)

// Typography & Ink
val JobiestInk = Color(0xFF17223B) // Deep petrol ink (primary text)
val JobiestInkSecondary = Color(0xFF243247) // Headings & strong text
val JobiestMuted = Color(0xFF616D81) // Secondary / helper text
val JobiestMuted2 = Color(0xFF727D90) // Caption & footer text

// Brand Accents
val JobiestBrand = Color(0xFFF8D64D) // Primary warm gold
val JobiestBrandStrong = Color(0xFFE3C53A) // Focus gold
val JobiestBrandSoft = Color(0xFFFFF3B9) // Highlight gold tint
val JobiestCobalt = Color(0xFF3B72BC) // Interactive cobalt blue

// Official Brand Logo Mark Colors (favicon.svg / Logo.tsx)
val JobiestLogoNavy = Color(0xFF062B68) // Container navy
val JobiestLogoGold = Color(0xFFFFD21A) // Trajectory line & dot

// Semantics
val JobiestSuccess = Color(0xFF16A34A)
val JobiestSuccessBg = Color(0xFFF0FDF4)
val JobiestSuccessBorder = Color(0xFFBBF7D0)

val JobiestWarning = Color(0xFFD97706)
val JobiestWarningBg = Color(0xFFFFFBEB)

val JobiestDanger = Color(0xFFDC2626)
val JobiestDangerBg = Color(0xFFFEF2F2)
val JobiestDangerBorder = Color(0xFFFECACA)

// Aliases for compatibility
val TextPrimary = JobiestInk
val TextSecondary = JobiestMuted
val TextMuted = JobiestMuted2
val JobiestNavy = JobiestLogoNavy
val JobiestNavyDark = JobiestLogoNavy
val JobiestGold = JobiestBrand
val JobiestBlue = JobiestCobalt
val JobiestCardBg = JobiestCard
val JobiestRed = JobiestDanger
val JobiestGreen = JobiestSuccess
