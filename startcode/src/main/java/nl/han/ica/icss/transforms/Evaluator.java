package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import javax.swing.text.Style;
import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        //variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        //variableValues = new HANLinkedList<>();
        applyStylesheet(ast.root);
    }

    private void applyStylesheet(Stylesheet sheet){
        applyStylerule((Stylerule) sheet.getChildren().get(0));
    }

    private void applyStylerule(Stylerule stylerule){
        for (ASTNode node : stylerule.getChildren()){
            if(node instanceof Declaration){
                applyDecleration((Declaration) node);
            }
        }
    }

    private void applyDecleration(Declaration declaration){
        declaration.expression = evaluateExpression((Expression) declaration.expression);
    }

    private Expression evaluateExpression(Expression expression){
        if(expression instanceof Literal){
            return (Literal) expression;
        } else if (expression instanceof MultiplyOperation) {
            return evaluateMultiplyExpression((MultiplyOperation) expression);
        }else if(expression instanceof AddOperation){
            return evaluateAddExpression((AddOperation) expression);
        } else {
            return evaluateSubtractExpression((SubtractOperation) expression);
        }
    }

    private Expression evaluateAddExpression(AddOperation expression) {
        PixelLiteral left = (PixelLiteral) evaluateExpression(expression.lhs);
        PixelLiteral right = (PixelLiteral) evaluateExpression(expression.rhs);
        return new PixelLiteral(left.value + right.value);
    }

    private Expression evaluateMultiplyExpression(MultiplyOperation expression) {
        PixelLiteral left = (PixelLiteral) evaluateExpression(expression.lhs);
        PixelLiteral right = (PixelLiteral) evaluateExpression(expression.rhs);
        return new PixelLiteral(left.value * right.value);
    }

    private Expression evaluateSubtractExpression(SubtractOperation expression) {
        PixelLiteral left = (PixelLiteral) evaluateExpression(expression.lhs);
        PixelLiteral right = (PixelLiteral) evaluateExpression(expression.rhs);
        return new PixelLiteral(left.value - right.value);
    }


}
