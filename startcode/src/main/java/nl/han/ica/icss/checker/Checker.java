package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;
import nl.han.ica.icss.parser.ICSSParser;

import java.io.Console;
import java.util.HashMap;
import java.util.concurrent.Flow;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes = new HANLinkedList<>();

    public void check(AST ast) {
        checkStylesheet(ast.root);
        this.variableTypes = new HANLinkedList<>();
    }

    private void checkStylesheet(Stylesheet sheet){
        this.variableTypes.addFirst(new HashMap<>());
        for (ASTNode parent : sheet.getChildren()) {
            if(parent instanceof Stylerule){
                checkStylerule((Stylerule) parent);
            }
            if(parent instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) parent);
            }
        }
        this.variableTypes.removeFirst();
    }

    private void checkVariableAssignment(VariableAssignment variableAssignment){
        if(variableAssignment.expression instanceof BoolLiteral){
            this.variableTypes.getFirst().put(variableAssignment.name.name, ExpressionType.BOOL);
        }
        if(variableAssignment.expression instanceof ColorLiteral){
            this.variableTypes.getFirst().put(variableAssignment.name.name, ExpressionType.COLOR);
        }
        if(variableAssignment.expression instanceof PercentageLiteral){
            this.variableTypes.getFirst().put(variableAssignment.name.name, ExpressionType.PERCENTAGE);
        }
        if(variableAssignment.expression instanceof PixelLiteral){
            this.variableTypes.getFirst().put(variableAssignment.name.name, ExpressionType.PIXEL);
        }
        if(variableAssignment.expression instanceof ScalarLiteral){
            this.variableTypes.getFirst().put(variableAssignment.name.name, ExpressionType.SCALAR);
        }

    }

    private void checkOperation(Operation operation) {
        Expression left = operation.lhs;
        Expression right = operation.rhs;

        if(left instanceof Operation){
            checkOperation((Operation) left);
        }
        if(right instanceof Operation){
            checkOperation((Operation) right);
        }

        if(left instanceof VariableReference){
            left = checkAndGetVariable(left);
        }

        if(right instanceof VariableReference){
            right = checkAndGetVariable(right);
        }



        if (operation instanceof MultiplyOperation) {
            if(!checkMultiplyOperation(left, right)){
                operation.setError("Wrong multiplication operation");
            }
        }

        if (operation instanceof AddOperation || operation instanceof SubtractOperation) {
            if(!checkAddOrSubtractOperation(left, right)){
                operation.setError("Wrong addition or subtraction operation");
            }
        }
    }

    private boolean checkMultiplyOperation(Expression left, Expression right) {
        return (left instanceof ScalarLiteral || left instanceof Operation) && (right instanceof PixelLiteral || right instanceof Operation) || (left instanceof PixelLiteral || left instanceof Operation) && (right instanceof ScalarLiteral || right instanceof Operation);
    }

    private boolean checkAddOrSubtractOperation(Expression left, Expression right) {
        return (left instanceof PixelLiteral || left instanceof Operation) && (right instanceof PixelLiteral || right instanceof Operation);
    }

    private Literal checkAndGetVariable(Expression expression){
        ExpressionType expressionType = variableTypes.getFirst().get(((VariableReference) expression).name);
        if(variableTypes.getFirst().get(((VariableReference) expression).name) == null){
            expression.setError("Variable not in scope");
        }else{
            Literal literal = getLiterateFromExpressionType(expressionType);
            if(literal == null){
                expression.setError("Literal from variable not allowed in operations");
            }
            return literal;
        }
        return null;
    }

    private Literal getLiterateFromExpressionType(ExpressionType expressionType){
        switch (expressionType){
            case PIXEL:
                return new PixelLiteral("1px");
            case PERCENTAGE:
                return new PercentageLiteral("0%");
            case SCALAR:
                return new ScalarLiteral(0);
        }
        return null;
    }


    private void checkStylerule(Stylerule rule){
        for (ASTNode child : rule.getChildren()){
            if (child instanceof Declaration){
                checkDeclaration((Declaration) child);
            }
            if(child instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) child);
            }
        }
    }

    private void checkDeclaration(Declaration declaration){

        switch (declaration.property.name)
        {
            case "width":
                if(declaration.expression instanceof VariableReference) {
                    if(variableTypes.getFirst().get(((VariableReference) declaration.expression).name) != ExpressionType.PIXEL){
                        declaration.setError("Wrong expression on property 'width'. Should be pixels.");
                    }
                }
                else if(declaration.expression instanceof Operation){
                    checkOperation((Operation) declaration.expression);
                }
                else if(!(declaration.expression instanceof PixelLiteral)){
                    declaration.setError("Wrong expression on property 'width'. Should be pixels.");
                }
                break;
            case "height":
                if(declaration.expression instanceof VariableReference) {
                    if(variableTypes.getFirst().get(((VariableReference) declaration.expression).name) != ExpressionType.PIXEL){
                        declaration.setError("Wrong expression on property 'height'. Should be pixels.");
                    }
                }
                else if(!(declaration.expression instanceof PixelLiteral)){
                    declaration.setError("Wrong expression on property 'height'. Should be pixels.");
                }
                break;
            case "color":
                if(declaration.expression instanceof VariableReference) {
                    if(variableTypes.getFirst().get(((VariableReference) declaration.expression).name) != ExpressionType.COLOR){
                        declaration.setError("Wrong expression on property 'color'. Should be a color.");
                    }
                }
                else if(!(declaration.expression instanceof ColorLiteral)){
                    declaration.setError("Wrong expression on property 'color'. Should be a color.");
                }
                break;
            case "background-color":
                if(declaration.expression instanceof VariableReference) {
                    if(variableTypes.getFirst().get(((VariableReference) declaration.expression).name) != ExpressionType.COLOR){
                        declaration.setError("Wrong expression on property 'background-color'. Should be a color.");
                    }
                }
                else if(!(declaration.expression instanceof ColorLiteral)){
                    declaration.setError("Wrong expression on property 'background-color'. Should be a color.");
                }
                break;
        }
    }

}
