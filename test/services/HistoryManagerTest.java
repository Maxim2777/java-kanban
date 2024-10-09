package services;

import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HistoryManagerTest {

    HistoryManager historyManager;
    Task task;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Test addNewTask", "Test addNewTask description");
        task.setID(0);
        historyManager.add(task);
    }

    @Test
    void historyShouldNotBeEmptyAfterSavingTask() {
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История не соответствует должному размеру.");
    }

    @Test
    void tasksShouldBeEqualsBeforeAndAfterAddingToHistory() {
        final ArrayList<Task> history = historyManager.getHistory();
        assertEquals(task, history.getFirst(), "Задача, сохраненная в истории, не совпадает с изначальной.");
    }

    @Test
    void taskShouldBeMovedToTheEndIfAddedAgain() {
        Task task2 = new Task("Test addNewTask2", "Test addNewTask description2");
        task2.setID(1);
        historyManager.add(task2);
        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(task2, history.get(1), "Вторая задача, сохраненная в истории, не совпадает с изначальной.");
        historyManager.add(task);
        history = historyManager.getHistory();
        assertEquals(task, history.get(1), "Первая задача не переместилась в конец, после добавления");
        assertEquals(2, history.size(), "История изменила размер" +
                " после добавления уже существующей задачи");
    }

    @Test
    void tasksShouldBeSuccessfullyDeletedByID() {
        historyManager.remove(0);
        final ArrayList<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "Задача не удалилась из истории по Id.");
    }

    @Test
    void clearingHistoryShouldWorkCorrectly() {
        Task task2 = new Task("Test addNewTask2", "Test addNewTask description2");
        task2.setID(1);
        historyManager.add(task2);
        ArrayList<Task> history = historyManager.getHistory();

        assertNotEquals(0, history.size(), "История пустая после добавления задач");

        historyManager.clearHistory();
        history = historyManager.getHistory();

        assertEquals(0, history.size(), "История не пустая после удаления всех задач");
    }
}