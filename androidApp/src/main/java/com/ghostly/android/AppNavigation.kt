package com.ghostly.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ghostly.android.home.HomeScreen
import com.ghostly.android.login.ui.LoginScreen
import com.ghostly.android.posts.NavigationMaps
import com.ghostly.android.posts.ui.EditPostScreen
import com.ghostly.android.posts.ui.PostDetailScreen
import com.ghostly.android.settings.StaffSettingsScreen
import com.ghostly.database.entities.PostWithAuthorsAndTags
import com.ghostly.posts.models.Post
import kotlinx.serialization.Serializable

object Destination {

    @Serializable
    data class Home(val something: Boolean = false)

    @Serializable
    data class Login(val userLoggedOut: Boolean = false)

    @Serializable
    object StaffSettings

    @Serializable
    data class EditPost(val post: Post)
}

@Composable
fun AppNavigation(
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = Destination.Login()) {
        composable<Destination.Login> { backStackEntry ->
            val login = backStackEntry.toRoute<Destination.Login>()
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Destination.Home()) {
                        popUpTo(Destination.Login()) {
                            inclusive = true
                        }
                    }
                },
                didUserLogout = login.userLoggedOut
            )
        }
        composable<Post>(
            typeMap = NavigationMaps.typeMap
        ) { backStackEntry ->
            val post = backStackEntry.toRoute<Post>()
            PostDetailScreen(
                post = post,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = {
                    navController.navigate(Destination.EditPost(it))
                }
            )
        }
        composable<Destination.Home> {
            HomeScreen(
                onPostClick = { navController.navigate(it) },
                onLogout = {
                    navController.navigate(Destination.Login(true)) {
                        popUpTo(Destination.Home()) {
                            inclusive = true
                        }
                    }
                },
                onStaffSettingsClicked = {
                    navController.navigate(Destination.StaffSettings)
                }
            )
        }
        composable<Destination.StaffSettings> {
            StaffSettingsScreen(
                navController = navController
            )
        }
        composable<Destination.EditPost>(
            typeMap = NavigationMaps.typeMap
        ) { backStackEntry ->
            val editPost = backStackEntry.toRoute<Destination.EditPost>()
            EditPostScreen(
                post = editPost.post,
                onEditSuccess = {
                    navController.navigateUp()
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}