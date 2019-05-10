package com.willing.restdoc.core.parse.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.var;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

/**
 * 有向循环图检测。用于检测循环引用
 */
public class GraphChecker<T> {
    /**
     * 如果这条边会导致循环引用，不会增加这条边到图中，并返回true。
     * 否则，增加边，并返回false。
     * 第一次增加时，from为null，此时to作为图的起点
     */
    public boolean add(T from, T to)
    {
        if (from == null)
        {
            _root = new Node<>();
            _root.setData(to);
            return false;
        }
        var fromNode = getNode(_root, from);
        var toNode = getNode(_root, to);
        if (toNode == null) {
            toNode = new Node<>();
            toNode.setData(to);
        }
        fromNode.getNodes().add(toNode);
        if (isCycle(_root))
        {
            fromNode.getNodes().remove(toNode);
            return true;
        }
        return false;
    }

    private boolean isCycle(Node<T> node)
    {
        var stack = new Stack<Node<T>>();
        return handle(node, stack);
    }

    private boolean handle(Node<T> node, Stack<Node<T>> stack) {
        stack.push(node);
        if (stackHasSameNode(stack))
            return true;
        for (var child : node.getNodes())
        {
            if (handle(child, stack))
                return true;
        }
        stack.pop();
        return false;
    }

    private boolean stackHasSameNode(Stack<Node<T>> stack) {
        var set = new HashSet<Node<T>>();
        for (var item : stack)
            set.add(item);
        if (set.size() < stack.size())
            return true;
        return false;
    }

    private Node<T> getNode(Node<T> node, T from) {
        if (node.getData().equals(from))
        {
            return node;
        }
        else
        {
            for (var child: node.getNodes())
            {
                var x = getNode(child, from);
                if (x != null)
                    return x;
            }
        }
        return null;
    }

    private Node<T> _root;

    @Getter
    @Setter
    static class Node<T>
    {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return Objects.equals(_data, node._data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_data);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "_data=" + _data +
                    ", _nodes=" + _nodes +
                    '}';
        }

        private T _data;
        private Set<Node<T>> _nodes = new HashSet<>();
    }
}
