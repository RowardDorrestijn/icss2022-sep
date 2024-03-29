package nl.han.ica.icss.generator;


import com.google.common.net.PercentEscaper;
import javafx.scene.control.Tab;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class Generator {

	public String generate(AST ast) {
        return generateStylesheet(ast.root);
	}

	private String generateStylesheet(Stylesheet stylesheet){
		StringBuilder stringBuilder = new StringBuilder();
		for(ASTNode child : stylesheet.getChildren()){
			if(child instanceof Stylerule){
				stringBuilder.append(generateStylerule((Stylerule) child));
				stringBuilder.append("}\n\n");
			}
		}
		return stringBuilder.toString();
	}


	private String generateStylerule(Stylerule stylerule){
		StringBuilder stringBuilder = new StringBuilder();
		for(ASTNode child : stylerule.getChildren()){
			if(child instanceof Selector){
				stringBuilder.append(generateSelector((Selector) child));
				stringBuilder.append(" {\n");
			} else if(child instanceof Declaration){
				stringBuilder.append("\t");
				stringBuilder.append(generateDecleration((Declaration) child));
				stringBuilder.append(";\n");
			}
		}
		return stringBuilder.toString();
	}

	private String generateSelector(Selector selector){
		if(selector instanceof ClassSelector){
			return ((ClassSelector) selector).cls;
		} else if (selector instanceof IdSelector){
			return ((IdSelector) selector).id;
		} else if(selector instanceof TagSelector){
			return  ((TagSelector) selector).tag;
		}
		return "";
	}


	private String generateDecleration(Declaration declaration){
		return declaration.property.name + ": " + getLiteralSuffix(declaration.expression);
	}

	private String getLiteralSuffix(Expression expression){
		if(expression instanceof PercentageLiteral){
			return ((PercentageLiteral) expression).value + "%";
		} else if(expression instanceof PixelLiteral){
			return ((PixelLiteral) expression).value + "px";
		} else if(expression instanceof ColorLiteral){
			return ((ColorLiteral) expression).value;
		}
		return "";
	}


}
