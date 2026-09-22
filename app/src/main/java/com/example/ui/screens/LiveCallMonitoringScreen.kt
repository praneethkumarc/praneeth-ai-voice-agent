package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.data.GeminiVoiceService
import com.example.data.MockRepository
import com.example.model.AiEmployee
import com.example.model.TranscriptMessage
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.OwnerBrandingFooter
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LiveCallMonitoringScreen(
  modifier: Modifier = Modifier
) {
  val aiEmployees by MockRepository.aiEmployees.collectAsState()
  val activeEmployee = aiEmployees.firstOrNull { it.isActive } ?: aiEmployees.first()

  var isMuted by remember { mutableStateOf(false) }
  var isCallEnded by remember { mutableStateOf(false) }
  var isTransferred by remember { mutableStateOf(false) }
  var secondsElapsed by remember { mutableIntStateOf(45) }

  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  val liveTranscript = remember {
    mutableStateListOf(
      TranscriptMessage("AI", "Hello! Good morning. Thank you for calling Praneeth Properties. How can I assist you today?", "00:03"),
      TranscriptMessage("Customer", "Hi! I saw your recent launch for 3BHK flats near Financial District. Are there units still available?", "00:14"),
      TranscriptMessage("AI", "Yes, absolutely! We have premium east and west facing 3BHK homes ranging from 1,850 to 2,200 sq ft. May I know your preferred budget and possession timeline?", "00:28"),
      TranscriptMessage("Customer", "Looking to move within 6 months. Budget is around 1.5 crore.", "00:40")
    )
  }

  var simulateCustomerText by remember { mutableStateOf("") }
  var isAiReplying by remember { mutableStateOf(false) }

  // Ticking duration counter
  LaunchedEffect(isCallEnded) {
    while (!isCallEnded) {
      delay(1000)
      secondsElapsed++
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    DemoModeBanner()

    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Live Call Monitoring",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Real-time AI voice stream, whisper monitoring & intervention",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }

      if (!isCallEnded) {
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFFE4E6))
            .padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentRose))
          Text("STREAMING LIVE", fontWeight = FontWeight.Bold, color = AccentRose, fontSize = 11.sp)
        }
      }
    }

    // Active Live Call Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // AI Employee & Customer info
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isCallEnded) Slate300 else BrandPrimary),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Filled.GraphicEq, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }

            Column {
              Text(
                text = "AI Employee: ${activeEmployee.name}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Customer: +91 98765 XXXXX (Inbound Call)",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            val mins = secondsElapsed / 60
            val secs = secondsElapsed % 60
            Text(
              text = String.format("%02d:%02d", mins, secs),
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = if (isCallEnded) Slate500 else BrandPrimary
              )
            )
            Text(
              text = if (isCallEnded) "Call Ended" else if (isTransferred) "Transferred" else "Connected",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isCallEnded) Slate500 else StatusActive
            )
          }
        }

        // Live status indicator / audio wave
        if (!isCallEnded) {
          Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isMuted) AccentAmber else BrandSecondary)
                )
                Text(
                  text = if (isMuted) "Mic Muted" else if (isAiReplying) "AI Speaking..." else "Listening to customer...",
                  color = Color.White,
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold
                )
              }

              // Visual pulse bars
              Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(8) { idx ->
                  Box(
                    modifier = Modifier
                      .width(3.dp)
                      .height((10 + (idx * 3) % 14).dp)
                      .clip(RoundedCornerShape(2.dp))
                      .background(if (isAiReplying) BrandSecondary else Color(0xFF38BDF8))
                  )
                }
              }
            }
          }
        }

        // Buttons: Mute, Transfer, End Call (Requirement 6)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = { isMuted = !isMuted },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f).testTag("live_mute_button"),
            colors = ButtonDefaults.outlinedButtonColors(
              containerColor = if (isMuted) Color(0xFFFEF3C7) else Color.Transparent
            )
          ) {
            Icon(
              imageVector = if (isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
              tint = if (isMuted) AccentAmber else BrandPrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isMuted) "Unmute" else "Mute")
          }

          OutlinedButton(
            onClick = {
              isTransferred = true
              liveTranscript.add(
                TranscriptMessage("AI", "Connecting you to Senior Advisor Praneeth (+91 89518 58777)...", "00:48")
              )
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f).testTag("live_transfer_button")
          ) {
            Icon(Icons.Filled.PhoneForwarded, contentDescription = null, modifier = Modifier.size(18.dp), tint = AccentPurple)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Transfer")
          }

          Button(
            onClick = { isCallEnded = true },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentRose),
            modifier = Modifier.weight(1f).testTag("live_end_call_button")
          ) {
            Icon(Icons.Filled.CallEnd, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("End Call")
          }
        }
      }
    }

    // Live Streaming Transcript Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Live Conversation Transcript",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          StatusBadge(text = "${liveTranscript.size} turns", color = BrandPrimary)
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

        LazyColumn(
          state = listState,
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(liveTranscript) { msg ->
            val isAi = msg.sender == "AI"
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = if (isAi) Alignment.Start else Alignment.End
            ) {
              Text(
                text = if (isAi) "${activeEmployee.name} • ${msg.timestamp}" else "Customer • ${msg.timestamp}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isAi) BrandPrimary else Slate500
                )
              )
              Surface(
                color = if (isAi) Color(0xFFF1F5F9) else Color(0xFFEEF2FF),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isAi) Color(0xFFE2E8F0) else Color(0xFFC7D2FE)
                ),
                modifier = Modifier
                  .padding(top = 4.dp)
                  .widthIn(max = 300.dp)
              ) {
                Text(
                  text = msg.text,
                  style = MaterialTheme.typography.bodySmall,
                  modifier = Modifier.padding(10.dp)
                )
              }
            }
          }

          if (isAiReplying) {
            item {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Text("AI generating speech response...", style = MaterialTheme.typography.bodySmall, color = Slate500)
              }
            }
          }
        }

        // Test caller interactive box
        if (!isCallEnded) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = simulateCustomerText,
              onValueChange = { simulateCustomerText = it },
              placeholder = { Text("Type caller response to test AI...") },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("simulate_caller_input")
            )

            Button(
              onClick = {
                val input = simulateCustomerText.trim()
                if (input.isNotEmpty()) {
                  val timeStr = String.format("%02d:%02d", secondsElapsed / 60, secondsElapsed % 60)
                  liveTranscript.add(TranscriptMessage("Customer", input, timeStr))
                  simulateCustomerText = ""
                  isAiReplying = true

                  coroutineScope.launch {
                    val reply = GeminiVoiceService.generateResponse(
                      aiEmployee = activeEmployee,
                      conversationHistory = liveTranscript,
                      userUtterance = input
                    )
                    isAiReplying = false
                    val replyTime = String.format("%02d:%02d", secondsElapsed / 60, secondsElapsed % 60)
                    liveTranscript.add(TranscriptMessage("AI", reply, replyTime))
                    listState.animateScrollToItem(liveTranscript.size - 1)
                  }
                }
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
              Text("Speak")
            }
          }
        }
      }
    }

    OwnerBrandingFooter()
  }
}
