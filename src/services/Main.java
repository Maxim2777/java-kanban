package services;

import models.Epic;
import models.Subtask;
import models.Task;
import models.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {
    static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        TaskManager taskManager = Managers.getDefault();
        TaskManager fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager();

                /*Меню добавлено с целью тестирования работоспособности методов.
                По возможности, добавил комментарии*/

        while (true) {
            printMenu();
            String commandValue = scanner.nextLine();
            switch (commandValue) {
                case "1.1":
                    //Добавить задачу
                    System.out.println("Введите название Задачи");
                    String name = scanner.nextLine();
                    System.out.println("Введите описание Задачи");
                    String description = scanner.nextLine();
                    Task task = new Task(name, description);
                    taskManager.addTask(task);
                    break;

                case "1.2":
                    //Добавить эпик задачу
                    System.out.println("Введите название Эпик Задачи");
                    String epicName = scanner.nextLine();
                    System.out.println("Введите описание Эпик Задачи");
                    String epicDescription = scanner.nextLine();
                    Epic epic = new Epic(epicName, epicDescription);
                    taskManager.addEpicTask(epic);
                    break;

                case "1.3":
                    //Добавить подзадачу
                    System.out.println("Введите ID Эпик задачи к которой относится подзадача");
                    int epicID = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите название Подзадачи");
                    String subtaskName = scanner.nextLine();
                    System.out.println("Введите описание Подзадачи");
                    String subtaskDescription = scanner.nextLine();
                    Subtask subtask = new Subtask(subtaskName, subtaskDescription);
                    subtask.setEpicID(epicID);
                    taskManager.addSubtask(subtask);
                    break;

                case "2.1":
                    //Получение списка всех обычных задач
                    System.out.println(taskManager.listOfTasks());
                    break;

                case "2.2":
                    //Получение списка всех эпик задач
                    System.out.println(taskManager.listOfEpics());
                    break;

                case "2.3":
                    //Получение списка всех подзадач
                    System.out.println(taskManager.listOfSubtasks());
                    break;

                case "3":
                    //Удаление всех задач
                    taskManager.deleteAllTasks();
                    break;

                case "3.1":
                    //Удаление задач
                    taskManager.deleteTasks();
                    break;

                case "3.2":
                    //Удаление эпик задач
                    taskManager.deleteEpics();
                    break;

                case "3.3":
                    //Удаление подзадач
                    taskManager.deleteSubtasks();
                    break;

                case "4":
                    //Получение задачи по id
                    System.out.println("Введите ID");
                    int ID = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println(taskManager.getTaskInfo(ID));
                    break;

                case "5.1":
                    //Обновить описание задачи по ID
                    System.out.println("Введите ID");
                    int idOfUpdatedTask = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("Введите новое имя");
                    String updatedTaskName = scanner.nextLine();

                    System.out.println("Введите новое содержание");
                    String updatedTaskDescription = scanner.nextLine();

                    System.out.println("Введите статус: 1 - NEW 2 - IN_PROGRESS 3 - DONE");
                    int choiceTaskStatus = scanner.nextInt();
                    scanner.nextLine();

                    Task updatedTask = new Task(updatedTaskName, updatedTaskDescription);
                    updatedTask.setID(idOfUpdatedTask);

                    if (choiceTaskStatus < 1 || choiceTaskStatus > 3) {
                        System.out.println("Неправильно выбран статус");
                        break;
                    }
                    switch (choiceTaskStatus) {
                        case 1:
                            updatedTask.setTaskStatus(TaskStatus.NEW);
                            break;
                        case 2:
                            updatedTask.setTaskStatus(TaskStatus.IN_PROGRESS);
                            break;
                        case 3:
                            updatedTask.setTaskStatus(TaskStatus.DONE);
                            break;
                    }
                    taskManager.updateTask(updatedTask);
                    break;

                case "5.2":
                    //Обновить описание эпик задачи по ID
                    System.out.println("Введите ID");
                    int idOfUpdatedEpic = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите новое имя");
                    String updatedEpicName = scanner.nextLine();
                    System.out.println("Введите новое содержание");
                    String updatedEpicDescription = scanner.nextLine();
                    Epic updatedEpic = new Epic(updatedEpicName, updatedEpicDescription);
                    updatedEpic.setID(idOfUpdatedEpic);
                    //У эпиков статус меняется только вслед за подзадачами, поэтому статус в обновлении не запрашиваю

                    taskManager.updateEpic(updatedEpic);
                    break;

                case "5.3":
                    //Обновить описание подзадачи по ID
                    System.out.println("Введите ID");
                    int idOfUpdatedSubtask = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите новое имя");
                    String updatedSubtaskName = scanner.nextLine();
                    System.out.println("Введите новое содержание");
                    String updatedSubtaskDescription = scanner.nextLine();

                    System.out.println("Введите статус: 1 - NEW 2 - IN_PROGRESS 3 - DONE");
                    int choiceSubtaskStatus = scanner.nextInt();
                    scanner.nextLine();

                    Subtask updatedSubtask = new Subtask(updatedSubtaskName, updatedSubtaskDescription);
                    updatedSubtask.setID(idOfUpdatedSubtask);

                    if (choiceSubtaskStatus < 1 || choiceSubtaskStatus > 3) {
                        System.out.println("Неправильно выбран статус");
                        break;
                    }
                    switch (choiceSubtaskStatus) {
                        case 1:
                            updatedSubtask.setTaskStatus(TaskStatus.NEW);
                            break;
                        case 2:
                            updatedSubtask.setTaskStatus(TaskStatus.IN_PROGRESS);
                            break;
                        case 3:
                            updatedSubtask.setTaskStatus(TaskStatus.DONE);
                            break;
                    }

                    taskManager.updateSubtask(updatedSubtask);
                    break;

                case "6":
                    //Удалить задачу по ID
                    System.out.println("Введите ID");
                    int deleteTaskID = scanner.nextInt();
                    scanner.nextLine();
                    taskManager.deleteByID(deleteTaskID);
                    break;

                case "7":
                    //Получить полный список подзадач по ID эпика
                    System.out.println("Введите ID");
                    int epicIDForFullInfo = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println(taskManager.createSubtaskListOfOneEpic(epicIDForFullInfo));
                    break;

                //Просмотреть задачи, вызванные через пункт 4
                case "8":
                    System.out.println(taskManager.getHistory());
                    break;


                //Дальнейшие команды нужны для тестирования сохранения задач:


                case "9.1":
                    //Добавить задачу
                    System.out.println("Введите название Задачи");
                    String name1 = scanner.nextLine();
                    System.out.println("Введите описание Задачи");
                    String description1 = scanner.nextLine();
                    Task task1 = new Task(name1, description1);
                    fileBackedTaskManager.addTask(task1);
                    break;

                case "9.2":
                    //Добавить эпик задачу
                    System.out.println("Введите название Эпик Задачи");
                    String epicName1 = scanner.nextLine();
                    System.out.println("Введите описание Эпик Задачи");
                    String epicDescription1 = scanner.nextLine();
                    Epic epic1 = new Epic(epicName1, epicDescription1);
                    fileBackedTaskManager.addEpicTask(epic1);
                    break;

                case "9.3":
                    //Добавить подзадачу
                    System.out.println("Введите ID Эпик задачи к которой относится подзадача");
                    int epicID1 = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите название Подзадачи");
                    String subtaskName1 = scanner.nextLine();
                    System.out.println("Введите описание Подзадачи");
                    String subtaskDescription1 = scanner.nextLine();
                    Subtask subtask1 = new Subtask(subtaskName1, subtaskDescription1);
                    subtask1.setEpicID(epicID1);
                    fileBackedTaskManager.addSubtask(subtask1);
                    break;

                    //Загрузка задач из сохранения
                case "10":
                    File file = new File("src/services/SavedTasks.CSV");
                    fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
                    break;

                case "11.1":
                    //Получение списка всех обычных задач
                    System.out.println(fileBackedTaskManager.listOfTasks());
                    break;

                case "11.2":
                    //Получение списка всех эпик задач
                    System.out.println(fileBackedTaskManager.listOfEpics());
                    break;

                case "11.3":
                    //Получение списка всех подзадач
                    System.out.println(fileBackedTaskManager.listOfSubtasks());
                    break;


                case "20.1":
                    //Добавить задачу
                    System.out.println("Введите название Задачи");
                    String name2 = scanner.nextLine();
                    System.out.println("Введите описание Задачи");
                    String description2 = scanner.nextLine();
                    System.out.println("Введите время в минутах");
                    int time = scanner.nextInt();
                    Duration duration2 = Duration.ofMinutes(time);
                    System.out.println("Введите время начала:");
                    System.out.println("Год:");
                    int year = scanner.nextInt();
                    System.out.println("Месяц:");
                    int month = scanner.nextInt();
                    System.out.println("День месяца:");
                    int dayOfMonth = scanner.nextInt();
                    System.out.println("Час:");
                    int hour = scanner.nextInt();
                    System.out.println("Минуты:");
                    int minute = scanner.nextInt();
                    LocalDateTime dateTime2 = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
                    Task task2 = new Task(name2, description2, duration2, dateTime2);
                    fileBackedTaskManager.addTask(task2);
                    break;

                case "20.3":
                    //Добавить подзадачу
                    System.out.println("Введите ID Эпик задачи к которой относится подзадача");
                    int epicID2 = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите название Подзадачи");
                    String subtaskName2 = scanner.nextLine();
                    System.out.println("Введите описание Подзадачи");
                    String subtaskDescription2 = scanner.nextLine();
                    System.out.println("Введите время в минутах");
                    int time2 = scanner.nextInt();
                    Duration duration3 = Duration.ofMinutes(time2);
                    System.out.println("Введите время начала:");
                    System.out.println("Год:");
                    int year2 = scanner.nextInt();
                    System.out.println("Месяц:");
                    int month2 = scanner.nextInt();
                    System.out.println("День месяца:");
                    int dayOfMonth2 = scanner.nextInt();
                    System.out.println("Час:");
                    int hour2 = scanner.nextInt();
                    System.out.println("Минуты:");
                    int minute2 = scanner.nextInt();
                    LocalDateTime dateTime3 = LocalDateTime.of(year2, month2, dayOfMonth2, hour2, minute2);
                    Subtask subtask2 = new Subtask(subtaskName2, subtaskDescription2, duration3, dateTime3);
                    subtask2.setEpicID(epicID2);
                    fileBackedTaskManager.addSubtask(subtask2);
                    break;

                case "21":
                    System.out.println(fileBackedTaskManager.getPrioritizedTasks());
                    break;

                case "22.1":
                    System.out.println("Введите ID");
                    int idOfUpdatedTask221 = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите название Задачи");
                    String name221 = scanner.nextLine();
                    System.out.println("Введите описание Задачи");
                    String description221 = scanner.nextLine();
                    System.out.println("Введите время в минутах");
                    int time221 = scanner.nextInt();
                    Duration duration221 = Duration.ofMinutes(time221);
                    System.out.println("Введите время начала:");
                    System.out.println("Год:");
                    int year221 = scanner.nextInt();
                    System.out.println("Месяц:");
                    int month221 = scanner.nextInt();
                    System.out.println("День месяца:");
                    int dayOfMonth221 = scanner.nextInt();
                    System.out.println("Час:");
                    int hour221 = scanner.nextInt();
                    System.out.println("Минуты:");
                    int minute221 = scanner.nextInt();
                    LocalDateTime dateTime221 = LocalDateTime.of(year221, month221, dayOfMonth221, hour221, minute221);
                    Task task221 = new Task(name221, description221, duration221, dateTime221);
                    task221.setID(idOfUpdatedTask221);
                    fileBackedTaskManager.updateTask(task221);
                    break;

                case "22.3":
                    System.out.println("Введите ID");
                    int idOfUpdatedSubtask223 = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите ID Эпик задачи к которой относится подзадача");
                    int epicID223 = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите название Подзадачи");
                    String subtaskName223 = scanner.nextLine();
                    System.out.println("Введите описание Подзадачи");
                    String subtaskDescription223 = scanner.nextLine();
                    System.out.println("Введите время в минутах");
                    int time223 = scanner.nextInt();
                    Duration duration223 = Duration.ofMinutes(time223);
                    System.out.println("Введите время начала:");
                    System.out.println("Год:");
                    int year223 = scanner.nextInt();
                    System.out.println("Месяц:");
                    int month223 = scanner.nextInt();
                    System.out.println("День месяца:");
                    int dayOfMonth223 = scanner.nextInt();
                    System.out.println("Час:");
                    int hour223 = scanner.nextInt();
                    System.out.println("Минуты:");
                    int minute223 = scanner.nextInt();
                    LocalDateTime dateTime223 = LocalDateTime.of(year223, month223, dayOfMonth223, hour223, minute223);
                    Subtask subtask223 = new Subtask(subtaskName223, subtaskDescription223, duration223, dateTime223);
                    subtask223.setEpicID(epicID223);
                    subtask223.setID(idOfUpdatedSubtask223);
                    fileBackedTaskManager.updateSubtask(subtask223);
                    break;

                case "0":
                    return;

                default:
                    System.out.println("Такой команды нет.");
            }
        }
    }

    //Меню
    private static void printMenu() {
        System.out.println("1.1 - Добавление обычной задачи, 1.2 - Добавление эпик задачи, 1.3 - Добавление подзадачи");
        System.out.println("2.1 - Список простых задач, 2.2 - Список эпик задач," +
                " 2.3 - Список подзадач");
        System.out.println("3 - Удаляет все задачи, 3.1 - Удаление простых задач, 3.2 - Удаление эпик задач, " +
                "3.3 - Удаление подзадач");
        System.out.println("4 - Получение подробной информации о задаче");
        System.out.println("5.1 - Изменить содержимое задачи, 5.2 - Изменить содержимое эпик задачи, " +
                "5.3 - Изменить содержимое подзадачи");
        System.out.println("6 - Удалить задачу по конкретному ID");
        System.out.println("7 - Получить полный список подзадач по ID эпика");
        System.out.println("8 - Показать историю просмотров задач " +
                "(показывает те задачи, которые были вызваны командой 4)");
        System.out.println("0 - Выход");
        System.out.println("\nДальнейшие команды нужны для тестирования сохранения задач:");
        System.out.println("9.1 - Добавление обычной задачи, 9.2 - Добавление эпик задачи, 9.3 - Добавление подзадачи");
        System.out.println("10 - Загрузка задач из сохранения");
        System.out.println("11.1 - Список простых задач, 11.2 - Список эпик задач," +
                " 11.3 - Список подзадач");
        System.out.println("\nДальнейшие команды нужны для тестирования сохранения задач с параметрами времени:");
        System.out.println("20.1 - Добавление обычной задачи, 20.3 - Добавление подзадачи, (эпик - 9.2)");
        System.out.println("21 - Список задач, отсортированный по времени их начала");
        System.out.println("22.1 - Обновление времени задачи, 22.3 - Обновление времени подзадачи");
    }
}
