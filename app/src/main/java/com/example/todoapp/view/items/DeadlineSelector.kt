package com.example.todoapp.view.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.todoapp.R
import java.time.Instant
import androidx.compose.material3.DatePickerDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import java.util.Date


@Composable
internal fun DeadlineItem(
    deadline: LocalDate?, // чтобы не передавать null сделать пустой state -> решил другим путём, чтобы не переписывать большую часть кода
    onChanged: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    var dialogOpened by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val formattedDate = deadline?.format(
        DateTimeFormatter.ofPattern("d MMMM yyyy", context.resources.configuration.locales[0])
    ) ?: ""

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.deadline),
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            TextButton(
                onClick = {
                    onClick()
                    dialogOpened = true
                },
                enabled = deadline != null,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                if (deadline != null) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Switch(
            checked = deadline != null,
            onCheckedChange = { isChecked ->
                onClick()
                if (!isChecked) {
                    onChanged(null)
                } else {
                    onChanged(LocalDate.now())
                }
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primaryContainer,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                checkedBorderColor = Color.Transparent,
                uncheckedThumbColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.outlineVariant,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }

    if (dialogOpened) {
        ComposeDatePickerDialog(
            initialDate = deadline ?: LocalDate.now(),
            onDateSelected = { selectedDate ->
                onChanged(selectedDate)
                dialogOpened = false
            },
            onDismiss = {
                dialogOpened = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComposeDatePickerDialog(
    initialDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate?.let {
            it.atStartOfDay(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli()
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        onDateSelected(selectedDate)
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                headlineContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                weekdayContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                navigationContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                yearContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                currentYearContentColor = MaterialTheme.colorScheme.primaryContainer,
                selectedYearContentColor = MaterialTheme.colorScheme.inverseOnSurface,
                selectedYearContainerColor = MaterialTheme.colorScheme.primaryContainer,
                dayContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedDayContentColor = MaterialTheme.colorScheme.inverseOnSurface,
                selectedDayContainerColor = MaterialTheme.colorScheme.primaryContainer,
                todayContentColor = MaterialTheme.colorScheme.primaryContainer,
                todayDateBorderColor = Color.Transparent,
                dividerColor = MaterialTheme.colorScheme.outline
            ),
            headline = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = datePickerState.selectedDateMillis?.let { millis ->
                            Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                                .format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
                        } ?: "",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        )
    }
}