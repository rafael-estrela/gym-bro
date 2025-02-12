package br.eti.rafaelcouto.gymbro.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import br.eti.rafaelcouto.gymbro.R
import br.eti.rafaelcouto.gymbro.presentation.components.BottomButtonColumn
import br.eti.rafaelcouto.gymbro.presentation.components.TextField
import br.eti.rafaelcouto.gymbro.presentation.uistate.ExerciseFormUiState
import br.eti.rafaelcouto.gymbro.presentation.uistate.MainActivityUiState
import br.eti.rafaelcouto.gymbro.presentation.viewmodel.ExerciseFormViewModel

@Composable
fun ExerciseFormScreen(
    onSaveExercise: () -> Unit = {},
    onDeleteExercise: () -> Unit = {},
    showMessage: (String) -> Unit = {},
    setMainActivityState: (MainActivityUiState) -> Unit = {}
) {

    val viewModel: ExerciseFormViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadContent() }

    ExerciseFormScreen(
        onSaveExercise = {
            viewModel.saveExercise()
            onSaveExercise()
        },
        onDeleteExercise = {
            viewModel.deleteExercise()
            onDeleteExercise()
        },
        showMessage = showMessage,
        setMainActivityState = setMainActivityState,
        state = state
    )
}

@Composable
fun ExerciseFormScreen(
    onSaveExercise: () -> Unit = {},
    onDeleteExercise: () -> Unit = {},
    showMessage: (String) -> Unit = {},
    setMainActivityState: (MainActivityUiState) -> Unit = {},
    state: ExerciseFormUiState = ExerciseFormUiState()
) {

    val saveExerciseMessage = stringResource(state.successMessage)
    val deleteExerciseMessage = stringResource(id = R.string.exercise_deleted)

    BottomButtonColumn(
        buttonText = stringResource(id = R.string.save_exercise),
        isButtonEnabled = state.isButtonEnabled,
        onButtonClick = {
            onSaveExercise()
            showMessage(saveExerciseMessage)
        },
        content = {
            TextField(
                testTag = "exerciseNameField",
                value = state.exerciseName,
                label = stringResource(id = R.string.exercise_name),
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next,
                onValueChange = state.onExerciseNameChange
            )
            TextField(
                testTag = "numberOfSetsField",
                value = state.numberOfSets,
                label = stringResource(id = R.string.number_of_sets),
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
                onValueChange = state.onNumberOfSetsChange
            )
            TextField(
                testTag = "minRepsField",
                value = state.minReps,
                label = stringResource(id = R.string.min_reps),
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
                onValueChange = state.onMinRepsChange
            )
            TextField(
                testTag = "maxRepsField",
                value = state.maxReps,
                label = stringResource(id = R.string.max_reps),
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
                onValueChange = state.onMaxRepsChange
            )
            TextField(
                testTag = "loadField",
                value = state.load,
                label = stringResource(id = R.string.load),
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                onValueChange = state.onLoadChange
            )
        }
    )

    LaunchedEffect(state.exerciseId) {
        val titleRes = if (state.exerciseId == 0L)
            R.string.add_exercise
        else
            R.string.edit_exercise

        val mainState = MainActivityUiState(
            titleRes = titleRes,
            showsBackButton = true,
            topAppBarActions = {
                if (state.exerciseId != 0L)
                    IconButton(
                        modifier = Modifier.semantics { testTag = "deleteAction" },
                        onClick = {
                            onDeleteExercise()
                            showMessage(deleteExerciseMessage)
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(id = R.string.delete_exercise)
                            )
                        }
                    )
            }
        )
        setMainActivityState(mainState)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ExerciseFormScreenDefaultPreview() {
    ExerciseFormScreen(state = ExerciseFormUiState())
}

@Preview(showSystemUi = true)
@Composable
private fun ExerciseFormScreenFilledPreview() {
    ExerciseFormScreen(
        state = ExerciseFormUiState(
            exerciseName = "Exercise",
            numberOfSets = "3",
            minReps = "8",
            maxReps = "12",
            load = "50",
            isButtonEnabled = true
        )
    )
}
