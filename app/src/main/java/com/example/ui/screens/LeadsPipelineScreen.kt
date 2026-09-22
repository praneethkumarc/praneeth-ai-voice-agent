package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import com.example.model.Lead
import com.example.model.LeadStage
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.OwnerBrandingFooter
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun LeadsPipelineScreen(
  onCallLead: (Lead) -> Unit,
  modifier: Modifier = Modifier
) {
  val leads by MockRepository.leads.collectAsState()
  var selectedStageFilter by remember { mutableStateOf<LeadStage?>(null) }
  var selectedLeadForDetails by remember { mutableStateOf<Lead?>(null) }
  var isKanbanMode by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    DemoModeBanner()

    // Title & View Mode Switcher
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Leads Pipeline & Stages",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "AI-qualified prospects moving toward closed deals",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }

      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(10.dp)
      ) {
        Row(modifier = Modifier.padding(4.dp)) {
          IconButton(
            onClick = { isKanbanMode = false },
            modifier = Modifier
              .size(32.dp)
              .background(if (!isKanbanMode) BrandPrimary else Color.Transparent, RoundedCornerShape(6.dp))
          ) {
            Icon(
              Icons.Filled.List,
              contentDescription = "List View",
              tint = if (!isKanbanMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
          }
          IconButton(
            onClick = { isKanbanMode = true },
            modifier = Modifier
              .size(32.dp)
              .background(if (isKanbanMode) BrandPrimary else Color.Transparent, RoundedCornerShape(6.dp))
          ) {
            Icon(
              Icons.Filled.ViewKanban,
              contentDescription = "Kanban View",
              tint = if (isKanbanMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }

    // Stage Filter chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FilterChip(
        selected = selectedStageFilter == null,
        onClick = { selectedStageFilter = null },
        label = { Text("All (${leads.size})", fontSize = 12.sp) }
      )
      LeadStage.values().forEach { stage ->
        val count = leads.count { it.stage == stage }
        FilterChip(
          selected = selectedStageFilter == stage,
          onClick = { selectedStageFilter = stage },
          label = { Text("${stage.displayName} ($count)", fontSize = 12.sp) }
        )
      }
    }

    val displayedLeads = if (selectedStageFilter != null) {
      leads.filter { it.stage == selectedStageFilter }
    } else {
      leads
    }

    if (!isKanbanMode) {
      // List Mode
      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(displayedLeads, key = { it.id }) { lead ->
          LeadRowCard(
            lead = lead,
            onClick = { selectedLeadForDetails = lead },
            onCall = { onCallLead(lead) },
            onStageChange = { newStage ->
              MockRepository.updateLeadStage(lead.id, newStage)
            }
          )
        }
      }
    } else {
      // Kanban Board Mode
      Row(
        modifier = Modifier
          .weight(1f)
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        LeadStage.values().forEach { stage ->
          val stageLeads = leads.filter { it.stage == stage }
          KanbanColumn(
            stage = stage,
            leads = stageLeads,
            onLeadClick = { selectedLeadForDetails = it },
            onCallLead = onCallLead
          )
        }
      }
    }

    OwnerBrandingFooter()
  }

  // Lead Details Modal Dialog
  if (selectedLeadForDetails != null) {
    val lead = selectedLeadForDetails!!
    AlertDialog(
      onDismissRequest = { selectedLeadForDetails = null },
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(lead.customerName, fontWeight = FontWeight.Bold)
          LeadStageBadge(stage = lead.stage)
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text("Phone: ${lead.phone}", fontWeight = FontWeight.SemiBold)
          Text("Assigned AI: ${lead.aiEmployee}", style = MaterialTheme.typography.bodySmall, color = BrandPrimary)
          Text("Budget: ${lead.budget} • Requirement: ${lead.requirement}", style = MaterialTheme.typography.bodySmall)
          Text("Preferred Location: ${lead.location}", style = MaterialTheme.typography.bodySmall)
          Text("AI Lead Score: ${lead.leadScore}/100", fontWeight = FontWeight.Bold, color = StatusQualified)
          Text("Last Interaction: ${lead.lastCall}", style = MaterialTheme.typography.bodySmall, color = Slate600)

          HorizontalDivider()

          Text("Update Stage:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            LeadStage.values().forEach { s ->
              FilterChip(
                selected = lead.stage == s,
                onClick = {
                  MockRepository.updateLeadStage(lead.id, s)
                  selectedLeadForDetails = lead.copy(stage = s)
                },
                label = { Text(s.displayName, fontSize = 11.sp) }
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val target = selectedLeadForDetails
            selectedLeadForDetails = null
            if (target != null) onCallLead(target)
          },
          colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
        ) {
          Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Call Lead")
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedLeadForDetails = null }) { Text("Close") }
      }
    )
  }
}

@Composable
fun LeadRowCard(
  lead: Lead,
  onClick: () -> Unit,
  onCall: () -> Unit,
  onStageChange: (LeadStage) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("lead_card_${lead.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(BrandPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = lead.customerName.take(1).uppercase(),
              fontWeight = FontWeight.Bold,
              color = BrandPrimary
            )
          }

          Column {
            Text(
              text = lead.customerName,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "${lead.phone} • AI: ${lead.aiEmployee}",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }
        }

        LeadStageBadge(stage = lead.stage)
      }

      // Qualification Pill strip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("Budget: ${lead.budget}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
        Text(lead.requirement, style = MaterialTheme.typography.labelSmall.copy(color = BrandPrimary))
        Text("Score: ${lead.leadScore}", style = MaterialTheme.typography.labelSmall.copy(color = StatusActive, fontWeight = FontWeight.Bold))
      }

      // Bottom row with call button & quick stage select
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Last: ${lead.lastCall} • ${lead.location}",
          style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 10.sp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(
            onClick = onCall,
            modifier = Modifier.size(32.dp).clip(CircleShape).background(BrandPrimary.copy(alpha = 0.12f))
          ) {
            Icon(Icons.Filled.PhoneInTalk, contentDescription = "Call", tint = BrandPrimary, modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}

@Composable
fun KanbanColumn(
  stage: LeadStage,
  leads: List<Lead>,
  onLeadClick: (Lead) -> Unit,
  onCallLead: (Lead) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.width(280.dp).fillMaxHeight(),
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(stage.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        Surface(color = BrandPrimary.copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)) {
          Text(
            text = "${leads.size}",
            color = BrandPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          )
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(leads, key = { it.id }) { lead ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onLeadClick(lead) },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(lead.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text("Budget: ${lead.budget}", fontSize = 11.sp, color = Slate600)
              Text(lead.requirement, fontSize = 10.sp, color = BrandPrimary, maxLines = 1)
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Score: ${lead.leadScore}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusQualified)
                IconButton(onClick = { onCallLead(lead) }, modifier = Modifier.size(24.dp)) {
                  Icon(Icons.Filled.Call, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(14.dp))
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
fun LeadStageBadge(stage: LeadStage) {
  val (color, bg) = when (stage) {
    LeadStage.NEW -> Pair(BrandPrimary, Color(0xFFE0E7FF))
    LeadStage.CONTACTED -> Pair(AccentPurple, Color(0xFFF3E8FF))
    LeadStage.QUALIFIED -> Pair(StatusQualified, Color(0xFFDCFCE7))
    LeadStage.APPOINTMENT -> Pair(AccentAmber, Color(0xFFFEF3C7))
    LeadStage.NEGOTIATION -> Pair(BrandSecondary, Color(0xFFE0F2FE))
    LeadStage.WON -> Pair(Color(0xFF16A34A), Color(0xFFDCFCE7))
    LeadStage.LOST -> Pair(AccentRose, Color(0xFFFFE4E6))
  }

  StatusBadge(text = stage.displayName, color = color, backgroundColor = bg)
}
