package lifelines.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

import app.JpaDatabase;
import java.util.HashMap;

import javax.persistence.EntityManager;

//import app.JDBCDatabase;

/*
 * Loads the data. Make sure that the datastructes is created!
 * If the datastructure is not created there will be
 * a Missing Features-list in the output.
 */
public class LoadLifeLinesData {

    public static void main(String[] args) throws Exception {
        String dataDir = "/Users/jorislops/Desktop/Archive/data";
        if (args != null && args.length > 0) {
            dataDir = args[0];
        }

        int paIdx = 0; // name for protocal Application

        JpaDatabase db = new JpaDatabase();
        EntityManager em = db.getEntityManager();

        String ql = "SELECT i FROM Investigation i WHERE i.name = 'Life Lines Study Dev1'";
        Investigation investigation = em.createQuery(ql, Investigation.class).getSingleResult();

        HashMap<String, Integer> fkMis = new HashMap<String, Integer>();

        File dir = new File(dataDir);
        //File dir = new File("/Users/robert/workspace/workspace_lifelines/gcc/Archive/data");

        String dataSetName = "Top10";

        try {
            for (File file : dir.listFiles()) {
                if (file.isHidden()) {
                    continue;
                }

                BufferedReader reader = new BufferedReader(new FileReader(file));
                String headerLine = reader.readLine();
                String[] columns = headerLine.split(",");

                Measurement[] features = new Measurement[columns.length];

                String fileName = file.getName();
                String tableName = fileName.substring(0, fileName.length() - 4);

//                List<Protocol> protocols = db.find(Protocol.class,
//                        new QueryRule("name", Operator.EQUALS, dataSetName));

                for (int i = 0; i < columns.length; ++i) {
                    List<Measurement> fList = db.find(Measurement.class,
                            new QueryRule("name", Operator.EQUALS, tableName
                            + "." + columns[i].toLowerCase().trim()));
                    if (fList.size() > 0) {
                        features[i] = fList.get(0);
                    } else {
                        String fieldName = columns[i].trim();
                        if (fieldName.contains("(") && fieldName.contains("")) {
                            fieldName = fieldName.substring(
                                    fieldName.indexOf("(") + 1,
                                    fieldName.indexOf(")")).trim();
                        }
                        fList = db.find(Measurement.class, new QueryRule(
                                "name", Operator.EQUALS, tableName + "."
                                + fieldName));
                        if (fList.size() > 0) {
                            features[i] = fList.get(0);
                        }
                    }
                }

                while (reader.ready()) {
                    db.beginTx();

                    String line = reader.readLine();
                    String[] values = line.split(",");

                    ObservationTarget target = null;
                    for (int i = 0; i < values.length; ++i) {
                        if (columns[i].equals("PA_ID")) {
                            ObservationTarget example = new ObservationTarget();
                            example.setName(values[i]);
                            example.setInvestigation(investigation);

                            // should be replaced by db.findByExample()
                            List<ObservationTarget> exampleTargets = em.createQuery(
                                    "SELECT o FROM ObservationTarget o WHERE o.name = :name AND o.investigation = :investigation",
                                    ObservationTarget.class).setParameter("name", values[i]).setParameter("investigation",
                                    investigation).getResultList();
                            if (exampleTargets.size() > 0) {
                                target = exampleTargets.get(0);
                            }

                            if (target == null) {
                                target = new ObservationTarget();
                                target.setInvestigation(investigation);
                                target.setName(values[i]);
                                db.add(target);
                            }
                        }

                        if (!values[i].trim().isEmpty()
                                && features[i] != null) {
                            if (target != null) {
                                ObservedValue ov = new ObservedValue();
                                ov.setFeature(features[i]);

                                Measurement feature = features[i];
                                String type = feature.getDataType();

                                String value = values[i].trim();
                                if (value.isEmpty()) {
                                    ov.setValue("empty");
                                } else { // if (type.equals("string")) {
                                    ov.setValue(value);
                                }

                                // else if (type.equals("int") ||
                                // type.equals("code")) {
                                // ov.setValue("incorrect");
                                // try {
                                // Integer.parseInt(value);
                                // ov.setValue(value);
                                // } catch (NumberFormatException nfe) {
                                // String errorMsg =
                                // "Invalid number: %s in File: %s For FieldName %s";
                                // System.err.println(String.format(errorMsg,
                                // value, fileName, columns[i]));
                                // }
                                // } else if (type.equals("datetime")) {
                                // ov.setValue("incorrect");
                                //
                                // try {
                                // SimpleDateFormat parse = new
                                // SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                                // ov.setValue(new
                                // java.sql.Timestamp(parse.parse(value).getTime()).toString());
                                // } catch (ParseException pe0) {
                                // try {
                                // SimpleDateFormat parse = new
                                // SimpleDateFormat("MM-dd-yyyy");
                                // ov.setValue(new
                                // java.sql.Timestamp(parse.parse(value).getTime()).toString());
                                // } catch (ParseException pe1) {
                                // String errorMsg =
                                // "Invalid DateTime: %s in File: %s For FieldName %s";
                                // System.err.println(String.format(errorMsg,
                                // value, fileName, columns[i]));
                                // }
                                // }
                                //
                                // } else if (type.equals("decimal")) {
                                // ov.setValue("incorrect");
                                // try {
                                // Float.parseFloat(value);
                                // ov.setValue(value);
                                // } catch (NumberFormatException nfe) {
                                // String errorMsg =
                                // "Invalid Decimal: %s in File: %s For FieldName %s";
                                // System.err.println(String.format(errorMsg,
                                // value, fileName, columns[i]));
                                // }
                                // } else if (type.equals("image")) {
                                // ov.setValue("image");
                                // } else if (type.equals("")) {
                                // ov.setValue("empty");
                                // } else {
                                // String errorMessage =
                                // "DataFile: %s for field: %s has unkown type: %s";
                                // throw new
                                // IllegalArgumentException(String.format(errorMessage,
                                // tableName, columns[i], type));
                                // }

//                                ProtocolApplication pa = new ProtocolApplication();
//                                pa.setInvestigation(investigation);
//                                pa.setName("" + paIdx++);
//
//                                pa.setProtocol(protocols.get(0));
//                                
//                                
//                                pa.setTime(new Date());
//
//                                db.add(pa);

//                                ov.setProtocolApplication(pa);
                                ov.setTarget(target);
                                ov.setInvestigation(investigation);

                                db.add(ov);
                            }
                            // Thread.sleep(1000);
                        } else {
                            if (fkMis.get(tableName) == null) {
                                fkMis.put(tableName, 1);
                            } else {
                                fkMis.put(tableName, fkMis.get(tableName) + 1);
                            }

                        }
                    }
                    db.commitTx();
                    db.getEntityManager().clear();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollbackTx();
        }
        System.out.println("Missing Features:");
        for (String key : fkMis.keySet()) {
            System.err.println(key + " " + fkMis.get(key));
        }
        System.err.println("End!");
    }
}
