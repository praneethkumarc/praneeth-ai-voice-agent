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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockRepository
import com.example.model.PhoneNumber
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.OwnerBrandingFooter
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun PhoneNumbersScreen(
  modifier: Modifier = Modifier
) {
  val phoneNumbers by MockRepository.phoneNumbers.collectAsState()
  val aiEmployees by MockRepository.aiEmployees.collectAsState()
  var showConnectDialog by remember { mutableStateOf(false) }
  var showBuyDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    DemoModeBanner()

    // Title & Actions
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Phone Numbers & SIP Trunks",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Telephony routing, caller IDs, and AI employee binding",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
          onClick = { showBuyDialog = true },
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Outlined.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Buy Number", fontSize = 12.sp)
        }

        Button(
          onClick = { showConnectDialog = true },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("connect_phone_button")
        ) {
          Icon(Icons.Filled.PhoneInTalk, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Connect Number", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Number Cards
    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      items(phoneNumbers, key = { it.id }) { phone ->
        PhoneNumberCard(
          phone = phone,
          onDelete = { MockRepository.deletePhoneNumber(phone.id) }
        )
      }
    }

    OwnerBrandingFooter()
  }

  // Connect Dialog
  if (showConnectDialog) {
    var rawNumber by remember { mutableStateOf("+91 89518 58777") }
    var selectedProvider by remember { mutableStateOf("Exotel / Telecom Cloud") }
    var selectedAiEmployee by remember { mutableStateOf(aiEmployees.firstOrNull()?.name ?: "Praneeth Real Estate AI") }

    AlertDialog(
      onDismissRequest = { showConnectDialog = false },
      title = { Text("Connect Existing Business Number", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = rawNumber,
            onValueChange = { rawNumber = it },
            label = { Text("E.164 Phone Number *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("connect_phone_input")
          )

          Text("Telephony Carrier / SIP Provider:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Exotel", "Twilio", "Plivo").forEach { prov ->
              FilterChip(
                selected = selectedProvider.startsWith(prov),
                onClick = { selectedProvider = "$prov / Telecom Cloud" },
                label = { Text(prov) }
              )
            }
          }

          Text("Route Inbound Calls To:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          OutlinedTextField(
            value = selectedAiEmployee,
            onValueChange = { selectedAiEmployee = it },
            label = { Text("Assigned AI Agent") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (rawNumber.isNotBlank()) {
              MockRepository.addPhoneNumber(
                PhoneNumber(
                  id = UUID.randomUUID().toString(),
                  number = rawNumber.trim(),
                  assignedAi = selectedAiEmployee,
                  type = "Mobile / Dedicated",
                  status = "Active",
                  inbound = true,
                  outbound = true,
                  provider = selectedProvider
                )
              )
              showConnectDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
        ) {
          Text("Connect & Verify")
        }
      },
      dismissButton = {
        TextButton(onClick = { showConnectDialog = false }) { Text("Cancel") }
      }
    )
  }

  // Buy Dialog
  if (showBuyDialog) {
    AlertDialog(
      onDismissRequest = { showBuyDialog = false },
      title = { Text("Provision Local Virtual Number", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Select virtual DID number to provision:", style = MaterialTheme.typography.bodySmall)
          listOf(
            "India (+91) — Bangalore Landline",
            "United States (+1) — San Francisco Local",
            "United Kingdom (+44) — London Local"
          ).forEach { opt ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  MockRepository.addPhoneNumber(
                    PhoneNumber(
                      id = UUID.randomUUID().toString(),
                      number = if (opt.startsWith("India")) "+91 80 4719 3300" else "+1 (415) 890-4421",
                      assignedAi = "Praneeth Sales AI",
                      type = if (opt.startsWith("India")) "Local Landline" else "International DID",
                      status = "Active",
                      inbound = true,
                      outbound = true,
                      provider = if (opt.startsWith("India")) "Exotel" else "Twilio"
                    )
                  )
                  showBuyDialog = false
                },
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
              Text(opt, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold)
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showBuyDialog = false }) { Text("Close") }
      }
    )
  }
}

@Composable
fun PhoneNumberCard(
  phone: PhoneNumber,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
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
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(BrandPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Filled.Phone, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
          }

          Column {
            Text(
              text = phone.number,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "${phone.type} • Provider: ${phone.provider}",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }
        }

        StatusBadge(text = phone.status, color = StatusActive)
      }

      // Metadata details
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Assigned AI Agent:", style = MaterialTheme.typography.labelSmall, color = Slate600)
            Text(phone.assignedAi, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrandPrimary))
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Routing Capabilities:", style = MaterialTheme.typography.labelSmall, color = Slate600)
            Text(
              text = "${if (phone.inbound) "Inbound Calls" else ""} ${if (phone.outbound) "• Outbound Dialing" else ""}".trim(),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
            )
          }
        }
      }

      // Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Owner Number: +91 89518 58777",
          style = MaterialTheme.typography.bodySmall,
          color = Slate500
        )

        IconButton(onClick = onDelete) {
          Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AccentRose)
        }
      }
    }
  }
}
