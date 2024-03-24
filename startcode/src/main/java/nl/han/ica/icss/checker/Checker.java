package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.io.Console;
import java.util.HashMap;



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
