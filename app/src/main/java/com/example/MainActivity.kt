package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.AiEmployee
import com.example.model.CallLog
import com.example.model.Lead
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        PraneethAiApp()
      }
    }
  }
}

@Composable
fun PraneethAiApp() {
  var isAuthenticated by remember { mutableStateOf(true) }
  var currentDestination by remember { mutableStateOf(AppDestination.DASHBOARD) }

  // Dialog & Overlay states
  var isSearchOpen by remember { mutableStateOf(false) }
  var isNotificationsOpen by remember { mutableStateOf(false) }
  var isCreateAiWizardOpen by remember { mutableStateOf(false) }
  var isTestAiDialogOpen by remember { mutableStateOf(false) }
  var testEmployeeTarget by remember { mutableStateOf<AiEmployee?>(null) }
  var selectedCallForDetails by remember { mutableStateOf<CallLog?>(null) }
  var helpDialogOpen by remember { mutableStateOf(false) }

  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  // Handle Android back button
  BackHandler(enabled = drawerState.isOpen || isCreateAiWizardOpen || currentDestination != AppDestination.DASHBOARD) {
    when {
      drawerState.isOpen -> scope.launch { drawerState.close() }
      isCreateAiWizardOpen -> isCreateAiWizardOpen = false
      currentDestination != AppDestination.DASHBOARD -> currentDestination = AppDestination.DASHBOARD
    }
  }

  if (!isAuthenticated) {
    AuthScreen(
      onAuthenticated = {
        isAuthenticated = true
      }
    )
    return
  }

  // Full Screen Wizard Overlay
  if (isCreateAiWizardOpen) {
    CreateEmployeeWizardScreen(
      onComplete = {
        isCreateAiWizardOpen = false
        currentDestination = AppDestination.AI_EMPLOYEES
      },
      onCancel = {
        isCreateAiWizardOpen = false
      }
    )
    return
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      AppDrawerSheet(
        currentDestination = currentDestination,
        onSelectDestination = { dest ->
          currentDestination = dest
        },
        onCloseDrawer = {
          scope.launch { drawerState.close() }
        },
        onOpenHelp = {
          helpDialogOpen = true
          scope.launch { drawerState.close() }
        }
      )
    }
  ) {
    Scaffold(
      topBar = {
        AppTopBar(
          currentDestination = currentDestination,
          onOpenDrawer = {
            scope.launch { drawerState.open() }
          },
          onOpenSearch = { isSearchOpen = true },
          onOpenNotifications = { isNotificationsOpen = true },
          onOpenProfile = { currentDestination = AppDestination.SETTINGS }
        )
      },
      contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (currentDestination) {
          AppDestination.DASHBOARD -> {
            DashboardScreen(
              onNavigateToDestination = { dest -> currentDestination = dest },
              onOpenCreateAiWizard = { isCreateAiWizardOpen = true },
              onOpenTestAiModal = {
                testEmployeeTarget = null
                isTestAiDialogOpen = true
              },
              onOpenCallDetails = { call ->
                selectedCallForDetails = call
              }
            )
          }

          AppDestination.AI_EMPLOYEES -> {
            AiEmployeesScreen(
              onOpenCreateWizard = { isCreateAiWizardOpen = true },
              onOpenEmployeeDetails = { emp ->
                testEmployeeTarget = emp
                isTestAiDialogOpen = true
              },
              onTestEmployee = { emp ->
                testEmployeeTarget = emp
                isTestAiDialogOpen = true
              }
            )
          }

          AppDestination.CALLS -> {
            CallsScreen(
              onSelectCall = { call ->
                selectedCallForDetails = call
              },
              selectedCallFromOutside = selectedCallForDetails
            )
          }

          AppDestination.LIVE_MONITOR -> {
            LiveCallMonitoringScreen()
          }

          AppDestination.CONTACTS -> {
            ContactsScreen()
          }

          AppDestination.LEADS -> {
            LeadsPipelineScreen(
              onCallLead = { lead ->
                testEmployeeTarget = null
                isTestAiDialogOpen = true
              }
            )
          }

          AppDestination.CAMPAIGNS -> {
            CampaignsScreen()
          }

          AppDestination.PHONE_NUMBERS -> {
            PhoneNumbersScreen()
          }

          AppDestination.KNOWLEDGE_BASE -> {
            KnowledgeBaseScreen()
          }

          AppDestination.INTEGRATIONS -> {
            IntegrationsScreen()
          }

          AppDestination.ANALYTICS -> {
            AnalyticsScreen()
          }

          AppDestination.SETTINGS -> {
            SettingsScreen(
              onLogout = {
                isAuthenticated = false
              }
            )
          }
        }
      }
    }
  }

  // Global Search Dialog
  if (isSearchOpen) {
    GlobalSearchDialog(
      onDismiss = { isSearchOpen = false },
      onNavigateToDestination = { dest ->
        currentDestination = dest
      }
    )
  }

  // Notifications Dialog
  if (isNotificationsOpen) {
    NotificationsDialog(
      onDismiss = { isNotificationsOpen = false }
    )
  }

  // Voice Test Simulator Dialog
  if (isTestAiDialogOpen) {
    TestAiVoiceDialog(
      initialEmployee = testEmployeeTarget,
      onDismiss = {
        isTestAiDialogOpen = false
        testEmployeeTarget = null
      }
    )
  }

  // Call Details Dialog
  if (selectedCallForDetails != null) {
    CallDetailsDialog(
      call = selectedCallForDetails!!,
      onDismiss = { selectedCallForDetails = null }
    )
  }

  // Help & Documentation Dialog
  if (helpDialogOpen) {
    AlertDialog(
      onDismissRequest = { helpDialogOpen = false },
      confirmButton = {
        TextButton(onClick = { helpDialogOpen = false }) { Text("Got It") }
      },
      title = { Text("Praneeth AI Employee Platform") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Owner & Architect: Praneeth Kumar")
          Text("Support Phone: 8951858777")
          Text("Documentation: https://docs.praneethai.com")
          Text("Telemetry & Voice Core: Gemini Flash Conversational Engine with Exotel/Twilio SIP Trunking.")
        }
      }
    )
  }
}
