package net.opendf.ir.am;

import java.util.List;
import java.util.Objects;

import net.opendf.ir.common.Expression;
import net.opendf.ir.common.Statement;

/**
 * A predicate condition represents the {@link Condition condition} that a
 * boolean expression evaluates to <tt>true</tt>.
 * 
 * @author Jorn W. Janneck <jwj@acm.org>
 * 
 */

public class PredicateCondition extends Condition {

	@Override
	public ConditionKind kind() {
		return ConditionKind.predicate;
	}

	@Override
	public <R, P> R accept(ConditionVisitor<R, P> v, P p) {
		return v.visitPredicateCondition(this, p);
	}

	public Expression getExpression() {
		return expression;
	}

	public PredicateCondition(Expression expression,int cScope,List<Statement>  peekStatements) {
		this(null, expression,cScope,peekStatements);
	}

	private PredicateCondition(PredicateCondition original, Expression expression,int cScope,List<Statement>  peekStatements) {
		super(original);
		this.expression = expression;
		this.cScope = cScope;
		this.peekStatements = peekStatements;
	}

	public PredicateCondition copy(Expression expression) {
		if (Objects.equals(this.expression, expression)) {
			return this;
		}
		return new PredicateCondition(this, expression,this.cScope,this.peekStatements);
	}
	public int getScopes() {
		return cScope;
	}

	

	public List<Statement> getPeekStatements() {
		return peekStatements;
	}

	

	private Expression expression;
	private int cScope;
	private List<Statement>  peekStatements;
}
