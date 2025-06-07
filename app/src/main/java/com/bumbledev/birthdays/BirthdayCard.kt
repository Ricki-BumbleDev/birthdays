package com.bumbledev.birthdays

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatters {
    fun getDateWithoutYearFormatter(): DateTimeFormatter {
        return when (Locale.getDefault().language) {
            "de" -> DateTimeFormatter.ofPattern("d. MMMM", Locale.getDefault())
            else -> DateTimeFormatter.ofPattern("MMMM d", Locale.getDefault())
        }
    }
    
    fun getDateWithYearFormatter(): DateTimeFormatter {
        return when (Locale.getDefault().language) {
            "de" -> DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.getDefault())
            else -> DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
        }
    }
}

@Composable
fun BirthdayCard(birthday: Birthday) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = birthday.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                val dateText = if (birthday.dateWithYear != null) {
                    birthday.dateWithYear.format(DateFormatters.getDateWithYearFormatter())
                } else {
                    birthday.dateWithoutYear.format(DateFormatters.getDateWithoutYearFormatter())
                }

                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                val daysText = when (birthday.daysUntil) {
                    0 -> stringResource(R.string.birthday_today_celebration)
                    1 -> stringResource(R.string.birthday_tomorrow)
                    else -> context.getString(R.string.birthday_in_days, birthday.daysUntil)
                }

                Text(
                    text = daysText,
                    style = MaterialTheme.typography.labelLarge,
                    color = when (birthday.daysUntil) {
                        0 -> MaterialTheme.colorScheme.primary
                        1 -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = if (birthday.daysUntil <= 1) FontWeight.Bold else FontWeight.Normal
                )
            }

            birthday.nextAge?.let { age ->
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = age.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}