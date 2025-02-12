package br.eti.rafaelcouto.gymbro.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.eti.rafaelcouto.gymbro.presentation.screens.WorkoutFormScreen
import br.eti.rafaelcouto.gymbro.presentation.uistate.MainActivityUiState

const val workoutFormRoute = "workout"
const val workoutIdArg = "workoutId"
private const val workoutFormFullRoute = "$workoutFormRoute?$workoutIdArg={$workoutIdArg}"

fun NavGraphBuilder.workoutFormScreen(
    navController: NavHostController,
    setMainActivityState: (MainActivityUiState) -> Unit = {},
    showMessage: (String) -> Unit = {}
) {
    composable(
        route = workoutFormFullRoute,
        arguments = listOf(
            navArgument(workoutIdArg) {
                type = NavType.LongType
                defaultValue = 0L
            }
        )
    ) {
        WorkoutFormScreen(
            onSaveWorkout = navController::popBackStack,
            showMessage = showMessage,
            setMainActivityState = setMainActivityState
        )
    }
}

fun NavController.navigateToWorkoutForm() {
    navigate(workoutFormRoute)
}

fun NavController.navigateToWorkoutForm(id: Long) {
    navigate("$workoutFormRoute?$workoutIdArg=$id")
}
