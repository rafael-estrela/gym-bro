package br.eti.rafaelcouto.gymbro.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import br.eti.rafaelcouto.gymbro.domain.model.Workout
import br.eti.rafaelcouto.gymbro.presentation.screens.WorkoutListScreen
import br.eti.rafaelcouto.gymbro.presentation.uistate.MainActivityUiState

const val workoutListRoute = "workoutList"

fun NavGraphBuilder.workoutListScreen(
    navController: NavHostController,
    setMainActivityState: (MainActivityUiState) -> Unit = {}
) {
    composable(route = workoutListRoute) {
        WorkoutListScreen(
            onWorkoutSelected = navController::navigateToExerciseList,
            onFabClicked = navController::navigateToWorkoutForm,
            setMainActivityState = setMainActivityState
        )
    }
}

private fun NavController.navigateToExerciseList(workout: Workout) {
    navigate(route = "$workoutFormRoute/${workout.id}/$exerciseListRoute")
}
