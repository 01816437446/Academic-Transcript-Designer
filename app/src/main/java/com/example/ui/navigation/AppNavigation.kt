package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.ui.screens.AiPasteScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PreviewScreen
import com.example.ui.screens.EditTranscriptScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                onAddTranscript = { navController.navigate(Screen.EditTranscript()) },
                onAiPaste = { navController.navigate(Screen.AiPaste) },
                onEditTranscript = { id -> navController.navigate(Screen.EditTranscript(id)) },
                onPreviewTranscript = { id -> navController.navigate(Screen.PreviewTranscript(id)) }
            )
        }

        composable<Screen.AiPaste> {
            AiPasteScreen(
                onParsed = { },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Screen.EditTranscript> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.EditTranscript>()
            EditTranscriptScreen(
                transcriptId = route.transcriptId,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Screen.PreviewTranscript> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.PreviewTranscript>()
            PreviewScreen(
                transcriptId = route.transcriptId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
