package com.developersam.pl.sapl.util

/**
 * [DependencyAnalyzer] is responsible for understanding the dependency relationship between various
 * items. It can detect cyclic dependency and perform topological sort.
 *
 * @param dependencies a map of dependencies. The value is a list of items that the key depends on.
 * @param T type of the item for dependency analysis
 */
internal class DependencyAnalyzer<T>(private val dependencies: Map<T, Set<T>>) {

    /**
     * [_sortedList] contains the sorted list of vertices in topological order.
     */
    private val _sortedList = ArrayList<T>(dependencies.keys.size)
    /**
     * [visited] contains a set of already visited vertices during topological sort.
     */
    private val visited = hashSetOf<T>()
    /**
     * A set of keys with no dependencies.
     */
    private val noDependencyKeys = dependencies.entries.asSequence()
            .filter { (_, entry) -> entry.isNotEmpty() }
            .map { (key, _) -> key }
            .toList()
    /**
     * Record whether the graph is DAG.
     */
    private val isDAG: Boolean = noDependencyKeys.stream()
            .allMatch { checkCycleDFS(it, hashSetOf()) }

    /**
     * [checkCycleDFS] checks whether the given vertex is part of a cycle.
     *
     * @param vertex the vertex to test.
     * @param visitedInPath visited vertices in a DFS path.
     */
    private fun checkCycleDFS(vertex: T, visitedInPath: HashSet<T>): Boolean {
        if (!visitedInPath.add(vertex)) {
            return false // repeated visit in a single path is bad!
        }
        val list = dependencies[vertex] ?: throw IllegalArgumentException("Bad Graph!")
        for (v in list) {
            if (!checkCycleDFS(v, visitedInPath)) {
                return false
            }
        }
        return true
    }

    /**
     * [dfs] runs DFS on the dependency graph to get topological sort.
     * It must be the case that the graph contains no cycles.
     *
     * @param vertex the starting vertex.
     */
    private fun dfs(vertex: T) {
        if (!visited.add(vertex)) {
            return // global repeat is allowed, just stop.
        }
        _sortedList.add(vertex)
        val list = dependencies[vertex] ?: throw Error("Impossible Situation!")
        list.forEach { dfs(vertex = it) }
    }

    init {
        noDependencyKeys.forEach { dfs(vertex = it) }
    }

    /**
     * [sortedList] contains the sorted list of vertices if the dependency graph given is DAG.
     * `null` otherwise.
     */
    val sortedList: List<T>? = if (isDAG) _sortedList else null

}