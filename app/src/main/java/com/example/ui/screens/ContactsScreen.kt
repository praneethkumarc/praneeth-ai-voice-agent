package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.model.Contact
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.OwnerBrandingFooter
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun ContactsScreen(
  modifier: Modifier = Modifier
) {
  val contacts by MockRepository.contacts.collectAsState()
  var searchQuery by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }
  var contactToEdit by remember { mutableStateOf<Contact?>(null) }
  var selectedContactDetails by remember { mutableStateOf<Contact?>(null) }
  var snackbarMessage by remember { mutableStateOf<String?>(null) }

  val filteredContacts = contacts.filter { c ->
    searchQuery.isBlank() ||
        c.name.contains(searchQuery, ignoreCase = true) ||
        c.phone.contains(searchQuery) ||
        c.company.contains(searchQuery, ignoreCase = true) ||
        c.location.contains(searchQuery, ignoreCase = true) ||
        c.tags.any { it.contains(searchQuery, ignoreCase = true) }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    DemoModeBanner()

    // Title and Actions
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Contacts & CRM",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "${contacts.size} prospects, qualified leads & customers",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
          onClick = {
            MockRepository.importSampleCsv()
            snackbarMessage = "Imported 2 new contacts from CSV!"
          },
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Outlined.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Import CSV", fontSize = 12.sp)
        }

        Button(
          onClick = { showAddDialog = true },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("add_contact_button")
        ) {
          Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Contact", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Search input
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("Search by name, phone, company, or tags...") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear")
          }
        }
      },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("contacts_search_input")
    )

    if (snackbarMessage != null) {
      Surface(
        color = Color(0xFFDCFCE7),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(snackbarMessage ?: "", color = Color(0xFF166534), style = MaterialTheme.typography.bodySmall)
          IconButton(onClick = { snackbarMessage = null }, modifier = Modifier.size(20.dp)) {
            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(14.dp))
          }
        }
      }
    }

    // Contacts List
    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(filteredContacts, key = { it.id }) { contact ->
        ContactCard(
          contact = contact,
          onClick = { selectedContactDetails = contact },
          onEdit = { contactToEdit = contact },
          onDelete = { MockRepository.deleteContact(contact.id) }
        )
      }
    }

    OwnerBrandingFooter()
  }

  // Add Contact Dialog
  if (showAddDialog) {
    ContactFormDialog(
      title = "Add New Contact",
      initialContact = null,
      onDismiss = { showAddDialog = false },
      onSave = { newContact ->
        MockRepository.addContact(newContact)
        showAddDialog = false
      }
    )
  }

  // Edit Contact Dialog
  if (contactToEdit != null) {
    ContactFormDialog(
      title = "Edit Contact",
      initialContact = contactToEdit,
      onDismiss = { contactToEdit = null },
      onSave = { updated ->
        MockRepository.updateContact(updated)
        contactToEdit = null
      }
    )
  }

  // Contact Details Dialog
  if (selectedContactDetails != null) {
    val c = selectedContactDetails!!
    AlertDialog(
      onDismissRequest = { selectedContactDetails = null },
      confirmButton = {
        Button(onClick = { selectedContactDetails = null }) { Text("Close") }
      },
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(BrandPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = BrandPrimary)
          }
          Column {
            Text(c.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(c.company, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          DetailItem("Phone", c.phone)
          DetailItem("Email", c.email)
          DetailItem("Location", c.location)
          DetailItem("Acquisition Source", c.source)
          DetailItem("Lifecycle Status", c.status)
          DetailItem("Last AI Contact", c.lastContact)
          DetailItem("Tags", c.tags.joinToString(", "))
          DetailItem("Agent Notes", c.notes)
        }
      }
    )
  }
}

@Composable
fun ContactCard(
  contact: Contact,
  onClick: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("contact_item_${contact.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(BrandPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = contact.name.take(2).uppercase(),
              fontWeight = FontWeight.Bold,
              color = BrandPrimary,
              fontSize = 13.sp
            )
          }

          Column {
            Text(
              text = contact.name,
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "${contact.company} • ${contact.location}",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          StatusBadge(text = contact.status, color = BrandPrimary)
          Box {
            IconButton(onClick = { menuExpanded = true }) {
              Icon(Icons.Default.MoreVert, contentDescription = "Options")
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
              DropdownMenuItem(
                text = { Text("Edit Details") },
                onClick = {
                  menuExpanded = false
                  onEdit()
                },
                leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) }
              )
              DropdownMenuItem(
                text = { Text("Delete Contact", color = AccentRose) },
                onClick = {
                  menuExpanded = false
                  onDelete()
                },
                leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = AccentRose) }
              )
            }
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(Icons.Outlined.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = Slate500)
          Text(contact.phone, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
        }

        Text(
          text = "Last: ${contact.lastContact}",
          style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
        )
      }

      // Tags Row
      Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        contact.tags.forEach { tag ->
          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "#$tag",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun ContactFormDialog(
  title: String,
  initialContact: Contact?,
  onDismiss: () -> Unit,
  onSave: (Contact) -> Unit
) {
  var name by remember { mutableStateOf(initialContact?.name ?: "") }
  var phone by remember { mutableStateOf(initialContact?.phone ?: "+91 ") }
  var email by remember { mutableStateOf(initialContact?.email ?: "") }
  var company by remember { mutableStateOf(initialContact?.company ?: "") }
  var location by remember { mutableStateOf(initialContact?.location ?: "") }
  var source by remember { mutableStateOf(initialContact?.source ?: "Direct Entry") }
  var tagsText by remember { mutableStateOf(initialContact?.tags?.joinToString(", ") ?: "VIP, Lead") }
  var status by remember { mutableStateOf(initialContact?.status ?: "Lead") }
  var notes by remember { mutableStateOf(initialContact?.notes ?: "") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(title, fontWeight = FontWeight.Bold) },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Full Name *") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("contact_form_name")
        )
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Phone Number *") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("contact_form_phone")
        )
        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email Address") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = company,
          onValueChange = { company = it },
          label = { Text("Company / Org") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = location,
          onValueChange = { location = it },
          label = { Text("Location (City / State)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = tagsText,
          onValueChange = { tagsText = it },
          label = { Text("Tags (comma separated)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Notes / Conversation Background") },
          maxLines = 3,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank() && phone.isNotBlank()) {
            val contact = Contact(
              id = initialContact?.id ?: UUID.randomUUID().toString(),
              name = name.trim(),
              phone = phone.trim(),
              email = email.trim(),
              company = company.trim().ifEmpty { "Independent" },
              location = location.trim().ifEmpty { "India" },
              source = source,
              tags = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() },
              status = status,
              lastContact = initialContact?.lastContact ?: "Just added",
              notes = notes.trim()
            )
            onSave(contact)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
        modifier = Modifier.testTag("save_contact_submit")
      ) {
        Text("Save Contact")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

@Composable
private fun DetailItem(label: String, value: String) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(value.ifBlank { "—" }, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
  }
}
