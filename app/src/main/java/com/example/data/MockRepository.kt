package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object MockRepository {

  private val _isDemoMode = MutableStateFlow(true)
  val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

  fun setDemoMode(enabled: Boolean) {
    _isDemoMode.value = enabled
  }

  // Profile & Business
  private val _userProfile = MutableStateFlow(
    UserProfile(
      name = "Praneeth Kumar",
      email = "praneethangel777@gmail.com",
      phone = "8951858777",
      role = "Owner",
      businessName = "Praneeth AI Employee"
    )
  )
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  fun updateProfile(name: String, email: String, phone: String, businessName: String) {
    _userProfile.value = _userProfile.value.copy(
      name = name,
      email = email,
      phone = phone,
      businessName = businessName
    )
  }

  // AI Employees
  private val _aiEmployees = MutableStateFlow(
    listOf(
      AiEmployee(
        id = "emp-1",
        name = "Praneeth Sales AI",
        businessName = "Praneeth AI Employee",
        industry = "Real Estate & High-Ticket Sales",
        description = "Handles inbound seller & buyer inquiries, captures criteria, qualifies budgets, and books site visits.",
        personality = "Authoritative, warm, persuasive",
        jobDescription = "You are a professional sales assistant for a real estate company. Answer customer questions, collect their name, preferred location, property type, budget and timeline. Qualify the lead and offer a site visit.",
        language = "English & Telugu",
        voiceGender = "Female",
        voiceSpeed = 1.0f,
        voiceTone = "Executive & Friendly",
        phoneNumber = "+91 89518 58777",
        isActive = true,
        callsHandled = 612,
        qualifiedLeads = 248,
        appointments = 52
      ),
      AiEmployee(
        id = "emp-2",
        name = "Praneeth Support AI",
        businessName = "Praneeth AI Employee",
        industry = "Customer Success & SaaS",
        description = "Answers troubleshooting queries, billing questions, and routes escalations to human specialists.",
        personality = "Patient, precise, solutions-oriented",
        jobDescription = "Answer product FAQs, verify account email, resolve basic subscription queries, and log support tickets.",
        language = "English & Hindi",
        voiceGender = "Male",
        voiceSpeed = 0.95f,
        voiceTone = "Helpful & Calm",
        phoneNumber = "+91 98765 12345",
        isActive = true,
        callsHandled = 428,
        qualifiedLeads = 94,
        appointments = 18
      ),
      AiEmployee(
        id = "emp-3",
        name = "Real Estate AI",
        businessName = "Praneeth AI Employee",
        industry = "Property Development",
        description = "Specialized in villa and luxury apartment pre-sales, mortgage eligibility, and instant booking slots.",
        personality = "Polite, sharp, structured",
        jobDescription = "Collect property type, budget over 1 Cr, timeline under 6 months, and trigger WhatsApp brochure.",
        language = "Telugu & English",
        voiceGender = "Female",
        voiceSpeed = 1.05f,
        voiceTone = "Enthusiastic & Cordial",
        phoneNumber = "+91 98765 67890",
        isActive = true,
        callsHandled = 156,
        qualifiedLeads = 58,
        appointments = 12
      ),
      AiEmployee(
        id = "emp-4",
        name = "Spa Booking AI",
        businessName = "Praneeth AI Employee",
        industry = "Wellness & Hospitality",
        description = "Handles appointment schedule requests, packages, pricing FAQs, and sends instant calendar invites.",
        personality = "Gentle, relaxing, organized",
        jobDescription = "Assist clients in choosing wellness therapies, confirm therapist availability, and book 60/90min sessions.",
        language = "English & Kannada",
        voiceGender = "Female",
        voiceSpeed = 0.9f,
        voiceTone = "Warm & Welcoming",
        phoneNumber = "+91 80123 45678",
        isActive = false,
        callsHandled = 52,
        qualifiedLeads = 12,
        appointments = 4
      )
    )
  )
  val aiEmployees: StateFlow<List<AiEmployee>> = _aiEmployees.asStateFlow()

  fun addAiEmployee(employee: AiEmployee) {
    _aiEmployees.value = listOf(employee) + _aiEmployees.value
  }

  fun updateAiEmployee(updated: AiEmployee) {
    _aiEmployees.value = _aiEmployees.value.map { if (it.id == updated.id) updated else it }
  }

  fun toggleAiEmployeeStatus(id: String) {
    _aiEmployees.value = _aiEmployees.value.map {
      if (it.id == id) it.copy(isActive = !it.isActive) else it
    }
  }

  fun duplicateAiEmployee(id: String) {
    val existing = _aiEmployees.value.find { it.id == id } ?: return
    val copy = existing.copy(
      id = UUID.randomUUID().toString(),
      name = "${existing.name} (Copy)",
      callsHandled = 0,
      qualifiedLeads = 0,
      appointments = 0
    )
    _aiEmployees.value = _aiEmployees.value + copy
  }

  fun deleteAiEmployee(id: String) {
    _aiEmployees.value = _aiEmployees.value.filter { it.id != id }
  }

  // Call Logs
  private val _calls = MutableStateFlow(
    listOf(
      CallLog(
        id = "call-1",
        customerName = "Tejeshwar Reddy",
        customerPhone = "+91 98765 43210",
        aiEmployeeName = "Praneeth Sales AI",
        dateTime = "Today, 10:14 AM",
        durationSeconds = 222,
        direction = CallDirection.INBOUND,
        status = CallStatus.COMPLETED,
        outcome = CallOutcome.QUALIFIED,
        aiSummary = "Customer is looking for a 3BHK property in Hyderabad with a budget of ₹1.5 crore. Requested a site visit on Saturday 11 AM.",
        leadBudget = "₹1.50 Cr",
        leadRequirement = "3 BHK Gated Community",
        leadLocation = "Kondapur, Hyderabad",
        sentiment = "Highly Interested",
        transcript = listOf(
          TranscriptMessage("AI", "Hello! Good morning. Thank you for calling Praneeth Properties. How can I assist you with your home search today?", "00:03"),
          TranscriptMessage("Customer", "Hi! I saw your recent launch for 3BHK flats near Financial District. Are there units still available?", "00:12"),
          TranscriptMessage("AI", "Yes, absolutely! We have premium east and west facing 3BHK homes ranging from 1,850 to 2,200 sq ft. May I know your preferred possession timeline and approximate budget?", "00:26"),
          TranscriptMessage("Customer", "Looking to move within 6 months. Budget is around 1.5 crore.", "00:34"),
          TranscriptMessage("AI", "That aligns nicely with Phase 1 ready-for-fitout towers. Would you like to schedule an executive site visit this weekend?", "00:46"),
          TranscriptMessage("Customer", "Yes please, Saturday around 11 AM works great.", "00:52"),
          TranscriptMessage("AI", "Confirmed! I've reserved your slot for Saturday at 11 AM with Senior Relationship Manager Rahul. Sending directions and brochure over WhatsApp now.", "01:05")
        )
      ),
      CallLog(
        id = "call-2",
        customerName = "Ananya Sharma",
        customerPhone = "+91 91234 56789",
        aiEmployeeName = "Praneeth Support AI",
        dateTime = "Today, 09:30 AM",
        durationSeconds = 145,
        direction = CallDirection.INBOUND,
        status = CallStatus.COMPLETED,
        outcome = CallOutcome.QUALIFIED,
        aiSummary = "Queried billing cycle switch from monthly to annual enterprise plan. Provided 20% discount coupon code and updated payment link.",
        leadBudget = "₹85,000 / yr",
        leadRequirement = "Enterprise SaaS Tier",
        leadLocation = "Bangalore",
        sentiment = "Positive",
        transcript = listOf(
          TranscriptMessage("AI", "Thank you for calling Praneeth AI support. How can I help you today?", "00:02"),
          TranscriptMessage("Customer", "We want to upgrade our 5 agent seats to the annual tier to get the invoice discount.", "00:11"),
          TranscriptMessage("AI", "I can certainly arrange that! I have generated the annual quote with the 20% discount applied and emailed your billing admin.", "00:28")
        )
      ),
      CallLog(
        id = "call-3",
        customerName = "Vikram Malhotra",
        customerPhone = "+91 99887 76655",
        aiEmployeeName = "Real Estate AI",
        dateTime = "Yesterday, 04:45 PM",
        durationSeconds = 310,
        direction = CallDirection.OUTBOUND,
        status = CallStatus.COMPLETED,
        outcome = CallOutcome.QUALIFIED,
        aiSummary = "Outbound campaign follow-up for Luxury Villas. Customer requested floor plans and verified budget ₹3.2 Cr.",
        leadBudget = "₹3.20 Cr",
        leadRequirement = "4 BHK Luxury Villa",
        leadLocation = "Mokila, Hyderabad",
        sentiment = "Enthusiastic"
      ),
      CallLog(
        id = "call-4",
        customerName = "Suresh Raina",
        customerPhone = "+91 97711 22334",
        aiEmployeeName = "Praneeth Sales AI",
        dateTime = "Yesterday, 02:15 PM",
        durationSeconds = 85,
        direction = CallDirection.INBOUND,
        status = CallStatus.COMPLETED,
        outcome = CallOutcome.NOT_QUALIFIED,
        aiSummary = "Caller was inquiring about low-budget rental studios under 15k. Not matching inventory.",
        leadBudget = "₹15,000/mo",
        leadRequirement = "Studio Rental",
        leadLocation = "Old City",
        sentiment = "Neutral"
      ),
      CallLog(
        id = "call-5",
        customerName = "Unknown Caller",
        customerPhone = "+91 94455 66778",
        aiEmployeeName = "Praneeth Sales AI",
        dateTime = "Yesterday, 11:20 AM",
        durationSeconds = 0,
        direction = CallDirection.INBOUND,
        status = CallStatus.FAILED,
        outcome = CallOutcome.MISSED,
        aiSummary = "Inbound call disconnected before AI greeting answered. Instant SMS fallback sent.",
        sentiment = "Unknown"
      ),
      CallLog(
        id = "call-6",
        customerName = "Pooja Hegde",
        customerPhone = "+91 88776 65544",
        aiEmployeeName = "Spa Booking AI",
        dateTime = "2 days ago, 06:10 PM",
        durationSeconds = 180,
        direction = CallDirection.INBOUND,
        status = CallStatus.COMPLETED,
        outcome = CallOutcome.APPOINTMENT_BOOKED,
        aiSummary = "Booked 90-minute Ayurvedic deep tissue therapy for Sunday 4 PM. Sent WhatsApp confirmation.",
        leadBudget = "₹4,500",
        leadRequirement = "Wellness Therapy",
        leadLocation = "Jubilee Hills",
        sentiment = "Satisfied"
      )
    )
  )
  val calls: StateFlow<List<CallLog>> = _calls.asStateFlow()

  fun addCall(call: CallLog) {
    _calls.value = listOf(call) + _calls.value
  }

  // Contacts
  private val _contacts = MutableStateFlow(
    listOf(
      Contact(
        id = "c-1",
        name = "Tejeshwar Reddy",
        phone = "+91 98765 43210",
        email = "tejeshwar.r@techcorp.in",
        company = "TechCorp Labs",
        location = "Hyderabad, TS",
        source = "Inbound Call",
        tags = listOf("High Value", "Buyer", "Site Visit"),
        status = "Qualified Lead",
        lastContact = "Today, 10:14 AM",
        notes = "Interested in 3BHK near Financial District. Site visit scheduled for Saturday."
      ),
      Contact(
        id = "c-2",
        name = "Ananya Sharma",
        phone = "+91 91234 56789",
        email = "ananya@fintechpulse.io",
        company = "Fintech Pulse",
        location = "Bangalore, KA",
        source = "Website Widget",
        tags = listOf("Enterprise", "Annual Renewal"),
        status = "Customer",
        lastContact = "Today, 09:30 AM",
        notes = "Requested annual payment link for 5 voice seats."
      ),
      Contact(
        id = "c-3",
        name = "Vikram Malhotra",
        phone = "+91 99887 76655",
        email = "vikram.m@malhotrainvest.com",
        company = "Malhotra Capital",
        location = "Hyderabad, TS",
        source = "Outbound Campaign",
        tags = listOf("Villa Buyer", "High Net Worth"),
        status = "Active Lead",
        lastContact = "Yesterday, 04:45 PM",
        notes = "Looking for 4BHK Villa in Mokila. Budget 3.2 Cr."
      ),
      Contact(
        id = "c-4",
        name = "Rahul Verma",
        phone = "+91 89518 58777",
        email = "rahul.v@vermaholdings.in",
        company = "Verma Retail",
        location = "Mumbai, MH",
        source = "Meta Lead Ads",
        tags = listOf("Campaign Contact", "WhatsApp"),
        status = "Contacted",
        lastContact = "3 days ago",
        notes = "Sent property catalogue over WhatsApp follow-up."
      ),
      Contact(
        id = "c-5",
        name = "Deepika Pillai",
        phone = "+91 98450 11223",
        email = "deepika.p@chennaidesigns.com",
        company = "Studio Deepika",
        location = "Chennai, TN",
        source = "Google Ads",
        tags = listOf("Interior Design", "Partner"),
        status = "Partner",
        lastContact = "4 days ago",
        notes = "Discussed referral commission for voice agent clients."
      )
    )
  )
  val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

  fun addContact(contact: Contact) {
    _contacts.value = listOf(contact) + _contacts.value
  }

  fun updateContact(updated: Contact) {
    _contacts.value = _contacts.value.map { if (it.id == updated.id) updated else it }
  }

  fun deleteContact(id: String) {
    _contacts.value = _contacts.value.filter { it.id != id }
  }

  fun importSampleCsv() {
    val sampleImport = listOf(
      Contact(
        id = UUID.randomUUID().toString(),
        name = "Siddharth Rao",
        phone = "+91 98200 44556",
        email = "siddharth.r@raoenterprises.com",
        company = "Rao Infra",
        location = "Hyderabad",
        source = "CSV Import",
        tags = listOf("Commercial", "Investor"),
        status = "New",
        lastContact = "Just now",
        notes = "Imported from Q3 Investor list."
      ),
      Contact(
        id = UUID.randomUUID().toString(),
        name = "Meera Krishnan",
        phone = "+91 97400 33221",
        email = "meera.k@krishnancare.org",
        company = "HealthFirst",
        location = "Bengaluru",
        source = "CSV Import",
        tags = listOf("Healthcare", "Voice Agent"),
        status = "New",
        lastContact = "Just now",
        notes = "Automated appointment booking prospect."
      )
    )
    _contacts.value = sampleImport + _contacts.value
  }

  // Leads
  private val _leads = MutableStateFlow(
    listOf(
      Lead(
        id = "lead-1",
        customerName = "Tejeshwar Reddy",
        phone = "+91 98765 43210",
        budget = "₹1.50 Cr",
        requirement = "3 BHK Gated Community",
        location = "Kondapur, Hyderabad",
        aiEmployee = "Praneeth Sales AI",
        lastCall = "Today, 10:14 AM",
        leadScore = 94,
        stage = LeadStage.APPOINTMENT
      ),
      Lead(
        id = "lead-2",
        customerName = "Vikram Malhotra",
        phone = "+91 99887 76655",
        budget = "₹3.20 Cr",
        requirement = "4 BHK Luxury Villa",
        location = "Mokila, Hyderabad",
        aiEmployee = "Real Estate AI",
        lastCall = "Yesterday",
        leadScore = 88,
        stage = LeadStage.QUALIFIED
      ),
      Lead(
        id = "lead-3",
        customerName = "Ananya Sharma",
        phone = "+91 91234 56789",
        budget = "₹85,000 / yr",
        requirement = "Annual SaaS Voice License",
        location = "Bangalore",
        aiEmployee = "Praneeth Support AI",
        lastCall = "Today, 09:30 AM",
        leadScore = 96,
        stage = LeadStage.NEGOTIATION
      ),
      Lead(
        id = "lead-4",
        customerName = "Rahul Verma",
        phone = "+91 89518 58777",
        budget = "₹90 Lakh",
        requirement = "2 BHK Investment",
        location = "Tellapur",
        aiEmployee = "Praneeth Sales AI",
        lastCall = "3 days ago",
        leadScore = 65,
        stage = LeadStage.CONTACTED
      ),
      Lead(
        id = "lead-5",
        customerName = "Rakesh Kothari",
        phone = "+91 98332 11009",
        budget = "₹2.10 Cr",
        requirement = "Commercial Office Space",
        location = "Hitec City",
        aiEmployee = "Praneeth Sales AI",
        lastCall = "4 days ago",
        leadScore = 82,
        stage = LeadStage.NEW
      ),
      Lead(
        id = "lead-6",
        customerName = "Pooja Hegde",
        phone = "+91 88776 65544",
        budget = "₹25,000 Package",
        requirement = "Annual Spa Membership",
        location = "Jubilee Hills",
        aiEmployee = "Spa Booking AI",
        lastCall = "2 days ago",
        leadScore = 99,
        stage = LeadStage.WON
      )
    )
  )
  val leads: StateFlow<List<Lead>> = _leads.asStateFlow()

  fun updateLeadStage(leadId: String, newStage: LeadStage) {
    _leads.value = _leads.value.map {
      if (it.id == leadId) it.copy(stage = newStage) else it
    }
  }

  fun addLead(lead: Lead) {
    _leads.value = listOf(lead) + _leads.value
  }

  fun deleteLead(leadId: String) {
    _leads.value = _leads.value.filter { it.id != leadId }
  }

  // Campaigns
  private val _campaigns = MutableStateFlow(
    listOf(
      Campaign(
        id = "camp-1",
        name = "Hyderabad Q3 Luxury Villa Outreach",
        aiEmployeeId = "emp-1",
        aiEmployeeName = "Praneeth Sales AI",
        objective = "Qualify high-net-worth buyers for newly launched gated villas.",
        callScript = "Good day! Calling from Praneeth Properties with an invitation to preview our upcoming lakeview villas.",
        callingHours = "10:00 AM - 06:30 PM IST",
        retrySettings = "Max 2 retries (4 hours gap)",
        maxConcurrentCalls = 8,
        totalContacts = 5000,
        callsStarted = 1420,
        connected = 1084,
        noAnswer = 336,
        qualified = 412,
        appointments = 86,
        isRunning = true
      ),
      Campaign(
        id = "camp-2",
        name = "Webinar Attendee Follow-Up",
        aiEmployeeId = "emp-2",
        aiEmployeeName = "Praneeth Support AI",
        objective = "Check feedback on voice agent demo & schedule 1-on-1 architecture call.",
        callScript = "Hi there! Praneeth AI team following up on yesterday's live session...",
        callingHours = "11:00 AM - 07:00 PM IST",
        retrySettings = "1 retry",
        maxConcurrentCalls = 4,
        totalContacts = 850,
        callsStarted = 850,
        connected = 740,
        noAnswer = 110,
        qualified = 210,
        appointments = 64,
        isRunning = false
      )
    )
  )
  val campaigns: StateFlow<List<Campaign>> = _campaigns.asStateFlow()

  fun addCampaign(campaign: Campaign) {
    _campaigns.value = listOf(campaign) + _campaigns.value
  }

  fun toggleCampaignStatus(id: String) {
    _campaigns.value = _campaigns.value.map {
      if (it.id == id) it.copy(isRunning = !it.isRunning) else it
    }
  }

  fun deleteCampaign(id: String) {
    _campaigns.value = _campaigns.value.filter { it.id != id }
  }

  // Phone Numbers
  private val _phoneNumbers = MutableStateFlow(
    listOf(
      PhoneNumber(
        id = "ph-1",
        number = "+91 89518 58777",
        assignedAi = "Praneeth Sales AI",
        type = "Mobile / Dedicated",
        status = "Active (Verified)",
        inbound = true,
        outbound = true,
        provider = "Telecom Cloud India"
      ),
      PhoneNumber(
        id = "ph-2",
        number = "+91 80 4012 9900",
        assignedAi = "Praneeth Support AI",
        type = "Bangalore Landline",
        status = "Active",
        inbound = true,
        outbound = true,
        provider = "Twilio SIP Trunk"
      ),
      PhoneNumber(
        id = "ph-3",
        number = "1800 209 8899",
        assignedAi = "Real Estate AI",
        type = "Toll-Free",
        status = "Demo Reserved",
        inbound = true,
        outbound = false,
        provider = "Cloud Telephony Demo"
      )
    )
  )
  val phoneNumbers: StateFlow<List<PhoneNumber>> = _phoneNumbers.asStateFlow()

  fun addPhoneNumber(number: PhoneNumber) {
    _phoneNumbers.value = _phoneNumbers.value + number
  }

  fun deletePhoneNumber(id: String) {
    _phoneNumbers.value = _phoneNumbers.value.filter { it.id != id }
  }

  // Knowledge Base
  private val _knowledgeDocs = MutableStateFlow(
    listOf(
      KnowledgeDocument(
        id = "doc-1",
        name = "Praneeth_Properties_Brochure_2026.pdf",
        type = "PDF",
        uploadDate = "Yesterday, 03:00 PM",
        status = "Indexed (48 pages)",
        usedByAiEmployees = listOf("Praneeth Sales AI", "Real Estate AI"),
        size = "4.6 MB",
        summary = "Comprehensive unit floor plans, price breakdowns, clubhouse amenities, and RERA approval numbers."
      ),
      KnowledgeDocument(
        id = "doc-2",
        name = "Frequently_Asked_Questions.docx",
        type = "DOCX",
        uploadDate = "3 days ago",
        status = "Indexed (120 Q&As)",
        usedByAiEmployees = listOf("Praneeth Sales AI", "Praneeth Support AI"),
        size = "850 KB",
        summary = "Standard customer objections, tax calculation, maintenance costs, and booking procedure."
      ),
      KnowledgeDocument(
        id = "doc-3",
        name = "https://praneeth.com/pricing-and-payment-plans",
        type = "Website URL",
        uploadDate = "1 week ago",
        status = "Synced Live",
        usedByAiEmployees = listOf("Praneeth Sales AI"),
        size = "Webpage",
        summary = "Real-time payment milestone schedule and bank loan tie-up list."
      ),
      KnowledgeDocument(
        id = "doc-4",
        name = "Spa_Therapies_Menu_and_Tariff.txt",
        type = "TXT",
        uploadDate = "2 weeks ago",
        status = "Indexed",
        usedByAiEmployees = listOf("Spa Booking AI"),
        size = "120 KB",
        summary = "Treatment durations, therapist specialities, organic herbal oils, and contraindications."
      )
    )
  )
  val knowledgeDocs: StateFlow<List<KnowledgeDocument>> = _knowledgeDocs.asStateFlow()

  fun addKnowledgeDoc(doc: KnowledgeDocument) {
    _knowledgeDocs.value = listOf(doc) + _knowledgeDocs.value
  }

  fun deleteKnowledgeDoc(id: String) {
    _knowledgeDocs.value = _knowledgeDocs.value.filter { it.id != id }
  }

  // Integrations
  private val _integrations = MutableStateFlow(
    listOf(
      IntegrationItem("int-1", "WhatsApp Business", "Messaging", "Send automated brochures, site visit passes, and qualification follow-ups.", isConnected = true, endpointOrApiKey = "+91 89518 58777 (Meta Cloud API)"),
      IntegrationItem("int-2", "Twilio / Indian Telecom", "Telephony", "SIP Trunking, high concurrency call routing, and phone number provisioning.", isConnected = true, endpointOrApiKey = "AC987...b52a"),
      IntegrationItem("int-3", "Google Calendar", "Scheduling", "Directly book site visits and meeting slots without double booking.", isConnected = true, endpointOrApiKey = "praneethangel777@gmail.com"),
      IntegrationItem("int-4", "Webhooks (Generic)", "Developer API", "Dispatch real-time JSON payloads for call finished, lead qualified, and sentiment alerts.", isConnected = true, endpointOrApiKey = "https://api.yourbusiness.com/voice-events"),
      IntegrationItem("int-5", "Google Sheets", "Productivity", "Auto-sync caller logs, phone numbers, and qualification notes to real-time spreadsheets.", isConnected = true, endpointOrApiKey = "Praneeth_AI_Employee_Leads_2026"),
      IntegrationItem("int-6", "Meta Lead Ads", "Advertising", "Instantly trigger outbound voice qualification within 60 seconds of form submission.", isConnected = false),
      IntegrationItem("int-7", "Google Forms", "Forms", "Trigger AI voice interviews from submitted inquiry questionnaires.", isConnected = false),
      IntegrationItem("int-8", "HubSpot / CRM", "CRM", "Bi-directional sync of lead stages, deal values, and call recordings.", isConnected = false),
      IntegrationItem("int-9", "SendGrid Email", "Email", "Send branded PDF proposals and site visit confirmations instantly.", isConnected = true, endpointOrApiKey = "SG.voice_alerts..."),
      IntegrationItem("int-10", "SMS Gateway (DLT)", "SMS", "Indian DLT compliant SMS templates for missed calls and appointment reminders.", isConnected = true, endpointOrApiKey = "PRANTH-SMS")
    )
  )
  val integrations: StateFlow<List<IntegrationItem>> = _integrations.asStateFlow()

  fun toggleIntegration(id: String) {
    _integrations.value = _integrations.value.map {
      if (it.id == id) it.copy(isConnected = !it.isConnected) else it
    }
  }

  fun updateIntegrationKey(id: String, key: String) {
    _integrations.value = _integrations.value.map {
      if (it.id == id) it.copy(endpointOrApiKey = key, isConnected = true) else it
    }
  }

  // Notifications
  private val _notifications = MutableStateFlow(
    listOf(
      NotificationItem("n-1", "New Qualified Lead", "Tejeshwar Reddy was qualified for ₹1.5 Cr 3BHK flat in Hyderabad.", "lead", "5m ago"),
      NotificationItem("n-2", "Appointment Booked", "Site visit scheduled for Saturday 11:00 AM with Tejeshwar Reddy.", "appointment", "15m ago"),
      NotificationItem("n-3", "Campaign Milestone", "Hyderabad Luxury Villa Outreach reached 1,420 calls completed.", "campaign", "1h ago"),
      NotificationItem("n-4", "Missed Call Handled", "Instant SMS fallback sent to +91 94455 66778.", "call", "2h ago"),
      NotificationItem("n-5", "Knowledge Base Synced", "Brochure PDF was fully indexed into vector embeddings.", "info", "5h ago")
    )
  )
  val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

  fun markAllNotificationsRead() {
    _notifications.value = _notifications.value.map { it.copy(isRead = true) }
  }

  fun clearNotifications() {
    _notifications.value = emptyList()
  }

  // Team Members
  private val _teamMembers = MutableStateFlow(
    listOf(
      TeamMember("tm-1", "Praneeth Kumar", "praneethangel777@gmail.com", "Owner", true),
      TeamMember("tm-2", "Rahul Sharma", "rahul.s@praneeth.com", "Admin", true),
      TeamMember("tm-3", "Priya Verma", "priya.v@praneeth.com", "Manager", true),
      TeamMember("tm-4", "Suresh Naidu", "suresh.n@praneeth.com", "Agent", true)
    )
  )
  val teamMembers: StateFlow<List<TeamMember>> = _teamMembers.asStateFlow()

  fun addTeamMember(member: TeamMember) {
    _teamMembers.value = _teamMembers.value + member
  }

  fun deleteTeamMember(id: String) {
    _teamMembers.value = _teamMembers.value.filter { it.id != id }
  }
}
