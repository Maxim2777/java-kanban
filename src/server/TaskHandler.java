package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import models.Task;
import services.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET":
                    if (path.matches("/tasks/\\d+")) {
                        handleGetById(exchange);
                    } else if ("/tasks".equals(path)) {
                        handleGet(exchange);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    if (path.matches("/tasks/\\d+")) {
                        handleDeleteById(exchange);
                    } else if ("/tasks".equals(path)) {
                        handleDelete(exchange);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                default:
                    sendNotFound(exchange);
                    break;
            }
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }

    // Обработка запроса на удаление задачи по ID
    private void handleDeleteById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        int id = Integer.parseInt(splitPath[splitPath.length - 1]);

        boolean success = taskManager.deleteByID(id);

        if (success) {
            sendText(exchange, "{\"message\": \"Task deleted successfully\"}", 200);
        } else {
            sendNotFound(exchange); // Если задача с указанным ID не найдена, вернуть 404 (Не найдено)
        }
    }

    // Обработка запроса на получение задачи по ID
    private void handleGetById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        int id = Integer.parseInt(splitPath[splitPath.length - 1]);

        Task task = taskManager.getTaskInfo(id);

        if (task == null) {
            sendNotFound(exchange); // Если задача с указанным ID не найдена, вернуть 404 (Не найдено)
        } else {
            String response = gson.toJson(task);
            sendText(exchange, response, 200); // Отправляем задачу в формате JSON с кодом 200 (Успех)
        }
    }

    // Обработка запроса на получение списка задач
    private void handleGet(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.listOfTasks());
        sendText(exchange, response, 200);
    }

    // Обработка запроса на добавление новой задачи
    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(json, Task.class);
        boolean success;
        if (task.getID() != 0) {
            success = taskManager.updateTask(task);
        } else {
            success = taskManager.addTask(task);
        }

        if (success) {
            sendText(exchange, "{\"message\": \"Task added successfully\"}", 201);
        } else {
            sendHasInteractions(exchange);
        }
    }

    // Обработка запроса на удаление всех задач
    private void handleDelete(HttpExchange exchange) throws IOException {
        taskManager.deleteTasks();
        sendText(exchange, "{\"message\": \"All tasks deleted successfully\"}", 200);
    }
}
