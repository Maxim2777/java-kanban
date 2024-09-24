package services;

import java.io.File;

public class Managers {

    public static final File CSV_FILE = new File("src/services/SavedTasks.CSV");

    public static TaskManager getDefault () {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultFileBackedTaskManager() {
        return new FileBackedTaskManager(CSV_FILE);
    }

}
