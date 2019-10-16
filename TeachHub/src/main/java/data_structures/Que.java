package data_structures;
public class Que<T> {
	private int counter;
	private Node<T> head;
	private Node<T> tail;
	
	public Que() {
		this.counter = 0;
		this.tail = null;
		this.head = null;
	}
	
	public void enque(T element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}
		Node<T> newNode = new Node<T>(element);
		if ((this.head == null) && (this.tail == null)) {
			this.head = newNode;
			this.tail = newNode;
			counter++;
			return;
		}
		newNode.next = this.tail;
		this.tail.prev = newNode;
		this.tail = newNode;
		counter++;		
	}
	
	public T deque() {
		if (counter == 0) { //if there is nothing, there is nothing
			return null;
		}
		Node<T> nodeToReturn = this.head;
		if (counter == 1) {
			this.head = null;
			this.tail = null;
			counter--;
			return nodeToReturn.data;
		}
		this.head = this.head.prev;
		this.head.next = null;
		counter--;
		return nodeToReturn.data;
	}
	
	public T peek() {
		return this.head.data;
	}
	
	public int size() {
		return counter;//number of elements in stack
	}
	
	
	@SuppressWarnings({"hiding", "unused"})
	private class Node<T>{
		private Node<T> next;	
		private Node<T> prev;
		private T data;
		
		public Node(T data) {
			this.next = null;
			this.data = data;
		}
	}


}
