package com.ghostly.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.outlined.ContactPage
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor

@Composable
fun DummyPage(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(48.dp)
            .padding(horizontal = 16.dp)
            .padding(bottom = 48.dp)
    ) {
        val richTextState = rememberRichTextState()
        RichTextEditor(
            modifier = Modifier.fillMaxSize(),
            state = richTextState,
        )

        val boldState = remember { mutableStateOf(false) }

        Row {
            IconToggleButton(
                checked = boldState.value,
                onCheckedChange = {
                    boldState.value = !boldState.value
                },
            ) {
                if (boldState.value) {
                    Icon(
                        imageVector = Icons.Filled.FormatBold,
                        contentDescription = "",
                        modifier = Modifier.background(Color.Gray)
                    )
                    richTextState.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
                }else{
                    Icon(
                        imageVector = Icons.Outlined.FormatBold,
                        contentDescription = ""
                    )
                }
            }
        }
    }
}