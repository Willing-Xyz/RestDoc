package cn.willingxyz.restdoc.spring.examples.jackson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * 简单分页模型
 *
 * @author hubin
 * @since 2018-06-09
 */
public class Page<T> {

    private static final long serialVersionUID = 8545996863226528798L;

    /**
     * 查询数据列表
     */
    protected List<T> records = Collections.emptyList();

    /**
     * 总数
     */
    protected long total = 0;
    /**
     * 每页显示条数，默认 10
     */
    protected long size = 10;

    /**
     * 当前页
     */
    protected long current = 1;

    /**
     * 自动优化 COUNT SQL
     */
    protected boolean optimizeCountSql = true;
    /**
     * 是否进行 count 查询
     */
    protected boolean isSearchCount = true;
    /**
     * 是否命中count缓存
     */
    protected boolean hitCount = false;

    public Page() {
    }

    /**
     * 分页构造函数
     *
     * @param current 当前页
     * @param size    每页显示条数
     */
    public Page(long current, long size) {
        this(current, size, 0);
    }

    public Page(long current, long size, long total) {
        this(current, size, total, true);
    }

    public Page(long current, long size, boolean isSearchCount) {
        this(current, size, 0, isSearchCount);
    }

    public Page(long current, long size, long total, boolean isSearchCount) {
        if (current > 1) {
            this.current = current;
        }
        this.size = size;
        this.total = total;
        this.isSearchCount = isSearchCount;
    }

    /**
     * 是否存在上一页
     *
     * @return true / false
     */
    public boolean hasPrevious() {
        return this.current > 1;
    }


    public List<T> getRecords() {
        return this.records;
    }

    public Page<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    public long getTotal() {
        return this.total;
    }

    public Page<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public long getSize() {
        return this.size;
    }

    public Page<T> setSize(long size) {
        this.size = size;
        return this;
    }

    public long getCurrent() {
        return this.current;
    }

    public Page<T> setCurrent(long current) {
        this.current = current;
        return this;
    }


    public boolean optimizeCountSql() {
        return optimizeCountSql;
    }

    public boolean isOptimizeCountSql() {
        return optimizeCountSql();
    }

    public boolean isSearchCount() {
        if (total < 0) {
            return false;
        }
        return isSearchCount;
    }

    public Page<T> setSearchCount(boolean isSearchCount) {
        this.isSearchCount = isSearchCount;
        return this;
    }

    public Page<T> setOptimizeCountSql(boolean optimizeCountSql) {
        this.optimizeCountSql = optimizeCountSql;
        return this;
    }

    public void hitCount(boolean hit) {
        this.hitCount = hit;
    }

    public void setHitCount(boolean hit) {
        this.hitCount = hit;
    }

    public boolean isHitCount() {
        return hitCount;
    }
}