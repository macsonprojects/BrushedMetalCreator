package com.example.brushedmetalcreator.ui.feature_about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.brushedmetalcreator.ui.theme.BrushedMetalCreatorTheme
import com.example.brushedmetalcreator.ui.theme.BrushedMetalTheme

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BrushedMetalTheme.colors.background // Dark grey background
    ) {
        val uriHandler = LocalUriHandler.current
        val githubUrl = "https://github.com/macsonprojects"
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate Back",
                        tint = BrushedMetalTheme.colors.textPrimary
                    )
                }
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleLarge,
                    color = BrushedMetalTheme.colors.textPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Brushed Metal Creator",
                        style = MaterialTheme.typography.headlineMedium,
                        color = BrushedMetalTheme.colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Version 1.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrushedMetalTheme.colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = "A real-time engine for creating procedural brushed metal textures",
                        style = MaterialTheme.typography.bodyLarge,
                        color = BrushedMetalTheme.colors.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = "© 2026 MacsonProjects",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrushedMetalTheme.colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = "Licenced under the MIT Licence",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrushedMetalTheme.colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // GitHub Link
                    Text(
                        text = "GitHub Repository",
                        style = MaterialTheme.typography.labelLarge,
                        color = BrushedMetalTheme.colors.textHyperlink,
                        modifier = Modifier.clickable {
                            uriHandler.openUri(githubUrl)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    BrushedMetalCreatorTheme {
        AboutScreen(onNavigateBack = {})
    }
}