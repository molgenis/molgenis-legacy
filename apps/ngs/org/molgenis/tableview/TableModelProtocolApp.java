package org.molgenis.tableview;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.Protocol;
import org.molgenis.util.Tuple;

public class TableModelProtocolApp extends TableModel
{
	// keep track of current protocol
	Protocol protocol;

	// keep list of all measurements (this doesn't scale, obviously)
	List<ObservableFeature> features = new ArrayList<ObservableFeature>();

	public TableModelProtocolApp(String name)
	{
		super(name);
		// TODO Auto-generated constructor stub
	}

	public void refresh(Database db) throws DatabaseException
	{
		// get name of the table, aka tablesource.
		// this translates to protocol
		if (protocol == null)
		{
			List<Protocol> result = db.query(Protocol.class).eq(Protocol.IDENTIFIER, getName()).find();
			if (result.size() != 1) throw new DatabaseException("no protocol found for name=" + getName());
			protocol = result.get(0);

			// get the measurements for protocol
			features = db.query(ObservableFeature.class).in(ObservableFeature.ID, protocol.getParameters_Id()).find();
		}

		// simply assume we want to see all columns (doesn't scale)
		// select pa.id as sample,
		// v1.value as v1,
		// v2.value as col2
		// from protocolApplication pa
		// join Characteristic target on (pa.target=target.id)
		// left join ObservedValue as v1 on (pa.id = v1.protocolApplication and
		// v1.feature=1)
		// left join ObservedValue as v2 on (pa.id = v2.protocolApplication and
		// v2.feature=2)

		String sql = "select pa.id, target.identifier as sample ";
		for (ObservableFeature m : features)
			sql += "\n,v" + m.getId() + ".value as " + m.getIdentifier();
		sql += "\nfrom ProtocolApplication pa join Characteristic target on (pa.target=target.id and pa.protocolUsed="
				+ protocol.getId() + ")";
		for (ObservableFeature m : features)
			sql += "\nleft join ObservedValue as v" + m.getId() + " on (pa.id=v" + m.getId()
					+ ".protocolApplication and v" + m.getId() + ".feature=" + m.getId() + ")";

		System.out.println(sql);

		List<Tuple> result = db.sql(sql);
	}

	public void refresh2(Database db) throws DatabaseException
	{
		// get name of the table, aka tablesource.
		// this translates to protocol
		if (protocol == null)
		{
			List<Protocol> result = db.query(Protocol.class).eq(Protocol.IDENTIFIER, getName()).find();
			if (result.size() != 1) throw new DatabaseException("no protocol found for name=" + getName());
			protocol = result.get(0);

			// get the measurements for protocol
			features = db.query(ObservableFeature.class).in(ObservableFeature.ID, protocol.getParameters_Id()).find();
		}

		// simply assume we want to see all columns (doesn't scale)
		// SELECT e.*
		// , MAX( IF(m.KEY= 'first name', m.VALUE, NULL) ) as 'first name'
		// , MAX( IF(m.KEY= 'last name', m.VALUE, NULL) ) as 'last name'
		// , MAX( IF(m.KEY= 'birthday', m.VALUE, NULL) ) as 'birthday'
		// FROM ENTITY e
		// JOIN META m
		// ON e.ID = m.EntityID

		String sql = "select pa.id, target.identifier as sample ";
		for (ObservableFeature m : features)
			sql += "\n,max(if(v.feature=" + m.getId() + ", v.value, NULL)) as " + m.getIdentifier();
		sql += "\nfrom ProtocolApplication pa join Characteristic target on (pa.target=target.id and pa.protocolUsed="
				+ protocol.getId() + ") left join ObservedValue v on v.protocolApplication=pa.id ";
		sql += "\ngroup by pa.id";
	
		System.out.println(sql);

		List<Tuple> result = db.sql(sql);
	}
}
