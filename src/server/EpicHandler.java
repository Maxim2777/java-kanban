package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import models.Epic;
import models.Subtask;
import models.Task;
import services.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
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
                    if (path.matches("/epics/\\d+/subtasks")) {
                        handleGetSubtasksByEpicId(exchange);
                    } else if (path.matches("/epics/\\d+")) {
                        handleGetById(exchange);
                    } else if ("/epics".equals(path)) {
                        handleGet(exchange);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    if (path.matches("/epics/\\d+")) {
                        handleDeleteById(exchange);
                    } else if ("/epics".equals(path)) {
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

    // Обработка запроса на получение списка подзадач эпика по ID
    private void handleGetSubtasksByEpicId(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        int id = Integer.parseInt(splitPath[splitPath.length - 2]);

        ArrayList<Subtask> subtasks = taskManager.createSubtaskListOfOneEpic(id);

        if (subtasks == null) {
            sendNotFound(exchange); // Если эпик с указанным ID не найден, вернуть 404 (Не найдено)
        } else {
            String response = gson.toJson(subtasks);
            sendText(exchange, response, 200); // Отправляем список подзадач в формате JSON с кодом 200 (Успех)
        }
    }

    // Обработка запроса на удаление эпика по ID
    private void handleDeleteById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        int id = Integer.parseInt(splitPath[splitPath.length - 1]);

        boolean success = taskManager.deleteByID(id);

        if (success) {
            sendText(exchange, "{\"message\": \"Epic deleted successfully\"}", 200);
        } else {
            sendNotFound(exchange); // Если эпик с указанным ID не найден, вернуть 404 (Не найдено)
        }
    }

    // Обработка запроса на получение эпика по ID
    private void handleGetById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        int id = Integer.parseInt(splitPath[splitPath.length - 1]);

        Task epic = taskManager.getTaskInfo(id);

        if (epic == null) {
            sendNotFound(exchange); // Если эпик с указанным ID не найден, вернуть 404 (Не найдено)
        } else {
            String response = gson.toJson(epic);
            sendText(exchange, response, 200); // Отправляем эпик в формате JSON с кодом 200 (Успех)
        }
    }

    // Обработка запроса на получение списка эпиков
    private void handleGet(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.listOfEpics());
        sendText(exchange, response, 200);
    }

    // Обработка запроса на добавление нового эпика
    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(json, Epic.class);

        boolean success = false;
        if (epic.getID() != 0) {
            success = taskManager.updateEpic(epic);
        } else {
            success = taskManager.addEpicTask(epic);
        }

        if (success) {
            sendText(exchange, "{\"message\": \"Epic added successfully\"}", 201);
        } else {
            sendHasInteractions(exchange); // Если задача конфликтует с уже существующими, вернуть 406 (Недопустимо)
        }
    }

    // Обработка запроса на удаление всех эпиков
    private void handleDelete(HttpExchange exchange) throws IOException {
        taskManager.deleteEpics();
        sendText(exchange, "{\"message\": \"All epics deleted successfully\"}", 200);
    }
}