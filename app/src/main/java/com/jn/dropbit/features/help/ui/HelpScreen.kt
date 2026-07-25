package com.jn.dropbit.features.help.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.dropbit.features.common.ui.DropbitScreen
import com.jn.dropbit.features.common.ui.HeaderBar
import com.jn.dropbit.features.help.HelpState
import com.jn.dropbit.features.help.HelpViewModel
import com.jn.dropbit.ui.theme.DropbitTheme
import com.jn.dropbit.ui.theme.NeonGreen
import com.jn.dropbit.ui.theme.ensureContrast

@Composable
fun HelpScreen(viewModel: HelpViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    HelpScreenContent(state = state, onBack = onBack)
}

@Composable
fun HelpScreenContent(state: HelpState, onBack: () -> Unit) {
    DropbitScreen {
        HeaderBar(
            coins = state.coins,
            title = "HELP & GUIDE",
            onBackClick = onBack
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp),
        ) {
            Spacer(Modifier.height(32.dp))

            Text(
                "HOW TO PLAY",
                color = NeonGreen.ensureContrast(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            state.instructions.forEach { instruction ->
                Text(instruction, color = Color.White.copy(alpha = 0.9f))
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpScreenPreview() {
    DropbitTheme {
        HelpScreenContent(state = HelpState()) {
            // onBack
        }
    }
}
