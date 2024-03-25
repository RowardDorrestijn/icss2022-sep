package nl.han.ica.icss.parser;

import java.util.Stack;


import com.sun.jdi.Value;
import nl.han.ica.datastructures.HANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private HANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<ASTNode>();
	}
    public AST getAST() {
        return ast;
    }

	@Override public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = new Stylesheet();
		currentContainer.push(stylesheet);
	}

	@Override public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = (Stylesheet) currentContainer.pop();
		ast.setRoot(stylesheet);
	}

	@Override public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment variable = new VariableAssignment();
		currentContainer.push(variable);
	}

	@Override public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment variable = (VariableAssignment) currentContainer.pop();
		currentContainer.peek().addChild(variable);
	}

	@Override public void enterStylerule (ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = new Stylerule();
		currentContainer.push(stylerule);
	}

	@Override public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = (Stylerule) currentContainer.pop();
		currentContainer.peek().addChild(stylerule);
	}

	@Override public void enterIdSelector(ICSSParser.IdSelectorContext ctx){
		Selector idSelector = new IdSelector(ctx.getText());
		currentContainer.push(idSelector);
	}

	@Override public void exitIdSelector(ICSSParser.IdSelectorContext ctx){
		Selector idSelector = (IdSelector) currentContainer.pop();
		currentContainer.peek().addChild(idSelector);
	}

	@Override public void enterClassSelector(ICSSParser.ClassSelectorContext ctx){
		Selector classSelector = new ClassSelector(ctx.getText());
		currentContainer.push(classSelector);
	}

	@Override public void exitClassSelector(ICSSParser.ClassSelectorContext ctx){
		Selector classSelector = (ClassSelector) currentContainer.pop();
		currentContainer.peek().addChild(classSelector);
	}

	@Override public void enterTagSelector(ICSSParser.TagSelectorContext ctx){
		Selector tagSelector = new TagSelector(ctx.getText());
		currentContainer.push(tagSelector);
	}

	@Override public void exitTagSelector(ICSSParser.TagSelectorContext ctx){
		Selector tagSelector = (TagSelector) currentContainer.pop();
		currentContainer.peek().addChild(tagSelector);
	}

	@Override public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = new Declaration(ctx.LOWER_IDENT().getText());
		currentContainer.push(declaration);
	}
	@Override public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = (Declaration) currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}


	@Override public void enterMultiplyOperation(ICSSParser.MultiplyOperationContext ctx){
		Operation multiplyOperation = new MultiplyOperation();
		currentContainer.push(multiplyOperation);
	}

	@Override public void exitMultiplyOperation(ICSSParser.MultiplyOperationContext ctx){
		Operation multiplyOperation = (MultiplyOperation) currentContainer.pop();
		currentContainer.peek().addChild(multiplyOperation);
	}

	@Override public void enterAddOrSubtractOperation(ICSSParser.AddOrSubtractOperationContext ctx){
		if(ctx.PLUS() != null){
			Operation addOperation = new AddOperation();
			currentContainer.push(addOperation);
		}else{
			Operation subtractOperation = new SubtractOperation();
			currentContainer.push(subtractOperation);
		}

	}

	@Override public void exitAddOrSubtractOperation(ICSSParser.AddOrSubtractOperationContext ctx){
		if(ctx.PLUS() != null){
			Operation addOperation = (AddOperation) currentContainer.pop();
			currentContainer.peek().addChild(addOperation);
		}else{
			Operation subtractOperation = (SubtractOperation) currentContainer.pop();
			currentContainer.peek().addChild(subtractOperation);
		}
	}

	@Override public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx){
		Literal colorLiteral = new ColorLiteral(ctx.getText());
		currentContainer.push(colorLiteral);
	}

	@Override public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx){
		Literal colorLiteral = (ColorLiteral) currentContainer.pop();
		currentContainer.peek().addChild(colorLiteral);
	}

	@Override public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx){
		Literal pixelLiteral = new PixelLiteral(ctx.getText());
		currentContainer.push(pixelLiteral);
	}

	@Override public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx){
		Literal pixelLiteral = (PixelLiteral) currentContainer.pop();
		currentContainer.peek().addChild(pixelLiteral);
	}

	@Override public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx){
		Literal scalarLiteral = new ScalarLiteral(ctx.getText());
		currentContainer.push(scalarLiteral);
	}

	@Override public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx){
		Literal scalarLiteral = (ScalarLiteral) currentContainer.pop();
		currentContainer.peek().addChild(scalarLiteral);
	}

	@Override public void enterTrueLiteral(ICSSParser.TrueLiteralContext ctx){
		Literal trueLiteral = new BoolLiteral(ctx.getText());
		currentContainer.push(trueLiteral);
	}

	@Override public void exitTrueLiteral(ICSSParser.TrueLiteralContext ctx){
		Literal trueLiteral = (BoolLiteral) currentContainer.pop();
		currentContainer.peek().addChild(trueLiteral);
	}

	@Override public void enterFalseLiteral(ICSSParser.FalseLiteralContext ctx){
		Literal falseLiteral = new BoolLiteral(ctx.getText());
		currentContainer.push(falseLiteral);
	}

	@Override public void exitFalseLiteral(ICSSParser.FalseLiteralContext ctx){
		Literal falseLiteral = (BoolLiteral) currentContainer.pop();
		currentContainer.peek().addChild(falseLiteral);
	}

	@Override public void enterVariableReference(ICSSParser.VariableReferenceContext ctx){
		VariableReference variableLiteral = new VariableReference(ctx.getText());
		currentContainer.push(variableLiteral);
	}

	@Override public void exitVariableReference(ICSSParser.VariableReferenceContext ctx){
		VariableReference variableLiteral = (VariableReference) currentContainer.pop();
		currentContainer.peek().addChild(variableLiteral);
	}

}