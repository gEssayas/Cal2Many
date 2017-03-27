package javarag;

import javarag.impl.inst.Request;

public class MissingDefinitionException extends RuntimeException {

	public MissingDefinitionException(Request request) {
		super(request.toString());
	}

}
