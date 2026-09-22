package com.example.model

enum class LeadStage(val displayName: String) {
  NEW("New"),
  CONTACTED("Contacted"),
  QUALIFIED("Qualified"),
  APPOINTMENT("Appointment"),
  NEGOTIATION("Negotiation"),
  WON("Won"),
  LOST("Lost")
}

enum class CallDirection {
  INBOUND,
  OUTBOUND
}

enum class CallOutcome {
  QUALIFIED,
  NOT_QUALIFIED,
  MISSED,
  APPOINTMENT_BOOKED
}

enum class CallStatus {
  COMPLETED,
  IN_PROGRESS,
  FAILED,
  CONNECTED
}

data class TranscriptMessage(
  val sender: String, // "AI" or "Customer"
  val text: String,
  val timestamp: String
)

data class AiEmployee(
  val id: String,
  val name: String,
  val businessName: String,
  val industry: String,
  val description: String,
  val personality: String = "Professional & Empathetic",
  val jobDescription: String = "",
  val knowledgeCount: Int = 3,
  val voiceGender: String = "Female",
  val language: String = "English",
  val voiceSpeed: Float = 1.0f,
  val voiceTone: String = "Friendly & Direct",
  val greeting: String = "Hello! Thanks for reaching out. How can I assist you today?",
  val fallbackResponse: String = "I apologize, I didn't quite catch that. Could you repeat that?",
  val phoneNumber: String = "+91 89518 58777",
  val isActive: Boolean = true,
  val callsHandled: Int = 340,
  val qualifiedLeads: Int = 112,
  val appointments: Int = 24,
  val inboundEnabled: Boolean = true,
  val outboundEnabled: Boolean = true,
  val whatsappFollowUpEnabled: Boolean = true
)

data class CallLog(
  val id: String,
  val customerName: String,
  val customerPhone: String,
  val aiEmployeeName: String,
  val dateTime: String,
  val durationSeconds: Int,
  val direction: CallDirection,
  val status: CallStatus,
  val outcome: CallOutcome,
  val recordingDuration: String = "03:42",
  val aiSummary: String = "Customer is looking for a 3BHK property in Hyderabad with a budget of ₹1.5 crore. Requested a site visit on Saturday morning.",
  val leadBudget: String = "₹1.5 Cr",
  val leadRequirement: String = "3 BHK Luxury Apartment",
  val leadLocation: String = "Gachibowli, Hyderabad",
  val sentiment: String = "Positive",
  val transcript: List<TranscriptMessage> = emptyList()
)

data class Contact(
  val id: String,
  val name: String,
  val phone: String,
  val email: String,
  val company: String,
  val location: String,
  val source: String,
  val tags: List<String>,
  val status: String, // "Active", "Lead", "Customer"
  val lastContact: String,
  val notes: String
)

data class Lead(
  val id: String,
  val customerName: String,
  val phone: String,
  val budget: String,
  val requirement: String,
  val location: String,
  val aiEmployee: String,
  val lastCall: String,
  val leadScore: Int, // 1 - 100
  val stage: LeadStage
)

data class Campaign(
  val id: String,
  val name: String,
  val aiEmployeeId: String,
  val aiEmployeeName: String,
  val objective: String,
  val callScript: String,
  val callingHours: String,
  val retrySettings: String,
  val maxConcurrentCalls: Int,
  val totalContacts: Int,
  val callsStarted: Int,
  val connected: Int,
  val noAnswer: Int,
  val qualified: Int,
  val appointments: Int,
  val isRunning: Boolean
)

data class PhoneNumber(
  val id: String,
  val number: String,
  val assignedAi: String,
  val type: String, // "Mobile", "Toll-Free", "Local"
  val status: String, // "Active", "Pending", "Demo"
  val inbound: Boolean,
  val outbound: Boolean,
  val provider: String = "Twilio / Telecom Cloud"
)

data class KnowledgeDocument(
  val id: String,
  val name: String,
  val type: String, // "PDF", "DOCX", "TXT", "Website", "FAQ", "Pricing"
  val uploadDate: String,
  val status: String, // "Indexed", "Syncing", "Ready"
  val usedByAiEmployees: List<String>,
  val size: String = "1.2 MB",
  val summary: String = ""
)

data class IntegrationItem(
  val id: String,
  val name: String,
  val category: String,
  val description: String,
  val isConnected: Boolean,
  val endpointOrApiKey: String = ""
)

data class NotificationItem(
  val id: String,
  val title: String,
  val message: String,
  val type: String, // "lead", "appointment", "call", "campaign", "error"
  val timeAgo: String,
  val isRead: Boolean = false
)

data class TeamMember(
  val id: String,
  val name: String,
  val email: String,
  val role: String, // "Owner", "Admin", "Manager", "Agent"
  val active: Boolean = true
)

data class UserProfile(
  val name: String = "Praneeth Kumar",
  val email: String = "praneethangel777@gmail.com",
  val phone: String = "8951858777",
  val role: String = "Owner",
  val businessName: String = "Praneeth AI Employee"
)
