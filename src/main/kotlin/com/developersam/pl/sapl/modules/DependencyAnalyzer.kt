package com.developersam.pl.sapl.modules

import com.developersam.pl.sapl.ast.raw.CompilationUnit
import com.developersam.pl.sapl.ast.raw.Module
import com.developersam.pl.sapl.ast.raw.ModuleMembers
import com.developersam.pl.sapl.exceptions.CompileTimeError
import com.developersam.pl.sapl.exceptions.CyclicDependencyError

/**
 * [DependencyAnalyzer] can analyze the dependency relationship in source.
 */
internal object DependencyAnalyzer {

    /**
     * [getCompilationSequence] obtains the correct sequence of compilation for the source file
     * given that the source has no cyclic dependencies.
     *
     * @param map the map with key as file name and value as the compilation unit.
     * Dependency relationship will be extracted from such map.
     * @return a module that contains all the modules in the sequence.
     * @throws CyclicDependencyError if there exists cyclic dependencies.
     */
    fun getCompilationSequence(map: Map<String, CompilationUnit>): Module {
        // Construct graph from map.
        val dependencyGraph: HashMap<String, Set<String>> = hashMapOf()
        for ((filename, compilationUnit) in map) {
            dependencyGraph[filename] = compilationUnit.imports
        }
        // Construct sequence of compilation
        val sequence: List<Pair<String, CompilationUnit>>
        try {
            sequence = DAGAnalyzer(dependencyGraph)
                    .sortedList
                    ?.map { it to (map[it]!!) }
                    ?: throw CyclicDependencyError()
        } catch (e: IllegalArgumentException) {
            throw CompileTimeError(reason = "Bad import statements!")
        }
        // Construct a single module
        return Module(
                name = "Main",
                members = ModuleMembers(
                        typeMembers = emptyList(),
                        constantMembers = emptyList(),
                        functionMembers = emptyList(),
                        nestedModuleMembers = sequence.map { (name, unit) ->
                            Module(name = name, members = unit.members)
                        }
                )
        )
    }

}
