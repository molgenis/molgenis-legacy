package org.molgenis.util;

public interface TupleIterable extends Iterable<Tuple>
{
	Tuple next();
}
