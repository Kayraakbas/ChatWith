package com.chatwith.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun HomeScreen(
    onChatStart: (String) -> Unit,
    onHistoryClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val interFont = FontFamily(Font(R.font.inter_regular))
    val characters = listOf(
        "Mustafa Kemal Atatürk" to "Leader",
        "Picasso" to "Artist",
        "Leonardo Da Vinci" to "Artist",
        "Kobra Murat" to "Philosopher",
        "Michael Jackson" to "Artist",
        "Jesus" to "Philosopher",
        "Steve Jobs" to "Philosopher"
    )
    val filters = listOf("All", "Artist", "Philosopher", "Actress", "Leader")
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 20.dp, vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable { onProfileClick() }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Chat With",
                fontFamily = interFont,
                fontSize = 22.sp,
                color = colors.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = onLogoutClick) {
                Text("Logout", color = colors.primary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = {
                Text("Search", fontFamily = interFont, color = colors.onSurface.copy(alpha = 0.6f))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    tint = colors.onSurface.copy(alpha = 0.4f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.surface,
                unfocusedContainerColor = colors.surface,
                disabledContainerColor = colors.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = colors.primary
            ),
            textStyle = LocalTextStyle.current.copy(color = colors.onSurface)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filters) { filter ->
                AssistChip(
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontFamily = interFont,
                            color = if (selectedFilter == filter) colors.primary else colors.onSurface
                        )
                    },
                    shape = RoundedCornerShape(50),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selectedFilter == filter) colors.primary.copy(alpha = 0.2f) else colors.surface
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(
                characters.filter { (name, category) ->
                    (selectedFilter == "All" || category == selectedFilter) &&
                            name.contains(searchText, ignoreCase = true)
                }
            ) { (name, _) ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clickable { onChatStart(name) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(
                                id = when (name) {
                                    "Mustafa Kemal Atatürk" -> R.drawable.`mustafa_kemal_ataturk`
                                    "Picasso" -> R.drawable.picasso
                                    "Leonardo Da Vinci" -> R.drawable.leonardo_da_vinci
                                    "Kobra Murat" -> R.drawable.kobra_murat
                                    "Michael Jackson" -> R.drawable.michael_jackson
                                    "Jesus" -> R.drawable.jesus
                                    "Steve Jobs" -> R.drawable.steve_jobs
                                    else -> R.drawable.placeholder_avatar
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = name, fontFamily = interFont, color = colors.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}