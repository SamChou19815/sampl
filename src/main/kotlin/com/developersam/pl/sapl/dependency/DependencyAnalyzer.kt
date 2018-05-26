package com.developersam.pl.sapl.dependency

import com.developersam.pl.sapl.ast.CompilationUnit
import com.developersam.pl.sapl.exceptions.CyclicDependencyError

/**
 * [DependencyAnalyzer] can analyze the dependency relationship in source.
 */
object DependencyAnalyzer {

    /**
     * [getCompilationSequence] obtains the correct sequence of compilation for the source file
     * given that the source has no cyclic dependencies.
     *
     * @param map the map with key as file name and value as the compilation unit.
     * Dependency relationship will be extracted from such map.
     * @return a list of the same compilation units if there is no cyclic dependencies. The list
     * represents the correct order of compilation.
     * @throws CyclicDependencyError if there exists cyclic dependencies.
     */
    fun getCompilationSequence(map: Map<String, CompilationUnit>): List<CompilationUnit> {
        // Construct graph from map.
        val dependencyGraph: HashMap<String, Set<String>> = hashMapOf()
        for ((filename, compilationUnit) in map) {
            dependencyGraph[filename] = compilationUnit.imports
        }
        // Construct sequence of compilation
        return DAGAnalyzer(dependencyGraph)
                .sortedList
                ?.map { map[it]!! }
                ?: throw CyclicDependencyError()
    }

}
