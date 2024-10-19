package services;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.ArrayList;
import java.util.TreeSet;

public interface TaskManager {
    //Удалить задачу по ID
    boolean deleteByID(int deleteTaskID);

    //Метод для удаления всех задач
    void deleteAllTasks();

    //Метод для удаления обычных задач
    void deleteTasks();

    //Метод для удаления эпик задач
    void deleteEpics();

    //Метод для удаления подзадач
    void deleteSubtasks();

    //Получение задачи по id
    Task getTaskInfo(int ID);

    //Обновление содержимого обычной задачи
    boolean updateTask(Task updatedTask);

    //Обновление содержимого эпик задачи
    boolean updateEpic(Epic updatedEpic);

    //Обновление содержимого подзадачи
    boolean updateSubtask(Subtask updatedSubtask);

    //Получение списка простых задач
    ArrayList<Task> listOfTasks();

    //Получение списка эпик задач
    ArrayList<Epic> listOfEpics();

    //Получение списка подзадач
    ArrayList<Subtask> listOfSubtasks();

    //Добавить обычную задачу
    boolean addTask(Task task);

    //Добавить эпик задачу
    boolean addEpicTask(Epic epic);

    //Добавить подзадачу
    boolean addSubtask(Subtask subtask);

    //Создание списка подзадач одного эпика
    ArrayList<Subtask> createSubtaskListOfOneEpic(int epicIDForFullInfo);

    //Для передачи истории
    ArrayList<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();
}
