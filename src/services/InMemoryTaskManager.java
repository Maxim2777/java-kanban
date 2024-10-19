package services;

import models.Epic;
import models.Subtask;
import models.Task;
import models.TaskStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    protected int taskID = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epicTasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    //В three set проводим сравнение по startTime, если оно совпадает, то это эпик и подзадача, и проводится сравнение по ID
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime)
                    .thenComparing(Task::getID));

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks); // Возвращаем копию TreeSet, чтобы избежать изменения оригинала
    }

    //Метод для удаления всех задач
    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epicTasks.clear();
        subtasks.clear();
        taskID = 0;
        historyManager.clearHistory();
        prioritizedTasks.clear();
    }

    //Метод для удаления обычных задач
    @Override
    public void deleteTasks() {
        for (Task task : tasks.values()) {
            int id = task.getID();
            historyManager.remove(id);
            //Если у задачи было начальное время, значит она была в prioritizedTasks, откуда её необходимо удалить
            if (task.getStartTime() != null) {
                prioritizedTasks.remove(task);
            }
        }
        tasks.clear();
    }

    //Метод для удаления эпик задач
    @Override
    public void deleteEpics() {
        for (Epic epic : epicTasks.values()) {
            int id = epic.getID();
            historyManager.remove(id);
            if (epic.getStartTime() != null) {
                prioritizedTasks.remove(epic);
            }
        }
        epicTasks.clear();

        for (Subtask subtask : subtasks.values()) {
            int id = subtask.getID();
            historyManager.remove(id);
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
        }
        subtasks.clear(); //Вслед за эпиками удаляются их подзадачи
    }

    //Метод для удаления подзадач
    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            int id = subtask.getID();
            historyManager.remove(id);
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
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
            //Уберем старую версию из списка prioritizedTasks, чтобы корректно проверить пересечение
            if (oldTask.getStartTime() != null) {
                prioritizedTasks.remove(oldTask);
            }
            //Проверка на то, пересекается ли обновленная задача с какой-либо из существующих
            if (updatedTask.getStartTime() != null) {
                boolean overlaps = prioritizedTasks.stream().anyMatch(existingTask -> existingTask.overlapsWith(updatedTask));
                //Если пересекает возвращаем старую обратно в prioritizedTasks, а обновление не проводим
                if (overlaps) {
                    System.out.println("Новая задача пересекается с существующей задачей и не может быть добавлена.");
                    if (oldTask.getStartTime() != null) {
                        prioritizedTasks.add(oldTask);
                    }
                    return;
                }
            }

            oldTask.setName(updatedTask.getName());
            oldTask.setDescription(updatedTask.getDescription());
            oldTask.setDuration(updatedTask.getDuration());
            oldTask.setStartTime(updatedTask.getStartTime());
            if (oldTask.getStartTime() != null) {
                prioritizedTasks.add(oldTask);
            }

            TaskStatus newTaskStatus = updatedTask.getTaskStatus();
            if (!(oldTask.getTaskStatus().equals(newTaskStatus))) {
                //Если обновленный статус не равен "новому", то он меняется, иначе остается как прежде.
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
            if (oldSubtask.getStartTime() != null) {
                prioritizedTasks.remove(oldSubtask);
            }

            if (updatedSubtask.getStartTime() != null) {
                boolean overlaps = prioritizedTasks.stream().anyMatch(existingTask -> existingTask.overlapsWith(updatedSubtask));

                if (overlaps) {
                    System.out.println("Новая задача пересекается с существующей задачей и не может быть добавлена.");
                    if (updatedSubtask.getStartTime() != null) {
                        prioritizedTasks.add(oldSubtask);
                    }
                    return;
                }
            }

            oldSubtask.setName(updatedSubtask.getName());
            oldSubtask.setDescription(updatedSubtask.getDescription());
            oldSubtask.setDuration(updatedSubtask.getDuration());
            oldSubtask.setStartTime(updatedSubtask.getStartTime());
            if (oldSubtask.getStartTime() != null) {
                prioritizedTasks.add(oldSubtask);
            }

            Epic epicOfThisSubtask = epicTasks.get(oldSubtask.getEpicID());
            TaskStatus newSubtaskStatus = updatedSubtask.getTaskStatus();
            if (!(oldSubtask.getTaskStatus().equals(newSubtaskStatus))) {
                if (newSubtaskStatus.equals(TaskStatus.IN_PROGRESS)) {
                    oldSubtask.setTaskStatus(TaskStatus.IN_PROGRESS);
                    epicOfThisSubtask.setTaskStatus(TaskStatus.IN_PROGRESS); //Если подзадача в процессе, то её эпик тоже
                } else if (newSubtaskStatus.equals(TaskStatus.DONE)) {
                    oldSubtask.setTaskStatus(TaskStatus.DONE); //Если подзадача готова, то эпик нужно проверить на готовность
                    epicOfThisSubtask.changeEpicStatusFromSubtask(subtasks); //(Все ли подзадачи готовы)
                } else {
                    System.out.println("Нельзя поменять статус на новый (NEW) т.к подзадача в работе или готова.");
                    System.out.println("Содержимое обновлено, но статус оставлен без изменений");
                }
            }

            //При обновлении времени подзадачи, необходимо пересчитать время эпика
            if (epicOfThisSubtask.getStartTime() != null) {
                prioritizedTasks.remove(epicOfThisSubtask);
            }
            epicOfThisSubtask.epicTimeCalculate(subtasks);
            if (epicOfThisSubtask.getStartTime() != null) {
                prioritizedTasks.add(epicOfThisSubtask); // Добавляем эпик в TreeSet только если время начала определено
            }

        } else {
            System.out.println("Такой ID не найден");
        }
    }

    //Удалить задачу по ID
    @Override
    public void deleteByID(int deleteTaskID) {

        if (tasks.containsKey(deleteTaskID)) {
            Task task = tasks.get(deleteTaskID);
            if (task.getStartTime() != null) {
                prioritizedTasks.remove(task);
            }
            tasks.remove(deleteTaskID);
            historyManager.remove(deleteTaskID);

        } else if (epicTasks.containsKey(deleteTaskID)) {
            Epic epicTask = epicTasks.get(deleteTaskID);
            if (!epicTask.emptySubtasksID()) {
                for (int subtaskID : epicTask.getSubtasksID()) {  // При удалении эпик задачи, все его подзадачи,
                    subtasks.remove(subtaskID);                   // удаляются вместе с ним
                    historyManager.remove(subtaskID);
                    Subtask subtask = subtasks.get(subtaskID);
                    if (subtask.getStartTime() != null) {
                        prioritizedTasks.remove(subtask);
                    }
                }
            }
            if (epicTask.getStartTime() != null) {
                prioritizedTasks.remove(epicTask);
            }
            epicTasks.remove(deleteTaskID);
            historyManager.remove(deleteTaskID);

        } else if (subtasks.containsKey(deleteTaskID)) {
            Subtask subtask = subtasks.get(deleteTaskID);
            int epicID2 = subtask.getEpicID();
            Epic epicTask = epicTasks.get(epicID2);                   // При удалении подзадачи, эпик проверяется на то,
            epicTask.deleteSubtaskID(deleteTaskID);                   // выполнены ли другие подзадачи, если - да, то
            epicTask.changeEpicStatusFromSubtaskAfterDelete(subtasks); // меняет статус на DONE
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
            //При удалении подзадачи, необходимо пересчитать время эпика
            if (epicTask.getStartTime() != null) {
                prioritizedTasks.remove(epicTask);
            }
            epicTask.epicTimeCalculate(subtasks);
            if (epicTask.getStartTime() != null) {
                prioritizedTasks.add(epicTask); // Добавляем эпик в TreeSet только если время начала определено
            }
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

        //Проверка на то пересекается ли обновленная задача с какой-либо из существующих
        if (task.getStartTime() != null) {
            boolean overlaps = prioritizedTasks.stream().anyMatch(existingTask -> existingTask.overlapsWith(task));
            //Если пересекает - не добавляем
            if (overlaps) {
                System.out.println("Задача пересекается с существующей задачей и не может быть добавлена.");
                return;
            } else {
                prioritizedTasks.add(task); // Добавляем задачу в TreeSet только если время начала определено
            }
        }

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

            if (subtask.getStartTime() != null) {
                boolean overlaps = prioritizedTasks.stream().anyMatch(existingTask -> existingTask.overlapsWith(subtask));

                if (overlaps) {
                    System.out.println("Подзадача пересекается с существующей задачей и не может быть добавлена.");
                    return;
                } else {
                    prioritizedTasks.add(subtask); // Добавляем задачу в TreeSet только если время начала определено
                }
            }

            subtasks.put(taskID, subtask);

            Epic epic = epicTasks.get(subtask.getEpicID());
            epic.subtasksAdd(taskID);
            epic.changeEpicStatusFromNewSubtask(); //Добавление в эпик подзадачи и изменение статуса

            System.out.println("Добавлена подзадача с ID " + taskID);
            //При добавлении подзадачи, необходимо пересчитать время эпика
            if (epic.getStartTime() != null) {
                prioritizedTasks.remove(epic);
            }
            epic.epicTimeCalculate(subtasks);
            if (epic.getStartTime() != null) {
                prioritizedTasks.add(epic); // Добавляем эпик в TreeSet только если время начала определено
            }

            taskID += 1;
        } else {
            System.out.println("Нет Эпик задачи с ID " + subtask.getEpicID());
        }
    }

    //Создание списка подзадач одного эпика
    @Override
    public ArrayList<Subtask> createSubtaskListOfOneEpic(int epicIDForFullInfo) {
        /*if (epicTasks.containsKey(epicIDForFullInfo)) {
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
        }*/
        return Optional.ofNullable(epicTasks.get(epicIDForFullInfo))
                .map(epic -> epic.getSubtasksID().stream()
                        .map(subtasks::get) // Преобразуем ID подзадачи в объект Subtask
                        .collect(Collectors.toCollection(ArrayList::new))) // Собираем в ArrayList
                .orElseGet(() -> {
                    System.out.println("Нет Эпик задачи с ID " + epicIDForFullInfo);
                    return null; // Возвращаем null, если Epic не найден
                });
    }

    //Для передачи истории
    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

}
