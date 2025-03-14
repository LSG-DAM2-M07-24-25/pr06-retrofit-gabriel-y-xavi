package com.example.retrofitapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.retrofitapp.view.CompactView
import com.example.retrofitapp.view.ExpandedView
import com.example.retrofitapp.view.MediumView
import com.example.retrofitapp.viewmodel.APIViewModel
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

sealed class Screen(val route: String) {
    object CharacterList : Screen("characterList")
    object CharacterDetail : Screen("characterDetail/{characterId}") {
        fun createRoute(characterId: Int) = "characterDetail/$characterId"
    }
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: APIViewModel,
    windowSizeClass: WindowWidthSizeClass
) {
    NavHost(
        navController = navController,
        startDestination = Screen.CharacterList.route
    ) {
        composable(Screen.CharacterList.route) {
            when (windowSizeClass) {
                WindowWidthSizeClass.Compact -> {
                    CompactView(
                        viewModel = viewModel,
                        onCharacterClick = { character ->
                            viewModel.setSelectedCharacter(character)
                            if (windowSizeClass != WindowWidthSizeClass.Expanded) {
                                navController.navigate(
                                    Screen.CharacterDetail.createRoute(character.id)
                                )
                            }
                        }
                    )
                }
                WindowWidthSizeClass.Medium -> {
                    MediumView(
                        viewModel = viewModel,
                        onCharacterClick = { character ->
                            viewModel.setSelectedCharacter(character)
                            if (windowSizeClass != WindowWidthSizeClass.Expanded) {
                                navController.navigate(
                                    Screen.CharacterDetail.createRoute(character.id)
                                )
                            }
                        }
                    )
                }
                else -> {
                    ExpandedView(
                        viewModel = viewModel,
                        onCharacterClick = { character ->
                            viewModel.setSelectedCharacter(character)
                        }
                    )
                }
            }
        }

        composable(
            route = Screen.CharacterDetail.route
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString("characterId")?.toIntOrNull()
            characterId?.let { id ->
                CharacterDetailScreen(
                    characterId = id,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                viewModel = viewModel,
                onCharacterClick = { character ->
                    navController.navigate(Screen.CharacterDetail.createRoute(character.id))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
} 