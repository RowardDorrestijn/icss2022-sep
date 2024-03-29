package nl.han.ica.icss.transforms;

import javafx.beans.binding.BooleanExpression;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
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

    @Override
    public void apply(AST ast) {
        this.variableValues = new LinkedList<>();
        applyStylesheet(ast.root);
    }

    private void applyStylesheet(Stylesheet sheet){
        this.variableValues.addFirst(new HashMap<>());
        for(ASTNode parent : sheet.getChildren()){
            if(parent instanceof Stylerule){
                applyStylerule(parent);
            }else if(parent instanceof VariableAssignment){
                applyVariableAssignment((VariableAssignment) parent);
            }
        }
        this.variableValues.removeFirst();
    }

    private void applyVariableAssignment(VariableAssignment variableAssignment){
        variableAssignment.expression = evaluateExpression(variableAssignment.expression);
        this.variableValues.getFirst().put(variableAssignment.name.name, (Literal) variableAssignment.expression);
    }

    private void applyStylerule(ASTNode node){
        for (ASTNode child : node.getChildren()){
            if(child instanceof Declaration){
                applyDecleration((Declaration) child);
            } else if(child instanceof VariableAssignment){
                applyVariableAssignment((VariableAssignment) child);
            } else if(child instanceof IfClause){
                applyIfClause((IfClause) child, node);
                node.removeChild(child); // Dit werkt dus niet, maar zou in theorie wel moeten werken. Zelfs als ik de regel hierboven comment wordt een ifClause niet verwijderd.
            }
        }
    }


    private void applyIfClause(IfClause ifClause, ASTNode node){
        applyStylerule(ifClause);
        if(ifClause.conditionalExpression instanceof BoolLiteral){
            if(!((BoolLiteral) ifClause.conditionalExpression).value){
                if(ifClause.elseClause != null){
                    for(ASTNode child : ifClause.elseClause.body){
                        node.addChild(child);
                    }
                }
            }else{
                for(ASTNode child : ifClause.body){
                    node.addChild(child);
                }
            }
        } else if(ifClause.conditionalExpression instanceof VariableReference){
            BoolLiteral variable = (BoolLiteral) this.variableValues.getFirst().get(((VariableReference) ifClause.conditionalExpression).name);
            if(!variable.value){
                if(ifClause.elseClause != null){
                    for(ASTNode child : ifClause.elseClause.body){
                        node.addChild(child);
                    }
                }
            }else{
                for(ASTNode child : ifClause.body){
                    node.addChild(child);
                }
            }
        }
    }

    private void applyDecleration(Declaration declaration){
        declaration.expression = evaluateExpression(declaration.expression);
    }

    private Expression evaluateExpression(Expression expression){
        if(expression instanceof Literal){
            return expression;
        } else if (expression instanceof MultiplyOperation) {
            return evaluateMultiplyExpression((MultiplyOperation) expression);
        }else if(expression instanceof AddOperation){
            return evaluateAddExpression((AddOperation) expression);
        } else if(expression instanceof VariableReference){
            return this.variableValues.getFirst().get(((VariableReference) expression).name);
        } else {
            return evaluateSubtractExpression((SubtractOperation) expression);
        }
    }

    private Expression evaluateAddExpression(AddOperation expression) {
        Literal left = (Literal) evaluateExpression(expression.lhs);
        Literal right = (Literal) evaluateExpression(expression.rhs);

        if(left instanceof PixelLiteral && right instanceof PixelLiteral){
            return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value);
        }else if(left instanceof PercentageLiteral && right instanceof PercentageLiteral){
            return new PercentageLiteral(((PercentageLiteral) left).value + ((PercentageLiteral) right).value);
        }

        return null;
    }

    private Expression evaluateMultiplyExpression(MultiplyOperation expression) {
        Literal left = (Literal) evaluateExpression(expression.lhs);
        Literal right = (Literal) evaluateExpression(expression.rhs);

        if(left instanceof PixelLiteral && right instanceof ScalarLiteral){
            return new PixelLiteral(((PixelLiteral) left).value * ((ScalarLiteral) right).value);
        }else if(left instanceof ScalarLiteral && right instanceof PixelLiteral){
            return new PixelLiteral(((ScalarLiteral) left).value * ((PixelLiteral) right).value);
        }else if(left instanceof ScalarLiteral && right instanceof ScalarLiteral){
            return new ScalarLiteral(((ScalarLiteral) left).value * ((ScalarLiteral) right).value);
        }else if(left instanceof PercentageLiteral && right instanceof ScalarLiteral){
            return new PercentageLiteral(((PercentageLiteral) left).value * ((ScalarLiteral) right).value);
        }else if(left instanceof ScalarLiteral && right instanceof PercentageLiteral){
            return new PercentageLiteral(((ScalarLiteral) left).value * ((PercentageLiteral) right).value);
        }
        return null;
    }

    private Expression evaluateSubtractExpression(SubtractOperation expression) {
        Literal left = (Literal) evaluateExpression(expression.lhs);
        Literal right = (Literal) evaluateExpression(expression.rhs);

        if(left instanceof PixelLiteral && right instanceof PixelLiteral){
            return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value);
        }else if(left instanceof PercentageLiteral && right instanceof PercentageLiteral){
            return new PercentageLiteral(((PercentageLiteral) left).value - ((PercentageLiteral) right).value);
        }

        return null;
    }


}
