package com.willing.springswagger.test.parse.utils;

import com.willing.springswagger.parse.utils.GraphChecker;
import lombok.var;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestGraphChecker {

    private GraphChecker _graph;

    @Before
    public void before()
    {
        _graph = new GraphChecker();
    }

    @Test
    public void test()
    {
        var _graph = new GraphChecker<String>();
        _graph.add(null, "R");
        var isCycle = _graph.add("R", "A1");
        assertFalse(isCycle);
        isCycle = _graph.add("R", "A2");
        assertFalse(isCycle);
        isCycle = _graph.add("R", "A3");
        assertFalse(isCycle);
        isCycle = _graph.add("A1", "B1");
        assertFalse(isCycle);
        isCycle = _graph.add("A1", "B2");
        assertFalse(isCycle);
        isCycle = _graph.add("B2", "A2");
        assertFalse(isCycle);
        isCycle = _graph.add("B2", "B2");
        assertTrue(isCycle);
        isCycle = _graph.add("A3", "B2");
        assertFalse(isCycle);
    }
    @Test
    public void t()
    {
        System.out.println(Object[].class.getTypeName());
    }
}
