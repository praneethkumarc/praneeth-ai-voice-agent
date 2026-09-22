package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockRepository
import com.example.model.CallLog
import com.example.model.CallOutcome
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
  onNavigateToDestination: (AppDestination) -> Unit,
  onOpenCreateAiWizard: () -> Unit,
  onOpenTestAiModal: () -> Unit,
  onOpenCallDetails: (CallLog) -> Unit,
  modifier: Modifier = Modifier
) {
  val userProfile by MockRepository.userProfile.collectAsState()
  val aiEmployees by MockRepository.aiEmployees.collectAsState()
  val calls by MockRepository.calls.collectAsState()
  val leads by MockRepository.leads.collectAsState()
  val activeAiCount = aiEmployees.count { it.isActive }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    // Mode indicator banner
    DemoModeBanner(onOpenSettings = { onNavigateToDestination(AppDestination.SETTINGS) })

    // Greeting Header
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Good morning, Praneeth 👋",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Your AI employees are handling your customer conversations.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }

        // Live Telephony Pulsing Status
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFDCFCE7))
            .padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(StatusActive)
          )
          Text(
            text = "Voice Core Active",
            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF166534), fontWeight = FontWeight.Bold)
          )
        }
      }
    }

    // Quick Actions (Requirement 21)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "QUICK ACTIONS",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          letterSpacing = 1.sp
        )
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = onOpenCreateAiWizard,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
          modifier = Modifier.testTag("quick_action_create_ai")
        ) {
          Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Create AI Employee", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = { onNavigateToDestination(AppDestination.CONTACTS) },
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
          modifier = Modifier.testTag("quick_action_add_contact")
        ) {
          Icon(Icons.Outlined.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp), tint = BrandPrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Add Contact", fontWeight = FontWeight.SemiBold)
        }

        OutlinedButton(
          onClick = { onNavigateToDestination(AppDestination.CAMPAIGNS) },
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
          modifier = Modifier.testTag("quick_action_start_campaign")
        ) {
          Icon(Icons.Outlined.Campaign, contentDescription = null, modifier = Modifier.size(18.dp), tint = BrandSecondary)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Start Campaign", fontWeight = FontWeight.SemiBold)
        }

        OutlinedButton(
          onClick = { onNavigateToDestination(AppDestination.PHONE_NUMBERS) },
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
          modifier = Modifier.testTag("quick_action_connect_phone")
        ) {
          Icon(Icons.Outlined.PhoneInTalk, contentDescription = null, modifier = Modifier.size(18.dp), tint = AccentPurple)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Connect Phone", fontWeight = FontWeight.SemiBold)
        }

        Button(
          onClick = onOpenTestAiModal,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
          modifier = Modifier.testTag("quick_action_test_ai")
        ) {
          Icon(Icons.Filled.Hearing, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF38BDF8))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Test AI Voice", fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }

    // Metric Cards Grid (Requirement 2)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        MetricCard(
          title = "AI Employees",
          value = "$activeAiCount",
          subtitle = "Active in production",
          trend = "+1 this week",
          icon = Icons.Filled.SmartToy,
          iconTint = BrandPrimary,
          modifier = Modifier.weight(1f),
          onClick = { onNavigateToDestination(AppDestination.AI_EMPLOYEES) }
        )
        MetricCard(
          title = "Total Calls",
          value = "1,248",
          subtitle = "Inbound & Outbound",
          trend = "+18% vs last week",
          icon = Icons.Filled.Call,
          iconTint = BrandSecondary,
          modifier = Modifier.weight(1f),
          onClick = { onNavigateToDestination(AppDestination.CALLS) }
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        MetricCard(
          title = "Qualified Leads",
          value = "412",
          subtitle = "33% qualification rate",
          trend = "+24 today",
          icon = Icons.Filled.Verified,
          iconTint = BrandTertiary,
          modifier = Modifier.weight(1f),
          onClick = { onNavigateToDestination(AppDestination.LEADS) }
        )
        MetricCard(
          title = "Talk Time",
          value = "184 hrs",
          subtitle = "Avg 2m 45s / call",
          trend = "99.8% uptime",
          icon = Icons.Filled.AccessTime,
          iconTint = AccentAmber,
          modifier = Modifier.weight(1f)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        MetricCard(
          title = "Appointments",
          value = "86",
          subtitle = "Site visits & meetings",
          trend = "+12 scheduled",
          icon = Icons.Filled.CalendarMonth,
          iconTint = AccentPurple,
          modifier = Modifier.weight(1f)
        )
        MetricCard(
          title = "Conversion Rate",
          value = "32%",
          subtitle = "Target: > 28%",
          trend = "+4.2%",
          icon = Icons.Filled.TrendingUp,
          iconTint = BrandTertiary,
          modifier = Modifier.weight(1f),
          onClick = { onNavigateToDestination(AppDestination.ANALYTICS) }
        )
      }
    }

    // Live Activity Section (Requirement 2)
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
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(AccentRose)
            )
            Text(
              text = "Live Activity & Recent Calls",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }

          TextButton(
            onClick = { onNavigateToDestination(AppDestination.CALLS) },
            contentPadding = PaddingValues(0.dp)
          ) {
            Text("View All Calls", color = BrandPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }

        // Live Active Monitoring Call Item
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToDestination(AppDestination.LIVE_MONITOR) }
            .testTag("live_call_banner"),
          color = Color(0xFFEFF6FF),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
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
                  .background(BrandPrimary),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Filled.GraphicEq, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
              }
              Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Text(
                    text = "Praneeth Real Estate AI",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                  )
                  StatusBadge(text = "LIVE NOW", color = AccentRose, backgroundColor = Color(0xFFFFE4E6))
                }
                Text(
                  text = "Customer: +91 98765 XXXXX • Gachibowli buyer inquiry",
                  style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "00:45",
                fontWeight = FontWeight.Bold,
                color = BrandPrimary,
                style = MaterialTheme.typography.titleMedium
              )
              Text("Connected", fontSize = 10.sp, color = StatusActive, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Recent Completed Calls List
        calls.take(4).forEach { call ->
          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onOpenCallDetails(call) }
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (call.outcome == CallOutcome.QUALIFIED) Icons.Filled.CheckCircle else Icons.Filled.Call,
                  contentDescription = null,
                  tint = if (call.outcome == CallOutcome.QUALIFIED) StatusQualified else Slate500,
                  modifier = Modifier.size(18.dp)
                )
              }

              Column {
                Text(
                  text = "${call.aiEmployeeName} ➔ ${call.customerName}",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.bodyMedium,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "${call.customerPhone} • ${call.dateTime}",
                  style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              StatusBadge(
                text = when (call.outcome) {
                  CallOutcome.QUALIFIED -> "Qualified"
                  CallOutcome.APPOINTMENT_BOOKED -> "Site Visit"
                  CallOutcome.MISSED -> "Missed"
                  else -> "Not Qualified"
                },
                color = when (call.outcome) {
                  CallOutcome.QUALIFIED -> StatusQualified
                  CallOutcome.APPOINTMENT_BOOKED -> AccentPurple
                  CallOutcome.MISSED -> AccentAmber
                  else -> AccentRose
                }
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${call.durationSeconds / 60}m ${call.durationSeconds % 60}s",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }
          }
        }
      }
    }

    // Attractive Analytics Charts
    CallsWeeklyBarChart()
    CallOutcomesDonutCard()

    // Owner branding footer
    OwnerBrandingFooter()
  }
}
