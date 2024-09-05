package services;

import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    Node head = null;
    Node tail = null;

    private final Map<Integer, Node> history = new HashMap<>();

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> tasksList = new ArrayList<>();
        Node buffer = head;
        for (int i = 0; i < history.size(); i++) {
            tasksList.add(buffer.value);
            buffer = buffer.next;
        }
        return tasksList;
    }

    @Override
    public void add(Task task) {
        int id = task.getID();
        if (history.containsKey(id)) {
            remove(id);
            linkLast(task);
        } else {
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }

    @Override
    public void clearHistory() {
        history.clear();
    }

    //Добавить в конец
    private void linkLast(Task task) {
        if (tail == null) {
            var node = new Node(task, null, null);
            tail = node;
            head = node;
            int id = task.getID();
            history.put(id, node);
        } else {
            var node = new Node(task, tail, null);
            tail.next = node;
            tail = node;
            int id = task.getID();
            history.put(id, node);
        }
    }

    //Удаление узла
    private void removeNode(Node node) {
        if (head == node) {
            if (head == tail) {
                head = null;
                tail = null;
            } else {
                head = head.next;
                head.previous = null;
            }
        } else if (tail == node) {
            tail = tail.previous;
            tail.next = null;
        } else {
            node.previous.next = node.next;
            node.next.previous = node.previous;
        }
    }
}
