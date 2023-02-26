package me.haroldmartin.intellijpluginevaluateselectedexpression

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import org.mariuszgromada.math.mxparser.Expression
import java.lang.Double
import kotlin.math.ceil
import kotlin.math.floor

class EvaluateExpression : IntentionAction {

    private var result: kotlin.Double = 0.0;
    override fun startInWriteAction(): Boolean {
        return true;
    }

    override fun getText(): String {
        return "Evaluate Expression to \"${formatResult()}\"";
    }

    override fun getFamilyName(): String {
        return "Evaluate Expression Inline"
    }

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile?): Boolean {
        val document: Document = editor.document

        val primaryCaret: Caret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart
        val end: Int = primaryCaret.selectionEnd

        if (start == end) return false;

        val expression = document.getText(TextRange(start, end))
        val mx = Expression(expression)

        @Suppress("SwallowedException", "TooGenericExceptionCaught")
        try {
            this.result = mx.calculate()

            if (!Double.isNaN(result) && formatResult() != expression) {
                return true;
            }
        } catch (_: Exception) {
            // invalid expression, ignoring
        }

        return false;
    }

    override fun invoke(project: Project, editor: Editor, file: PsiFile?) {
        val document: Document = editor.document

        val primaryCaret: Caret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart
        val end: Int = primaryCaret.selectionEnd

        if (start == end) return

        WriteCommandAction.runWriteCommandAction(
            project
        ) {
            @Suppress("SwallowedException", "TooGenericExceptionCaught")
            try {
                document.replaceString(start, end, formatResult());
            } catch (e: Exception) {
                // invalid expression, ignoring
            }
        }

        primaryCaret.removeSelection()
    }

    private fun formatResult(): String {
        return if (floor(result) == ceil(result)) {
            result.toInt().toString()
        } else {
            result.toString()
        }
    }
}
