package com.developersam.pl.sapl.dependency

import java.util.stream.Collectors

import com.developersam.pl.sapl.exceptions.CyclicDependencyError
import com.developersam.pl.sapl.antlr.PLParser.CompilationUnitContext as C

/**
 * [DependencyAnalyzer] can analyze the dependency relationship in source.
 */
object DependencyAnalyzer {

    /**
     * [getCompilationSequence] obtains the correct sequence of compilation for the source file
     * given that the source has no cyclic dependencies.
     *
     * @param compilationUnitMap the map with key as file name and value as the compilation unit.
     * Dependency relationship will be extracted from such map.
     * @return a list of the same compilation units if there is no cyclic dependencies. The list
     * represents the correct order of compilation.
     * @throws CyclicDependencyError if there exists cyclic dependencies.
     */
    fun getCompilationSequence(compilationUnitMap: Map<String, C>): List<C> {
        // Construct graph from map.
        val dependencyGraph: HashMap<String, Set<String>> = hashMapOf()
        for ((filename, compilationUnit) in compilationUnitMap) {
            val importDeclarationContext = compilationUnit.importDeclaration()
            val dependsOn: Set<String> = importDeclarationContext
                    ?.UpperIdentifier()
                    ?.stream()
                    ?.map { it.symbol.text }
                    ?.collect(Collectors.toSet())
                    ?: emptySet()
            dependencyGraph[filename] = dependsOn
        }
        // Construct sequence of compilation
        return DAGAnalyzer(dependencyGraph)
                .sortedList
                ?.map { compilationUnitMap[it]!! }
                ?: throw CyclicDependencyError()
    }

}
