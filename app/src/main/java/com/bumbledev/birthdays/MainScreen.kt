package com.bumbledev.birthdays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    val birthdays = remember { ContactRepository.fetchBirthdays(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Birthdays") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (birthdays.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No contacts with birthdays found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(birthdays) { birthday ->
                   BirthdayCard(birthday)
                }
            }
        }
    }
}

//@Composable
//fun BirthdayCard(birthday: Birthday) {
//    Card(
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth()
//        ) {
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = birthday.name, fontWeight = FontWeight.Bold, fontSize = 20.sp
//                )
//                Spacer(Modifier.height(4.dp))
//                Row {
//                    val dateText = if (birthday.dateWithYear != null) birthday.dateWithYear.format(
//                        dateWithYearFormatter
//                    ) else birthday.dateWithoutYear.format(dateWithoutYearFormatter)
//                    Text(text = dateText)
//                    Spacer(modifier = Modifier.width(16.dp))
//
//                    val daysText = when (birthday.daysUntil) {
//                        0 -> "today"
//                        1 -> "tomorrow"
//                        else -> "in ${birthday.daysUntil} days"
//                    }
//                    Text(text = daysText)
//                }
//            }
//            birthday.nextAge?.let { age ->
//                Text(
//                    text = age.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold
//                )
//            }
//        }
//    }
//}
