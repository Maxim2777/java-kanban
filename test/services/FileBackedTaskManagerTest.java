package services;

import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    public void afterEach() {
        fileBackedTaskManager.deleteAllTasks();
    }

    @Test
    void TasksShouldBeEmptyBeforeAndAfterLoadingIfTasksWereNotCreated() {
        assertTrue(fileBackedTaskManager.listOfTasks().isEmpty(), "Задачи не пустые, после сохранения");
        assertTrue(fileBackedTaskManager.listOfEpics().isEmpty(), "Эпики не пустые, после сохранения");
        assertTrue(fileBackedTaskManager.listOfSubtasks().isEmpty(), "Подзадачи не пустые, после сохранения");

        File file = new File("src/services/SavedTasks.CSV");
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(fileBackedTaskManager.listOfTasks().isEmpty(), "Задачи не пусты, после загрузки");
        assertTrue(fileBackedTaskManager.listOfEpics().isEmpty(), "Эпики не пустые, после загрузки");
        assertTrue(fileBackedTaskManager.listOfSubtasks().isEmpty(), "Подзадачи не пустые, после загрузки");
    }

    @Test
    void TasksShouldBeEqualAfterSaveAndAfterLoading() {
        final Task task = new Task("Test taskName", "Test taskDescription");
        fileBackedTaskManager.addTask(task);
        final Epic epic = new Epic("Test epicName", "Test epicDescription");
        fileBackedTaskManager.addEpicTask(epic);
        final Subtask subtask = new Subtask("Test subtaskName", "Test subtaskDescription");
        subtask.setEpicID(2);
        fileBackedTaskManager.addSubtask(subtask);

        Task savedTask = fileBackedTaskManager.getTaskInfo(1);
        Task savedEpic = fileBackedTaskManager.getTaskInfo(2);
        Task savedSubtask = fileBackedTaskManager.getTaskInfo(3);

        assertEquals(task.toString(), savedTask.toString(), "Задачи не совпадают, после сохранения");
        assertEquals(epic.toString(), savedEpic.toString(), "Эпики не совпадают, после сохранения");
        assertEquals(subtask.toString(), savedSubtask.toString(), "Подзадачи не совпадают, после сохранения");

        File file = new File("src/services/SavedTasks.CSV");
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        savedTask = fileBackedTaskManager.getTaskInfo(1);
        savedEpic = fileBackedTaskManager.getTaskInfo(2);
        savedSubtask = fileBackedTaskManager.getTaskInfo(3);

        assertEquals(task.toString(), savedTask.toString(), "Задачи не совпадают, после загрузки");
        assertEquals(epic.toString(), savedEpic.toString(), "Эпики не совпадают, после загрузки");
        assertEquals(subtask.toString(), savedSubtask.toString(), "Подзадачи не совпадают, после загрузки");
    }
}
