package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import services.Managers;
import services.TaskManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    private TaskManager taskManager;
    private Epic epic;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        epic = new Epic("Test epicName", "Test epicDescription");
        taskManager.addEpicTask(epic);
        Subtask subtask = new Subtask("Test subtaskName", "Test subtaskName");
        subtask.setEpicID(0);
        taskManager.addSubtask(subtask);
        Subtask subtask2 = new Subtask("Test subtaskName2", "Test subtaskName2");
        subtask.setEpicID(0);
        taskManager.addSubtask(subtask2);

    }

    @Test
    void epicShouldHaveStatusNewAfterAddingSubtasks() {

        assertFalse(epic.emptySubtasksID(), "Список привязанных подзадач пуст");
        assertEquals(TaskStatus.NEW, epic.taskStatus, "Эпик не имеет статус NEW");
    }

    @Test
    void epicShouldHaveStatusInProgressWhenNotAllSubtasksNewAndDoneWhenAllSubtasksDone() {
        Subtask subtaskUpdate = new Subtask("Test updatedName", "Test updatedDescription");
        subtaskUpdate.setTaskStatus(TaskStatus.IN_PROGRESS);
        subtaskUpdate.setID(1);
        taskManager.updateSubtask(subtaskUpdate);


        assertEquals(TaskStatus.IN_PROGRESS, epic.taskStatus, "Эпик не изменил статус на IN_PROGRESS");

        subtaskUpdate.setTaskStatus(TaskStatus.DONE);
        subtaskUpdate.setID(1);
        taskManager.updateSubtask(subtaskUpdate);

        assertEquals(TaskStatus.IN_PROGRESS, epic.taskStatus, "Эпик не имеет статус IN_PROGRESS");

        subtaskUpdate.setID(2);
        taskManager.updateSubtask(subtaskUpdate);

        assertEquals(TaskStatus.DONE, epic.taskStatus, "Эпик не изменил статус на DONE");
    }

    @Test
    void epicShouldBeNewIfAllSubtasksNewOrHasNoSubtasksAfterDeleteSubtask() {
        Subtask subtaskUpdate = new Subtask("Test updatedName", "Test updatedDescription");
        subtaskUpdate.setTaskStatus(TaskStatus.IN_PROGRESS);
        subtaskUpdate.setID(1);
        taskManager.updateSubtask(subtaskUpdate);


        assertEquals(TaskStatus.IN_PROGRESS, epic.taskStatus, "Эпик не изменил статус на IN_PROGRESS");

        taskManager.deleteByID(1);

        assertEquals(TaskStatus.NEW, epic.taskStatus, "Эпик не изменил статус на NEW, когда все подзадачи NEW");

        subtaskUpdate.setID(2);
        taskManager.updateSubtask(subtaskUpdate);

        assertEquals(TaskStatus.IN_PROGRESS, epic.taskStatus, "Эпик не изменил статус на IN_PROGRESS");

        taskManager.deleteByID(2);

        assertTrue(epic.emptySubtasksID(), "Список привязанных подзадач не пуст, после удаления всех подзадач");
        assertEquals(TaskStatus.NEW, epic.taskStatus, "Эпик не изменил статус на NEW, когда нет подзадач");

    }
}