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

    //Загрузка сохраненных задач из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file.getPath()))) {
            fileReader.readLine();      //Для пропуска первой строки, т.к. в ней оглавление
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] taskParameters = line.split(",");

                switch (taskParameters[1]) {
                    case "TASK" -> {
                        Task task = Task.fromString(line);
                        fileBackedTaskManager.tasks.put(task.getID(), task);
                        if (task.getStartTime() != null) {
                            fileBackedTaskManager.prioritizedTasks.add(task);
                        }
                        if (task.getID() > fileBackedTaskManager.taskID) {
                            fileBackedTaskManager.taskID = task.getID();
                        }
                    }
                    case "EPIC" -> {
                        Epic epic = Epic.fromString(line);
                        fileBackedTaskManager.epicTasks.put(epic.getID(), epic);
                        if (epic.getStartTime() != null) {
                            fileBackedTaskManager.prioritizedTasks.add(epic);
                        }
                        if (epic.getID() > fileBackedTaskManager.taskID) {
                            fileBackedTaskManager.taskID = epic.getID();
                        }
                    }
                    case "SUBTASK" -> {
                        Subtask subtask = Subtask.fromString(line);
                        fileBackedTaskManager.subtasks.put(subtask.getID(), subtask);
                        if (subtask.getStartTime() != null) {
                            fileBackedTaskManager.prioritizedTasks.add(subtask);
                        }
                        if (subtask.getID() > fileBackedTaskManager.taskID) {
                            fileBackedTaskManager.taskID = subtask.getID();
                        }
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

        //Обновим время в эпиках
        for (Epic epic : fileBackedTaskManager.epicTasks.values()) {
            epic.epicTimeCalculate(fileBackedTaskManager.subtasks);
        }

        fileBackedTaskManager.taskID += 1;
        return fileBackedTaskManager;
    }

    //Сохранение существующих задач в файл
    private void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,duration,startTime,epic");

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
            return String.format("\n%d,%s,%s,%s,%s,%s,%s,",
                    task.getID(),
                    TaskType.EPIC,
                    task.getName(),
                    task.getTaskStatus(),
                    task.getDescription(),
                    (task.getDuration() != null ? task.getDuration().toMinutes() : ""),
                    (task.getStartTime() != null ? task.getStartTime() : ""));
        } else if (task instanceof Subtask) {
            return String.format("\n%d,%s,%s,%s,%s,%s,%s,%s",
                    task.getID(),
                    TaskType.SUBTASK,
                    task.getName(),
                    task.getTaskStatus(),
                    task.getDescription(),
                    (task.getDuration() != null ? task.getDuration().toMinutes() : ""),
                    (task.getStartTime() != null ? task.getStartTime() : ""),
                    ((Subtask) task).getEpicID());
        } else {
            return String.format("\n%d,%s,%s,%s,%s,%s,%s,",
                    task.getID(),
                    TaskType.TASK,
                    task.getName(),
                    task.getTaskStatus(),
                    task.getDescription(),
                    (task.getDuration() != null ? task.getDuration().toMinutes() : ""),
                    (task.getStartTime() != null ? task.getStartTime() : ""));
        }
    }

    @Override
    public boolean addTask(Task task) {
        boolean isAdded = super.addTask(task);
        if (isAdded) {
            save();
        }
        return isAdded;
    }

    @Override
    public boolean addEpicTask(Epic epic) {
        boolean isAdded = super.addEpicTask(epic);
        if (isAdded) {
            save();
        }
        return isAdded;
    }

    @Override
    public boolean addSubtask(Subtask subtask) {
        boolean isAdded = super.addSubtask(subtask);
        if (isAdded) {
            save();
        }
        return isAdded;
    }

    @Override
    public boolean updateTask(Task updatedTask) {
        boolean isUpdated = super.updateTask(updatedTask);
        if (isUpdated) {
            save();
        }
        return isUpdated;
    }

    @Override
    public boolean updateEpic(Epic updatedEpic) {
        boolean isUpdated = super.updateEpic(updatedEpic);
        if (isUpdated) {
            save();
        }
        return isUpdated;
    }

    @Override
    public boolean updateSubtask(Subtask updatedSubtask) {
        boolean isUpdated = super.updateSubtask(updatedSubtask);
        if (isUpdated) {
            save();
        }
        return isUpdated;
    }

    @Override
    public boolean deleteByID(int deleteTaskID) {
        boolean isDeleted = super.deleteByID(deleteTaskID);
        if (isDeleted) {
            save();
        }
        return isDeleted;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }
}
