package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.FamilyEventEntity
import com.example.data.local.FamilyTaskEntity
import com.example.data.model.TaskStatus
import com.example.ui.FamilyViewModel

@Composable
fun CalendarTasksScreen(
    events: List<FamilyEventEntity>,
    tasks: List<FamilyTaskEntity>,
    viewModel: FamilyViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val showAddTaskSheet by viewModel.showAddTaskSheet.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("calendar_tasks_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Family Schedule & Tasks",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Shared calendar, countdowns & household chores",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        if (selectedTab == 1) {
                            Button(
                                onClick = { viewModel.setShowAddTaskSheet(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("add_task_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Task", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = Color(0xFF1E3A8A),
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Events & Dates (${events.size})", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Chores & To-Dos (${tasks.size})", fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }

            if (selectedTab == 0) {
                // Events List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(events, key = { it.id }) { event ->
                        EventItemCard(event = event)
                    }
                }
            } else {
                // Tasks List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskItemCard(
                            task = task,
                            onToggleStatus = { viewModel.toggleTaskStatus(task) }
                        )
                    }
                }
            }
        }

        if (showAddTaskSheet) {
            AddTaskDialog(
                onDismiss = { viewModel.setShowAddTaskSheet(false) },
                onAdd = { title, assignedTo, date ->
                    viewModel.addNewTask(title, assignedTo, date)
                }
            )
        }
    }
}

@Composable
private fun EventItemCard(event: FamilyEventEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        when (event.iconType) {
                            "cake" -> Color(0xFFFDE68A)
                            "celebration" -> Color(0xFFE9D5FF)
                            "medical" -> Color(0xFFFEE2E2)
                            else -> Color(0xFFDBEAFE)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (event.iconType) {
                        "cake" -> Icons.Default.Cake
                        "celebration" -> Icons.Default.Celebration
                        "medical" -> Icons.Default.LocalHospital
                        else -> Icons.Default.DirectionsCar
                    },
                    contentDescription = null,
                    tint = when (event.iconType) {
                        "cake" -> Color(0xFFB45309)
                        "celebration" -> Color(0xFF7E22CE)
                        "medical" -> Color(0xFFDC2626)
                        else -> Color(0xFF1D4ED8)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = event.dateStr,
                    fontSize = 13.sp,
                    color = Color(0xFF475569)
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFEFF6FF)
            ) {
                Text(
                    text = event.daysRemaining,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun TaskItemCard(
    task: FamilyTaskEntity,
    onToggleStatus: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleStatus),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleStatus,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (task.status == TaskStatus.COMPLETED)
                        Icons.Default.CheckCircle
                    else
                        Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Status",
                    tint = if (task.status == TaskStatus.COMPLETED)
                        Color(0xFF059669)
                    else
                        Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.status == TaskStatus.COMPLETED) Color(0xFF94A3B8) else Color(0xFF1E293B)
                )
                Text(
                    text = "Assigned to ${task.assignedToName} • Due: ${task.dueDate}",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (task.status) {
                    TaskStatus.COMPLETED -> Color(0xFFD1FAE5)
                    TaskStatus.IN_PROGRESS -> Color(0xFFFEF3C7)
                    TaskStatus.PENDING -> Color(0xFFF1F5F9)
                }
            ) {
                Text(
                    text = task.status.label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (task.status) {
                        TaskStatus.COMPLETED -> Color(0xFF065F46)
                        TaskStatus.IN_PROGRESS -> Color(0xFF92400E)
                        TaskStatus.PENDING -> Color(0xFF475569)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, assignedTo: String, dueDate: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var assignedTo by remember { mutableStateOf("Samiul") }
    var dueDate by remember { mutableStateOf("Tomorrow") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "New Family Task / Chore",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Description") },
                    placeholder = { Text("e.g. Pick up dry cleaning") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = assignedTo,
                    onValueChange = { assignedTo = it },
                    label = { Text("Assign To (e.g. Samiul, Dad, Mom)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date / Time") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = Color(0xFF475569))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onAdd(title.trim(), assignedTo.trim(), dueDate.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                        shape = RoundedCornerShape(10.dp),
                        enabled = title.isNotBlank()
                    ) {
                        Text("Create Task", color = Color.White)
                    }
                }
            }
        }
    }
}
