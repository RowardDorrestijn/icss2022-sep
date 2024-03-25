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

    private boolean checkOperation(Operation operation) {
        Expression left = operation.lhs;
        Expression right = operation.rhs;

        if (operation instanceof MultiplyOperation) {
            return checkMultiplyOperation(left, right);
        }
        if (operation instanceof AddOperation) {
            return checkAddOperation(left, right);
        }
        if (operation instanceof SubtractOperation) {
            return checkSubtractOperation(left, right);
        }
        return false;
    }

    private boolean checkMultiplyOperation(Expression left, Expression right) {
        if (right instanceof Operation) {
            return (left instanceof ScalarLiteral || left instanceof PixelLiteral) && checkOperation((Operation) right);
        }else if (left instanceof Operation) {
            return (right instanceof  ScalarLiteral || right instanceof PixelLiteral) && checkOperation((Operation) left);
        } else if (left instanceof ScalarLiteral && right instanceof PixelLiteral) {
            return true;
        } else return left instanceof PixelLiteral && right instanceof ScalarLiteral;
    }

    private boolean checkAddOperation(Expression left, Expression right) {
        if (right instanceof Operation) {
            return (left instanceof PixelLiteral && checkOperation((Operation) right));
        } else if(left instanceof Operation){
            return (right instanceof PixelLiteral && checkOperation((Operation) left));
        }
        return left instanceof PixelLiteral && right instanceof PixelLiteral;
    }

    private boolean checkSubtractOperation(Expression left, Expression right) {
        if (right instanceof Operation) {
            return checkOperation((Operation) right);
        } else return left instanceof PixelLiteral && right instanceof PixelLiteral;
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
                    if(!checkOperation((Operation) declaration.expression)){
                        declaration.setError("Wrong operation on property 'width'.");
                    }
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
