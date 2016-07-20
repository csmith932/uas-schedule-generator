package gov.faa.ang.swac.common.interfaces;

public interface Visitor<N> {
	void visit(N visitee);
}