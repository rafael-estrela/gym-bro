package br.eti.rafaelcouto.gymbro.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.eti.rafaelcouto.gymbro.presentation.screens.ExerciseListScreen
import br.eti.rafaelcouto.gymbro.presentation.uistate.MainActivityUiState

const val exerciseListRoute = "exercises"
private const val exerciseListFullRoute = "$workoutFormRoute/{$workoutIdArg}/$exerciseListRoute"

fun NavGraphBuilder.exerciseListScreen(
    navController: NavController,
    setMainActivityState: (MainActivityUiState) -> Unit = {},
    showMessage: (String) -> Unit = {}
) {
    composable(
        route = exerciseListFullRoute,
        arguments = listOf(
            navArgument(workoutIdArg) {
                type = NavType.LongType
                defaultValue = 0L
            }
        )
    ) {
        ExerciseListScreen(
            onEditExerciseClick = { exercise ->
                navController.navigateToExerciseForm(
                    workoutId = exercise.workoutId,
                    exerciseId = exercise.id
                )
            },
            onAddExercise = navController::navigateToExerciseForm,
            onEditWorkout = navController::navigateToWorkoutForm,
            onDeleteWorkout = navController::popBackStack,
            showMessage = showMessage,
            setMainActivityState = setMainActivityState
        )
    }
}

private fun NavController.navigateToExerciseForm(workoutId: Long) {
    navigate("$workoutFormRoute/$workoutId/$exerciseFormRoute")
}

private fun NavController.navigateToExerciseForm(workoutId: Long, exerciseId: Long) {
    navigate("$workoutFormRoute/$workoutId/$exerciseFormRoute?$exerciseIdArg=$exerciseId")
}
