package org.molgenis.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Field;
import org.molgenis.model.elements.Model;

public class MolgenisModel
{
	private static final transient Logger logger = Logger.getLogger(MolgenisModel.class.getSimpleName());

	public static Model parse(MolgenisOptions options) throws Exception
	{
		Model model = null;

		try
		{
			logger.info("parsing db-schema from " + options.model_database);
			Vector<String> db_files = options.model_database;
			for (int i = 0; i < db_files.size(); i++)
				db_files.set(i, options.path + db_files.get(i));

			model = MolgenisModelParser.parseDbSchema(options.model_database);

			logger.debug("read: " + model);

			// if (!options.exclude_system) Model.createSystemTables(model);
			MolgenisModelValidator.validate(model, options);

			logger.info("parsing ui-schema");
			model = MolgenisModelParser.parseUiSchema(options.path + options.model_userinterface, model);
			// if (options.force_molgenis_package == true)
			// model.setName("molgenis");
			
			MolgenisModelValidator.validateUI(model, options);

			logger.debug("validated: " + model);
		}
		catch (MolgenisModelException e)
		{
			logger.error("Parsing failed: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return model;
	}

	public static Model parse(Properties p) throws Exception
	{
		MolgenisOptions options = new MolgenisOptions(p);
		return parse(options);
	}

	public static void sortEntitiesByDependency(List<Entity> entityList, Model model) throws Exception
	{
		// buble sort
		int count = 0;
		int maxcount = entityList.size() * entityList.size();
		Entity swapCheck = null;
		for (int i = 0; i < entityList.size() - 1; i++)
		{
			if (count == maxcount)
			{
				logger
						.warn("you have a cyclic relationship in you database scheme. check the 'swapped .. with ..' messages for clues.");
				return;
			}

			Entity currentEntity = entityList.get(i);
			List<String> dependencies = getDependencies(currentEntity, model);
			
			//logger.debug(currentEntity.getName()+" depends on: "+ dependencies);
			

			if (currentEntity.hasAncestor()) dependencies.add(currentEntity.getAncestor().getName());
			// //ancestor

			// if (currentEntity.hasImplements())
			// {
			// for (Entity e : currentEntity.getImplements())
			// dependencies.add(e.getName()); //imlements
			// }

			// move all dependencies before 'self'
			for (String entity : dependencies)
			{
				int xrefPosition = indexOf(entityList, entity);
				if (xrefPosition > i)
				// need to swap when index of referenced entity is larger than
				// own index
				// this is endless in case of cyclic relationship!
				{
					// swap
					Entity xrefEntity = entityList.get(xrefPosition);

					// don't swap if the other refers to 'me'
					if (!getDependencies(xrefEntity, model).contains(currentEntity.getName()))
					{

						entityList.set(i, xrefEntity);
						entityList.set(xrefPosition, currentEntity);
						//logger.debug("swapped " + entityList.get(xrefPosition).getName() + " with "
						//		+ entityList.get(i).getName());
						i--; // check swapped entity
						break;
					}
					else
					{
						//logger.debug("loop detected between " + currentEntity.getName() + " and "
						//		+ xrefEntity.getName());
					}
				}
			}
			count++;
		}
	}

	private static int indexOf(List<Entity> entityList, String entityName)
	{
		for (int i = 0; i < entityList.size(); i++)
		{
			if (entityList.get(i).getName().equals(entityName)) return i;
		}
		return 0;
	}


	private static List<String> getDependencies(Entity currentEntity, Model model) throws MolgenisModelException
	{
		List<String> dependencies = new ArrayList<String>();
		
		for (Field field : currentEntity.getAllFields())
		{
			if (field.getType().toString().equals("xref"))
			{
				dependencies.add(field.getXrefEntityName()); 

				Entity xrefEntity = field.getXrefEntity();

				// also all subclasses have this xref!!!!
				for (Entity e : xrefEntity.getAllDescendants())
				{
					if (!dependencies.contains(e.getName())) dependencies.add(e.getName());
					// System.out.println("PARENT OF "+field.getXRefEntity()+
					// "="+
					// model.getEntity(field.getXRefEntity()).getParents());
				}
			}
			 if (field.getType().toString() == "mref")
			 {
			 dependencies.add(field.getXrefEntity().getName()); //mref fields
			// including super classes
			 dependencies.addAll(model.getEntity(field.getXrefEntity().getName()).getParents());
			 }
		}

		return dependencies;
	}
}
