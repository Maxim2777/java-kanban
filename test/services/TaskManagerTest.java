package services;

import models.Epic;
import models.Subtask;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

        assertFalse(tasks.isEmpty(), "Задачи не возвращаются.");
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

        assertFalse(epics.isEmpty(), "Эпики не возвращаются.");
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

        assertFalse(subtasks.isEmpty(), "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    void subtaskOrTaskShouldNotBeAddedAsEpicToSubtask() {
        final Subtask subtask = new Subtask("Test subtaskName", "Test subtaskName");
        subtask.setEpicID(0);
        taskManager.addSubtask(subtask);

        final ArrayList<Subtask> subtasks = taskManager.listOfSubtasks();

        assertTrue(subtasks.isEmpty(), "Подзадача добавилась со своим же ID в качсетве эпика.");

        final Task task = new Task("Test taskName", "Test taskDescription");
        taskManager.addTask(task);
        final Subtask subtask2 = new Subtask("Test subtaskName2", "Test subtaskName2");
        subtask2.setEpicID(0);
        taskManager.addSubtask(subtask2);

        final ArrayList<Subtask> subtasks2 = taskManager.listOfSubtasks();

        assertTrue(subtasks2.isEmpty(), "Подзадача добавилась с ID обычной задачи в качестве эпика.");

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

    @Test
    void taskManagerShouldGenerateNewIDWhenAddingNewTask() {
        final Task task = new Task("Test taskName", "Test taskDescription");
        task.setID(7);
        taskManager.addTask(task);

        assertNotNull(taskManager.getTaskInfo(0), "Для задачи не сгенерировался новый ID");
        assertNull(taskManager.getTaskInfo(7), "Задаче был присвоен заданный ID");
    }

    @Test
    void epicsShouldBeDeletedCorrectlyWithAssociatedSubtasks() {
        final Epic epic = new Epic("Test epicName", "Test epicDescription");
        taskManager.addEpicTask(epic);
        final Epic epic2 = new Epic("Test epicName2", "Test epicDescription2");
        taskManager.addEpicTask(epic2);
        final Subtask subtask = new Subtask("Test subtaskName", "Test subtaskName");
        subtask.setEpicID(0);
        taskManager.addSubtask(subtask);

        ArrayList<Subtask> subtasks = taskManager.listOfSubtasks();
        ArrayList<Epic> epics = taskManager.listOfEpics();

        assertFalse(subtasks.isEmpty(), "Подзадача не добавлена.");
        assertFalse(epics.isEmpty(), "Эпики не добавлены.");

        taskManager.deleteEpics();

        subtasks = taskManager.listOfSubtasks();
        epics = taskManager.listOfEpics();

        assertTrue(epics.isEmpty(), "Эпики не удалены");
        assertTrue(subtasks.isEmpty(), "Подзадача не удалена.");
    }

    @Test
    void tasksShouldBeDeletedCorrectly() {
        final Task task = new Task("Test taskName", "Test taskDescription");
        taskManager.addTask(task);
        final Task task2 = new Task("Test taskName2", "Test taskDescription2");
        taskManager.addTask(task2);

        ArrayList<Task> tasks = taskManager.listOfTasks();

        assertFalse(tasks.isEmpty(), "Задачи не добавлены.");

        taskManager.deleteTasks();
        tasks = taskManager.listOfTasks();

        assertTrue(tasks.isEmpty(), "Задачи не удалены");
    }

    @Test
    void subtasksShouldBeDeletedCorrectly() {
        final Epic epic = new Epic("Test epicName", "Test epicDescription");
        taskManager.addEpicTask(epic);
        final Subtask subtask = new Subtask("Test subtaskName", "Test subtaskName");
        subtask.setEpicID(0);
        taskManager.addSubtask(subtask);
        final Subtask subtask2 = new Subtask("Test subtaskName2", "Test subtaskName2");
        subtask2.setEpicID(0);
        taskManager.addSubtask(subtask2);

        ArrayList<Subtask> subtasks = taskManager.listOfSubtasks();
        ArrayList<Integer> subtasksIDFromEpic = epic.getSubtasksID();


        assertFalse(subtasks.isEmpty(), "Подзадачи не добавлены.");
        assertFalse(subtasksIDFromEpic.isEmpty(), "Подзадачи не добавлены в эпик.");

        taskManager.deleteSubtasks();
        subtasks = taskManager.listOfSubtasks();
        subtasksIDFromEpic = epic.getSubtasksID();

        assertTrue(subtasks.isEmpty(), "Подзадачи не удалены");
        assertTrue(subtasksIDFromEpic.isEmpty(), "Подзадачи не удалены из эпика");
    }

    @Test
    void allTasksTypesShouldBeDeletedCorrectly() {
        final Epic epic = new Epic("Test epicName", "Test epicDescription");
        taskManager.addEpicTask(epic);
        final Subtask subtask = new Subtask("Test subtaskName", "Test subtaskName");
        subtask.setEpicID(0);
        taskManager.addSubtask(subtask);
        final Task task = new Task("Test taskName", "Test taskDescription");
        taskManager.addTask(task);

        ArrayList<Subtask> subtasks = taskManager.listOfSubtasks();
        ArrayList<Epic> epics = taskManager.listOfEpics();
        ArrayList<Task> tasks = taskManager.listOfTasks();

        assertFalse(tasks.isEmpty(), "Задачи не добавлены.");
        assertFalse(subtasks.isEmpty(), "Подзадачи не добавлены.");
        assertFalse(epics.isEmpty(), "Эпики не добавлены.");

        taskManager.deleteAllTasks();

        subtasks = taskManager.listOfSubtasks();
        epics = taskManager.listOfEpics();
        tasks = taskManager.listOfTasks();

        assertTrue(tasks.isEmpty(), "Задачи не удалены.");
        assertTrue(subtasks.isEmpty(), "Подзадачи не удалены.");
        assertTrue(epics.isEmpty(), "Эпики не удалены.");
    }

    @Test
    void updatedTaskShouldBeEqualToUpdate() {
        final Task task = new Task("Test taskName", "Test taskDescription");
        taskManager.addTask(task);
        final Task taskUpdate = new Task("Updated taskName", "Updated taskDescription");
        taskUpdate.setID(0);
        taskUpdate.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(taskUpdate);

        Task updatedTask = taskManager.getTaskInfo(0);

        assertEquals(taskUpdate.getName(), updatedTask.getName(), "Имя не обновилось");
        assertEquals(taskUpdate.getDescription(), updatedTask.getDescription(), "Описание не обновилось");
        assertEquals(taskUpdate.getTaskStatus(), updatedTask.getTaskStatus(), "Статус не обновился");
    }

    @Test
    void updatedEpicShouldBeEqualToUpdate() {
        final Epic epic = new Epic("Test epicName", "Test epicDescription");
        taskManager.addEpicTask(epic);
        final Epic epicUpdate = new Epic("Updated epicName", "Updated epicDescription");
        epicUpdate.setID(0);
        taskManager.updateEpic(epicUpdate);

        Epic updatedEpic = (Epic) taskManager.getTaskInfo(0);

        assertEquals(epicUpdate.getName(), updatedEpic.getName(), "Имя не обновилось");
        assertEquals(epicUpdate.getDescription(), updatedEpic.getDescription(), "Описание не обновилось");
    }

    @Test
    void updatedSubtaskShouldBeEqualToUpdate() {
        final Epic epic = new Epic("Test epicName", "Test epicDescription");
        taskManager.addEpicTask(epic);
        final Subtask subtask = new Subtask("Test subtaskName", "Test subtaskName");
        subtask.setEpicID(0);
        taskManager.addSubtask(subtask);
        final Subtask subtaskUpdate = new Subtask("Updated subtaskName",
                "Updated subtaskDescription");
        subtaskUpdate.setID(1);
        subtaskUpdate.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtaskUpdate);

        Subtask updatedSubtask = (Subtask) taskManager.getTaskInfo(1);

        assertEquals(subtaskUpdate.getName(), updatedSubtask.getName(), "Имя не обновилось");
        assertEquals(subtaskUpdate.getDescription(), updatedSubtask.getDescription(), "Описание не обновилось");
        assertEquals(subtaskUpdate.getTaskStatus(), updatedSubtask.getTaskStatus(), "Статус не обновился");
    }

    @Test
    void listOfSubtasksFromTheEpicShouldBeEqualToLinkedSubtasks() {
        final Epic epic = new Epic("Test epicName", "Test epicDescription");
        taskManager.addEpicTask(epic);
        final Subtask subtask = new Subtask("Test subtaskName", "Test subtaskName");
        subtask.setEpicID(0);
        taskManager.addSubtask(subtask);

        ArrayList<Subtask> subtasksFromEpic = taskManager.createSubtaskListOfOneEpic(0);

        assertFalse(subtasksFromEpic.isEmpty(), "Подзадачи не возвращаются.");
        assertEquals(1, subtasksFromEpic.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasksFromEpic.getFirst(), "Подзадачи не совпадают.");

        taskManager.deleteSubtasks();
        subtasksFromEpic = taskManager.createSubtaskListOfOneEpic(0);

        assertEquals(0, subtasksFromEpic.size(), "Удаленная подзадача не открепилась от эпика");
    }

    @Test
    void tasksReturnedByHistoryShouldBeEqualToThoseStoredInHistory() {
        final Task task = new Task("Test taskName", "Test taskDescription");
        taskManager.addTask(task);
        final Epic epic = new Epic("Test epicName", "Test epicDescription");
        taskManager.addEpicTask(epic);
        final Subtask subtask = new Subtask("Test subtaskName", "Test subtaskName");
        subtask.setEpicID(1);
        taskManager.addSubtask(subtask);

        taskManager.getTaskInfo(0);
        taskManager.getTaskInfo(1);
        taskManager.getTaskInfo(2);


        final ArrayList<Task> history = taskManager.getHistory();

        assertFalse(history.isEmpty(), "Задачи не сохраняются в истории.");
        assertEquals(3, history.size(), "Неверное количество задач в истории.");
        assertEquals(task, history.get(0), "Задачи не совпадают.");
        assertEquals(epic, history.get(1), "Эпики не совпадают.");
        assertEquals(subtask, history.get(2), "Подзадачи не совпадают.");
    }

    @Test
    void endTimeOfTaskShouldBeCalculatedCorrectly() {
        Duration duration = Duration.ofMinutes(10);
        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 1, 1);
        final Task task = new Task("Test taskName", "Test taskDescription", duration, dateTime);

        assertEquals(dateTime.plus(duration), task.getEndTime(), "Время окончания задачи рассчитанно не корректно");
    }

    @Test
    void durationAndStartTimeShouldBeSavedInTheTaskCorrectly() {
        Duration duration = Duration.ofMinutes(10);
        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 1, 1);
        final Task task = new Task("Test taskName", "Test taskDescription", duration, dateTime);

        assertEquals(duration, task.getDuration(), "Продолжительность задачи сохранилась не корректно");
        assertEquals(dateTime, task.getStartTime(), "Время начала задачи сохранилось не корректно");
    }

    @Test
    void tasksShouldBeSortedCorrectlyByDate() {
        Duration duration = Duration.ofMinutes(10);
        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 1, 1);
        final Task task = new Task("Test taskName", "Test taskDescription", duration, dateTime);
        taskManager.addTask(task);
        Duration duration2 = Duration.ofMinutes(10);
        LocalDateTime dateTime2 = LocalDateTime.of(1999, 1, 1, 1, 1);
        final Task task2 = new Task("Test taskName", "Test taskDescription", duration2, dateTime2);
        taskManager.addTask(task2);

        assertEquals(taskManager.getPrioritizedTasks().first(), task2,
                "Задача с более ранним временем начал не стоит первой");
        assertEquals(taskManager.getPrioritizedTasks().last(), task,
                "Задача с более поздним временем начала не стоит последней");
    }

    @Test
    void tasksWithoutTimeShouldNotBeAddedToPrioritizedTasks() {
        final Task task = new Task("Test taskName", "Test taskDescription");
        taskManager.addTask(task);

        assertEquals(taskManager.getPrioritizedTasks().size(), 0,
                "Задача без времени добавилась в PrioritizedTasks");
    }

    @Test
    void taskThatOverlapsAnotherInTimeShouldNotBeAddedToPrioritizedTasks() {
        Duration duration = Duration.ofMinutes(10);
        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 1, 1);
        final Task task = new Task("Test taskName", "Test taskDescription", duration, dateTime);
        taskManager.addTask(task);
        Duration duration2 = Duration.ofMinutes(10);
        LocalDateTime dateTime2 = LocalDateTime.of(2000, 1, 1, 1, 1);
        final Task task2 = new Task("Test taskName", "Test taskDescription", duration2, dateTime2);
        taskManager.addTask(task2);
        Duration duration3 = Duration.ofMinutes(10);
        LocalDateTime dateTime3 = LocalDateTime.of(2000, 1, 1, 1, 0);
        final Task task3 = new Task("Test taskName", "Test taskDescription", duration3, dateTime3);
        taskManager.addTask(task3);
        Duration duration4 = Duration.ofMinutes(1000);
        LocalDateTime dateTime4 = LocalDateTime.of(2000, 1, 1, 1, 0);
        final Task task4 = new Task("Test taskName", "Test taskDescription", duration4, dateTime4);
        taskManager.addTask(task4);

        assertEquals(taskManager.getPrioritizedTasks().size(), 1,
                "Задача пересекающая другую была добавлена в PrioritizedTasks");
        assertEquals(taskManager.getPrioritizedTasks().first(), task,
                "Задача пересекающая другую была добавлена в PrioritizedTasks и заменила её");
    }
}
