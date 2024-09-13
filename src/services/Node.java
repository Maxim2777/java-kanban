package services;

import models.Task;

class Node {

    Task value;
    Node previous;
    Node next;

    Node(Task value, Node previous, Node next) {
        this.value = value;
        this.previous = previous;
        this.next = next;
    }
}
