package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import com.example.model.*
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.OwnerBrandingFooter
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun CallsScreen(
  onSelectCall: (CallLog) -> Unit,
  selectedCallFromOutside: CallLog? = null,
  modifier: Modifier = Modifier
) {
  val calls by MockRepository.calls.collectAsState()
  var selectedFilter by remember { mutableStateOf("All") }
  var searchQuery by remember { mutableStateOf("") }
  var openedCall by remember { mutableStateOf(selectedCallFromOutside) }

  val filterOptions = listOf(
    "All", "Today", "Yesterday", "Inbound", "Outbound", "Qualified", "Not Qualified", "Missed"
  )

  val filteredCalls = calls.filter { call ->
    val matchesSearch = searchQuery.isBlank() ||
        call.customerName.contains(searchQuery, ignoreCase = true) ||
        call.customerPhone.contains(searchQuery) ||
        call.aiEmployeeName.contains(searchQuery, ignoreCase = true)

    val matchesFilter = when (selectedFilter) {
      "Today" -> call.dateTime.startsWith("Today")
      "Yesterday" -> call.dateTime.startsWith("Yesterday")
      "Inbound" -> call.direction == CallDirection.INBOUND
      "Outbound" -> call.direction == CallDirection.OUTBOUND
      "Qualified" -> call.outcome == CallOutcome.QUALIFIED || call.outcome == CallOutcome.APPOINTMENT_BOOKED
      "Not Qualified" -> call.outcome == CallOutcome.NOT_QUALIFIED
      "Missed" -> call.outcome == CallOutcome.MISSED
      else -> true
    }
    matchesSearch && matchesFilter
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    DemoModeBanner()

    // Title & Export
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Call Logs & Audio Transcripts",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "${calls.size} recorded conversations across all AI employees",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }
    }

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("Search by customer name, phone, or agent...") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear")
          }
        }
      },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("calls_search_input")
    )

    // Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      filterOptions.forEach { filter ->
        FilterChip(
          selected = selectedFilter == filter,
          onClick = { selectedFilter = filter },
          label = { Text(filter, fontSize = 12.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BrandPrimary.copy(alpha = 0.15f),
            selectedLabelColor = BrandPrimary
          )
        )
      }
    }

    // Calls Table / List
    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      if (filteredCalls.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(40.dp),
            contentAlignment = Alignment.Center
          ) {
            Text("No calls match the selected filter.", color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      } else {
        items(filteredCalls, key = { it.id }) { call ->
          CallRowCard(
            call = call,
            onClick = {
              openedCall = call
              onSelectCall(call)
            }
          )
        }
      }
    }

    OwnerBrandingFooter()
  }

  // Call Details Dialog
  if (openedCall != null) {
    CallDetailsDialog(
      call = openedCall!!,
      onDismiss = { openedCall = null }
    )
  }
}

@Composable
fun CallRowCard(
  call: CallLog,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("call_item_${call.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
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
              .size(38.dp)
              .clip(CircleShape)
              .background(
                if (call.direction == CallDirection.INBOUND) Color(0xFFEFF6FF) else Color(0xFFF5F3FF)
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (call.direction == CallDirection.INBOUND) Icons.Filled.CallReceived else Icons.Filled.CallMade,
              contentDescription = null,
              tint = if (call.direction == CallDirection.INBOUND) BrandPrimary else AccentPurple,
              modifier = Modifier.size(18.dp)
            )
          }

          Column {
            Text(
              text = call.customerName,
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "${call.customerPhone} • ${call.dateTime}",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            text = if (call.durationSeconds == 0) "0s" else "${call.durationSeconds / 60}m ${call.durationSeconds % 60}s",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.SemiBold
            )
          )
        }
      }

      // Summary preview & AI Employee tag
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = call.aiSummary,
          style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
          color = MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = call.aiEmployeeName,
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.sp,
              fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}

@Composable
fun CallDetailsDialog(
  call: CallLog,
  onDismiss: () -> Unit
) {
  var isPlayingAudio by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(onClick = onDismiss) { Text("Close") }
    },
    title = {
      Column {
        Text(text = "Call Details & Transcript", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(
          text = "${call.customerName} • ${call.customerPhone}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    },
    text = {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 480.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Meta Information Grid
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              DetailRow(label = "AI Employee", value = call.aiEmployeeName)
              DetailRow(label = "Date & Time", value = call.dateTime)
              DetailRow(label = "Duration", value = "${call.durationSeconds / 60}m ${call.durationSeconds % 60}s")
              DetailRow(label = "Direction", value = if (call.direction == CallDirection.INBOUND) "Inbound Call" else "Outbound Call")
              DetailRow(label = "Lead Sentiment", value = call.sentiment)
              DetailRow(label = "Budget Discussed", value = call.leadBudget)
              DetailRow(label = "Requirement", value = call.leadRequirement)
              DetailRow(label = "Location", value = call.leadLocation)
            }
          }
        }

        // Call Recording Player UI
        item {
          Text("Call Recording Audio:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              IconButton(
                onClick = { isPlayingAudio = !isPlayingAudio },
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(BrandPrimary)
              ) {
                Icon(
                  imageVector = if (isPlayingAudio) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                  contentDescription = if (isPlayingAudio) "Pause" else "Play",
                  tint = Color.White
                )
              }

              Column(modifier = Modifier.weight(1f)) {
                // Waveform bars simulation
                Row(
                  modifier = Modifier.fillMaxWidth().height(24.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  val heights = listOf(8, 14, 22, 10, 18, 24, 16, 12, 20, 15, 9, 21, 17, 13, 23, 11)
                  heights.forEach { h ->
                    Box(
                      modifier = Modifier
                        .width(4.dp)
                        .height(h.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isPlayingAudio) BrandSecondary else Color(0xFF64748B))
                    )
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("01:14", color = Color(0xFF94A3B8), fontSize = 10.sp)
                  Text(call.recordingDuration, color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
              }
            }
          }
        }

        // AI Summary
        item {
          Text("AI Executive Summary:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Surface(
            color = Color(0xFFEEF2FF),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = call.aiSummary,
              style = MaterialTheme.typography.bodyMedium,
              color = Slate800,
              modifier = Modifier.padding(12.dp)
            )
          }
        }

        // Transcript
        item {
          Text("Full Voice Transcript (${call.transcript.size} turns):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        if (call.transcript.isEmpty()) {
          item {
            Text(
              "No recorded audio transcript for this missed/aborted call.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          items(call.transcript) { msg ->
            val isAi = msg.sender == "AI"
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = if (isAi) Alignment.Start else Alignment.End
            ) {
              Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = if (isAi) "${call.aiEmployeeName} (AI)" else call.customerName,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isAi) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                )
                Text(
                  text = msg.timestamp,
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Slate400)
                )
              }

              Surface(
                color = if (isAi) Color(0xFFF1F5F9) else Color(0xFFEEF2FF),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isAi) Color(0xFFE2E8F0) else Color(0xFFC7D2FE)
                ),
                modifier = Modifier.padding(top = 4.dp).widthIn(max = 280.dp)
              ) {
                Text(
                  text = msg.text,
                  style = MaterialTheme.typography.bodySmall,
                  modifier = Modifier.padding(10.dp)
                )
              }
            }
          }
        }
      }
    }
  )
}

@Composable
private fun DetailRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
  }
}
