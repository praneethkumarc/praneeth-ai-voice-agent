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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockRepository
import com.example.model.AiEmployee
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.OwnerBrandingFooter
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun AiEmployeesScreen(
  onOpenCreateWizard: () -> Unit,
  onOpenEmployeeDetails: (AiEmployee) -> Unit,
  onTestEmployee: (AiEmployee) -> Unit,
  modifier: Modifier = Modifier
) {
  val employees by MockRepository.aiEmployees.collectAsState()
  var employeeToEdit by remember { mutableStateOf<AiEmployee?>(null) }
  var deleteConfirmationId by remember { mutableStateOf<String?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    DemoModeBanner()

    // Header & Actions
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "AI Employees",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Configure autonomous voice agents & persona instructions",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }

      Button(
        onClick = onOpenCreateWizard,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
        modifier = Modifier.testTag("create_ai_employee_button")
      ) {
        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Create AI Employee", fontWeight = FontWeight.Bold)
      }
    }

    // List of AI Employees
    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      items(employees, key = { it.id }) { employee ->
        AiEmployeeCard(
          employee = employee,
          onOpen = { onOpenEmployeeDetails(employee) },
          onEdit = { employeeToEdit = employee },
          onDuplicate = { MockRepository.duplicateAiEmployee(employee.id) },
          onTogglePause = { MockRepository.toggleAiEmployeeStatus(employee.id) },
          onDelete = { deleteConfirmationId = employee.id },
          onTest = { onTestEmployee(employee) }
        )
      }
    }

    OwnerBrandingFooter()
  }

  // Delete Confirmation Dialog
  if (deleteConfirmationId != null) {
    AlertDialog(
      onDismissRequest = { deleteConfirmationId = null },
      title = { Text("Delete AI Employee?") },
      text = { Text("This will permanently remove this voice agent, its phone assignments, and active call routing.") },
      confirmButton = {
        Button(
          onClick = {
            deleteConfirmationId?.let { MockRepository.deleteAiEmployee(it) }
            deleteConfirmationId = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = AccentRose)
        ) {
          Text("Delete Agent")
        }
      },
      dismissButton = {
        TextButton(onClick = { deleteConfirmationId = null }) { Text("Cancel") }
      }
    )
  }

  // Edit Dialog
  if (employeeToEdit != null) {
    val emp = employeeToEdit!!
    var editedName by remember { mutableStateOf(emp.name) }
    var editedIndustry by remember { mutableStateOf(emp.industry) }
    var editedPersonality by remember { mutableStateOf(emp.personality) }
    var editedGreeting by remember { mutableStateOf(emp.greeting) }

    AlertDialog(
      onDismissRequest = { employeeToEdit = null },
      title = { Text("Edit ${emp.name}", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = editedName,
            onValueChange = { editedName = it },
            label = { Text("Agent Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = editedIndustry,
            onValueChange = { editedIndustry = it },
            label = { Text("Industry") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = editedPersonality,
            onValueChange = { editedPersonality = it },
            label = { Text("Personality Tone") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = editedGreeting,
            onValueChange = { editedGreeting = it },
            label = { Text("Spoken Greeting") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            MockRepository.updateAiEmployee(
              emp.copy(
                name = editedName,
                industry = editedIndustry,
                personality = editedPersonality,
                greeting = editedGreeting
              )
            )
            employeeToEdit = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
        ) {
          Text("Save Changes")
        }
      },
      dismissButton = {
        TextButton(onClick = { employeeToEdit = null }) { Text("Cancel") }
      }
    )
  }
}

@Composable
fun AiEmployeeCard(
  employee: AiEmployee,
  onOpen: () -> Unit,
  onEdit: () -> Unit,
  onDuplicate: () -> Unit,
  onTogglePause: () -> Unit,
  onDelete: () -> Unit,
  onTest: () -> Unit,
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("ai_employee_card_${employee.id}"),
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
      // Top Row: Avatar, Name, Industry, Status, Menu
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(if (employee.isActive) BrandPrimary.copy(alpha = 0.15f) else Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Filled.Headphones,
              contentDescription = null,
              tint = if (employee.isActive) BrandPrimary else Slate500,
              modifier = Modifier.size(24.dp)
            )
          }

          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = employee.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              StatusBadge(
                text = if (employee.isActive) "Active" else "Paused",
                color = if (employee.isActive) StatusActive else StatusInactive,
                backgroundColor = if (employee.isActive) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
              )
            }
            Text(
              text = "${employee.industry} • ${employee.language} (${employee.voiceGender})",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }
        }

        Box {
          IconButton(onClick = { menuExpanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More actions")
          }
          DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
          ) {
            DropdownMenuItem(
              text = { Text("Open Configuration") },
              onClick = {
                menuExpanded = false
                onOpen()
              },
              leadingIcon = { Icon(Icons.Outlined.Visibility, contentDescription = null) }
            )
            DropdownMenuItem(
              text = { Text("Edit Persona") },
              onClick = {
                menuExpanded = false
                onEdit()
              },
              leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) }
            )
            DropdownMenuItem(
              text = { Text("Duplicate Agent") },
              onClick = {
                menuExpanded = false
                onDuplicate()
              },
              leadingIcon = { Icon(Icons.Outlined.ContentCopy, contentDescription = null) }
            )
            DropdownMenuItem(
              text = { Text(if (employee.isActive) "Pause Calling" else "Resume Calling") },
              onClick = {
                menuExpanded = false
                onTogglePause()
              },
              leadingIcon = {
                Icon(
                  if (employee.isActive) Icons.Outlined.PauseCircle else Icons.Outlined.PlayCircle,
                  contentDescription = null
                )
              }
            )
            HorizontalDivider()
            DropdownMenuItem(
              text = { Text("Delete Agent", color = AccentRose) },
              onClick = {
                menuExpanded = false
                onDelete()
              },
              leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = AccentRose) }
            )
          }
        }
      }

      // Description snippet
      Text(
        text = employee.description,
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      // Metrics Strip: Calls handled, Qualified leads, Appointments
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        AgentStatMetric(label = "Calls Handled", value = "${employee.callsHandled}", icon = Icons.Outlined.Call)
        AgentStatMetric(label = "Qualified Leads", value = "${employee.qualifiedLeads}", icon = Icons.Outlined.Verified)
        AgentStatMetric(label = "Appointments", value = "${employee.appointments}", icon = Icons.Outlined.Event)
      }

      // Bottom Row: Phone Number & Test Voice Action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(Icons.Outlined.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = Slate500)
          Text(
            text = employee.phoneNumber,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = Slate700)
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(
            onClick = onOpen,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text("Open Details", fontSize = 12.sp)
          }

          Button(
            onClick = onTest,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Icon(Icons.Filled.Hearing, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Test Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun AgentStatMetric(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
    )
  }
}
