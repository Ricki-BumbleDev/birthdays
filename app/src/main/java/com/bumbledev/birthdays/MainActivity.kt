package com.bumbledev.birthdays

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumbledev.birthdays.ui.theme.BirthdaysTheme

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            initializeNotifications()
            // Permission granted, navigate to main screen
            setContent {
                BirthdaysTheme {
                    AppNavigation(hasPermission = true)
                }
            }
        } else {
            // Permission denied, show start screen with denial message
            setContent {
                BirthdaysTheme {
                    AppNavigation(hasPermission = false, permissionDenied = true)
                }
            }
        }
    }
    
    private fun initializeNotifications() {
        val notificationPreferences = NotificationPreferences(this)
        val notificationManager = BirthdayNotificationManager(this)
        val contactRepository = ContactRepository(this)
        
        val settings = notificationPreferences.getNotificationSettings()
        val birthdays = contactRepository.getBirthdays()
        notificationManager.scheduleNotifications(birthdays, settings)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hasContactsPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        if (hasContactsPermission) {
            initializeNotifications()
        }

        setContent {
            BirthdaysTheme {
                AppNavigation(
                    hasPermission = hasContactsPermission,
                    onRequestPermission = {
                        requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    }
                )
            }
        }
    }
}

