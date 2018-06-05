package com.developersam.pl.sapl.classes

import com.developersam.pl.sapl.ast.raw.ClassMembers
import com.developersam.pl.sapl.ast.raw.Clazz
import com.developersam.pl.sapl.ast.raw.CompilationUnit
import com.developersam.pl.sapl.ast.type.TypeDeclaration
import com.developersam.pl.sapl.ast.type.TypeIdentifier
import com.developersam.pl.sapl.exceptions.CompileTimeError
import com.developersam.pl.sapl.exceptions.CyclicDependencyError
import com.developersam.pl.sapl.exceptions.FileClassNameDoesNotMatchError

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
     * @return a class that contains all the modules in the sequence.
     * @throws CyclicDependencyError if there exists cyclic dependencies.
     */
    fun getCompilationSequence(map: Map<String, CompilationUnit>): Clazz {
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
            sequence = DAGAnalyzer(dependencyGraph)
                    .sortedList
                    ?.map { it to (map[it]!!) }
                    ?: throw CyclicDependencyError()
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

}
