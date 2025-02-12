package br.eti.rafaelcouto.gymbro.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import br.eti.rafaelcouto.gymbro.R
import br.eti.rafaelcouto.gymbro.data.structure.CircularLinkedList
import br.eti.rafaelcouto.gymbro.domain.model.Workout
import br.eti.rafaelcouto.gymbro.extensions.items
import br.eti.rafaelcouto.gymbro.presentation.components.EmptyMessage
import br.eti.rafaelcouto.gymbro.presentation.components.FloatingActionButton
import br.eti.rafaelcouto.gymbro.presentation.uistate.MainActivityUiState
import br.eti.rafaelcouto.gymbro.presentation.uistate.WorkoutListScrenUiState
import br.eti.rafaelcouto.gymbro.presentation.viewmodel.WorkoutListViewModel

@Composable
fun WorkoutListScreen(
    onWorkoutSelected: (Workout) -> Unit = {},
    onFabClicked: () -> Unit = {},
    setMainActivityState: (MainActivityUiState) -> Unit = {}
) {
    val viewModel: WorkoutListViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadContent() }

    WorkoutListScreen(
        onWorkoutSelected = onWorkoutSelected,
        onFabClicked = onFabClicked,
        setMainActivityState = setMainActivityState,
        state = state
    )
}

@Composable
fun WorkoutListScreen(
    onWorkoutSelected: (Workout) -> Unit = {},
    onFabClicked: () -> Unit = {},
    setMainActivityState: (MainActivityUiState) -> Unit = {},
    state: WorkoutListScrenUiState
) {
    if (state.shouldDisplayEmptyMessage)
        EmptyMessage(
            text = stringResource(id = R.string.no_workouts_found)
        )

    WorkoutList(
        onItemClick = onWorkoutSelected,
        workouts = state.workouts
    )

    LaunchedEffect(Unit) {
        val mainState = MainActivityUiState(
            titleRes = R.string.my_workouts,
            floatingActionButton = {
                FloatingActionButton(
                    icon = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.create_workout),
                    onClick = onFabClicked
                )
            }
        )
        setMainActivityState(mainState)
    }
}

@Composable
fun WorkoutList(
    onItemClick: (Workout) -> Unit = {},
    workouts: CircularLinkedList<Workout>
) {
    LazyColumn(
        content = {
            items(collection = workouts) { workout ->
                WorkoutItem(
                    modifier = Modifier.semantics { testTag = "workout-${workout.id}" },
                    workout = workout,
                    onItemClick = onItemClick
                )
            }
        }
    )
}

@Composable
fun WorkoutItem(
    workout: Workout,
    modifier: Modifier = Modifier,
    onItemClick: (Workout) -> Unit = {}
) {
    val (backgroundColor, fontWeight) = if (workout.isLast)
        colorResource(id = R.color.mediumGrey) to FontWeight.Bold
    else
        Color.Transparent to FontWeight.Normal

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .clickable { onItemClick(workout) },
        content = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.padding_m),
                        vertical = dimensionResource(id = R.dimen.padding_p)
                    ),
                text = workout.name,
                fontWeight = fontWeight
            )
        }
    )
    HorizontalDivider(color = colorResource(id = R.color.colorPrimaryAlpha))
}

@Preview(showSystemUi = true)
@Composable
private fun WorkoutListScreenEmptyPreview() {
    WorkoutListScreen(state = WorkoutListScrenUiState())
}

@Preview(showSystemUi = true)
@Composable
private fun WorkoutListScreenNotEmptyPreview() {
    WorkoutListScreen(
        state = WorkoutListScrenUiState(
            workouts = CircularLinkedList<Workout>().apply {
                add(
                    Workout(
                        name = "Workout 1",
                        isLast = true
                    )
                )
                add(Workout(name = "Workout 2"))
                add(Workout(name = "Workout 3"))
            }
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun WorkoutListPreview() {
    WorkoutList(
        workouts = CircularLinkedList<Workout>().apply {
            add(
                Workout(
                    name = "Workout 1",
                    isLast = true
                )
            )
            add(Workout(name = "Workout 2"))
            add(Workout(name = "Workout 3"))
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun WorkoutItemNotLastPreview() {
    WorkoutItem(
        workout = Workout(
            name = "Workout"
        )
    )
}

@Preview
@Composable
private fun WorkoutItemLastPreview() {
    WorkoutItem(
        workout = Workout(
            name = "Workout",
            isLast = true
        )
    )
}
