package org.molgenis.framework.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jdbc.JDBCConnectionHelper;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

public class JoinQuery extends QueryImp {
	
	private transient static final Logger logger = Logger.getLogger(JoinQuery.class);
	Map<String, Field> fields = new LinkedHashMap<String, Field>();

	public JoinQuery(Database db, List<String> fieldnames) throws DatabaseException {
		
		this.setDatabase(db);
		this.setFields(fieldnames);
	}

	private JoinQuery setFields(List<String> fieldnames) throws DatabaseException {
		for (String f : fieldnames) {
			try {
				Field field = getDatabase().getMetaData().findField(f);
				if (field == null) {
					throw new DatabaseException("couldn't find " + f);
				}
				this.fields.put(f, field);
			} catch (MolgenisModelException e) {
				e.printStackTrace();
				throw new DatabaseException(e);

			}
		}
		return this;
	}

	@Override
	public List<Tuple> find() throws DatabaseException {
		try {
			String sql = toFindSql(super.getRules());
			return ((JDBCDatabase) getDatabase()).sql(sql);
		} catch (MolgenisModelException e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		}
	}

	@Override
	public int count() throws DatabaseException {
		return ((JDBCDatabase) getDatabase()).sql(toCountSql(), super.getRules()).get(0).getInt(0);

	}

	private String toCountSql() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toFindSql(QueryRule... ruleArray) throws DatabaseException,
			MolgenisModelException {
		if (fields.size() == 0) {
			throw new DatabaseException("no fields selected");
		}
		List<Entity> entities = new ArrayList<Entity>();
		List<QueryRule> rules = new ArrayList<QueryRule>();
		if (ruleArray != null) {
			rules.addAll(Arrays.asList(ruleArray));
		}

		// cleanup rules to have right names
		for (QueryRule r : ruleArray) {
			if (r.getField() != null) {
				Field f = this.fields.get(r.getField());
				r.setField(f.getEntity().getName() + "." + f.getName());
			}
			if (r.getOperator() == Operator.SORTASC
					|| r.getOperator() == Operator.SORTDESC) {
				Field f = this.fields.get(r.getValue());
				r.setValue(f.getEntity().getName() + "." + f.getName());
			}
		}

		// find the entities
		for (Field f : fields.values()) {
			Entity ed = f.getEntity();
			if (!entities.contains(ed)) {
				entities.add(ed);
			}
		}

		// find join edges by finding links between entities
		for (Entity ed1 : entities) {
			for (Entity ed2 : entities) {
				if (ed1 != ed2) {
					for (QueryJoinRule jr : findShortestJoinPath(ed1, ed2)) {
						if (!rules.contains(jr)) {
							rules.add(jr);
						}
					}

					// add join for extends relationship
					for (Entity extend : ed2.getAllAncestors()) {
						if (extend.getName().equals(ed1.getName())) {
							String pkey1 = ed2.getPrimaryKey().getName();
							String pkey2 = extend.getPrimaryKey().getName();

							rules.add(new QueryJoinRule(ed2.getName(), pkey1,
									extend.getName(), pkey2));
						}
					}
				}
			}
		}

		for (QueryRule r : rules) {
			if (r instanceof QueryJoinRule) {
				QueryJoinRule jr = (QueryJoinRule) r;
				// add intermediate entities
				if (!entities.contains(getDatabase().getMetaData().getEntity(jr.getA()))) {
					entities.add(getDatabase().getMetaData().getEntity(jr.getA()));
				}
				if (!entities.contains(getDatabase().getMetaData().getEntity(jr.getB()))) {
					entities.add(getDatabase().getMetaData().getEntity(jr.getB()));
				}
			}
		}

		addRules(rules.toArray(new QueryRule[rules.size()]));

		// naive
		for (Entity ed1 : entities) {
			for (Entity ed2 : entities) {
				if (ed1 != ed2) {
					// there should be path
					if (!isConnected(ed1.getName(), ed2.getName(), rules)) {
						throw new DatabaseException("Entities " + ed1.getName()
								+ " and " + ed2.getName()
								+ " could not be joined");
					}
				}
			}
		}

		// assemble the sql
		String sql = "SELECT ";
		for (String fieldLabel : fields.keySet()) {
			Field f = fields.get(fieldLabel);
			sql += f.getEntity().getName() + "." + f.getName() + " as '"
					+ fieldLabel + "', ";
		}
		sql = sql.substring(0, sql.length() - 2) + " FROM ";
		// get rid of trailing ', '
		for (Entity md : entities) {
			sql += md.getName() + ", ";
		}
		sql = sql.substring(0, sql.length() - 2);

		sql += JDBCConnectionHelper.createWhereSql(null, false, true,
				rules.toArray(new QueryRule[rules.size()]));

		logger.debug("created custom query: " + sql);
		return sql;
	}

	private boolean isConnected(String ed1, String ed2, List<QueryRule> rules) {

		// is connected if the pair is there
		for (QueryRule r : rules) {
			if (r instanceof QueryJoinRule) {
				QueryJoinRule edge = (QueryJoinRule) r;
				// Logger.getLogger("HAAT").debug("comparing " + edge.getA() +
				// " " + edge.getB());

				// direct?
				if (edge.getA().equals(ed1) 
						&& edge.getB().equals(ed2)) {
					return true;
				}
				if (edge.getB().equals(ed1) 
						&& edge.getA().equals(ed2)) {
					return true;
				}
				// indirect?
				if (edge.getA().equals(ed1)
						&& isConnected(edge.getB(), ed2, rules)) {
					return true;
				}
				if (edge.getA().equals(ed2)
						&& isConnected(edge.getB(), ed1, rules)) {
					return true;
				}
			}
		}
		return false;
	}

	private List<QueryJoinRule> findShortestJoinPath(Entity e1, Entity e2) throws MolgenisModelException, DatabaseException {
		List<QueryJoinRule> shortestPath = new ArrayList<QueryJoinRule>();
		if (e1.getName().equals(e2.getName())) {
			return shortestPath;
		}

		// direct xref?
		// todo: mref
		for (Field fd : e2.getFields()) {
			// if (fd.getType().equals(Field.Type.XREF_SINGLE))
			// {
			// List<QueryJoinRule> path = new ArrayList<QueryJoinRule>();
			// path.add(new QueryJoinRule(fd.getEntity().getName(),
			// fd.getName(), fd.getXRefEntity(), fd
			// .getXRefField()));
			// // direct path?
			// if (fd.getXRefEntity().equals(e1.getName()))
			// {
			// return path;
			// }
			// // cyclic path are not supported
			// else if (fd.getXRefEntity().equals(e2.getName()))
			// {
			// ;
			// }
			// else
			// // indirect path
			// {
			// path.addAll(findShortestJoinPath(getDatabase().getMetaData().getEntity(fd.getXRefEntity()),
			// e1));
			// if (path.size() < shortestPath.size() || shortestPath.size() ==
			// 0) shortestPath = path;
			// }
			// }
		}
		// direct path via inheritance?
		// add join for extends relationship
		if (e2.hasAncestor()) {
			Entity extend = e2.getAncestor();
			List<QueryJoinRule> path = new ArrayList<QueryJoinRule>();
			String pkey1 = e2.getKey(0).getFields().get(0).getName();
			String pkey2 = extend.getKey(0).getFields().get(0).getName();

			path.add(new QueryJoinRule(e2.getName(), pkey1, extend.getName(),
					pkey2));
			if (extend.getName().equals(e1.getName())) {
				return path;
			} else {
				path.addAll(findShortestJoinPath(extend, e1));
				if (path.size() < shortestPath.size()
						|| shortestPath.size() == 0) {
					shortestPath = path;
				}
			}
		}

		for (Field fd : e1.getFields()) {
			// if (fd.getType().equals(Field.Type.XREF_SINGLE))
			// {
			// List<QueryJoinRule> path = new ArrayList<QueryJoinRule>();
			// path.add(new QueryJoinRule(fd.getEntity().getName(),
			// fd.getName(), fd.getXRefEntity(), fd
			// .getXRefField()));
			// // direct path?
			// if (fd.getXRefEntity().equals(e2.getName()))
			// {
			// return path;
			// }
			// // cyclic path
			// else if (fd.getXRefEntity().equals(e1.getName()))
			// {
			// ;
			// }
			// // indirect path
			// else
			// {
			// path.addAll(findShortestJoinPath(getDatabase().getMetaData().getEntity(fd.getXRefEntity()),
			// e2));
			// if (path.size() < shortestPath.size() || shortestPath.size() ==
			// 0) shortestPath = path;
			// }
			// }
		}
		// direct path via inheritance?
		// add join for extends relationship
		if (e1.hasAncestor()) {
			Entity extend = e1.getAncestor();

			List<QueryJoinRule> path = new ArrayList<QueryJoinRule>();
			String pkey1 = e1.getPrimaryKey().getName();
			String pkey2 = extend.getPrimaryKey().getName();
			path.add(new QueryJoinRule(e1.getName(), pkey1, extend.getName(),
					pkey2));
			if (extend.getName().equals(e1.getName())) {
				return path;
			} else {
				path.addAll(findShortestJoinPath(extend, e2));
				if (path.size() < shortestPath.size()
						|| shortestPath.size() == 0) {
					shortestPath = path;
				}
			}
		}
		// Logger.getLogger("HAAT").debug("path " + e1.getName() + " to " +
		// e2.getName() + ":" + shortestPath.size());
		if (shortestPath.size() > 1) {
			// otherwise it was a dead end
			return shortestPath;
		}
		return new ArrayList<QueryJoinRule>();
	}
}
