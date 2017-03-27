package net.opendf.ir.common;

import java.util.Objects;

import net.opendf.ir.util.ImmutableList;

public class StmtConsume extends Statement {
	private Port port;
	private int tokens;
	private ImmutableList<String> variables;

	@Override
	public <R, P> R accept(StatementVisitor<R, P> v, P p) {
		return v.visitStmtConsume(this, p);
	}

	public Port getPort() {
		return port;
	}

	public int getNumberOfTokens() {
		return tokens;
	}
	public ImmutableList<String> getVariabls() {
		return variables;
	}

	public StmtConsume(Port port, int tokens, ImmutableList<String> vars) {
		this(null, port, tokens,vars);
	}

	private StmtConsume(StmtConsume original, Port port, int tokens, ImmutableList<String> vars) {
		super(original);
		this.port = port;
		this.tokens = tokens;
		this.variables = vars;
	}

	public StmtConsume copy(Port port, int tokens) {
		if (Objects.equals(this.port, port) && this.tokens == tokens) {
			return this;
		}
		return new StmtConsume(this, port, tokens,variables);
	}
}