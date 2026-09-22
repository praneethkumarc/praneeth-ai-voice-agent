package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AnalyticsScreen(
  modifier: Modifier = Modifier
) {
  val aiEmployees by MockRepository.aiEmployees.collectAsState()
  var selectedTimeRange by remember { mutableStateOf("Last 7 Days") }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    DemoModeBanner()

    // Title & Time Range Filter
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Analytics & Performance",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Conversion funnel, call metrics & ROI reporting",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }

      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
      ) {
        Text(
          text = selectedTimeRange,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrandPrimary),
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
      }
    }

    // High Level Metric Cards (Requirement 13)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        MetricCard(
          title = "Total Calls",
          value = "1,248",
          subtitle = "Inbound 812 • Outbound 436",
          trend = "+18% vs prev",
          icon = Icons.Filled.Call,
          iconTint = BrandPrimary,
          modifier = Modifier.weight(1f)
        )
        MetricCard(
          title = "Answer Rate",
          value = "89.1%",
          subtitle = "1,112 answered calls",
          trend = "+3.4%",
          icon = Icons.Filled.PhoneInTalk,
          iconTint = StatusActive,
          modifier = Modifier.weight(1f)
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        MetricCard(
          title = "Qualified Leads",
          value = "412",
          subtitle = "33.0% qualification rate",
          trend = "+24 today",
          icon = Icons.Filled.Verified,
          iconTint = BrandTertiary,
          modifier = Modifier.weight(1f)
        )
        MetricCard(
          title = "Site Visits Booked",
          value = "86",
          subtitle = "Direct Google Cal sync",
          trend = "+14% wow",
          icon = Icons.Filled.CalendarMonth,
          iconTint = AccentPurple,
          modifier = Modifier.weight(1f)
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        MetricCard(
          title = "Avg Call Duration",
          value = "2m 45s",
          subtitle = "11,040 total voice mins",
          trend = "Optimal pace",
          icon = Icons.Filled.Timer,
          iconTint = AccentAmber,
          modifier = Modifier.weight(1f)
        )
        MetricCard(
          title = "Cost / Qual Lead",
          value = "₹18.50",
          subtitle = "vs ₹350 human agency",
          trend = "-94.7% cost",
          icon = Icons.Filled.Savings,
          iconTint = StatusActive,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Visual Charts
    CallsWeeklyBarChart()
    CallOutcomesDonutCard()

    // AI Employee Performance Comparison Table (Requirement 13)
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "AI Employee Performance Comparison",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Autonomous voice agent productivity & conversion breakdown",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

        aiEmployees.forEach { emp ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(BrandPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Filled.SmartToy, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
              }
              Column {
                Text(emp.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text("${emp.industry} • ${emp.language}", style = MaterialTheme.typography.labelSmall, color = Slate500)
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              Text("${emp.callsHandled} calls", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
              Text(
                "${emp.qualifiedLeads} leads (${if (emp.callsHandled > 0) (emp.qualifiedLeads * 100 / emp.callsHandled) else 0}%)",
                style = MaterialTheme.typography.labelSmall.copy(color = StatusQualified, fontWeight = FontWeight.SemiBold)
              )
            }
          }
          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }
      }
    }

    OwnerBrandingFooter()
  }
}
