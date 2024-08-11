import models.*;
import services.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void tasksShouldNotChangeAtAllStagesOfAdding() {
        final Task task = new Task("Test taskName", "Test taskDescription");
        taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskInfo(0);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.listOfTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void epicsShouldNotChangeAtAllStagesOfAdding() {
        final Epic epic = new Epic("Test epicName", "Test epicDescription");
        taskManager.addEpicTask(epic);
        final Task savedEpic = taskManager.getTaskInfo(0);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final ArrayList<Epic> epics = taskManager.listOfEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    void subtasksShouldNotChangeAtAllStagesOfAdding() {
        final Epic epic = new Epic("Test epicName", "Test epicDescription");
        taskManager.addEpicTask(epic);
        final Subtask subtask = new Subtask("Test subtaskName", "Test subtaskName");
        subtask.setEpicID(0);
        taskManager.addSubtask(subtask);
        final Task savedSubtask = taskManager.getTaskInfo(1);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final ArrayList<Subtask> subtasks = taskManager.listOfSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    void subtaskShouldNotBeAddedAsEpicToSubtask() {
        final Subtask subtask = new Subtask("Test subtaskName", "Test subtaskName");
        subtask.setEpicID(0);
        taskManager.addSubtask(subtask);

        final ArrayList<Subtask> subtasks = taskManager.listOfSubtasks();

        assertTrue(subtasks.isEmpty(), "Подзадача добавилась со своим же ID в качсетве эпика.");
    }
    @Test
    void managerMustAddTasksOfDifferentTypesAndSearchForThemByID() {
        final Task task = new Task("Test taskName", "Test taskDescription");
        taskManager.addTask(task);
        final Epic epic = new Epic("Test epicName", "Test epicDescription");
        taskManager.addEpicTask(epic);
        final Subtask subtask = new Subtask("Test subtaskName", "Test subtaskDescription");
        subtask.setEpicID(1);
        taskManager.addSubtask(subtask);

        final Task savedTask = taskManager.getTaskInfo(0);
        final Task savedEpic = taskManager.getTaskInfo(1);
        final Task savedSubtask = taskManager.getTaskInfo(2);

        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

    }

    @Test
    void allTaskElementsShouldNotChangeAfterAddingToManager() {
        String name = "Test taskName";
        String description = "Test taskDescription";
        final Task task = new Task(name, description);
        taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskInfo(0);

        assertEquals(name, savedTask.getName(), "Имена задач не совпадают.");
        assertEquals(description, savedTask.getDescription(), "Описание задач не совпадает.");
    }

    @Test
    void allEpicElementsShouldNotChangeAfterAddingToManager() {
        String name = "Test epicName";
        String description = "Test epicDescription";
        int subtaskID = 7;
        final Epic epic = new Epic(name, description);
        epic.subtasksAdd(subtaskID);
        taskManager.addEpicTask(epic);

        final Task savedEpic = taskManager.getTaskInfo(0);
        final ArrayList<Integer> subtasksID = epic.getSubtasksID();

        assertEquals(name, savedEpic.getName(), "Имена эпиков не совпадают.");
        assertEquals(description, savedEpic.getDescription(), "Описание эпиков не совпадает.");
        assertEquals(subtaskID, subtasksID.getFirst(), "ID привязанной подзадачи не совпадает.");
    }

    @Test
    void allSubtaskElementsShouldNotChangeAfterAddingToManager() {
        final Epic epic = new Epic("Test epicName", "Test epicDescription");
        taskManager.addEpicTask(epic);

        String name = "Test subtaskName";
        String description = "Test subtaskDescription";
        final Subtask subtask = new Subtask(name, description);
        subtask.setEpicID(0);
        taskManager.addSubtask(subtask);

        final Subtask savedSubtask = (Subtask) taskManager.getTaskInfo(1);

        assertEquals(name, savedSubtask.getName(), "Имена подзадач не совпадают.");
        assertEquals(description, savedSubtask.getDescription(), "Описание подзадач не совпадает.");
        assertEquals(0, savedSubtask.getEpicID(), "ID привязанного эпика не совпадает.");
    }
}