package io.github.jd1378.otphelper.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/** Returns the correct icon based on the current layout direction. */
@Composable
fun mirroringIcon(ltrIcon: ImageVector, rtlIcon: ImageVector): ImageVector =
    if (LocalLayoutDirection.current == LayoutDirection.Ltr) ltrIcon else rtlIcon

/** Returns the correct back navigation icon based on the current layout direction. */
@Composable
fun mirroringBackIcon() =
    mirroringIcon(ltrIcon = Icons.Outlined.ArrowBack, rtlIcon = Icons.Outlined.ArrowForward)
