package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import services.Managers;
import services.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    private TaskManager taskManager;
    private Epic epic;
    private Subtask subtask;
    private Subtask subtask2;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        epic = new Epic("Test epicName", "Test epicDescription");
        taskManager.addEpicTask(epic);
        Duration duration = Duration.ofMinutes(10);
        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 1, 1);
        subtask = new Subtask("Test subtaskName", "Test subtaskName", duration, dateTime);
        subtask.setEpicID(0);
        taskManager.addSubtask(subtask);
        Duration duration2 = Duration.ofMinutes(100);
        LocalDateTime dateTime2 = LocalDateTime.of(2001, 1, 1, 1, 1);
        subtask2 = new Subtask("Test subtaskName2", "Test subtaskName2", duration2, dateTime2);
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

    @Test
    void startTimeOfEpicMustMatchEarliestStartTimeAmongSubtasks() {
        assertEquals(subtask.getStartTime(), epic.getStartTime(),
                "Эпик начинается не одновременно с самой ранней подзадачей");
    }

    @Test
    void endTimeOfEpicMustMatchLatestEndTimeAmongSubtasks() {
        assertEquals(subtask2.getEndTime(), epic.getEndTime(),
                "Эпик заканчивается не одновременно с самой поздней подзадачей");
    }

    @Test
    void durationOfEpicMustBeEqualToDurationOfAllItsSubtasks() {
        assertEquals(subtask.getDuration().plus(subtask2.getDuration()), epic.getDuration(),
                "Продолжительность эпика должна быть равна продолжительности всех его подзадач");
    }
}