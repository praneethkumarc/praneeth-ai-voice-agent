package com.example.ui.components

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockRepository
import com.example.model.NotificationItem
import com.example.ui.theme.*

enum class AppDestination(
  val title: String,
  val icon: ImageVector,
  val selectedIcon: ImageVector
) {
  DASHBOARD("Dashboard", Icons.Outlined.Dashboard, Icons.Filled.Dashboard),
  AI_EMPLOYEES("AI Employees", Icons.Outlined.SmartToy, Icons.Filled.SmartToy),
  CALLS("Calls", Icons.Outlined.Call, Icons.Filled.Call),
  LIVE_MONITOR("Live Monitor", Icons.Outlined.GraphicEq, Icons.Filled.GraphicEq),
  CONTACTS("Contacts", Icons.Outlined.People, Icons.Filled.People),
  LEADS("Leads Pipeline", Icons.Outlined.FilterList, Icons.Filled.FilterList),
  CAMPAIGNS("Campaigns", Icons.Outlined.Campaign, Icons.Filled.Campaign),
  PHONE_NUMBERS("Phone Numbers", Icons.Outlined.PhoneInTalk, Icons.Filled.PhoneInTalk),
  KNOWLEDGE_BASE("Knowledge Base", Icons.Outlined.LibraryBooks, Icons.Filled.LibraryBooks),
  INTEGRATIONS("Integrations", Icons.Outlined.Extension, Icons.Filled.Extension),
  ANALYTICS("Analytics", Icons.Outlined.BarChart, Icons.Filled.BarChart),
  SETTINGS("Settings", Icons.Outlined.Settings, Icons.Filled.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
  currentDestination: AppDestination,
  onOpenDrawer: () -> Unit,
  onOpenSearch: () -> Unit,
  onOpenNotifications: () -> Unit,
  onOpenProfile: () -> Unit,
  modifier: Modifier = Modifier
) {
  val userProfile by MockRepository.userProfile.collectAsState()
  val notifications by MockRepository.notifications.collectAsState()
  val unreadCount = notifications.count { !it.isRead }

  TopAppBar(
    modifier = modifier.testTag("app_top_bar"),
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(BrandPrimary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.SmartToy,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
        Column {
          Text(
            text = userProfile.businessName,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = currentDestination.title,
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            )
          )
        }
      }
    },
    navigationIcon = {
      IconButton(
        onClick = onOpenDrawer,
        modifier = Modifier.testTag("open_drawer_button")
      ) {
        Icon(Icons.Filled.Menu, contentDescription = "Open Navigation Menu")
      }
    },
    actions = {
      IconButton(
        onClick = onOpenSearch,
        modifier = Modifier.testTag("global_search_button")
      ) {
        Icon(Icons.Outlined.Search, contentDescription = "Search Everything")
      }

      IconButton(
        onClick = onOpenNotifications,
        modifier = Modifier.testTag("notifications_button")
      ) {
        BadgedBox(
          badge = {
            if (unreadCount > 0) {
              Badge(
                containerColor = AccentRose,
                contentColor = Color.White
              ) {
                Text("$unreadCount")
              }
            }
          }
        ) {
          Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
        }
      }

      // User Profile Avatar
      Box(
        modifier = Modifier
          .padding(end = 12.dp)
          .size(34.dp)
          .clip(CircleShape)
          .background(BrandPrimary.copy(alpha = 0.15f))
          .border(1.5.dp, BrandPrimary.copy(alpha = 0.5f), CircleShape)
          .clickable { onOpenProfile() }
          .testTag("user_avatar_button"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "PK",
          style = MaterialTheme.typography.labelMedium.copy(
            color = BrandPrimary,
            fontWeight = FontWeight.Bold
          )
        )
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface,
      titleContentColor = MaterialTheme.colorScheme.onSurface
    )
  )
}

@Composable
fun AppDrawerSheet(
  currentDestination: AppDestination,
  onSelectDestination: (AppDestination) -> Unit,
  onCloseDrawer: () -> Unit,
  onOpenHelp: () -> Unit,
  modifier: Modifier = Modifier
) {
  val userProfile by MockRepository.userProfile.collectAsState()

  ModalDrawerSheet(
    modifier = modifier.width(300.dp),
    drawerContainerColor = MaterialTheme.colorScheme.surface,
    drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxHeight()
        .padding(16.dp)
    ) {
      // Header Branding
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(BrandPrimary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Headphones,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }
        Column {
          Text(
            text = "Praneeth AI",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
          )
          Text(
            text = "Voice Agent OS",
            style = MaterialTheme.typography.labelSmall.copy(
              color = BrandPrimary,
              fontWeight = FontWeight.SemiBold
            )
          )
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

      Spacer(modifier = Modifier.height(12.dp))

      // Navigation Items List
      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        items(AppDestination.values()) { dest ->
          val selected = currentDestination == dest
          NavigationDrawerItem(
            label = {
              Text(
                text = dest.title,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
              )
            },
            selected = selected,
            onClick = {
              onSelectDestination(dest)
              onCloseDrawer()
            },
            icon = {
              Icon(
                imageVector = if (selected) dest.selectedIcon else dest.icon,
                contentDescription = dest.title,
                tint = if (selected) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            },
            colors = NavigationDrawerItemDefaults.colors(
              selectedContainerColor = BrandPrimary.copy(alpha = 0.12f),
              selectedTextColor = BrandPrimary,
              unselectedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("nav_item_${dest.name.lowercase()}")
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
      Spacer(modifier = Modifier.height(8.dp))

      // Bottom Section: Help & Account Profile
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .clickable { onOpenHelp() }
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Icon(Icons.Outlined.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
          text = "Help & Documentation",
          style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
        )
      }

      // User account item
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .clickable {
            onSelectDestination(AppDestination.SETTINGS)
            onCloseDrawer()
          }
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(BrandPrimary.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Text("PK", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
        }
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = userProfile.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "Owner • ${userProfile.phone}",
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            maxLines = 1
          )
        }
      }
    }
  }
}

@Composable
fun GlobalSearchDialog(
  onDismiss: () -> Unit,
  onNavigateToDestination: (AppDestination) -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  val aiEmployees by MockRepository.aiEmployees.collectAsState()
  val contacts by MockRepository.contacts.collectAsState()
  val calls by MockRepository.calls.collectAsState()
  val campaigns by MockRepository.campaigns.collectAsState()

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(onClick = onDismiss) { Text("Close") }
    },
    title = {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search AI, contacts, calls, leads...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
          }
        },
        modifier = Modifier.fillMaxWidth().testTag("global_search_input"),
        singleLine = true
      )
    },
    text = {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 380.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val q = searchQuery.trim().lowercase()

        // Filtered AI Employees
        val matchingEmployees = if (q.isEmpty()) emptyList() else aiEmployees.filter {
          it.name.lowercase().contains(q) || it.industry.lowercase().contains(q)
        }
        if (matchingEmployees.isNotEmpty()) {
          item {
            Text(
              "AI EMPLOYEES",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrandPrimary)
            )
          }
          items(matchingEmployees) { emp ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onNavigateToDestination(AppDestination.AI_EMPLOYEES)
                  onDismiss()
                },
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(Icons.Default.SmartToy, contentDescription = null, tint = BrandPrimary)
                Column {
                  Text(emp.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                  Text(emp.industry, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }
            }
          }
        }

        // Filtered Contacts
        val matchingContacts = if (q.isEmpty()) emptyList() else contacts.filter {
          it.name.lowercase().contains(q) || it.phone.contains(q) || it.company.lowercase().contains(q)
        }
        if (matchingContacts.isNotEmpty()) {
          item {
            Text(
              "CONTACTS & CRM",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrandPrimary)
            )
          }
          items(matchingContacts) { c ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onNavigateToDestination(AppDestination.CONTACTS)
                  onDismiss()
                },
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = BrandSecondary)
                Column {
                  Text(c.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                  Text("${c.phone} • ${c.company}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }
            }
          }
        }

        // Filtered Calls
        val matchingCalls = if (q.isEmpty()) emptyList() else calls.filter {
          it.customerName.lowercase().contains(q) || it.customerPhone.contains(q) || it.aiSummary.lowercase().contains(q)
        }
        if (matchingCalls.isNotEmpty()) {
          item {
            Text(
              "CALL LOGS & TRANSCRIPTS",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrandPrimary)
            )
          }
          items(matchingCalls) { call ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onNavigateToDestination(AppDestination.CALLS)
                  onDismiss()
                },
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(Icons.Default.Call, contentDescription = null, tint = BrandTertiary)
                Column {
                  Text(call.customerName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                  Text(call.aiSummary, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }
            }
          }
        }

        if (q.isNotEmpty() && matchingEmployees.isEmpty() && matchingContacts.isEmpty() && matchingCalls.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              contentAlignment = Alignment.Center
            ) {
              Text("No matching records found for \"$searchQuery\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        } else if (q.isEmpty()) {
          item {
            Text(
              "Type to search across AI Employees, CRM Contacts, Calls, and Campaigns.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  )
}

@Composable
fun NotificationsDialog(
  onDismiss: () -> Unit
) {
  val notifications by MockRepository.notifications.collectAsState()

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(onClick = onDismiss) { Text("Done") }
    },
    dismissButton = {
      TextButton(
        onClick = {
          MockRepository.markAllNotificationsRead()
        }
      ) {
        Text("Mark All Read")
      }
    },
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Notifications (${notifications.size})", fontWeight = FontWeight.Bold)
      }
    },
    text = {
      if (notifications.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("No notifications", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 360.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(notifications) { notif ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(
                containerColor = if (notif.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else Color(0xFFEEF2FF)
              ),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = when (notif.type) {
                      "lead" -> Icons.Default.PersonAdd
                      "appointment" -> Icons.Default.Event
                      "campaign" -> Icons.Default.Campaign
                      else -> Icons.Default.Notifications
                    },
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(16.dp)
                  )
                }

                Column(modifier = Modifier.weight(1f)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(notif.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(notif.timeAgo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(notif.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }
            }
          }
        }
      }
    }
  )
}
