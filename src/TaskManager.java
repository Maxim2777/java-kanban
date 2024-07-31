import java.util.HashMap;
import java.util.Map;

public class TaskManager {

    static int taskID = 0;
    static HashMap<Integer, Task> tasks  = new HashMap<>();
    static HashMap<Integer, Epic> epicTasks = new HashMap<>();
    static HashMap<Integer, Subtask> subtasks = new HashMap<>();

    //Метод для удаления всех заадач
    public static void deleteAllTasks() {
        tasks.clear();
        epicTasks.clear();
        subtasks.clear();
        taskID = 0;
    }

    //Получение задачи по id
    //Метод проверяет, к какому типу задачи принадлежит данный ID и выводит информацию, с учетом этого
    public static String getTaskInfo(int ID) {
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

    //Обновить содержимое задачи по ID
    public static void updateTaskDescription(int updateTaskID, String updatedDescription) {

        if (tasks.containsKey(updateTaskID)) {
            Task updatedTask = tasks.get(updateTaskID);
            updatedTask.setDescription(updatedDescription);
            tasks.put(updateTaskID, updatedTask);

        } else if (epicTasks.containsKey(updateTaskID)) {
            Epic updatedEpicTask = epicTasks.get(updateTaskID);
            updatedEpicTask.setDescription(updatedDescription);
            epicTasks.put(updateTaskID, updatedEpicTask);

        } else if (subtasks.containsKey(updateTaskID)) {
            Subtask updatedSubtask = subtasks.get(updateTaskID);
            updatedSubtask.setDescription(updatedDescription);
            subtasks.put(updateTaskID, updatedSubtask);

        } else {
            System.out.println("Такой ID не найден");
        }
    }

    //Удалить задачу по ID
    public static void deleteByID(int deleteTaskID) {

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

    //Измение статуса задачи
    public static void changeTaskStatus(int changeTaskStatusID, int choice) {

        if (tasks.containsKey(changeTaskStatusID)) {
            Task newStatusTask = tasks.get(changeTaskStatusID);
            newStatusTask.changeTaskStatus(choice);

        } else if (epicTasks.containsKey(changeTaskStatusID)) {
            Epic newStatusEpic = epicTasks.get(changeTaskStatusID);
            newStatusEpic.changeTaskStatus(choice);
            for (int subtaskID : newStatusEpic.getSubtasksID()) { // При изменинии статуса эпика напрямую, его подзадачи
                Subtask subtask = subtasks.get(subtaskID);        // меняют статусвместе с ним
                subtask.changeStatusFromEpic(choice);
            }
        } else if (subtasks.containsKey(changeTaskStatusID)) {
            Subtask newStatusSubtask = subtasks.get(changeTaskStatusID);
            newStatusSubtask.changeTaskStatus(choice);
            Epic newStatusEpic = epicTasks.get(newStatusSubtask.getEpicID());
            if (choice == 1 && newStatusEpic.getTaskStatus() == TaskStatus.NEW) { // Если эпик новый и подзадача меняет
                newStatusEpic.changeTaskStatus(choice);                           // статус, он переходит в статус IN_PROGRESS
            } else if (choice == 2) {                                             // Если все подзадачи DONE, эпик тоже
                newStatusEpic.changeEpicStatusFromSubtask(subtasks);              // получает статус DONE
            }

        } else {
            System.out.println("Такой ID не найден");
        }
    }

    //Получение списка всех задач
    public static String listOfAllTasks() {
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

    //Добавить обычную задачу
    public static void addTask(String name, String description) {
        Task addTask = new Task(name, description);
        tasks.put(taskID, addTask);
        System.out.println("Добавлена задача с ID " + taskID);
        taskID += 1;
    }

    //Добавить эпик задачу
    public static void addEpicTask(String epicName, String epicDescription) {
        Epic addTask1 = new Epic(epicName, epicDescription);
        epicTasks.put(taskID, addTask1);
        System.out.println("Добавлена эпик задача с ID " + taskID);
        taskID += 1;
    }

    //Добавить подзадачу
    public static void addSubtask(int epicID, String subtaskName,String subtaskDescription) {
        if (epicTasks.containsKey(epicID)) {

            Subtask addTask3 = new Subtask(subtaskName, subtaskDescription);
            addTask3.setEpicID(epicID);

            subtasks.put(taskID, addTask3);

            Epic epic = epicTasks.get(epicID);
            epic.subtasksAdd(taskID);
            epic.changeEpicStatusFromNewSubtask(); //Добавление в эпик подзадачи и изменение статуса

            System.out.println("Добавлена подзадача с ID " + taskID);
            taskID += 1;
        } else {
            System.out.println("Нет Эпик задачи с ID " + epicID);
        }
    }


}
