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
import androidx.compose.ui.platform.LocalContext
import com.example.todoapp.R
import java.util.*

@Composable
internal fun DeadlineItem(
    deadline: LocalDate?,
    onChanged: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    var dialogOpened by remember { mutableStateOf(false) }
    val formattedDate = deadline?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""

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
                    if (deadline != null) {
                        onChanged(null)
                    } else {
                        dialogOpened = true
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = if (formattedDate.isNotEmpty()) "Selected Date: $formattedDate" else "Select Date",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Switch(
            checked = deadline != null,
            onCheckedChange = { isChecked ->
                onClick()
                if (!isChecked) {
                    onChanged(null)
                } else {
                    dialogOpened = true
                }
            }
        )
    }

    if (dialogOpened) {
        showDatePickerDialog(LocalContext.current, deadline) { selectedDate ->
            onChanged(selectedDate)
            dialogOpened = false
        }
    }
}

private fun showDatePickerDialog(
    context: Context,
    initialDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val calendar = Calendar.getInstance()
    initialDate?.let {
        calendar.set(Calendar.YEAR, it.year)
        calendar.set(Calendar.MONTH, it.monthValue - 1)
        calendar.set(Calendar.DAY_OF_MONTH, it.dayOfMonth)
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            val selectedDate = selectedCalendar.time.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.show()
}
