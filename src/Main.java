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
                    taskManager.addTask(name, description);
                    break;

                case "1.2":
                    //Добавить эпик задачу
                    System.out.println("Введите название Эпик Задачи");
                    String epicName = scanner.nextLine();
                    System.out.println("Введите описание Эпик Задачи");
                    String epicDescription = scanner.nextLine();
                    taskManager.addEpicTask(epicName, epicDescription);
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
                    taskManager.addSubtask(epicID, subtaskName, subtaskDescription);
                    break;

                case "2":
                    //Получение списка всех задач
                    String allTasks = taskManager.listOfAllTasks();
                    System.out.println(allTasks);
                    break;

                case "3":
                    //Удалеие всех заадач
                    taskManager.deleteAllTasks();
                    break;

                case "4":
                    //Получение задачи по id
                    System.out.println("Введите ID");
                    int ID = scanner.nextInt();
                    scanner.nextLine();
                    String taskInfo = taskManager.getTaskInfo(ID);
                    System.out.println(taskInfo);
                    break;

                case "5":
                    //Обновить описание задачи по ID
                    System.out.println("Введите ID");
                    int updateTaskID = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Введите новое содержание");
                    String updatedDescription = scanner.nextLine();
                    taskManager.updateTaskDescription(updateTaskID, updatedDescription);
                    break;

                case "6":
                    //Удалить задачу по ID
                    System.out.println("Введите ID");
                    int deleteTaskID = scanner.nextInt();
                    scanner.nextLine();
                    taskManager.deleteByID(deleteTaskID);
                    break;

                case "7":
                    //Измение статуса задачи
                    System.out.println("Введите ID");
                    int changeTaskStatusID = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("На какой статус хотите сменить? Введите 1 - в работе или 2 - готово");
                    System.out.println("(Измениеие статуса эпик задачи, повлечет измение статуса всех его подзадач)");
                    System.out.println("(Измениеие статуса подзадачи, может повлеч измение статуса его эпика)");
                    int choice = scanner.nextInt();
                    scanner.nextLine();
                    taskManager.changeTaskStatus(changeTaskStatusID,choice);
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
        System.out.println("2 - Создает список всех задач");
        System.out.println("3 - Удаляет все задачи");
        System.out.println("4 - Получение подробной информации о задаче");
        System.out.println("5 - Изменить описание задачи");
        System.out.println("6 - Удалить задачу по конкретному ID");
        System.out.println("7 - Смена статуса задачи на IN_PROGRESS или DONE");
        System.out.println("0 - Выход");
    }
}
