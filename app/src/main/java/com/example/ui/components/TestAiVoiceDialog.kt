package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TestAiVoiceDialog(
  initialEmployee: AiEmployee? = null,
  onDismiss: () -> Unit
) {
  val employees by MockRepository.aiEmployees.collectAsState()
  var selectedEmployee by remember {
    mutableStateOf(initialEmployee ?: employees.firstOrNull { it.isActive } ?: employees.first())
  }

  var isCallActive by remember { mutableStateOf(false) }
  var secondsElapsed by remember { mutableIntStateOf(0) }
  var userInputText by remember { mutableStateOf("") }
  var isAiSpeaking by remember { mutableStateOf(false) }

  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  val conversationHistory = remember {
    mutableStateListOf<TranscriptMessage>()
  }

  // Ticking duration
  LaunchedEffect(isCallActive) {
    if (isCallActive) {
      if (conversationHistory.isEmpty()) {
        conversationHistory.add(
          TranscriptMessage(
            sender = "AI",
            text = selectedEmployee.greeting.ifBlank {
              "Hello! This is ${selectedEmployee.name}. How can I help you today?"
            },
            timestamp = "00:01"
          )
        )
      }
      while (isCallActive) {
        delay(1000)
        secondsElapsed++
      }
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(onClick = onDismiss) { Text("Close") }
    },
    title = {
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
            modifier = Modifier.size(32.dp).clip(CircleShape).background(BrandPrimary),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Filled.Headphones, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
          }
          Column {
            Text("Voice Agent Simulator", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(selectedEmployee.name, style = MaterialTheme.typography.labelSmall, color = BrandPrimary)
          }
        }

        if (isCallActive) {
          StatusBadge(text = "LIVE CALL", color = AccentRose, backgroundColor = Color(0xFFFFE4E6))
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 520.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Agent Selector if call is not yet connected
        if (!isCallActive) {
          Text("Select AI Employee to Test:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            employees.take(2).forEach { emp ->
              FilterChip(
                selected = selectedEmployee.id == emp.id,
                onClick = { selectedEmployee = emp },
                label = { Text(emp.name, fontSize = 11.sp) }
              )
            }
          }

          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
          ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text("Industry: ${selectedEmployee.industry}", style = MaterialTheme.typography.labelSmall)
              Text("Voice: ${selectedEmployee.language} • ${selectedEmployee.voiceGender}", style = MaterialTheme.typography.labelSmall)
              Text("Role: ${selectedEmployee.jobDescription}", style = MaterialTheme.typography.labelSmall, maxLines = 2)
            }
          }

          Button(
            onClick = {
              isCallActive = true
              secondsElapsed = 0
            },
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("start_test_voice_call_button"),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
          ) {
            Icon(Icons.Filled.Call, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Start Voice Simulation Call", fontWeight = FontWeight.Bold)
          }
        } else {
          // Live simulation in progress
          Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Box(
                  modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isAiSpeaking) BrandSecondary else StatusActive)
                )
                Text(
                  text = if (isAiSpeaking) "AI Speaking..." else "AI Listening...",
                  color = Color.White,
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.Bold
                )
              }

              val mins = secondsElapsed / 60
              val secs = secondsElapsed % 60
              Text(
                text = String.format("%02d:%02d", mins, secs),
                color = Color.White,
                fontWeight = FontWeight.Bold
              )

              IconButton(
                onClick = { isCallActive = false },
                modifier = Modifier.size(32.dp).clip(CircleShape).background(AccentRose)
              ) {
                Icon(Icons.Filled.CallEnd, contentDescription = "Hang Up", tint = Color.White, modifier = Modifier.size(16.dp))
              }
            }
          }

          // Live transcript turns
          LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(conversationHistory) { msg ->
              val isAi = msg.sender == "AI"
              Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = if (isAi) Alignment.Start else Alignment.End
              ) {
                Text(
                  text = if (isAi) "${selectedEmployee.name} (AI)" else "You (Caller)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAi) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                )
                Surface(
                  color = if (isAi) Color(0xFFF1F5F9) else Color(0xFFEEF2FF),
                  shape = RoundedCornerShape(10.dp),
                  border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isAi) Color(0xFFE2E8F0) else Color(0xFFC7D2FE)
                  ),
                  modifier = Modifier.padding(top = 2.dp).widthIn(max = 270.dp)
                ) {
                  Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                  )
                }
              }
            }

            if (isAiSpeaking) {
              item {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                  Text("Synthesizing speech response...", fontSize = 11.sp, color = Slate500)
                }
              }
            }
          }

          // Spoken prompt quick chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("What are the prices?", "Can I schedule a visit this Saturday?", "Where is it located?").forEach { chip ->
              AssistChip(
                onClick = {
                  val timeStr = String.format("%02d:%02d", secondsElapsed / 60, secondsElapsed % 60)
                  conversationHistory.add(TranscriptMessage("Customer", chip, timeStr))
                  isAiSpeaking = true
                  coroutineScope.launch {
                    val reply = GeminiVoiceService.generateResponse(
                      aiEmployee = selectedEmployee,
                      conversationHistory = conversationHistory,
                      userUtterance = chip
                    )
                    isAiSpeaking = false
                    val replyTime = String.format("%02d:%02d", secondsElapsed / 60, secondsElapsed % 60)
                    conversationHistory.add(TranscriptMessage("AI", reply, replyTime))
                    listState.animateScrollToItem(conversationHistory.size - 1)
                  }
                },
                label = { Text(chip, fontSize = 10.sp) }
              )
            }
          }

          // Input utterance row
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = userInputText,
              onValueChange = { userInputText = it },
              placeholder = { Text("Speak or type response...") },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("test_voice_user_input")
            )

            Button(
              onClick = {
                val input = userInputText.trim()
                if (input.isNotBlank()) {
                  val timeStr = String.format("%02d:%02d", secondsElapsed / 60, secondsElapsed % 60)
                  conversationHistory.add(TranscriptMessage("Customer", input, timeStr))
                  userInputText = ""
                  isAiSpeaking = true

                  coroutineScope.launch {
                    val reply = GeminiVoiceService.generateResponse(
                      aiEmployee = selectedEmployee,
                      conversationHistory = conversationHistory,
                      userUtterance = input
                    )
                    isAiSpeaking = false
                    val replyTime = String.format("%02d:%02d", secondsElapsed / 60, secondsElapsed % 60)
                    conversationHistory.add(TranscriptMessage("AI", reply, replyTime))
                    listState.animateScrollToItem(conversationHistory.size - 1)
                  }
                }
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
              Text("Send")
            }
          }
        }
      }
    }
  )
}
