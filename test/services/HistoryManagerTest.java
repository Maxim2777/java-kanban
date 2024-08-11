package services;

import models.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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

}