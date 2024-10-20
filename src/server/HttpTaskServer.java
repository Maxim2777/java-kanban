package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import services.Managers;
import services.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private final Gson gson;

    // Создает сервер на порту 8080
    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        initializeHandlers();
    }

    // Инициализирует обработчики для различных типов задач и связывает их с соответствующими URL
    private void initializeHandlers() {
        // Обработчик для задач
        server.createContext("/tasks", new TaskHandler(taskManager, gson));

        // Обработчик для подзадач
        server.createContext("/subtasks", new SubtaskHandler(taskManager, gson));

        // Обработчик для эпиков
        server.createContext("/epics", new EpicHandler(taskManager, gson));

        // Обработчик для истории задач
        server.createContext("/history", new HistoryHandler(taskManager, gson));

        // Обработчик для задач по приоритету
        server.createContext("/prioritized", new PrioritizedTasksHandler(taskManager, gson));
    }

    // Запускает HTTP сервер и выводит сообщение о начале работы
    public void start() {
        server.start();
        System.out.println("HTTP Task Server started on port " + PORT);
    }

    // Останавливает HTTP сервер и выводит сообщение об остановке работы
    public void stop() {
        server.stop(0);
        System.out.println("HTTP Task Server stopped.");
    }

    // Создает экземпляр менеджера задач и запускает сервер
    public static void main(String[] args) {
        try {
            TaskManager taskManager = Managers.getDefaultFileBackedTaskManager();
            HttpTaskServer server = new HttpTaskServer(taskManager);
            server.start();
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
}