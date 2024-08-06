import java.util.Scanner;

public class Main {
    static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager();

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

                case "2":
                    //Получение списка всех задач
                    String allTasks = taskManager.listOfAllTasks();
                    System.out.println(allTasks);
                    break;

                case "2.1":
                    //Получение списка всех обычных задач
                    String tasksList = taskManager.listOfTasks();
                    System.out.println(tasksList);
                    break;

                case "2.2":
                    //Получение списка всех эпик задач
                    String epicsList = taskManager.listOfEpics();
                    System.out.println(epicsList);
                    break;

                case "2.3":
                    //Получение списка всех подзадач
                    String subtasksList = taskManager.listOfSubtasks();
                    System.out.println(subtasksList);
                    break;

                case "3":
                    //Удалеие всех обычных заадач
                    taskManager.deleteAllTasks();
                    break;

                case "3.1":
                    //Удалеие заадач
                    taskManager.deleteTasks();
                    break;

                case "3.2":
                    //Удалеие эпик заадач
                    taskManager.deleteEpics();
                    break;

                case "3.3":
                    //Удалеие подзаадач
                    taskManager.deleteSubtasks();
                    break;

                case "4":
                    //Получение задачи по id
                    System.out.println("Введите ID");
                    int ID = scanner.nextInt();
                    scanner.nextLine();
                    String taskInfo = taskManager.getTaskInfo(ID);
                    System.out.println(taskInfo);
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
                    String subtasksListOfOneEpic = taskManager.createSubtaskListOfOneEpic(epicIDForFullInfo);
                    System.out.println(subtasksListOfOneEpic);
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
        System.out.println("2 - Список всех задач, 2.1 - Список простых задач, 2.2 - Список эпик задач," +
                " 2.3 - Список подзадач");
        System.out.println("3 - Удаляет все задачи, 3.1 - Удаление простых задач, 3.2 - Удаление эпик задач, " +
                "3.3 - Удаление подзадач");
        System.out.println("4 - Получение подробной информации о задаче");
        System.out.println("5.1 - Изменить содержимое задачи, 5.2 - Изменить содержимое эпик задачи, " +
                "5.3 - Изменить содержимое подзадачи");
        System.out.println("6 - Удалить задачу по конкретному ID");
        System.out.println("7 - Получить полный список подзадач по ID эпика");
        System.out.println("0 - Выход");
    }
}
