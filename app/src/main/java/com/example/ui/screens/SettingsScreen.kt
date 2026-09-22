package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockRepository
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.OwnerBrandingFooter
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val userProfile by MockRepository.userProfile.collectAsState()
  val isDemoMode by MockRepository.isDemoMode.collectAsState()

  var selectedTab by remember { mutableStateOf("General") }
  val tabs = listOf("General", "Telephony", "AI & Voice", "Webhooks & API", "Notifications", "Team", "Billing")

  // Form states
  var businessName by remember { mutableStateOf(userProfile.businessName) }
  var ownerName by remember { mutableStateOf(userProfile.name) }
  var phone by remember { mutableStateOf(userProfile.phone) }
  var timezone by remember { mutableStateOf("Asia/Kolkata (IST)") }
  var emailAlerts by remember { mutableStateOf(true) }
  var smsAlerts by remember { mutableStateOf(true) }
  var whatsappSummary by remember { mutableStateOf(true) }
  var statusMessage by remember { mutableStateOf<String?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    DemoModeBanner()

    // Title
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Settings & Configuration",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Platform credentials, owner profile & enterprise settings",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }
    }

    // Tabs
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      tabs.forEach { tab ->
        FilterChip(
          selected = selectedTab == tab,
          onClick = { selectedTab = tab },
          label = { Text(tab, fontSize = 12.sp) }
        )
      }
    }

    if (statusMessage != null) {
      Surface(
        color = Color(0xFFDCFCE7),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(statusMessage ?: "", color = Color(0xFF166534), style = MaterialTheme.typography.bodySmall)
          IconButton(onClick = { statusMessage = null }, modifier = Modifier.size(20.dp)) {
            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(14.dp))
          }
        }
      }
    }

    // Scrollable Content
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      when (selectedTab) {
        "General" -> {
          SettingsSectionCard(title = "Company & Owner Profile") {
            OutlinedTextField(
              value = businessName,
              onValueChange = { businessName = it },
              label = { Text("Application / Business Name") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = ownerName,
              onValueChange = { ownerName = it },
              label = { Text("Owner Full Name") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = phone,
              onValueChange = { phone = it },
              label = { Text("Owner Phone Number") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = timezone,
              onValueChange = { timezone = it },
              label = { Text("Primary Timezone") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            Button(
              onClick = {
                MockRepository.updateProfile(
                  name = ownerName,
                  email = userProfile.email,
                  phone = phone,
                  businessName = businessName
                )
                statusMessage = "Settings saved successfully!"
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
              Text("Save General Settings")
            }
          }

          SettingsSectionCard(title = "Environment & Demo Mode") {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("Operational Mode", fontWeight = FontWeight.SemiBold)
                Text(
                  if (isDemoMode) "Currently in DEMO MODE with simulated telephony" else "LIVE PRODUCTION MODE enabled",
                  style = MaterialTheme.typography.bodySmall,
                  color = Slate600
                )
              }
              Switch(
                checked = !isDemoMode,
                onCheckedChange = { isLive ->
                  MockRepository.setDemoMode(!isLive)
                  statusMessage = if (isLive) "Switched to LIVE PRODUCTION MODE" else "Switched to DEMO SIMULATION MODE"
                }
              )
            }
          }

          // Sign Out Action
          OutlinedButton(
            onClick = onLogout,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRose),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Sign Out of Account")
          }
        }

        "Telephony" -> {
          SettingsSectionCard(title = "Telephony Providers & Webhooks") {
            Text(
              "Configure Twilio, Exotel, or Plivo credentials to enable real inbound/outbound calls.",
              style = MaterialTheme.typography.bodySmall,
              color = Slate600
            )

            OutlinedTextField(
              value = "AC7f8931294821a89c927f8a19283741",
              onValueChange = {},
              label = { Text("Twilio Account SID") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = "••••••••••••••••••••••••••••••••",
              onValueChange = {},
              label = { Text("Twilio Auth Token") },
              visualTransformation = PasswordVisualTransformation(),
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = "https://api.praneethai.com/v1/telephony/inbound",
              onValueChange = {},
              label = { Text("Inbound Voice Webhook URL") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            Button(
              onClick = { statusMessage = "Telephony configuration verified & saved!" },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
              Text("Save Telephony Settings")
            }
          }
        }

        "AI & Voice" -> {
          SettingsSectionCard(title = "Gemini API & Spoken Voice Engine") {
            Text(
              "Gemini powers contextual reasoning and qualification logic. Voice synthesis is delivered via high-fidelity neural TTS.",
              style = MaterialTheme.typography.bodySmall,
              color = Slate600
            )

            OutlinedTextField(
              value = "gemini-3.5-flash",
              onValueChange = {},
              label = { Text("Active AI Model") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = "Configured via AI Studio Secrets Panel (.env)",
              onValueChange = {},
              label = { Text("Gemini API Key Status") },
              readOnly = true,
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = "250 ms (Ultra-low latency streaming)",
              onValueChange = {},
              label = { Text("Voice Turnaround Latency Target") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }

        "Webhooks & API" -> {
          SettingsSectionCard(title = "REST API Keys & Webhook Endpoints") {
            Text(
              "Use these credentials to trigger calls programmatically from your CRM or backend.",
              style = MaterialTheme.typography.bodySmall,
              color = Slate600
            )

            OutlinedTextField(
              value = "pk_live_8951858777_enterprise_token",
              onValueChange = {},
              label = { Text("Live API Key") },
              readOnly = true,
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = "https://api.praneethai.com/webhooks/call-completed",
              onValueChange = {},
              label = { Text("Post-Call Webhook Destination") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            Button(
              onClick = { statusMessage = "Test webhook payload dispatched successfully (200 OK)!" },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary)
            ) {
              Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Test Webhook")
            }
          }
        }

        "Notifications" -> {
          SettingsSectionCard(title = "Alerts & Escalations") {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("Email Alerts for Qualified Leads", fontWeight = FontWeight.SemiBold)
                Text("Sent immediately upon call qualification", style = MaterialTheme.typography.bodySmall, color = Slate500)
              }
              Switch(checked = emailAlerts, onCheckedChange = { emailAlerts = it })
            }

            HorizontalDivider()

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("SMS / WhatsApp for Site Visits", fontWeight = FontWeight.SemiBold)
                Text("Notify owner (+91 89518 58777) on new appointments", style = MaterialTheme.typography.bodySmall, color = Slate500)
              }
              Switch(checked = smsAlerts, onCheckedChange = { smsAlerts = it })
            }

            HorizontalDivider()

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("Daily Evening WhatsApp Digest", fontWeight = FontWeight.SemiBold)
                Text("Summary of total calls, talk time & hot leads", style = MaterialTheme.typography.bodySmall, color = Slate500)
              }
              Switch(checked = whatsappSummary, onCheckedChange = { whatsappSummary = it })
            }
          }
        }

        "Billing" -> {
          SettingsSectionCard(title = "Enterprise Pro Subscription") {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("Enterprise Pro Plan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("Unlimited AI Employees • 500 Calling Hours / mo", style = MaterialTheme.typography.bodySmall, color = Slate600)
              }
              Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                Text("Active", color = Color(0xFF166534), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
              }
            }

            LinearProgressIndicator(
              progress = { 184f / 500f },
              modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
              color = BrandPrimary
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("184 hours used", style = MaterialTheme.typography.labelSmall)
              Text("316 hours remaining", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
      }
    }

    OwnerBrandingFooter()
  }
}

@Composable
private fun SettingsSectionCard(
  title: String,
  content: @Composable ColumnScope.() -> Unit
) {
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
      Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
      content()
    }
  }
}
