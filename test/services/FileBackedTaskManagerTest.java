package services;

import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {

    private TaskManager fileBackedTaskManager;

    @BeforeEach
    public void beforeEach() {
        fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager();
    }

    @Test
    void TasksShouldBeEmptyBeforeAndAfterLoadingIfTasksWereNotCreated() {
        assertTrue(fileBackedTaskManager.listOfTasks().isEmpty(), "Задачи не пустые, полсе сохранения");
        assertTrue(fileBackedTaskManager.listOfEpics().isEmpty(), "Эпики не пустые, полсе сохранения");
        assertTrue(fileBackedTaskManager.listOfSubtasks().isEmpty(), "Подзадачи не пустые, полсе сохранения");

        File file = new File("src/services/SavedTasks.CSV");
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(fileBackedTaskManager.listOfTasks().isEmpty(), "Задачи не пусты, полсе загрузки");
        assertTrue(fileBackedTaskManager.listOfEpics().isEmpty(), "Эпики не пустые, полсе загрузки");
        assertTrue(fileBackedTaskManager.listOfSubtasks().isEmpty(), "Подзадачи не пустые, полсе загрузки");
    }

    @Test
    void TasksShouldBeEqualAfterSaveAndAfterLoading() {
        final Task task = new Task("Test taskName", "Test taskDescription");
        fileBackedTaskManager.addTask(task);
        final Epic epic = new Epic("Test epicName", "Test epicDescription");
        fileBackedTaskManager.addEpicTask(epic);
        final Subtask subtask = new Subtask("Test subtaskName", "Test subtaskDescription");
        subtask.setEpicID(1);
        fileBackedTaskManager.addSubtask(subtask);

        Task savedTask = fileBackedTaskManager.getTaskInfo(0);
        Task savedEpic = fileBackedTaskManager.getTaskInfo(1);
        Task savedSubtask = fileBackedTaskManager.getTaskInfo(2);

        assertEquals(task.toString(), savedTask.toString(), "Задачи не совпадают, после сохранения");
        assertEquals(epic.toString(), savedEpic.toString(), "Эпики не совпадают, после сохранения");
        assertEquals(subtask.toString(), savedSubtask.toString(), "Подзадачи не совпадают, после сохранения");

        File file = new File("src/services/SavedTasks.CSV");
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        savedTask = fileBackedTaskManager.getTaskInfo(0);
        savedEpic = fileBackedTaskManager.getTaskInfo(1);
        savedSubtask = fileBackedTaskManager.getTaskInfo(2);

        assertEquals(task.toString(), savedTask.toString(), "Задачи не совпадают, полсе загрузки");
        assertEquals(epic.toString(), savedEpic.toString(), "Эпики не совпадают, полсе загрузки");
        assertEquals(subtask.toString(), savedSubtask.toString(), "Подзадачи не совпадают, полсе загрузки");

        fileBackedTaskManager.deleteAllTasks();
    }
}
