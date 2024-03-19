package nl.han.ica.icss.parser;

import java.util.Stack;


import com.sun.jdi.Value;
import nl.han.ica.datastructures.HANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
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

	@Override public void enterVariable(ICSSParser.VariableContext ctx) {
		VariableAssignment variable = new VariableAssignment();
		variable.name = new VariableReference(ctx.ASSIGNMENT_OPERATOR().getText());
//		variable.expression = ctx.
		currentContainer.push(variable);
	}

	@Override public void exitVariable(ICSSParser.VariableContext ctx) {
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

	@Override public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx){
		ColorLiteral colorLiteral = new ColorLiteral(ctx.getText());
		currentContainer.push(colorLiteral);
	}

	@Override public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx){
		ColorLiteral colorLiteral = (ColorLiteral) currentContainer.pop();
		currentContainer.peek().addChild(colorLiteral);
	}

	@Override public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx){
		PixelLiteral pixelLiteral = new PixelLiteral(ctx.getText());
		currentContainer.push(pixelLiteral);
	}

	@Override public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx){
		PixelLiteral pixelLiteral = (PixelLiteral) currentContainer.pop();
		currentContainer.peek().addChild(pixelLiteral);
	}

	@Override public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx){
		ScalarLiteral scalarLiteral = new ScalarLiteral(ctx.getText());
		currentContainer.push(scalarLiteral);
	}

	@Override public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx){
		ScalarLiteral scalarLiteral = (ScalarLiteral) currentContainer.pop();
		currentContainer.peek().addChild(scalarLiteral);
	}

	@Override public void enterVariableLiteral(ICSSParser.VariableLiteralContext ctx){
		VariableReference variableLiteral = new VariableReference(ctx.getText());
		currentContainer.push(variableLiteral);
	}

	@Override public void exitVariableLiteral(ICSSParser.VariableLiteralContext ctx){
		VariableReference variableLiteral = (VariableReference) currentContainer.pop();
		currentContainer.peek().addChild(variableLiteral);
	}

}