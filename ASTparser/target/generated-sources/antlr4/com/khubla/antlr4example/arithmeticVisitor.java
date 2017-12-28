// Generated from com/khubla/antlr4example/arithmetic.g4 by ANTLR 4.7
package com.khubla.antlr4example;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link arithmeticParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface arithmeticVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link arithmeticParser#equation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEquation(arithmeticParser.EquationContext ctx);
	/**
	 * Visit a parse tree produced by {@link arithmeticParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(arithmeticParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link arithmeticParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(arithmeticParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link arithmeticParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor(arithmeticParser.FactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link arithmeticParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(arithmeticParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link arithmeticParser#scientific}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScientific(arithmeticParser.ScientificContext ctx);
	/**
	 * Visit a parse tree produced by {@link arithmeticParser#relop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelop(arithmeticParser.RelopContext ctx);
	/**
	 * Visit a parse tree produced by {@link arithmeticParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(arithmeticParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link arithmeticParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(arithmeticParser.VariableContext ctx);
}