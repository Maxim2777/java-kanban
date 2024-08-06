import java.util.HashMap;
import java.util.Map;

public class TaskManager {

    private int taskID = 0;
    private final HashMap<Integer, Task> tasks  = new HashMap<>();
    private final HashMap<Integer, Epic> epicTasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    //Метод для удаления всех заадач
    public void deleteAllTasks() {
        tasks.clear();
        epicTasks.clear();
        subtasks.clear();
        taskID = 0;
    }

    //Метод для удаления обычных заадач
    public void deleteTasks() {
        tasks.clear();
    }

    //Метод для удаления эпик заадач
    public void deleteEpics() {
        epicTasks.clear();
        subtasks.clear(); //Вслед за эпиками удаляются их подзадачи
    }

    //Метод для удаления подзадач
    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epicTasks.values()) { //Также удаляем все ID подзадач из эпиков
            epic.deleteAllSubtasksID();
        }
    }

    //Получение задачи по id
    //Метод проверяет, к какому типу задачи принадлежит данный ID и выводит информацию, с учетом этого
    public String getTaskInfo(int ID) {
        String taskInfo;

        if (tasks.containsKey(ID)) {
            Task task = tasks.get(ID);
            taskInfo = "Название задачи: " + task.getName() + "\nОписание задачи: " + task.getDescription();

        } else if (epicTasks.containsKey(ID)) {
            Epic epic = epicTasks.get(ID);
            taskInfo = "Название эпик задачи: " + epic.getName() + "\nОписание эпик задачи: " + epic.getDescription()
                    + "\nID подзадач которые в него входят: " + epic.getSubtasksID();

        } else if (subtasks.containsKey(ID)) {
            Subtask subtask = subtasks.get(ID);
            taskInfo = "Название задачи: " + subtask.getName() + "\nОписание задачи: " + subtask.getDescription()
                    + "\nЭпик к которому относится: " + subtask.getEpicID();

        } else {
            taskInfo = "Такой ID не найден";
        }
        return taskInfo;
    }

       //Обновление содержимого обычной задачи
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
    public void updateSubtask (Subtask updatedSubtask) {
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
    public void deleteByID(int deleteTaskID) {

        if (tasks.containsKey(deleteTaskID)) {
            tasks.remove(deleteTaskID);

        } else if (epicTasks.containsKey(deleteTaskID)) {
            Epic epicTask = epicTasks.get(deleteTaskID);
            if (!epicTask.emptySubtasksID()) {
                for (int subtaskID : epicTask.getSubtasksID()) {  // При удалении эпик задачи, все его подзадачи,
                    subtasks.remove(subtaskID);                   // удаляются вместе с ним
                }
            }
            epicTasks.remove(deleteTaskID);

        } else if (subtasks.containsKey(deleteTaskID)) {
            Subtask subtask = subtasks.get(deleteTaskID);
            int epicID2 = subtask.getEpicID();
            Epic epicTask = epicTasks.get(epicID2);                   // При удалении подзадачи, эпик проверяется на то,
            epicTask.deleteSubtaskID(deleteTaskID);                   // выполнены ли другие подзадачи, если - да, то
            epicTask.changeEpicStatusFromSubtaskAfterDelete(subtasks); // меняет стаутс на DONE
            subtasks.remove(deleteTaskID);
        } else {
            System.out.println("Такой ID не найден");
        }
    }

    //Получение списка всех задач
    public String listOfAllTasks() {
        String allTasks = "Список задач:";
        for (Map.Entry<Integer, Task> hashMap : tasks.entrySet()) {
            Integer ID = hashMap.getKey();
            Task task = hashMap.getValue();
            allTasks += "\nЗадача " + task.getName() + ", с ID " + ID + ", в статусе "
                    + task.getTaskStatus().name();
        }

        allTasks += "\nСписок эпик задач:";
        for (Map.Entry<Integer, Epic> hashMap : epicTasks.entrySet()) {
            Integer ID = hashMap.getKey();
            Epic epic = hashMap.getValue();
            allTasks += "\nЭпик задача " + epic.getName() + ", с ID " + ID + ", в статусе "
                    + epic.getTaskStatus().name() + ", с подзадачами " + epic.getSubtasksID();
        }

        allTasks += "\nСписок подзадач:";
        for (Map.Entry<Integer, Subtask> hashMap : subtasks.entrySet()) {
            Integer ID = hashMap.getKey();
            Subtask subtask = hashMap.getValue();
            allTasks += "\nПодзадача " + subtask.getName() + ", с ID " + ID + ", в статусе "
                    + subtask.getTaskStatus().name() + ", относится к эпику с ID " + subtask.getEpicID();
        }

        return allTasks;
    }

    //Получение задач
    public String listOfTasks() {
        String tasksList = "Список задач:";
        for (Map.Entry<Integer, Task> hashMap : tasks.entrySet()) {
            Integer ID = hashMap.getKey();
            Task task = hashMap.getValue();
            tasksList += "\nЗадача " + task.getName() + ", с ID " + ID + ", в статусе "
                    + task.getTaskStatus().name();
        }

        return tasksList;
    }

    //Получение эпик задач
    public String listOfEpics() {
        String epicsList = "Список эпик задач:";
        for (Map.Entry<Integer, Epic> hashMap : epicTasks.entrySet()) {
            Integer ID = hashMap.getKey();
            Epic epic = hashMap.getValue();
            epicsList += "\nЭпик задача " + epic.getName() + ", с ID " + ID + ", в статусе "
                    + epic.getTaskStatus().name() + ", с подзадачами " + epic.getSubtasksID();
        }

        return epicsList;
    }
    
    //Получение подзадач
    public String listOfSubtasks() {
        String subtasksList = "Список подзадач:";
        for (Map.Entry<Integer, Subtask> hashMap : subtasks.entrySet()) {
            Integer ID = hashMap.getKey();
            Subtask subtask = hashMap.getValue();
            subtasksList += "\nПодзадача " + subtask.getName() + ", с ID " + ID + ", в статусе "
                    + subtask.getTaskStatus().name() + ", относится к эпику с ID " + subtask.getEpicID();
        }

        return subtasksList;
    }
    
    

    //Добавить обычную задачу
    public void addTask(Task task) {
        task.setID(taskID);
        tasks.put(taskID, task);
        System.out.println("Добавлена задача с ID " + taskID);
        taskID += 1;
    }

    //Добавить эпик задачу
    public void addEpicTask(Epic epic) {
        epic.setID(taskID);
        epicTasks.put(taskID, epic);
        System.out.println("Добавлена эпик задача с ID " + taskID);
        taskID += 1;
    }

    //Добавить подзадачу
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

    public String createSubtaskListOfOneEpic (int epicIDForFullInfo) {
        if (epicTasks.containsKey(epicIDForFullInfo)) {
            Epic epic = epicTasks.get(epicIDForFullInfo);
            String subtasksListOfOneEpic = "Название эпик задачи: " + epic.getName() + "\nОписание эпик задачи: " + epic.getDescription()
                    + "\nID подзадач которые в него входят: " + epic.getSubtasksID();
            int i = 1;
            for (int subtaskID : epic.getSubtasksID()) {
                Subtask subtask = subtasks.get(subtaskID);
                subtasksListOfOneEpic += "\nПодзадача №" + i + "\nНазвание: " + subtask.getName() + "\nID: " + subtaskID + "\nСтатус: "
                        + subtask.getTaskStatus().name();
                i++;
            }
            return subtasksListOfOneEpic;
        } else {
            return "Такой ID не найден";
        }
    }

}
