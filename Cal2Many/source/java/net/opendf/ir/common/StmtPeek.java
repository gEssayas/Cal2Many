package net.opendf.ir.common;

import java.util.Objects;

import net.opendf.ir.util.ImmutableList;

public class StmtPeek extends Statement {
	private Port port;
	private int tokens;
	private ImmutableList<String> variables;

	@Override
	public <R, P> R accept(StatementVisitor<R, P> v, P p) {
		return v.visitStmtPeek(this, p);
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


	public StmtPeek(Port port, int tokens, ImmutableList<String> vars) {
		this(null, port, tokens,vars);
	}

	private StmtPeek(StmtPeek original, Port port, int tokens, ImmutableList<String> vars) {
		super(original);
		this.port = port;
		this.tokens = tokens;
		this.variables = vars;
	}

	public StmtPeek copy(Port port, int tokens) {
		if (Objects.equals(this.port, port) && this.tokens == tokens) {
			return this;
		}
		return new StmtPeek(this, port, tokens,variables);
	}
}
