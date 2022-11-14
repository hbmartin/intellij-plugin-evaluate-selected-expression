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
import org.mariuszgromada.math.mxparser.Expression
import java.lang.Double
import kotlin.math.ceil
import kotlin.math.floor

class EvaluateExpression : AnAction("Evaluate Expression Inline") {
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
            val mx = Expression(expression)
            @Suppress("SwallowedException")
            try {
                val result = mx.calculate()
                if (!Double.isNaN(result)) {
                    if (floor(result) == ceil(result)) {
                        document.replaceString(start, end, result.toInt().toString())
                    } else {
                        document.replaceString(start, end, result.toString())
                    }
                }
            } catch (e: Exception) {
                // invalid expression, ignoring
            }
        }

        primaryCaret.removeSelection()
    }
}
