package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.AiEmployee
import com.example.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEmployeeWizardScreen(
  onComplete: () -> Unit,
  onCancel: () -> Unit,
  modifier: Modifier = Modifier
) {
  var currentStep by remember { mutableStateOf(1) }

  // Step 1: Basic Information
  var employeeName by remember { mutableStateOf("Praneeth Luxury Real Estate AI") }
  var businessName by remember { mutableStateOf("Praneeth AI Employee") }
  var industry by remember { mutableStateOf("Real Estate & Property") }
  var description by remember { mutableStateOf("Assists high-net-worth buyers in discovering premium flats and scheduling private site tours.") }
  var personality by remember { mutableStateOf("Professional, Warm, Empathetic & Persuasive") }

  // Step 2: Job Description
  var jobDescription by remember {
    mutableStateOf(
      "You are a professional sales assistant for a real estate company. Answer customer questions, collect their name, preferred location, property type, budget and timeline. Qualify the lead and offer a site visit."
    )
  }

  // Step 3: Knowledge Sources
  val knowledgeSources = remember {
    mutableStateListOf(
      "Brochure_Hyderabad_2026.pdf",
      "Frequently_Asked_Questions.docx",
      "https://praneeth.com/pricing-plans"
    )
  }
  var newSourceUrl by remember { mutableStateOf("") }

  // Step 4: Voice
  var voiceGender by remember { mutableStateOf("Female") }
  var selectedLanguage by remember { mutableStateOf("English") }
  val supportedLanguages = listOf("English", "Telugu", "Hindi", "Kannada", "Tamil", "Malayalam")
  var voiceSpeed by remember { mutableFloatStateOf(1.0f) }
  var voiceTone by remember { mutableStateOf("Professional & Friendly") }
  var greeting by remember { mutableStateOf("Hello! Thank you for calling Praneeth Properties. How can I assist you with your home search today?") }
  var fallbackResponse by remember { mutableStateOf("I apologize, I didn't quite catch that. Could you repeat that for me?") }

  // Step 5: Call Behavior
  var inboundCalls by remember { mutableStateOf(true) }
  var outboundCalls by remember { mutableStateOf(true) }
  var businessHours by remember { mutableStateOf("09:00 AM - 07:00 PM IST") }
  var afterHoursMessage by remember { mutableStateOf("We are currently outside business hours. Our AI agent has logged your callback request!") }
  var maxDurationMinutes by remember { mutableFloatStateOf(8.0f) }
  var callTransferEnabled by remember { mutableStateOf(true) }
  var transferPhone by remember { mutableStateOf("+91 89518 58777") }
  var appointmentBooking by remember { mutableStateOf(true) }
  var leadQualification by remember { mutableStateOf(true) }
  var whatsappFollowUp by remember { mutableStateOf(true) }

  // Step 6: Test Call
  var testPhoneNumber by remember { mutableStateOf("+91 89518 58777") }
  var isTestCallActive by remember { mutableStateOf(false) }
  var testCallStatus by remember { mutableStateOf("Ready to test voice prompt") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Create AI Employee", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Step $currentStep of 6", style = MaterialTheme.typography.labelSmall, color = BrandPrimary)
          }
        },
        navigationIcon = {
          IconButton(onClick = onCancel) {
            Icon(Icons.Default.Close, contentDescription = "Cancel")
          }
        },
        actions = {
          TextButton(onClick = onCancel) { Text("Cancel") }
        }
      )
    },
    bottomBar = {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (currentStep > 1) {
            OutlinedButton(
              onClick = { currentStep-- },
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Back")
            }
          } else {
            Spacer(modifier = Modifier.width(1.dp))
          }

          if (currentStep < 6) {
            Button(
              onClick = { currentStep++ },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
              modifier = Modifier.testTag("wizard_next_step_button")
            ) {
              Text("Continue to Step ${currentStep + 1}")
              Spacer(modifier = Modifier.width(6.dp))
              Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
          } else {
            Button(
              onClick = {
                // Save AI Employee to Repository
                val newEmployee = AiEmployee(
                  id = UUID.randomUUID().toString(),
                  name = employeeName,
                  businessName = businessName,
                  industry = industry,
                  description = description,
                  personality = personality,
                  jobDescription = jobDescription,
                  knowledgeCount = knowledgeSources.size,
                  voiceGender = voiceGender,
                  language = selectedLanguage,
                  voiceSpeed = voiceSpeed,
                  voiceTone = voiceTone,
                  greeting = greeting,
                  fallbackResponse = fallbackResponse,
                  phoneNumber = "+91 89518 58777",
                  isActive = true,
                  callsHandled = 0,
                  qualifiedLeads = 0,
                  appointments = 0
                )
                MockRepository.addAiEmployee(newEmployee)
                onComplete()
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandTertiary),
              modifier = Modifier.testTag("wizard_finish_button")
            ) {
              Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Deploy AI Employee", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      // Step Progress Indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        repeat(6) { stepIdx ->
          val stepNumber = stepIdx + 1
          val isActive = currentStep >= stepNumber
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(if (isActive) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant)
              .border(
                1.5.dp,
                if (currentStep == stepNumber) BrandSecondary else Color.Transparent,
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "$stepNumber",
              color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
          if (stepIdx < 5) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(3.dp)
                .background(if (currentStep > stepIdx + 1) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant)
            )
          }
        }
      }

      // Step Contents
      when (currentStep) {
        1 -> {
          // STEP 1 — BASIC INFORMATION
          StepHeader(title = "Step 1: Basic Information", subtitle = "Define the identity and industry of your AI employee.")

          OutlinedTextField(
            value = employeeName,
            onValueChange = { employeeName = it },
            label = { Text("Employee Name *") },
            placeholder = { Text("e.g. Praneeth Sales AI") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("wizard_input_name")
          )

          OutlinedTextField(
            value = businessName,
            onValueChange = { businessName = it },
            label = { Text("Business / Company Name *") },
            placeholder = { Text("e.g. Praneeth AI Employee") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = industry,
            onValueChange = { industry = it },
            label = { Text("Industry *") },
            placeholder = { Text("e.g. Real Estate, Healthcare, SaaS") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Brief Role Description") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = personality,
            onValueChange = { personality = it },
            label = { Text("AI Personality") },
            placeholder = { Text("e.g. Professional, authoritative, empathetic, conversational") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }

        2 -> {
          // STEP 2 — JOB DESCRIPTION
          StepHeader(
            title = "Step 2: Job Description & Prompting",
            subtitle = "Write instructions in plain English specifying exactly what your AI employee should ask, qualify, and answer."
          )

          OutlinedTextField(
            value = jobDescription,
            onValueChange = { jobDescription = it },
            label = { Text("What should your AI employee do? *") },
            placeholder = {
              Text(
                "You are a professional sales assistant for a real estate company. Answer customer questions, collect their name, preferred location, property type, budget and timeline. Qualify the lead and offer a site visit."
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(240.dp)
              .testTag("wizard_input_prompt"),
            supportingText = {
              Text("Be specific about qualification questions, rejection criteria, and meeting booking triggers.")
            }
          )

          Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text("Example Real Estate Instructions:", fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 12.sp)
              Text(
                "\"Ask for preferred BHK configuration and budget. If budget is greater than ₹1 Cr and timeline is under 6 months, offer Saturday morning site inspection.\"",
                style = MaterialTheme.typography.bodySmall,
                color = Slate700
              )
            }
          }
        }

        3 -> {
          // STEP 3 — KNOWLEDGE
          StepHeader(
            title = "Step 3: Knowledge Base & Documents",
            subtitle = "Provide brochures, FAQs, website URLs, and pricing sheets so the AI speaks with accurate business context."
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = {
                knowledgeSources.add("Product_Catalog_V2.pdf")
              },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Outlined.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Upload File (PDF/DOCX)")
            }

            OutlinedButton(
              onClick = {
                knowledgeSources.add("Pricing_Sheet.xlsx")
              },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Outlined.AttachFile, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Upload TXT/FAQ")
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = newSourceUrl,
              onValueChange = { newSourceUrl = it },
              label = { Text("Website URL or FAQ Page") },
              placeholder = { Text("https://yourcompany.com/faqs") },
              singleLine = true,
              modifier = Modifier.weight(1f)
            )
            Button(
              onClick = {
                if (newSourceUrl.isNotBlank()) {
                  knowledgeSources.add(newSourceUrl)
                  newSourceUrl = ""
                }
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
              Text("Add URL")
            }
          }

          Text("Indexed Knowledge Sources (${knowledgeSources.size}):", fontWeight = FontWeight.Bold)

          knowledgeSources.forEach { src ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(Icons.Filled.Description, contentDescription = null, tint = BrandPrimary)
                  Text(src, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }

                IconButton(
                  onClick = { knowledgeSources.remove(src) }
                ) {
                  Icon(Icons.Outlined.Delete, contentDescription = "Remove", tint = AccentRose)
                }
              }
            }
          }
        }

        4 -> {
          // STEP 4 — VOICE
          StepHeader(
            title = "Step 4: Voice & Language",
            subtitle = "Select natural synthetic voice profile, regional languages, tone, and spoken greeting."
          )

          Text("Voice Gender:", fontWeight = FontWeight.Bold)
          Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FilterChip(
              selected = voiceGender == "Female",
              onClick = { voiceGender = "Female" },
              label = { Text("Female Voice") },
              leadingIcon = { Icon(Icons.Filled.Female, contentDescription = null) }
            )
            FilterChip(
              selected = voiceGender == "Male",
              onClick = { voiceGender = "Male" },
              label = { Text("Male Voice") },
              leadingIcon = { Icon(Icons.Filled.Male, contentDescription = null) }
            )
          }

          Text("Primary Spoken Language:", fontWeight = FontWeight.Bold)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            supportedLanguages.take(3).forEach { lang ->
              FilterChip(
                selected = selectedLanguage == lang,
                onClick = { selectedLanguage = lang },
                label = { Text(lang) }
              )
            }
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            supportedLanguages.drop(3).forEach { lang ->
              FilterChip(
                selected = selectedLanguage == lang,
                onClick = { selectedLanguage = lang },
                label = { Text(lang) }
              )
            }
          }

          Text("Voice Speed: ${String.format("%.2f", voiceSpeed)}x", fontWeight = FontWeight.Bold)
          Slider(
            value = voiceSpeed,
            onValueChange = { voiceSpeed = it },
            valueRange = 0.75f..1.35f,
            steps = 6
          )

          OutlinedTextField(
            value = greeting,
            onValueChange = { greeting = it },
            label = { Text("Greeting (First sentence spoken on answer)") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = fallbackResponse,
            onValueChange = { fallbackResponse = it },
            label = { Text("Fallback Response (When speech is unclear)") },
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
          )
        }

        5 -> {
          // STEP 5 — CALL BEHAVIOR
          StepHeader(
            title = "Step 5: Call Routing & Automation",
            subtitle = "Configure inbound/outbound triggers, working hours, and automated follow-ups."
          )

          BehaviorSwitchRow(
            title = "Handle Inbound Calls",
            subtitle = "Answer incoming phone inquiries automatically",
            checked = inboundCalls,
            onCheckedChange = { inboundCalls = it }
          )

          BehaviorSwitchRow(
            title = "Run Outbound Calling",
            subtitle = "Eligible for outbound lead qualification campaigns",
            checked = outboundCalls,
            onCheckedChange = { outboundCalls = it }
          )

          BehaviorSwitchRow(
            title = "Automatic Appointment Booking",
            subtitle = "Sync with Google Calendar slots during conversation",
            checked = appointmentBooking,
            onCheckedChange = { appointmentBooking = it }
          )

          BehaviorSwitchRow(
            title = "Lead Qualification Scoring",
            subtitle = "Evaluate criteria and tag leads as Qualified or Disqualified",
            checked = leadQualification,
            onCheckedChange = { leadQualification = it }
          )

          BehaviorSwitchRow(
            title = "Instant WhatsApp Follow-up",
            subtitle = "Trigger brochure, directions, or quote message after call completes",
            checked = whatsappFollowUp,
            onCheckedChange = { whatsappFollowUp = it }
          )

          OutlinedTextField(
            value = businessHours,
            onValueChange = { businessHours = it },
            label = { Text("Calling Operating Hours") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = transferPhone,
            onValueChange = { transferPhone = it },
            label = { Text("Human Specialist Escalation Phone") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }

        6 -> {
          // STEP 6 — TEST
          StepHeader(
            title = "Step 6: Test your AI Employee",
            subtitle = "Initiate an instant test simulation to verify response tone, speed, and qualification criteria."
          )

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
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Text("Enter Phone Number to Test:", fontWeight = FontWeight.Bold)

              OutlinedTextField(
                value = testPhoneNumber,
                onValueChange = { testPhoneNumber = it },
                label = { Text("Your Phone Number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("wizard_test_phone_input")
              )

              Button(
                onClick = {
                  isTestCallActive = true
                  testCallStatus = "Connecting call to $testPhoneNumber... AI greeting ready!"
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("wizard_test_call_button"),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
              ) {
                Icon(Icons.Filled.Call, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Test Call Now")
              }

              if (isTestCallActive) {
                Surface(
                  color = Color(0xFFDCFCE7),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusActive))
                      Text("SIMULATION CONNECTED", fontWeight = FontWeight.Bold, color = Color(0xFF166534), fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Spoken greeting: \"$greeting\"",
                      style = MaterialTheme.typography.bodySmall,
                      color = Color(0xFF14532D)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun StepHeader(title: String, subtitle: String) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(text = title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
    Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
  }
}

@Composable
private fun BehaviorSwitchRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
      .padding(horizontal = 14.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
      Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Switch(checked = checked, onCheckedChange = onCheckedChange)
  }
}
