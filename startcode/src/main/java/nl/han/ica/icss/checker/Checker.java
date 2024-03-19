package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;



public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        checkStylesheet(ast.root);
        // variableTypes = new HANLinkedList<>();

    }

    private void checkStylesheet(Stylesheet sheet){
        for (ASTNode parent : sheet.getChildren()) {
            if(parent instanceof Stylerule){
                checkStylerule((Stylerule) parent);
            }
        }
    }

    private void checkStylerule(Stylerule rule){
        for (ASTNode child: rule.getChildren()){
            if (child instanceof Declaration){
                checkDeclaration((Declaration) child);
            }
        }
    }

    private void checkDeclaration(Declaration declaration){
//        switch (declaration.property.name){
//            case "width":
//                break;
//        }
        if(declaration.property.name.equals("width")){
            if(!(declaration.expression instanceof PixelLiteral)){
                declaration.setError("Wrong expression on property 'width'. Should be pixels.");
            }
        }
    }
    
}