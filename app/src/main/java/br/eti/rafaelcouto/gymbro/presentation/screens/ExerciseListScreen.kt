package br.eti.rafaelcouto.gymbro.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.eti.rafaelcouto.gymbro.R
import br.eti.rafaelcouto.gymbro.domain.model.Exercise
import br.eti.rafaelcouto.gymbro.presentation.components.Checkbox
import br.eti.rafaelcouto.gymbro.presentation.components.DropDownMenu
import br.eti.rafaelcouto.gymbro.presentation.components.EmptyMessage
import br.eti.rafaelcouto.gymbro.presentation.components.FloatingActionButton
import br.eti.rafaelcouto.gymbro.presentation.components.RoundedButton
import br.eti.rafaelcouto.gymbro.presentation.uistate.ExerciseListUiState
import br.eti.rafaelcouto.gymbro.presentation.uistate.MainActivityUiState
import br.eti.rafaelcouto.gymbro.presentation.viewmodel.ExerciseListViewModel

@Composable
fun ExerciseListScreen(
    onEditExerciseClick: (Exercise) -> Unit = {},
    onAddExercise: (Long) -> Unit = {},
    onEditWorkout: (Long) -> Unit = {},
    onDeleteWorkout: () -> Unit = {},
    showMessage: (String) -> Unit = {},
    setMainActivityState: (MainActivityUiState) -> Unit = {}
) {

    val viewModel: ExerciseListViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadContent()
    }

    ExerciseListScreen(
        onIncreaseLoad = viewModel::increaseLoad,
        onDecreaseLoad = viewModel::decreaseLoad,
        onSetFinshed = viewModel::finishSet,
        onEditExercise = onEditExerciseClick,
        onAddExercise = onAddExercise,
        onFinishWorkout = viewModel::finishWorkout,
        onMenuToggle = viewModel::setMenuState,
        onEditWorkout = onEditWorkout,
        onDeleteWorkout = {
            viewModel.deleteWorkout()
            onDeleteWorkout()
        },
        showMessage = showMessage,
        setMainActivityState = setMainActivityState,
        state = state
    )
}

@Composable
fun ExerciseListScreen(
    onIncreaseLoad: (Exercise) -> Unit = {},
    onDecreaseLoad: (Exercise) -> Unit = {},
    onSetFinshed: (exercise: Exercise.UI, set: Int) -> Unit = { _, _ -> },
    onEditExercise: (Exercise) -> Unit = {},
    onAddExercise: (Long) -> Unit = {},
    onFinishWorkout: () -> Unit = {},
    onMenuToggle: (Boolean) -> Unit = {},
    onEditWorkout: (Long) -> Unit = {},
    onDeleteWorkout: () -> Unit = {},
    showMessage: (String) -> Unit = {},
    setMainActivityState: (MainActivityUiState) -> Unit = {},
    state: ExerciseListUiState = ExerciseListUiState()
) {

    val workoutDeletedSuccessMessage = stringResource(id = R.string.workout_deleted)
    val workoutFinishedSuccessMessage = stringResource(id = R.string.workout_finished)

    LaunchedEffect(key1 = state.canFinishWorkout) {
        if (!state.canFinishWorkout)
            showMessage(workoutFinishedSuccessMessage)
    }

    if (state.shouldDisplayEmptyMessage)
        EmptyMessage(
            text = stringResource(id = R.string.no_exercises_found)
        )

    ExerciseList(
        exercises = state.exercises,
        onIncreaseLoad = onIncreaseLoad,
        onDecreaseLoad = onDecreaseLoad,
        onEditExercise = onEditExercise,
        onSetFinished = onSetFinshed
    )

    LaunchedEffect(
        state.workout.name,
        state.canFinishWorkout,
        state.isMenuExpanded,
        state.shouldDisplayEmptyMessage,
        block = {
            val mainState = MainActivityUiState(
                title = state.workout.name,
                showsBackButton = true,
                floatingActionButton = {
                    FloatingActionButton(
                        icon = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.add_exercise),
                        onClick = {
                            onAddExercise(state.workout.id)
                        }
                    )
                },
                topAppBarActions = {
                    if (!state.shouldDisplayEmptyMessage)
                        IconButton(
                            modifier = Modifier.semantics { testTag = "finishWorkoutButton" },
                            enabled = state.canFinishWorkout,
                            onClick = onFinishWorkout,
                            content = {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = stringResource(id = R.string.finish_workout)
                                )
                            }
                        )

                    DropDownMenu(
                        expanded = state.isMenuExpanded,
                        onToggle = onMenuToggle
                    ) {
                        DropdownMenuItem(
                            modifier = Modifier.semantics { testTag = "editAction" },
                            text = { Text(text = stringResource(id = R.string.edit)) },
                            onClick = {
                                onEditWorkout(state.workout.id)
                                onMenuToggle(false)
                            }
                        )
                        DropdownMenuItem(
                            modifier = Modifier.semantics { testTag = "deleteAction" },
                            text = { Text(text = stringResource(id = R.string.delete)) },
                            onClick = {
                                onDeleteWorkout()
                                showMessage(workoutDeletedSuccessMessage)
                            }
                        )
                    }
                }
            )
            setMainActivityState(mainState)
        }
    )
}

@Composable
fun ExerciseList(
    exercises: List<Exercise.UI> = emptyList(),
    onIncreaseLoad: (Exercise) -> Unit = {},
    onDecreaseLoad: (Exercise) -> Unit = {},
    onEditExercise: (Exercise) -> Unit = {},
    onSetFinished: (exercise: Exercise.UI, set: Int) -> Unit = { _, _ -> }
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        content = {
            items(exercises) { exercise ->
                ExerciseItem(
                    exercise = exercise,
                    onIncreaseLoad = onIncreaseLoad,
                    onDecreaseLoad = onDecreaseLoad,
                    onEditExercise = onEditExercise,
                    onSetFinished = onSetFinished
                )
            }
        }
    )
}

@Composable
fun ExerciseItem(
    exercise: Exercise.UI,
    onIncreaseLoad: (Exercise) -> Unit = {},
    onDecreaseLoad: (Exercise) -> Unit = {},
    onEditExercise: (Exercise) -> Unit = {},
    onSetFinished: (exercise: Exercise.UI, set: Int) -> Unit = { _, _ ->}
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_m),
                vertical = dimensionResource(id = R.dimen.padding_p)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.padding_xp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = exercise.original.name
            )
            RoundedButton(
                modifier = Modifier.semantics { testTag = "edit-${exercise.original.id}" },
                onClick = {
                    onEditExercise(exercise.original)
                },
                icon = Icons.Filled.Edit,
                contentDescription = stringResource(id = R.string.edit_exercise)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.padding_p)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val setsAndReps = if (exercise.isSameNumberOfReps)
                stringResource(
                    id = R.string.sets_and_reps,
                    exercise.original.sets,
                    exercise.original.minReps
                )
            else
                stringResource(
                    id = R.string.sets_min_max_reps,
                    exercise.original.sets,
                    exercise.original.minReps,
                    exercise.original.maxReps
                )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = setsAndReps
            )
            RoundedButton(
                modifier = Modifier
                    .semantics { testTag = "decrease-${exercise.original.id}" }
                    .padding(end = dimensionResource(id = R.dimen.padding_m)),
                onClick = {
                    onDecreaseLoad(exercise.original)
                },
                icon = Icons.Filled.KeyboardArrowDown,
                contentDescription = stringResource(id = R.string.decrease_load)
            )
            Text(
                modifier = Modifier.padding(end = dimensionResource(id = R.dimen.padding_m)),
                text = stringResource(id = R.string.load_kg, exercise.original.load)
            )
            RoundedButton(
                modifier = Modifier.semantics { testTag = "increase-${exercise.original.id}" },
                onClick = {
                    onIncreaseLoad(exercise.original)
                },
                icon = Icons.Filled.KeyboardArrowUp,
                contentDescription = stringResource(id = R.string.increase_weight)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.padding_p)),
            verticalAlignment = Alignment.CenterVertically,
            content = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    text = stringResource(id = R.string.sets_done)
                )
                exercise.setsState.forEachIndexed { index, value ->
                    val paddingStart = if (index == 0)
                        0.dp
                    else
                        dimensionResource(id = R.dimen.padding_p)

                    Checkbox(
                        modifier = Modifier
                            .semantics { testTag = "exercise-${exercise.original.id}-set-$index" }
                            .padding(start = paddingStart),
                        checked = value,
                        onCheckedChange = {
                            onSetFinished(exercise, index)
                        },
                        enabled = !exercise.finished
                    )
                }
            }
        )
    }
    HorizontalDivider(color = colorResource(id = R.color.colorPrimaryAlpha))
}

@Preview(showSystemUi = true)
@Composable
private fun ExerciseListScreenEmptyPreview() {
    ExerciseListScreen(state = ExerciseListUiState())
}

@Preview(showSystemUi = true)
@Composable
private fun ExerciseListScreenNotEmptyPreview() {
    ExerciseListScreen(
        state = ExerciseListUiState(
            exercises = listOf(
                Exercise.UI(
                    original = Exercise(
                        name = "Exercise 1",
                        sets = 3,
                        minReps = 8,
                        maxReps = 12,
                        load = 50,
                        workoutId = 1
                    )
                ),
                Exercise.UI(
                    original = Exercise(
                        name = "Exercise 2",
                        sets = 4,
                        minReps = 10,
                        maxReps = 10,
                        load = 20,
                        workoutId = 1
                    ),
                    setsState = listOf(true, true, false, false)
                ),
                Exercise.UI(
                    original = Exercise(
                        name = "Exercise 3",
                        sets = 3,
                        minReps = 8,
                        maxReps = 12,
                        load = 50,
                        workoutId = 1
                    ),
                    setsState = listOf(true, true, true)
                )
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ExerciseListPreview() {
    ExerciseList(
        exercises = listOf(
            Exercise.UI(
                original = Exercise(
                    name = "Exercise 1",
                    sets = 3,
                    minReps = 8,
                    maxReps = 12,
                    load = 50,
                    workoutId = 1
                )
            ),
            Exercise.UI(
                original = Exercise(
                    name = "Exercise 2",
                    sets = 4,
                    minReps = 10,
                    maxReps = 10,
                    load = 20,
                    workoutId = 1
                ),
                setsState = listOf(true, true, false, false)
            ),
            Exercise.UI(
                original = Exercise(
                    name = "Exercise 3",
                    sets = 3,
                    minReps = 8,
                    maxReps = 12,
                    load = 50,
                    workoutId = 1
                ),
                setsState = listOf(true, true, true)
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ExerciseItemDefaultPreview() {
    ExerciseItem(
        exercise = Exercise.UI(
            original = Exercise(
                name = "Exercise 2",
                sets = 3,
                minReps = 8,
                maxReps = 12,
                load = 50,
                workoutId = 1
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ExerciseItemPartiallyDonePreview() {
    ExerciseItem(
        exercise = Exercise.UI(
            original = Exercise(
                name = "Exercise 2",
                sets = 3,
                minReps = 8,
                maxReps = 12,
                load = 50,
                workoutId = 1
            ),
            setsState = listOf(true, false, false)
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ExerciseItemDonePreview() {
    ExerciseItem(
        exercise = Exercise.UI(
            original = Exercise(
                name = "Exercise 2",
                sets = 3,
                minReps = 10,
                maxReps = 10,
                load = 50,
                workoutId = 1
            ),
            setsState = listOf(true, true, true)
        )
    )
}
