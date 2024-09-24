package services;

import models.Epic;
import models.Subtask;
import models.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    //Загрузка сохраненых задач из файала
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file.getPath()))) {
            fileReader.readLine();      //для пропуска первой строки, т.к. в ней оглавление
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] taskParameters = line.split(",");

                switch (taskParameters[1]) {
                    case "TASK" -> {
                        Task task = Task.fromString(line);
                        fileBackedTaskManager.tasks.put(task.getID(), task);
                    }
                    case "EPIC" -> {
                        Epic epic = Epic.fromString(line);
                        fileBackedTaskManager.epicTasks.put(epic.getID(), epic);
                    }
                    case "SUBTASK" -> {
                        Subtask subtask = Subtask.fromString(line);
                        fileBackedTaskManager.subtasks.put(subtask.getID(), subtask);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке:" + e.getMessage());
        }

        for (Subtask subtask : fileBackedTaskManager.subtasks.values()) { //Добавляем id подзадач в эпики
            Epic epic = fileBackedTaskManager.epicTasks.get(subtask.getEpicID());
            epic.subtasksAdd(subtask.getID());
            fileBackedTaskManager.epicTasks.put(epic.getID(), epic);
        }

        return fileBackedTaskManager;
    }

    //Сохранение существующих задач в файл
    private void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,epic");

            for (Task task : tasks.values()) {
                fileWriter.write(toString(task));
            }
            for (Epic epic : epicTasks.values()) {
                fileWriter.write(toString(epic));
            }
            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(toString(subtask));
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении:" + e.getMessage());
        }
    }

    //Преобразование информации о задаче в строку
    public String toString(Task task) {
        if (task instanceof Epic) {
            return String.format("\n%d,%s,%s,%s,%s,", task.getID(), TaskType.EPIC, task.getName(), task.getTaskStatus(), task.getDescription());
        } else if (task instanceof Subtask) {
            return String.format("\n%d,%s,%s,%s,%s,%s",
                    task.getID(), TaskType.SUBTASK, task.getName(), task.getTaskStatus(), task.getDescription(), ((Subtask) task).getEpicID());
        } else {
            return String.format("\n%d,%s,%s,%s,%s,",
                    task.getID(), TaskType.TASK, task.getName(), task.getTaskStatus(), task.getDescription());
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpicTask(Epic epic) {
        super.addEpicTask(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public void deleteByID(int deleteTaskID) {
        super.deleteByID(deleteTaskID);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }
}
