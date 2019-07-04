package xyz.iridiumion.iridiumhighlightingeditor.highlightingdefinitions

import xyz.iridiumion.iridiumhighlightingeditor.editor.HighlightingDefinition
import xyz.iridiumion.iridiumhighlightingeditor.highlightingdefinitions.definitions.*

/**
 * Author: 0xFireball
 */
class HighlightingDefinitionLoader {

    fun selectDefinitionFromFileExtension(selectedFileExt: String): HighlightingDefinition {
        return when (selectedFileExt) {
            "js" -> JavaScriptHighlightingDefinition()
            "java" -> JavaHighlightingDefinition()
            "cs" -> CSharpHighlightingDefinition()
            "cpp", "cxx" -> CPlusPlusHighlightingDefinition()
            "lua" -> LuaHighlightingDefinition()
            "py" -> PythonHighlightingDefinition() //Not yet ready!
            "txt" -> NoHighlightingDefinition()
            else -> {
                GenericHighlightingDefinition()
            }
        }
    }
}
