package br.eti.rafaelcouto.gymbro.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.eti.rafaelcouto.gymbro.domain.model.Exercise
import br.eti.rafaelcouto.gymbro.domain.usecase.ExerciseUseCaseAbs
import br.eti.rafaelcouto.gymbro.domain.usecase.WorkoutUseCaseAbs
import br.eti.rafaelcouto.gymbro.navigation.workoutIdArg
import br.eti.rafaelcouto.gymbro.presentation.uistate.ExerciseListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val exerciseUseCase: ExerciseUseCaseAbs,
    private val workoutUseCase: WorkoutUseCaseAbs
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseListUiState())
    val uiState
        get() = _uiState.asStateFlow()

    private var isDeleting = false

    fun loadContent() {
        val workoutId: Long = requireNotNull(savedStateHandle[workoutIdArg])

        loadExercises(workoutId)
        loadWorkout(workoutId)
    }

    fun increaseLoad(exercise: Exercise) {
        viewModelScope.launch {
            exerciseUseCase.increaseLoad(exercise)
        }
    }

    fun decreaseLoad(exercise: Exercise) {
        viewModelScope.launch {
            exerciseUseCase.decreaseLoad(exercise)
        }
    }

    fun finishSet(exercise: Exercise.UI, set: Int) {
        _uiState.update { state ->
            state.copy(
                exercises = state.exercises.map { item ->
                    if (exercise.original.id == item.original.id)
                        item.copy(
                            setsState = item.setsState.mapIndexed { index, setState ->
                                if (index == set)
                                    !setState
                                else
                                    setState
                            }
                        )
                    else
                        item
                }
            )
        }

        updateWorkoutState()
    }

    fun deleteWorkout() {
        isDeleting = true
        viewModelScope.launch {
            workoutUseCase.deleteWorkout(_uiState.value.workout.id)
        }
    }

    fun finishWorkout() {
        updateLastWorkout()
        finishAllExercises()
    }

    fun setMenuState(isMenuExpanded: Boolean) {
        _uiState.update { state ->
            state.copy(isMenuExpanded = isMenuExpanded)
        }
    }

    private fun loadExercises(workoutId: Long) {
        viewModelScope.launch {
            exerciseUseCase.getAllExercises(workoutId).collect { exercises ->
                if (isDeleting) return@collect

                _uiState.update { state ->
                    state.copy(
                        exercises = exercises.map { exercise ->
                            val currentExercise = state.exercises.firstOrNull {
                                it.original.id == exercise.original.id
                            } ?: return@map exercise

                            exercise.copy(
                                setsState = exercise.setsState.mapIndexed { index, setState ->
                                    if (index < currentExercise.original.sets)
                                        currentExercise.setsState[index]
                                    else
                                        setState
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    private fun loadWorkout(workoutId: Long) {
        viewModelScope.launch {
            workoutUseCase.getWorkoutByIdAsFlow(workoutId).collect {
                it?.let { workout ->
                    _uiState.update { state ->
                        state.copy(workout = workout)
                    }
                }
            }
        }
    }

    private fun updateWorkoutState() {
        val allFinished = _uiState.value.exercises.all { it.finished }

        _uiState.update { state ->
            state.copy(
                canFinishWorkout = !allFinished
            )
        }

        if (allFinished)
            updateLastWorkout()
    }

    private fun updateLastWorkout() {
        viewModelScope.launch {
            workoutUseCase.finishWorkout(_uiState.value.workout.id)
        }
    }

    private fun finishAllExercises() {
        _uiState.update { state ->
            state.copy(
                exercises = state.exercises.map { exercise ->
                    exercise.copy(
                        setsState = MutableList(exercise.original.sets.toInt()) { true }.toList()
                    )
                },
                canFinishWorkout = false
            )
        }
    }
}
