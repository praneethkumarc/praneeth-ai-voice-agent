package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.KnowledgeDocument
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.OwnerBrandingFooter
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun KnowledgeBaseScreen(
  modifier: Modifier = Modifier
) {
  val knowledgeDocs by MockRepository.knowledgeDocs.collectAsState()
  var selectedTab by remember { mutableStateOf("All") }
  var searchQuery by remember { mutableStateOf("") }
  var showAddFaqDialog by remember { mutableStateOf(false) }
  var showUploadDialog by remember { mutableStateOf(false) }

  val tabs = listOf("All", "PDF", "DOCX", "TXT", "Website URL", "FAQ")

  val filteredItems = knowledgeDocs.filter { item ->
    val matchesSearch = searchQuery.isBlank() ||
        item.name.contains(searchQuery, ignoreCase = true) ||
        item.summary.contains(searchQuery, ignoreCase = true)
    val matchesTab = if (selectedTab == "All") true else item.type.equals(selectedTab, ignoreCase = true)
    matchesSearch && matchesTab
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    DemoModeBanner()

    // Title and Add Buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Knowledge Base & RAG Index",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Documents, brochures & live FAQs feeding AI reasoning",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
          onClick = { showAddFaqDialog = true },
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Outlined.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add FAQ", fontSize = 12.sp)
        }

        Button(
          onClick = { showUploadDialog = true },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("upload_knowledge_button")
        ) {
          Icon(Icons.Filled.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Upload File", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Search
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("Search knowledge docs, FAQs and rules...") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("knowledge_search_input")
    )

    // Category Tabs
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      tabs.take(4).forEach { tab ->
        FilterChip(
          selected = selectedTab == tab,
          onClick = { selectedTab = tab },
          label = { Text(tab, fontSize = 12.sp) }
        )
      }
    }

    // Knowledge Items List
    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(filteredItems, key = { it.id }) { item ->
        KnowledgeDocCard(
          doc = item,
          onDelete = { MockRepository.deleteKnowledgeDoc(item.id) }
        )
      }
    }

    OwnerBrandingFooter()
  }

  // Upload Document Dialog
  if (showUploadDialog) {
    var title by remember { mutableStateOf("Hyderabad_Villas_Brochure.pdf") }
    var summary by remember { mutableStateOf("Clubhouse, payment plans, master plan and RERA documents.") }

    AlertDialog(
      onDismissRequest = { showUploadDialog = false },
      title = { Text("Upload Document to AI Memory", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Document File Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = summary,
            onValueChange = { summary = it },
            label = { Text("Summary / Extraction Scope") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
          )
          Text(
            text = "Supported: PDF, DOCX, TXT. Vector embeddings are auto-indexed into Gemini.",
            style = MaterialTheme.typography.labelSmall,
            color = Slate500
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (title.isNotBlank()) {
              MockRepository.addKnowledgeDoc(
                KnowledgeDocument(
                  id = UUID.randomUUID().toString(),
                  name = title.trim(),
                  type = "PDF",
                  uploadDate = "Just now",
                  status = "Indexed",
                  usedByAiEmployees = listOf("Praneeth Sales AI"),
                  size = "2.4 MB",
                  summary = summary.trim()
                )
              )
              showUploadDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
        ) {
          Text("Index Document")
        }
      },
      dismissButton = {
        TextButton(onClick = { showUploadDialog = false }) { Text("Cancel") }
      }
    )
  }

  // Add FAQ Dialog
  if (showAddFaqDialog) {
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddFaqDialog = false },
      title = { Text("Add Spoken Voice FAQ", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = question,
            onValueChange = { question = it },
            label = { Text("Customer Question") },
            placeholder = { Text("e.g. What are the possession dates?") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = answer,
            onValueChange = { answer = it },
            label = { Text("AI Spoken Answer") },
            placeholder = { Text("e.g. Phase 1 possession commences in December 2026.") },
            maxLines = 4,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (question.isNotBlank() && answer.isNotBlank()) {
              MockRepository.addKnowledgeDoc(
                KnowledgeDocument(
                  id = UUID.randomUUID().toString(),
                  name = question.trim(),
                  type = "FAQ",
                  uploadDate = "Just now",
                  status = "Indexed",
                  usedByAiEmployees = listOf("Praneeth Sales AI", "Real Estate AI"),
                  size = "Text",
                  summary = answer.trim()
                )
              )
              showAddFaqDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
        ) {
          Text("Save FAQ")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddFaqDialog = false }) { Text("Cancel") }
      }
    )
  }
}

@Composable
fun KnowledgeDocCard(
  doc: KnowledgeDocument,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
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
              .size(36.dp)
              .clip(CircleShape)
              .background(BrandPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = when (doc.type) {
                "PDF" -> Icons.Filled.PictureAsPdf
                "FAQ" -> Icons.Filled.QuestionAnswer
                "Website URL" -> Icons.Filled.Language
                else -> Icons.Filled.Description
              },
              contentDescription = null,
              tint = BrandPrimary,
              modifier = Modifier.size(18.dp)
            )
          }

          Column {
            Text(
              text = doc.name,
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
              maxLines = 1
            )
            Text(
              text = "${doc.type} • ${doc.size} • Uploaded ${doc.uploadDate}",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          StatusBadge(text = doc.status, color = StatusActive)
          IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AccentRose, modifier = Modifier.size(18.dp))
          }
        }
      }

      Text(
        text = doc.summary,
        style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
        maxLines = 2
      )

      if (doc.usedByAiEmployees.isNotEmpty()) {
        Text(
          text = "Used by: ${doc.usedByAiEmployees.joinToString(", ")}",
          style = MaterialTheme.typography.labelSmall.copy(color = BrandPrimary, fontSize = 10.sp)
        )
      }
    }
  }
}
