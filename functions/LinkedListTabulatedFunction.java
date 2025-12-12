package functions;
import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable {
    
    private class Node {
        FunctionPoint point;
        Node prev, next;
        Node(FunctionPoint p) { point = p; }
    }
    
    private Node head;
    private int size;
    
    // Конструктор по умолчанию (добавлен для clone())
    public LinkedListTabulatedFunction() {
        init();
    }
    
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX || pointsCount < 2) 
            throw new IllegalArgumentException("Неверная область определения или количество точек: левая граница должна быть меньше правой и точек должно быть не менее 2");
        
        init();
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) 
            addNodeToTail(new FunctionPoint(leftX + i * step, 0));
    }
    
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX || values.length < 2) 
            throw new IllegalArgumentException("Неверная область определения или количество точек: левая граница должна быть меньше правой и точек должно быть не менее 2");
        
        init();
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) 
            addNodeToTail(new FunctionPoint(leftX + i * step, values[i]));
    }

    public LinkedListTabulatedFunction(FunctionPoint[] pointsArray) {
        if (pointsArray.length < 2) {
            throw new IllegalArgumentException("Требуется не менее 2 точек");
        }
        
        for (int i = 0; i < pointsArray.length - 1; i++) {
            if (pointsArray[i].getX() >= pointsArray[i + 1].getX()) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по возрастанию X");
            }
        }
        
        init();
        for (FunctionPoint point : pointsArray) {
            addNodeToTail(new FunctionPoint(point));
        }
    }
    
    private void init() {
        head = new Node(null);
        head.prev = head;
        head.next = head;
        size = 0;
    }
    
    private void addNodeToTail(FunctionPoint point) {
        Node newNode = new Node(point);
        Node last = head.prev;
        
        newNode.prev = last;
        newNode.next = head;
        last.next = newNode;
        head.prev = newNode;
        size++;
    }
    
    private Node getNode(int index) {
        if (index < 0 || index >= size)
            throw new FunctionPointIndexOutOfBoundsException("Индекс вне диапазона: " + index);
        
        Node current = head.next;
        for (int i = 0; i < index; i++) 
            current = current.next;
        
        return current;
    }
    
    private Node addNodeAtIndex(int index, FunctionPoint point) {
        Node newNode = new Node(point);
        Node target = (index == size) ? head : getNode(index);
        
        newNode.prev = target.prev;
        newNode.next = target;
        target.prev.next = newNode;
        target.prev = newNode;
        size++;
        
        return newNode;
    }
    
    private void removeNode(int index) {
        Node toRemove = getNode(index);
        toRemove.prev.next = toRemove.next;
        toRemove.next.prev = toRemove.prev;
        size--;
    }

    @Override
    public double getLeftDomainBorder() { 
        return head.next.point.getX(); 
    }
    
    @Override
    public double getRightDomainBorder() { 
        return head.prev.point.getX(); 
    }
    
    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) 
            return Double.NaN;
        
        Node current = head.next;
        for (int i = 0; i < size; i++) {
            if (Math.abs(current.point.getX() - x) < 1e-9) 
                return current.point.getY();
            current = current.next;
        }
        
        current = head.next;
        for (int i = 0; i < size - 1; i++) {
            if (x >= current.point.getX() && x <= current.next.point.getX()) {
                double x1 = current.point.getX(), y1 = current.point.getY();
                double x2 = current.next.point.getX(), y2 = current.next.point.getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            current = current.next;
        }
        return Double.NaN;
    }
    
    @Override
    public int getPointsCount() { return size; }
    
    @Override
    public FunctionPoint getPoint(int index) {
        return new FunctionPoint(getNode(index).point);
    }
    
    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        Node node = getNode(index);
        
        if ((index > 0 && point.getX() <= node.prev.point.getX()) || 
            (index < size - 1 && point.getX() >= node.next.point.getX())) {
            throw new InappropriateFunctionPointException("Некорректный порядок значений X");
        }
        
        node.point = new FunctionPoint(point);
    }
    
    @Override
    public double getPointX(int index) { 
        return getNode(index).point.getX(); 
    }
    
    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        Node node = getNode(index);
        
        if ((index > 0 && x <= node.prev.point.getX()) || 
            (index < size - 1 && x >= node.next.point.getX())) {
            throw new InappropriateFunctionPointException("Некорректный порядок значений X");
        }
        
        node.point.setX(x);
    }
    
    @Override
    public double getPointY(int index) { 
        return getNode(index).point.getY(); 
    }
    
    @Override
    public void setPointY(int index, double y) {
        getNode(index).point.setY(y);
    }
    
    @Override
    public void deletePoint(int index) {
        if (size < 3) 
            throw new IllegalStateException("Минимум 2 точки требуется");
        removeNode(index);
    }
    
    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        Node current = head.next;
        int i = 0;
        
        while (i < size && current.point.getX() < point.getX()) {
            current = current.next;
            i++;
        }
        
        if (i < size && Math.abs(current.point.getX() - point.getX()) < 1e-9)
            throw new InappropriateFunctionPointException("Дублирование X координаты");
        
        addNodeAtIndex(i, new FunctionPoint(point));
    }
    
    // Задание 1: Реализация итератора
    @Override
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private Node current = head.next;
            private int visited = 0;
            
            @Override
            public boolean hasNext() {
                return visited < size;
            }
            
            @Override
            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Нет следующего элемента");
                }
                FunctionPoint point = new FunctionPoint(current.point);
                current = current.next;
                visited++;
                return point;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Удаление не поддерживается");
            }
        };
    }
    
    // Задание 2: Фабричный метод
    public static class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new LinkedListTabulatedFunction(leftX, rightX, pointsCount);
        }
        
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new LinkedListTabulatedFunction(leftX, rightX, values);
        }
        
        @Override
        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new LinkedListTabulatedFunction(points);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        Node current = head.next;
        int count = 0;
        
        while (current != head) {
            if (count > 0) sb.append(", ");
            sb.append(current.point.toString());
            current = current.next;
            count++;
        }
        sb.append("}");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        
        if (o instanceof TabulatedFunction) {
            TabulatedFunction other = (TabulatedFunction) o;
            
            if (this.getPointsCount() != other.getPointsCount()) {
                return false;
            }
            
            if (o instanceof LinkedListTabulatedFunction) {
                LinkedListTabulatedFunction listOther = (LinkedListTabulatedFunction) o;
                
                Node thisCurrent = this.head.next;
                Node otherCurrent = listOther.head.next;
                
                while (thisCurrent != this.head && otherCurrent != listOther.head) {
                    if (!thisCurrent.point.equals(otherCurrent.point)) {
                        return false;
                    }
                    thisCurrent = thisCurrent.next;
                    otherCurrent = otherCurrent.next;
                }
                return true;
            } else {
                for (int i = 0; i < size; i++) {
                    FunctionPoint thisPoint = this.getPoint(i);
                    FunctionPoint otherPoint = other.getPoint(i);
                    
                    if (!thisPoint.equals(otherPoint)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hash = size;
        Node current = head.next;
        
        while (current != head) {
            hash ^= current.point.hashCode();
            current = current.next;
        }
        return hash;
    }
    
    @Override
    public Object clone() {
        LinkedListTabulatedFunction clone = new LinkedListTabulatedFunction();
        
        if (size > 0) {
            Node current = this.head.next;
            FunctionPoint[] pointsArray = new FunctionPoint[size];
            
            for (int i = 0; i < size; i++) {
                pointsArray[i] = (FunctionPoint) current.point.clone();
                current = current.next;
            }
            
            Node prevNode = clone.head;
            for (int i = 0; i < size; i++) {
                Node newNode = new Node(pointsArray[i]);
                
                newNode.prev = prevNode;
                prevNode.next = newNode;
                
                prevNode = newNode;
            }
            
            prevNode.next = clone.head;
            clone.head.prev = prevNode;
            clone.size = size;
        }
        
        return clone;
    }
}