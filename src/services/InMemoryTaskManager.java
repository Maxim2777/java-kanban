package services;

import models.Epic;
import models.Subtask;
import models.Task;
import models.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private int taskID = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epicTasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    //Метод для удаления всех заадач
    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epicTasks.clear();
        subtasks.clear();
        taskID = 0;
        historyManager.clearHistory();
    }

    //Метод для удаления обычных заадач
    @Override
    public void deleteTasks() {
        for (Task task : tasks.values()) {
            int id = task.getID();
            historyManager.remove(id);
        }
        tasks.clear();
    }

    //Метод для удаления эпик заадач
    @Override
    public void deleteEpics() {
        for (Epic epic : epicTasks.values()) {
            int id = epic.getID();
            for (int subtaskID : epic.getSubtasksID()) {
                historyManager.remove(subtaskID);
            }
            historyManager.remove(id);
        }
        epicTasks.clear();
        subtasks.clear(); //Вслед за эпиками удаляются их подзадачи
    }

    //Метод для удаления подзадач
    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            int id = subtask.getID();
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epicTasks.values()) { //Также удаляем все ID подзадач из эпиков
            epic.deleteAllSubtasksID();
            epic.setTaskStatus(TaskStatus.NEW);
        }
    }

    //Получение задачи по id
    //Метод проверяет, к какому типу задачи принадлежит данный ID и выводит информацию, с учетом этого
    @Override
    public Task getTaskInfo(int ID) {
        Task taskInfo;

        if (tasks.containsKey(ID)) {
            taskInfo = tasks.get(ID);
        } else if (epicTasks.containsKey(ID)) {
            taskInfo = epicTasks.get(ID);
        } else if (subtasks.containsKey(ID)) {
            taskInfo = subtasks.get(ID);
        } else {
            System.out.println("Такой ID не найден");
            return null;
        }
        historyManager.add(taskInfo);
        return taskInfo;
    }

    //Обновление содержимого обычной задачи
    @Override
    public void updateTask(Task updatedTask) {
        int idOfUpdatedTask = updatedTask.getID();
        if (tasks.containsKey(idOfUpdatedTask)) {
            Task oldTask = tasks.get(idOfUpdatedTask);
            oldTask.setName(updatedTask.getName());
            oldTask.setDescription(updatedTask.getDescription());

            TaskStatus newTaskStatus = updatedTask.getTaskStatus();
            if (!(oldTask.getTaskStatus().equals(newTaskStatus))) {
                //Если обновленный статус не равен "новому", то он меняется иначе остается как прежде.
                if (!(newTaskStatus.equals(TaskStatus.NEW))) {
                    oldTask.setTaskStatus(newTaskStatus);
                } else {
                    System.out.println("Нельзя поменять статус на новый (NEW) т.к задача в работе или готова.");
                    System.out.println("Содержимое обновлено, но статус оставлен без изменений");
                }
            }
        } else {
            System.out.println("Такой ID не найден");
        }
    }

    //Обновление содержимого эпик задачи
    @Override
    public void updateEpic(Epic updatedEpic) {
        int idOfUpdatedEpic = updatedEpic.getID();
        if (epicTasks.containsKey(idOfUpdatedEpic)) {
            Epic oldEpic = epicTasks.get(idOfUpdatedEpic);
            oldEpic.setName(updatedEpic.getName());
            oldEpic.setDescription(updatedEpic.getDescription()); //У эпиков статус меняется только вслед за подзадачами
        } else {
            System.out.println("Такой ID не найден");
        }
    }

    //Обновление содержимого подзадачи
    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        int idOfUpdatedSubtask = updatedSubtask.getID();
        if (subtasks.containsKey(idOfUpdatedSubtask)) {
            Subtask oldSubtask = subtasks.get(idOfUpdatedSubtask);
            oldSubtask.setName(updatedSubtask.getName());
            oldSubtask.setDescription(updatedSubtask.getDescription());

            Epic epicOfThisSubtask = epicTasks.get(oldSubtask.getEpicID());
            TaskStatus newSubtaskStatus = updatedSubtask.getTaskStatus();
            if (!(oldSubtask.getTaskStatus().equals(newSubtaskStatus))) {
                if (newSubtaskStatus.equals(TaskStatus.IN_PROGRESS)) {
                    oldSubtask.setTaskStatus(TaskStatus.IN_PROGRESS);
                    epicOfThisSubtask.setTaskStatus(TaskStatus.IN_PROGRESS); //Эсли подзадача в процеесе, то её эпик тоже
                } else if (newSubtaskStatus.equals(TaskStatus.DONE)) {
                    oldSubtask.setTaskStatus(TaskStatus.DONE); //Если подзадача готова, то эпик нужно проверить на готовность
                    epicOfThisSubtask.changeEpicStatusFromSubtask(subtasks); //(Все ли подзадачи готовы)
                } else {
                    System.out.println("Нельзя поменять статус на новый (NEW) т.к подзадача в работе или готова.");
                    System.out.println("Содержимое обновлено, но статус оставлен без изменений");
                }
            }
        } else {
            System.out.println("Такой ID не найден");
        }
    }

    //Удалить задачу по ID
    @Override
    public void deleteByID(int deleteTaskID) {

        if (tasks.containsKey(deleteTaskID)) {
            tasks.remove(deleteTaskID);
            historyManager.remove(deleteTaskID);

        } else if (epicTasks.containsKey(deleteTaskID)) {
            Epic epicTask = epicTasks.get(deleteTaskID);
            if (!epicTask.emptySubtasksID()) {
                for (int subtaskID : epicTask.getSubtasksID()) {  // При удалении эпик задачи, все его подзадачи,
                    subtasks.remove(subtaskID);                   // удаляются вместе с ним
                    historyManager.remove(subtaskID);
                }
            }
            epicTasks.remove(deleteTaskID);
            historyManager.remove(deleteTaskID);

        } else if (subtasks.containsKey(deleteTaskID)) {
            Subtask subtask = subtasks.get(deleteTaskID);
            int epicID2 = subtask.getEpicID();
            Epic epicTask = epicTasks.get(epicID2);                   // При удалении подзадачи, эпик проверяется на то,
            epicTask.deleteSubtaskID(deleteTaskID);                   // выполнены ли другие подзадачи, если - да, то
            epicTask.changeEpicStatusFromSubtaskAfterDelete(subtasks); // меняет стаутс на DONE
            subtasks.remove(deleteTaskID);
            historyManager.remove(deleteTaskID);
        } else {
            System.out.println("Такой ID не найден");
        }
    }

    //Получение списка простых задач
    @Override
    public ArrayList<Task> listOfTasks() {
        return new ArrayList<>(tasks.values());
    }

    //Получение списка эпик задач
    @Override
    public ArrayList<Epic> listOfEpics() {
        return new ArrayList<>(epicTasks.values());
    }

    //Получение списка подзадач
    @Override
    public ArrayList<Subtask> listOfSubtasks() {
        return new ArrayList<>(subtasks.values());
    }


    //Добавить обычную задачу
    @Override
    public void addTask(Task task) {
        task.setID(taskID);
        tasks.put(taskID, task);
        System.out.println("Добавлена задача с ID " + taskID);
        taskID += 1;
    }

    //Добавить эпик задачу
    @Override
    public void addEpicTask(Epic epic) {
        epic.setID(taskID);
        epicTasks.put(taskID, epic);
        System.out.println("Добавлена эпик задача с ID " + taskID);
        taskID += 1;
    }

    //Добавить подзадачу
    @Override
    public void addSubtask(Subtask subtask) {
        if (epicTasks.containsKey(subtask.getEpicID())) {
            subtask.setID(taskID);
            subtasks.put(taskID, subtask);

            Epic epic = epicTasks.get(subtask.getEpicID());
            epic.subtasksAdd(taskID);
            epic.changeEpicStatusFromNewSubtask(); //Добавление в эпик подзадачи и изменение статуса

            System.out.println("Добавлена подзадача с ID " + taskID);
            taskID += 1;
        } else {
            System.out.println("Нет Эпик задачи с ID " + subtask.getEpicID());
        }
    }

    //Создание списка подзадач одного эпика
    @Override
    public ArrayList<Subtask> createSubtaskListOfOneEpic(int epicIDForFullInfo) {
        if (epicTasks.containsKey(epicIDForFullInfo)) {
            Epic epic = epicTasks.get(epicIDForFullInfo);
            ArrayList<Subtask> subtaskList = new ArrayList<>();
            for (int subtaskID : epic.getSubtasksID()) {
                Subtask subtask = subtasks.get(subtaskID);
                subtaskList.add(subtask);
            }
            return subtaskList;
        } else {
            System.out.println("Нет Эпик задачи с ID " + epicIDForFullInfo);
            return null;
        }
    }

    //Для передачи истории
    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

}
