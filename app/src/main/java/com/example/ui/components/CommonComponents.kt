package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockRepository
import com.example.ui.theme.*

@Composable
fun DemoModeBanner(
  modifier: Modifier = Modifier,
  onOpenSettings: () -> Unit = {}
) {
  val isDemoMode by MockRepository.isDemoMode.collectAsState()

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = if (isDemoMode) Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isDemoMode) Color(0xFFFDE68A) else Color(0xFFBBF7D0)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(if (isDemoMode) AccentAmber else StatusActive)
        )
        Text(
          text = if (isDemoMode) "DEMO MODE ACTIVE" else "LIVE PRODUCTION MODE",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = if (isDemoMode) Color(0xFF92400E) else Color(0xFF166534),
            letterSpacing = 0.5.sp
          )
        )
        Text(
          text = if (isDemoMode) "• Realistic telephony simulator active" else "• Connected to real telecom credentials",
          style = MaterialTheme.typography.labelSmall.copy(
            color = if (isDemoMode) Color(0xFFB45309) else Color(0xFF15803D)
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        AssistChip(
          onClick = {
            MockRepository.setDemoMode(!isDemoMode)
          },
          label = {
            Text(
              if (isDemoMode) "Switch to Live" else "Switch to Demo",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          },
          colors = AssistChipDefaults.assistChipColors(
            containerColor = Color.White.copy(alpha = 0.8f),
            labelColor = if (isDemoMode) Color(0xFF92400E) else Color(0xFF166534)
          ),
          border = null,
          modifier = Modifier.testTag("toggle_demo_mode_button")
        )
      }
    }
  }
}

@Composable
fun MetricCard(
  title: String,
  value: String,
  subtitle: String? = null,
  trend: String? = null,
  isPositive: Boolean = true,
  icon: ImageVector,
  iconTint: Color = BrandPrimary,
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null
) {
  Card(
    modifier = modifier
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
      .testTag("metric_card_${title.lowercase().replace(" ", "_")}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
          )
        )
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(iconTint.copy(alpha = 0.12f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
      )

      if (trend != null || subtitle != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          if (trend != null) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isPositive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = trend,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isPositive) Color(0xFF166534) else Color(0xFF991B1B)
                )
              )
            }
          }
          if (subtitle != null) {
            Text(
              text = subtitle,
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }
  }
}

@Composable
fun StatusBadge(
  text: String,
  color: Color,
  backgroundColor: Color = color.copy(alpha = 0.12f),
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(backgroundColor)
      .padding(horizontal = 8.dp, vertical = 3.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall.copy(
        color = color,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp
      )
    )
  }
}

@Composable
fun OwnerBrandingFooter(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Text(
      text = "Powered by Praneeth Kumar • Owner Phone: 8951858777",
      style = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        fontWeight = FontWeight.Medium
      )
    )
    Text(
      text = "Praneeth AI Employee OS • Enterprise Voice Telephony & AI Automation",
      style = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        fontSize = 10.sp
      )
    )
  }
}
