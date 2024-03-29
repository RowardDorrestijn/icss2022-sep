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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Flow;


public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes = new LinkedList<>();

    public void check(AST ast) {
        checkStylesheet(ast.root);
        this.variableTypes = new LinkedList<>();
    }

    private void checkStylesheet(Stylesheet sheet){
        this.variableTypes.addFirst(new HashMap<>()); // Maakt nieuwe LinkedList aan voor het toevoegen van de variabelen
        for (ASTNode parent : sheet.getChildren()) {
            if(parent instanceof Stylerule){
                checkStylerule(parent.getChildren());
            }
            if(parent instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) parent);
            }
        }
        this.variableTypes.removeFirst(); // Maakt de LinkedList leeg zodat deze weer schoon is voor volgende checks
    }

    private void checkVariableAssignment(VariableAssignment variableAssignment){
        Expression variableExpression = variableAssignment.expression;
        if(variableExpression instanceof VariableReference){
            ExpressionType expressionType = getExpressionTypeFromVariable(((VariableReference) variableExpression).name);
            if(expressionType == null){
                variableAssignment.setError("Variable not in scope");
            }else{
                this.variableTypes.getFirst().put(variableAssignment.name.name, expressionType);
            }
        }

        if(variableExpression instanceof Operation){
            variableExpression = getLiteralFromOperation((Operation) variableAssignment.expression);

        }

        if(variableExpression instanceof BoolLiteral){
            this.variableTypes.getFirst().put(variableAssignment.name.name, ExpressionType.BOOL); // Voegt de naam van de variabele en de expressiontype toe aan de LinkedList
        }else if(variableExpression instanceof ColorLiteral){
            this.variableTypes.getFirst().put(variableAssignment.name.name, ExpressionType.COLOR);
        }else if(variableExpression instanceof PercentageLiteral){
            this.variableTypes.getFirst().put(variableAssignment.name.name, ExpressionType.PERCENTAGE);
        }else if(variableExpression instanceof PixelLiteral){
            this.variableTypes.getFirst().put(variableAssignment.name.name, ExpressionType.PIXEL);
        }else if(variableExpression instanceof ScalarLiteral){
            this.variableTypes.getFirst().put(variableAssignment.name.name, ExpressionType.SCALAR);
        }

    }

    private ExpressionType getExpressionTypeFromVariable(String variableName){
        for(HashMap<String, ExpressionType> variableList : this.variableTypes){
            if(variableList.get(variableName) != null){
                return variableList.get(variableName);
            }
        }
        return null;
    }

    private Literal getLiteralFromOperation(Operation operation) {
        Expression left = operation.lhs;
        Expression right = operation.rhs;

        if(left instanceof Operation){
            left = getLiteralFromOperation((Operation) left);
        }
        if(right instanceof Operation){
            right = getLiteralFromOperation((Operation) right);
        }


        if(left instanceof VariableReference){
            left = checkAndGetVariable(left);
        }

        if(right instanceof VariableReference){
            right = checkAndGetVariable(right);
        }


        if (operation instanceof MultiplyOperation) {
            return getLiteralFromMultiplyOperation(left, right);
        }else if (operation instanceof AddOperation || operation instanceof SubtractOperation) {
            return getLiteralFromAddOrSubtractOperation(left, right);
        }

        return null;
    }

    private Literal getLiteralFromMultiplyOperation(Expression left, Expression right) {
        if(left instanceof ScalarLiteral && right instanceof ScalarLiteral){
            return new ScalarLiteral(0);
        }else if((left instanceof ScalarLiteral && right instanceof PixelLiteral) | (left instanceof PixelLiteral && right instanceof ScalarLiteral)){
            return new PixelLiteral("0px");
        }else if((left instanceof PercentageLiteral && right instanceof ScalarLiteral) | (left instanceof ScalarLiteral && right instanceof PercentageLiteral)){
            return new PercentageLiteral("0%");
        }
        return null;
    }

    private Literal getLiteralFromAddOrSubtractOperation(Expression left, Expression right) {
        if(left instanceof PixelLiteral && right instanceof PixelLiteral){
            return new PixelLiteral("0px");
        } else if(left instanceof PercentageLiteral && right instanceof PercentageLiteral){
            return new PercentageLiteral("0%");
        }

        return null;
    }

    private Literal checkAndGetVariable(Expression expression){
        ExpressionType expressionType = getExpressionTypeFromVariable(((VariableReference) expression).name);
        if(expressionType == null){
            expression.setError("Variable not in scope");
        }else{
            Literal literal = getLiteralFromExpressionType(expressionType);
            if(literal == null){
                expression.setError("Literal from variable not allowed in operations");
            }
            return literal;
        }
        return null;
    }

    private Literal getLiteralFromExpressionType(ExpressionType expressionType){
        switch (expressionType){
            case PIXEL:
                return new PixelLiteral("0px");
            case PERCENTAGE:
                return new PercentageLiteral("0%");
            case SCALAR:
                return new ScalarLiteral(0);
        }
        return null;
    }


    private void checkStylerule(ArrayList<ASTNode> body){
        this.variableTypes.addFirst(new HashMap<>());
        for (ASTNode child : body){
            if (child instanceof Declaration){
                checkDeclaration((Declaration) child);
            }else if(child instanceof IfClause){
                checkIfClause((IfClause) child);
            } else if(child instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) child);
            }
        }
        this.variableTypes.removeFirst();
    }

    private void checkIfClause(IfClause ifClause){
        if(ifClause.conditionalExpression instanceof VariableReference){
            ExpressionType expressionType = getExpressionTypeFromVariable(((VariableReference) ifClause.conditionalExpression).name);
            if(expressionType == null){
                ifClause.setError("Variable not in scope");
            }else if(expressionType != ExpressionType.BOOL){
                ifClause.setError("Wrong conditional expression. Should be a boolean");
            }
        }
        else if(!(ifClause.conditionalExpression instanceof BoolLiteral)){
            ifClause.setError("Wrong conditional expression. Should be a boolean");
        }
        checkStylerule(ifClause.body);
        if(ifClause.elseClause != null){
            checkStylerule(ifClause.elseClause.body);
        }
    }

    private void checkDeclaration(Declaration declaration){

        switch (declaration.property.name)
        {
            case "width":
                if(declaration.expression instanceof VariableReference) {
                    ExpressionType expressionType = getExpressionTypeFromVariable(((VariableReference) declaration.expression).name);
                    if(expressionType == null){
                        declaration.expression.setError("Variable not in scope");
                    }else if(!(expressionType == ExpressionType.PIXEL | expressionType == ExpressionType.PERCENTAGE)){
                        declaration.setError("Wrong expression on property 'width'. Should be pixels or a percentage.");
                    }
                }
                else if(declaration.expression instanceof Operation){
                    Literal literal = getLiteralFromOperation((Operation) declaration.expression);
                    if(!(literal instanceof PixelLiteral | literal instanceof PercentageLiteral)){
                        declaration.setError("Wrong operation expression.");
                    }
                }
                else if(!(declaration.expression instanceof PixelLiteral | declaration.expression instanceof PercentageLiteral)){
                    declaration.setError("Wrong expression on property 'width'. Should be pixels or a percentage.");
                }
                break;
            case "height":
                if(declaration.expression instanceof VariableReference) {
                    ExpressionType expressionType = getExpressionTypeFromVariable(((VariableReference) declaration.expression).name);
                    if(expressionType == null){
                        declaration.expression.setError("Variable not in scope");
                    }else if(!(expressionType == ExpressionType.PIXEL | expressionType == ExpressionType.PERCENTAGE)){
                        declaration.setError("Wrong expression on property 'height'. Should be pixels or a percentage.");
                    }
                }
                else if(declaration.expression instanceof Operation){
                    Literal literal = getLiteralFromOperation((Operation) declaration.expression);
                    if(!(literal instanceof PixelLiteral | literal instanceof PercentageLiteral)){
                        declaration.setError("Wrong operation expression.");
                    }
                }
                else if(!(declaration.expression instanceof PixelLiteral | declaration.expression instanceof PercentageLiteral)){
                    declaration.setError("Wrong expression on property 'height'. Should be pixels or a percentage.");
                }
                break;
            case "color":
                if(declaration.expression instanceof VariableReference) {
                    ExpressionType expressionType = getExpressionTypeFromVariable(((VariableReference) declaration.expression).name);
                    if(expressionType == null){
                        declaration.expression.setError("Variable not in scope");
                    }else if(expressionType != ExpressionType.COLOR){
                        declaration.setError("Wrong expression on property 'color'. Should be a color.");
                    }
                }
                else if(!(declaration.expression instanceof ColorLiteral)){
                    declaration.setError("Wrong expression on property 'color'. Should be a color.");
                }
                break;
            case "background-color":
                if(declaration.expression instanceof VariableReference) {
                    ExpressionType expressionType = getExpressionTypeFromVariable(((VariableReference) declaration.expression).name);
                    if(expressionType == null){
                        declaration.expression.setError("Variable not in scope");
                    }else if(expressionType != ExpressionType.COLOR){
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
