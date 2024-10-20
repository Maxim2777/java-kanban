package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import models.Subtask;
import models.Task;
import services.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
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
                    if (path.matches("/subtasks/\\d+")) {
                        handleGetById(exchange);
                    } else if ("/subtasks".equals(path)) {
                        handleGet(exchange);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    if (path.matches("/subtasks/\\d+")) {
                        handleDeleteById(exchange);
                    } else if ("/subtasks".equals(path)) {
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

    // Обработка запроса на удаление подзадачи по ID
    private void handleDeleteById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        int id = Integer.parseInt(splitPath[splitPath.length - 1]);

        boolean success = taskManager.deleteByID(id);

        if (success) {
            sendText(exchange, "{\"message\": \"Subtask deleted successfully\"}", 200);
        } else {
            sendNotFound(exchange); // Если подзадача с указанным ID не найдена, вернуть 404 (Не найдено)
        }
    }

    // Обработка запроса на получение подзадачи по ID
    private void handleGetById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        int id = Integer.parseInt(splitPath[splitPath.length - 1]);

        Task subtask = taskManager.getTaskInfo(id);

        if (subtask == null) {
            sendNotFound(exchange); // Если подзадача с указанным ID не найдена, вернуть 404 (Не найдено)
        } else {
            String response = gson.toJson(subtask);
            sendText(exchange, response, 200); // Отправляем подзадачу в формате JSON с кодом 200 (Успех)
        }
    }

    // Обработка запроса на получение списка подзадач
    private void handleGet(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.listOfSubtasks());
        sendText(exchange, response, 200);
    }

    // Обработка запроса на добавление новой подзадачи
    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        Subtask subtask = gson.fromJson(json, Subtask.class);

        boolean success;
        if (subtask.getID() != 0) {
            success = taskManager.updateSubtask(subtask);
        } else {
            success = taskManager.addSubtask(subtask);
        }

        if (success) {
            sendText(exchange, "{\"message\": \"Subtask added successfully\"}", 201);
        } else {
            sendHasInteractions(exchange);
        }

    }

    // Обработка запроса на удаление всех подзадач
    private void handleDelete(HttpExchange exchange) throws IOException {
        taskManager.deleteSubtasks();
        sendText(exchange, "{\"message\": \"All subtasks deleted successfully\"}", 200);
    }
}