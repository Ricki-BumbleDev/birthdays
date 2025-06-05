package com.bumbledev.birthdays

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val notificationPreferences = remember { NotificationPreferences(context) }
    val notificationManager = remember { BirthdayNotificationManager(context) }
    val contactRepository = remember { ContactRepository(context) }
    
    var sameDayNotificationEnabled by remember { 
        mutableStateOf(notificationPreferences.sameDayEnabled) 
    }
    var threeDayAdvanceNotificationEnabled by remember { 
        mutableStateOf(notificationPreferences.threeDayEnabled) 
    }
    var sevenDayAdvanceNotificationEnabled by remember { 
        mutableStateOf(notificationPreferences.sevenDayEnabled) 
    }
    
    var pendingToggleType by remember { mutableStateOf<String?>(null) }
    var pendingToggleValue by remember { mutableStateOf(false) }
    
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    fun updateNotifications() {
        val settings = NotificationSettings(
            sameDayEnabled = sameDayNotificationEnabled,
            threeDayEnabled = threeDayAdvanceNotificationEnabled,
            sevenDayEnabled = sevenDayAdvanceNotificationEnabled
        )
        notificationPreferences.saveNotificationSettings(settings)
        
        if (hasNotificationPermission() || !settings.hasAnyEnabled()) {
            val birthdays = contactRepository.getBirthdays()
            notificationManager.scheduleNotifications(birthdays, settings)
        }
    }
    
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && pendingToggleType != null) {
            when (pendingToggleType) {
                "same_day" -> {
                    sameDayNotificationEnabled = pendingToggleValue
                    updateNotifications()
                }
                "three_day" -> {
                    threeDayAdvanceNotificationEnabled = pendingToggleValue
                    updateNotifications()
                }
                "seven_day" -> {
                    sevenDayAdvanceNotificationEnabled = pendingToggleValue
                    updateNotifications()
                }
            }
        }
        pendingToggleType = null
        pendingToggleValue = false
    }
    
    fun requestNotificationPermissionIfNeeded(toggleType: String, newValue: Boolean) {
        if (newValue && !hasNotificationPermission()) {
            pendingToggleType = toggleType
            pendingToggleValue = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            when (toggleType) {
                "same_day" -> {
                    sameDayNotificationEnabled = newValue
                    updateNotifications()
                }
                "three_day" -> {
                    threeDayAdvanceNotificationEnabled = newValue
                    updateNotifications()
                }
                "seven_day" -> {
                    sevenDayAdvanceNotificationEnabled = newValue
                    updateNotifications()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                            Text(
                                text = stringResource(R.string.birthday_notifications_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(R.string.birthday_notifications_description),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = sameDayNotificationEnabled,
                            onCheckedChange = { 
                                requestNotificationPermissionIfNeeded("same_day", it)
                            }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.three_day_notifications_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(R.string.three_day_notifications_description),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = threeDayAdvanceNotificationEnabled,
                            onCheckedChange = { 
                                requestNotificationPermissionIfNeeded("three_day", it)
                            }
                        )
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.seven_day_notifications_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(R.string.seven_day_notifications_description),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = sevenDayAdvanceNotificationEnabled,
                            onCheckedChange = { 
                                requestNotificationPermissionIfNeeded("seven_day", it)
                            }
                        )
                    }
                }
            }
        }
    }
}