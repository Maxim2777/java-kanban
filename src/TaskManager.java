import java.util.ArrayList;

public interface TaskManager {
    //Метод для удаления всех заадач
    void deleteAllTasks();

    //Метод для удаления обычных заадач
    void deleteTasks();

    //Метод для удаления эпик заадач
    void deleteEpics();

    //Метод для удаления подзадач
    void deleteSubtasks();

    //Получение задачи по id
    Task getTaskInfo(int ID);

    //Обновление содержимого обычной задачи
    void updateTask(Task updatedTask);

    //Обновление содержимого эпик задачи
    void updateEpic(Epic updatedEpic);

    //Обновление содержимого подзадачи
    void updateSubtask(Subtask updatedSubtask);

    //Удалить задачу по ID
    void deleteByID(int deleteTaskID);

    //Получение списка простых задач
    ArrayList<Task> listOfTasks();

    //Получение списка эпик задач
    ArrayList<Epic> listOfEpics();

    //Получение списка подзадач
    ArrayList<Subtask> listOfSubtasks();

    //Добавить обычную задачу
    void addTask(Task task);

    //Добавить эпик задачу
    void addEpicTask(Epic epic);

    //Добавить подзадачу
    void addSubtask(Subtask subtask);

    ArrayList<Subtask> createSubtaskListOfOneEpic(int epicIDForFullInfo);

    ArrayList<Task> getHistory();
}
