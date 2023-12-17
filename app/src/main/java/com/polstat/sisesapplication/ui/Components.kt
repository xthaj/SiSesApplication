package com.polstat.sisesapplication.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.polstat.sisesapplication.model.MeetingAttendee
import com.polstat.sisesapplication.model.User

@Composable
fun MeetingCard(
    title: String,
    meetingDate: String,
    ruang: String,
    meetingSummary: String?,
    modifier: Modifier = Modifier,
    options: @Composable (() -> Unit) = {}
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
        Card(
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.size(25.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = null
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options()
                        }
                    }
                }

                Text(
                    text = "Date: $meetingDate"
                )

                Text(
                    text = "Room: $ruang"
                )

                meetingSummary?.let {
                    Text(
                        text = "Summary: $it"
                    )
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    options: @Composable (() -> Unit) = {}
) {
    Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = user.nama,
                        fontWeight = FontWeight.Bold
                    )
                }

                user.kelas?.let { Text(text = "Kelas: $it") }
                user.divisi?.let { Text(text = "Divisi: $it") }

                // Buttons from the options lambda
                options()
            }
        }
    }
}

@Composable
fun AttendeeCard(
    attendee: MeetingAttendee,
    options: @Composable (() -> Unit) = {}
) {
    Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = attendee.name,
                        fontWeight = FontWeight.Bold
                    )
                }

                attendee.divisi?.let { Text(text = "Divisi: $it") }
                attendee.time?.let { Text(text = "Waktu absen: $it") }

                // Buttons from the options lambda
                options()
            }
        }
    }
}

