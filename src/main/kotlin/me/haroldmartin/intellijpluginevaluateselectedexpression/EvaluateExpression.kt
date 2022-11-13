package me.haroldmartin.intellijpluginevaluateselectedexpression

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class EvaluateExpression : AnAction("Evaluate Expression Inline") {
    val scriptEngine: ScriptEngine = ScriptEngineManager().getEngineByName("JavaScript")

    override fun actionPerformed(event: AnActionEvent) {
        val editor: Editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val project: Project = event.getRequiredData(CommonDataKeys.PROJECT)
        val document: Document = editor.document

        val primaryCaret: Caret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart
        val end: Int = primaryCaret.selectionEnd

        if (start == end) return

        WriteCommandAction.runWriteCommandAction(
            project
        ) {
            val expression = document.getText(TextRange(start, end))
            @Suppress("SwallowedException")
            try {
                val result = scriptEngine.eval(expression)
                document.replaceString(start, end, result.toString())
            } catch (e: ScriptException) {
                // invalid expression, ignoring
            }
        }

        primaryCaret.removeSelection()
    }
}
