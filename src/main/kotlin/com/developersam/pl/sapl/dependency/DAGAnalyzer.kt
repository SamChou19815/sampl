package com.developersam.pl.sapl.dependency

/**
 * [DAGAnalyzer] is responsible for understanding the DAG relationship between various
 * items in graph. It can detect cyclic dependency and perform topological sort.
 *
 * @param graph the graph to analyze. The value is a list of items that the key directs to.
 * @param T type of the item for DAG analysis analysis
 */
class DAGAnalyzer<T>(private val graph: Map<T, Set<T>>) {

    /**
     * [_sortedList] contains the sorted list of vertices in topological order.
     */
    private val _sortedList = ArrayList<T>(graph.keys.size)
    /**
     * [visited] contains a set of already visited vertices during topological sort.
     */
    private val visited = hashSetOf<T>()
    /**
     * A set of keys with no dependencies.
     */
    private val noValueKeys = graph.entries.asSequence()
            .filter { (_, entry) -> entry.isNotEmpty() }
            .map { (key, _) -> key }
            .toList()
    /**
     * Record whether the graph is DAG.
     */
    private val isDAG: Boolean = noValueKeys.stream()
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
        val list = graph[vertex] ?: throw IllegalArgumentException("Bad Graph!")
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
        val list = graph[vertex] ?: throw Error("Impossible Situation!")
        list.forEach { dfs(vertex = it) }
    }

    init {
        noValueKeys.forEach { dfs(vertex = it) }
    }

    /**
     * [sortedList] contains the sorted list of vertices if the dependency graph given is DAG.
     * `null` otherwise.
     */
    val sortedList: List<T>? = if (isDAG) _sortedList else null

}