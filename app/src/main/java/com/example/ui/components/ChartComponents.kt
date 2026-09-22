package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CallsWeeklyBarChart(
  modifier: Modifier = Modifier,
  days: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
  callValues: List<Int> = listOf(142, 198, 220, 245, 210, 130, 103),
  qualifiedValues: List<Int> = listOf(48, 65, 74, 86, 72, 38, 29)
) {
  val maxVal = (callValues.maxOrNull() ?: 250).coerceAtLeast(100)

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
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
        Column {
          Text(
            text = "Calls & Qualified Leads This Week",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "1,248 total conversations handled",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          LegendItem(label = "Total Calls", color = BrandPrimary)
          LegendItem(label = "Qualified", color = BrandTertiary)
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Custom bar chart canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(160.dp)
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val width = size.width
          val height = size.height
          val numDays = days.size
          val barGroupWidth = width / numDays
          val singleBarWidth = (barGroupWidth * 0.28f).coerceAtMost(24f)

          // Background grid lines
          val gridSteps = 4
          for (i in 0..gridSteps) {
            val y = height * (i.toFloat() / gridSteps)
            drawLine(
              color = Color(0x1F94A3B8),
              start = Offset(0f, y),
              end = Offset(width, y),
              strokeWidth = 1f
            )
          }

          days.indices.forEach { index ->
            val groupCenterX = barGroupWidth * index + barGroupWidth / 2
            val totalH = (callValues[index].toFloat() / maxVal) * (height - 24f)
            val qualH = (qualifiedValues[index].toFloat() / maxVal) * (height - 24f)

            // Draw Total Calls bar
            drawRoundRect(
              color = BrandPrimary,
              topLeft = Offset(groupCenterX - singleBarWidth - 2f, height - totalH),
              size = Size(singleBarWidth, totalH),
              cornerRadius = CornerRadius(6f, 6f)
            )

            // Draw Qualified Leads bar
            drawRoundRect(
              color = BrandTertiary,
              topLeft = Offset(groupCenterX + 2f, height - qualH),
              size = Size(singleBarWidth, qualH),
              cornerRadius = CornerRadius(6f, 6f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // X-Axis labels
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        days.forEach { day ->
          Text(
            text = day,
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.Medium
            )
          )
        }
      }
    }
  }
}

@Composable
fun CallOutcomesDonutCard(
  modifier: Modifier = Modifier,
  answered: Int = 1112,
  missed: Int = 136,
  qualified: Int = 412,
  appointments: Int = 86
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Text(
        text = "Answered vs Missed & Outcomes",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
      Text(
        text = "Telephony reliability & conversion breakdown",
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Donut ring canvas
        Box(
          modifier = Modifier.size(110.dp),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val strokeWidth = 14.dp.toPx()
            val total = (answered + missed).toFloat()
            val answeredSweep = (answered / total) * 360f
            val missedSweep = (missed / total) * 360f

            drawArc(
              color = BrandPrimary,
              startAngle = -90f,
              sweepAngle = answeredSweep,
              useCenter = false,
              style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
              color = AccentAmber,
              startAngle = -90f + answeredSweep + 4f,
              sweepAngle = (missedSweep - 8f).coerceAtLeast(10f),
              useCenter = false,
              style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "89.1%",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Answered",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Breakdown stats
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutcomeStatRow(label = "Answered Calls", count = "$answered", color = BrandPrimary, pct = "89.1%")
          OutcomeStatRow(label = "Missed (Auto-SMS)", count = "$missed", color = AccentAmber, pct = "10.9%")
          OutcomeStatRow(label = "Qualified Leads", count = "$qualified", color = BrandTertiary, pct = "33.0%")
          OutcomeStatRow(label = "Site Visits Booked", count = "$appointments", color = AccentPurple, pct = "6.9%")
        }
      }
    }
  }
}

@Composable
private fun OutcomeStatRow(
  label: String,
  count: String,
  color: Color,
  pct: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(color)
      )
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp
        )
      )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      Text(
        text = count,
        style = MaterialTheme.typography.bodySmall.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
      )
      Text(
        text = "($pct)",
        style = MaterialTheme.typography.bodySmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
      )
    }
  }
}

@Composable
fun LegendItem(label: String, color: Color) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp
      )
    )
  }
}
