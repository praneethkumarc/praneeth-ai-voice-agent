package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.Campaign
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.OwnerBrandingFooter
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun CampaignsScreen(
  modifier: Modifier = Modifier
) {
  val campaigns by MockRepository.campaigns.collectAsState()
  val aiEmployees by MockRepository.aiEmployees.collectAsState()
  var showCreateDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    DemoModeBanner()

    // Title & Create button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Outbound AI Campaigns",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Automated voice outreach & cold lead qualification",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }

      Button(
        onClick = { showCreateDialog = true },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
        modifier = Modifier.testTag("create_campaign_button")
      ) {
        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("New Campaign", fontWeight = FontWeight.Bold)
      }
    }

    // Campaign List
    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      items(campaigns, key = { it.id }) { campaign ->
        CampaignCard(
          campaign = campaign,
          onToggleStatus = { MockRepository.toggleCampaignStatus(campaign.id) },
          onDelete = { MockRepository.deleteCampaign(campaign.id) }
        )
      }
    }

    OwnerBrandingFooter()
  }

  // Create Campaign Dialog
  if (showCreateDialog) {
    var name by remember { mutableStateOf("") }
    var selectedEmployee by remember { mutableStateOf(aiEmployees.firstOrNull() ?: aiEmployees.first()) }
    var objective by remember { mutableStateOf("Qualify interested prospects for site visit") }
    var script by remember { mutableStateOf("Hi! Calling from Praneeth Properties with an invitation...") }
    var totalContactsStr by remember { mutableStateOf("500") }
    var callingHours by remember { mutableStateOf("10:00 AM - 06:30 PM IST") }

    AlertDialog(
      onDismissRequest = { showCreateDialog = false },
      title = { Text("Create Outbound Voice Campaign", fontWeight = FontWeight.Bold) },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Campaign Name *") },
            placeholder = { Text("e.g. Q3 Villa Outreach") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("campaign_name_input")
          )

          OutlinedTextField(
            value = objective,
            onValueChange = { objective = it },
            label = { Text("Campaign Objective") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = script,
            onValueChange = { script = it },
            label = { Text("AI Opening Pitch & Script") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = totalContactsStr,
            onValueChange = { totalContactsStr = it },
            label = { Text("Total Number of Contacts") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = callingHours,
            onValueChange = { callingHours = it },
            label = { Text("Calling Hours Window") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (name.isNotBlank()) {
              val newCamp = Campaign(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                aiEmployeeId = selectedEmployee.id,
                aiEmployeeName = selectedEmployee.name,
                objective = objective.trim(),
                callScript = script.trim(),
                callingHours = callingHours.trim(),
                retrySettings = "Max 2 retries (4 hours gap)",
                maxConcurrentCalls = 8,
                totalContacts = totalContactsStr.toIntOrNull() ?: 500,
                callsStarted = 0,
                connected = 0,
                noAnswer = 0,
                qualified = 0,
                appointments = 0,
                isRunning = true
              )
              MockRepository.addCampaign(newCamp)
              showCreateDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
          modifier = Modifier.testTag("submit_campaign_button")
        ) {
          Text("Launch Campaign")
        }
      },
      dismissButton = {
        TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
      }
    )
  }
}

@Composable
fun CampaignCard(
  campaign: Campaign,
  onToggleStatus: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val progress = if (campaign.totalContacts > 0) {
    (campaign.callsStarted.toFloat() / campaign.totalContacts.toFloat()).coerceIn(0f, 1f)
  } else 0f

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("campaign_card_${campaign.id}"),
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
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = campaign.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "AI: ${campaign.aiEmployeeName} • ${campaign.callingHours}",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }

        StatusBadge(
          text = if (campaign.isRunning) "Running" else "Paused",
          color = if (campaign.isRunning) StatusActive else AccentAmber,
          backgroundColor = if (campaign.isRunning) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
        )
      }

      // Progress bar
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Progress: ${campaign.callsStarted} / ${campaign.totalContacts} started (${(progress * 100).toInt()}%)",
            style = MaterialTheme.typography.labelSmall.copy(color = Slate600)
          )
          Text(
            text = "${campaign.connected} Connected",
            style = MaterialTheme.typography.labelSmall.copy(color = BrandPrimary, fontWeight = FontWeight.Bold)
          )
        }
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
          color = BrandPrimary,
          trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
      }

      // Metrics strip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        CampaignStat(label = "Qualified", value = "${campaign.qualified}")
        CampaignStat(label = "Appointments", value = "${campaign.appointments}")
        CampaignStat(label = "No Answer", value = "${campaign.noAnswer}")
      }

      // Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = campaign.retrySettings,
          style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(
            onClick = onToggleStatus,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = if (campaign.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
              contentDescription = null,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (campaign.isRunning) "Pause" else "Resume", fontSize = 11.sp)
          }

          IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AccentRose, modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun CampaignStat(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
    Text(label, style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 10.sp))
  }
}
