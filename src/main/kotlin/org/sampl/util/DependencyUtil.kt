@file:JvmName(name = "DependencyUtil")

package org.sampl.util

import org.sampl.ast.raw.ClassMembers
import org.sampl.ast.raw.Clazz
import org.sampl.ast.raw.CompilationUnit
import org.sampl.ast.type.TypeDeclaration
import org.sampl.ast.type.TypeIdentifier
import org.sampl.exceptions.CompileTimeError
import org.sampl.exceptions.CyclicDependencyError
import org.sampl.exceptions.FileClassNameDoesNotMatchError

/**
 * [DAGAnalyzer] is responsible for understanding the DAG relationship between various
 * items in graph. It can detect cyclic dependency and perform topological sort.
 *
 * @param graph the graph to analyze. The value is a list of items that the key directs to.
 */
private class DAGAnalyzer(private val graph: Map<String, Set<String>>) {

    /**
     * [_sortedList] contains the sorted list of vertices in topological order.
     */
    private val _sortedList = ArrayList<String>(graph.keys.size)
    /**
     * [visited] contains a set of already visited vertices during topological sort.
     */
    private val visited = hashSetOf<String>()
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
    private fun checkCycleDFS(vertex: String, visitedInPath: HashSet<String>): Boolean {
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
    private fun dfs(vertex: String) {
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
    val sortedList: List<String>? = if (isDAG) _sortedList else null

}

/**
 * [createCompilationSequence] obtains the correct sequence of compilation for the source file
 * given that the source has no cyclic dependencies.
 *
 * @param map the map with key as file name and value as the compilation unit.
 * Dependency relationship will be extracted from such map.
 * @return a class that contains all the modules in the sequence.
 * @throws CyclicDependencyError if there exists cyclic dependencies.
 */
internal fun createCompilationSequence(map: Map<String, CompilationUnit>): Clazz {
    // Construct graph from map.
    val dependencyGraph: HashMap<String, Set<String>> = hashMapOf()
    for ((filename, compilationUnit) in map) {
        val className = compilationUnit.clazz.name
        if (filename != className) {
            throw FileClassNameDoesNotMatchError(filename = filename, className = className)
        }
        dependencyGraph[filename] = compilationUnit.imports
    }
    // Construct sequence of compilation
    val sequence: List<Pair<String, CompilationUnit>>
    try {
        sequence = DAGAnalyzer(graph = dependencyGraph).sortedList
                ?.map { it to (map[it]!!) } ?: throw CyclicDependencyError()
    } catch (e: IllegalArgumentException) {
        throw CompileTimeError(reason = "Bad import statements!")
    }
    // Construct a single class
    return Clazz(
            identifier = TypeIdentifier(name = "Main"),
            declaration = TypeDeclaration.Struct(map = emptyMap()),
            members = ClassMembers(
                    constantMembers = emptyList(),
                    functionMembers = emptyList(),
                    nestedClassMembers = sequence.map { (_, unit) -> unit.clazz }
            )
    )
}
