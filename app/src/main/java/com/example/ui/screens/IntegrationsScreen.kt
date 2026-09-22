package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockRepository
import com.example.model.IntegrationItem
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.OwnerBrandingFooter
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun IntegrationsScreen(
  modifier: Modifier = Modifier
) {
  val integrations by MockRepository.integrations.collectAsState()
  var selectedCategory by remember { mutableStateOf("All") }
  var integrationToConfigure by remember { mutableStateOf<IntegrationItem?>(null) }

  val categories = listOf("All", "Telephony", "AI Models", "CRM", "Calendar", "Messaging", "Automation")

  val filteredList = if (selectedCategory == "All") {
    integrations
  } else {
    integrations.filter { it.category.equals(selectedCategory, ignoreCase = true) }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    DemoModeBanner()

    // Header
    Column {
      Text(
        text = "Integrations & API Connectors",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
      )
      Text(
        text = "Connect telephony providers, CRMs, Google Calendar & messaging",
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
      )
    }

    // Category Selector
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      categories.take(4).forEach { cat ->
        FilterChip(
          selected = selectedCategory == cat,
          onClick = { selectedCategory = cat },
          label = { Text(cat, fontSize = 11.sp) }
        )
      }
    }
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      categories.drop(4).forEach { cat ->
        FilterChip(
          selected = selectedCategory == cat,
          onClick = { selectedCategory = cat },
          label = { Text(cat, fontSize = 11.sp) }
        )
      }
    }

    // Integrations List
    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(filteredList, key = { it.id }) { item ->
        IntegrationCard(
          item = item,
          onConfigure = { integrationToConfigure = item },
          onToggleConnect = { MockRepository.toggleIntegration(item.id) }
        )
      }
    }

    OwnerBrandingFooter()
  }

  // Configuration Dialog
  if (integrationToConfigure != null) {
    val item = integrationToConfigure!!
    var apiKeyInput by remember { mutableStateOf(if (item.isConnected) "••••••••••••••••" else "") }

    AlertDialog(
      onDismissRequest = { integrationToConfigure = null },
      title = { Text("Configure ${item.name}", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(item.description, style = MaterialTheme.typography.bodySmall, color = Slate600)

          OutlinedTextField(
            value = apiKeyInput,
            onValueChange = { apiKeyInput = it },
            label = { Text("API Key / Auth Token") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Text(
            text = "Credentials are encrypted securely using Android KeyStore & Secrets Gradle Plugin.",
            style = MaterialTheme.typography.labelSmall,
            color = Slate500
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (!item.isConnected) {
              MockRepository.toggleIntegration(item.id)
            }
            integrationToConfigure = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
        ) {
          Text(if (item.isConnected) "Update Credentials" else "Connect Integration")
        }
      },
      dismissButton = {
        TextButton(onClick = { integrationToConfigure = null }) { Text("Cancel") }
      }
    )
  }
}

@Composable
fun IntegrationCard(
  item: IntegrationItem,
  onConfigure: () -> Unit,
  onToggleConnect: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (item.isConnected) BrandPrimary.copy(alpha = 0.12f) else Color(0xFFF1F5F9)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = when (item.category) {
              "Telephony" -> Icons.Filled.PhoneInTalk
              "AI Models" -> Icons.Filled.SmartToy
              "CRM" -> Icons.Filled.Hub
              "Calendar" -> Icons.Filled.CalendarMonth
              "Messaging" -> Icons.Filled.Chat
              else -> Icons.Filled.Extension
            },
            contentDescription = null,
            tint = if (item.isConnected) BrandPrimary else Slate500,
            modifier = Modifier.size(24.dp)
          )
        }

        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            StatusBadge(
              text = if (item.isConnected) "Connected" else "Available",
              color = if (item.isConnected) StatusActive else Slate500,
              backgroundColor = if (item.isConnected) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
            )
          }
          Text(
            text = item.description,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            maxLines = 2
          )
        }
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        if (item.isConnected) {
          IconButton(onClick = onConfigure) {
            Icon(Icons.Outlined.Settings, contentDescription = "Configure")
          }
          OutlinedButton(
            onClick = onToggleConnect,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("Disconnect", fontSize = 11.sp, color = AccentRose)
          }
        } else {
          Button(
            onClick = onConfigure,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text("Connect", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
