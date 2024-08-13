package services;

import models.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HistoryManagerTest {

    HistoryManager historyManager;
    Task task;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Test addNewTask", "Test addNewTask description");
        historyManager.add(task);
    }

    @Test
    void historyShouldNotBeEmptyAfterSavingTask() {
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    void tasksShouldBeEqualsBeforeAndAfterAddingToHistory() {
        final ArrayList<Task> history = historyManager.getHistory();
        assertEquals(task, history.getFirst(), "Задача, сохраненая в истории, не совпадает с иначальной.");
    }

    @Test
    void historyShouldNotContainMoreThanTenTasks() {
        for (int i = 1; i < 20 ; i = i + 1) {
            historyManager.add(task);
        }
        final ArrayList<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "В истории сохранилось более 10 задач.");
    }

}