package org.molgenis.lifelines;

import org.molgenis.matrix.component.SliceablePhenoMatrixMV;
import org.molgenis.pheno.ObservationElement;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.HandleException;

import app.DatabaseFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import org.gridgain.grid.util.GridBoundedLinkedHashMap;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.Utils.MatrixExporters;
import org.molgenis.matrix.component.Column;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;

//import static ch.lambdaj.Lambda.*;
//import static org.hamcrest.core.IsEqual.*;

/**
 * Servlet implementation class jqGrid
 */
public class jqGrid extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private SliceablePhenoMatrixMV<ObservationTarget, Measurement> matrix;
    int investigationId = 50;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public jqGrid() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        Database db = null;
        try {
            String operation = request.getParameter("op");
            String selectedColumnsPar = request.getParameter("selectedCols");
            String exportType = request.getParameter("exportType");

            //        @SuppressWarnings("unchecked")
            //        Enumeration<String> en = request.getParameterNames();
            //        while (en.hasMoreElements()) {
            //            String name = en.nextElement();
            //            System.out.println("name: " + name + " value: "
            //                    + request.getParameter(name));
            //        }        

            EntityManager em = null;
            db = DatabaseFactory.create();
            em = db.getEntityManager();

            LinkedHashMap<Protocol, List<Measurement>> selectedMeasurementByProtocol = getSelectedMeasurements(em, selectedColumnsPar);
            Investigation investigation = em.find(Investigation.class, investigationId);
            LinkedHashMap<Protocol, List<Measurement>> measurementByProtocol = getMeasurementByProtocol(em, investigation);

            try {
                if (StringUtils.isNotEmpty(operation) && operation.equals("jsTreeJson")) {
                    jsTreeJson(response, measurementByProtocol);
                    return;
                }
            } catch (Exception ex) {
                HandleException.handle(ex);
            }

            
            if (selectedMeasurementByProtocol.isEmpty()) {
                //first time only, when data is loadeded!
                Protocol protocolBezoek1 = em.find(Protocol.class, 50);
                selectedMeasurementByProtocol = new LinkedHashMap<Protocol, List<Measurement>>();
                selectedMeasurementByProtocol.put(protocolBezoek1, (List<Measurement>) (List) protocolBezoek1.getFeatures());
            }
 
            //initalize matrix!
            matrix = new SliceablePhenoMatrixMV<ObservationTarget, Measurement>(db, ObservationTarget.class, Measurement.class, investigation, selectedMeasurementByProtocol);
            List<Column> columns = matrix.getColumns();

            if (StringUtils.isNotEmpty(operation) && operation.equals("getColModel")) {
                getColModel(response.getWriter(), selectedMeasurementByProtocol);
                return;
            } else if (StringUtils.isNotEmpty(operation) && operation.equals("getColumnsNames")) {
                getColumnNames(response.getWriter(), selectedMeasurementByProtocol);
                return;
            }

            renderMatrix(request, response, em, exportType, columns, matrix, db);
        } catch (Exception ex) {
            Logger.getLogger(jqGrid.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                db.close();
            } catch (DatabaseException dbEx) {
                Logger.getLogger(jqGrid.class.getName()).log(Level.SEVERE, null, dbEx);
            }
        }
    }

    private void addJoinColumns(LinkedHashMap<Protocol, List<Measurement>> mesurementsByProtocol, EntityManager em, String joinColumn) {
        
        for (Map.Entry<Protocol, List<Measurement>> entry : mesurementsByProtocol.entrySet()) {
            List<Measurement> measurements = entry.getValue();
            //boolean paIDExists = exists(entry.getValue(), having(on(Measurement.class).getName(), IsEqualIgnoringCase.equalToIgnoringCase(JOIN_COLUMN)));
            boolean paIDExists = false;
            for (Measurement m : entry.getValue()) {
                if (m.getName().equalsIgnoreCase(joinColumn)) {
                    paIDExists = true;
                    break;
                }
            }

            if (!paIDExists) {
                Measurement paIdMes = em.createQuery("SELECT m FROM Measurement m JOIN m.featuresCollection p WHERE m.name = :name and p = :protocol", Measurement.class).setParameter("name", joinColumn).setParameter("protocol", entry.getKey()).getSingleResult();
                measurements.add(paIdMes);
            }               
        }
    }
    
    public void renderMatrix(HttpServletRequest request, HttpServletResponse response, EntityManager em,
            String exportType,
            List<Column> columns,
            SliceablePhenoMatrixMV<ObservationTarget, Measurement> matrix, Database db) {
        String pageParam = request.getParameter("page");
        String limitParam = request.getParameter("rows");
        String sidx = request.getParameter("sidx");
        String sord = request.getParameter("sord");

        int page = 1;
        if (StringUtils.isNotEmpty(pageParam)) {
            page = Integer.parseInt(pageParam);
        }
        
        int limit = 10;
        if (StringUtils.isNotEmpty(limitParam)) {
            limit = Integer.parseInt(limitParam);
        }

        if (StringUtils.isNotEmpty(sidx)) {
            String[] parts = sidx.split("\\.");
            Protocol p = em.find(Protocol.class, Integer.parseInt(parts[0]));
            Measurement m = em.find(Measurement.class, Integer.parseInt(parts[1]));
            if (StringUtils.isEmpty(sord)) {
                sord = "ASC";
            } 
            matrix.setSort(p, m, sord);
        }
        //put constraints in matrix (offset, limit, filters)
        matrix.setRowOffset(page);
        matrix.setRowLimit(limit);
        
        
        //{"groupOp":"AND","rules":[{"field":"GEWICHT","op":"eq","data":"136"},{"field":"LENGTE","op":"eq","data":"1212"}]}
        String filters = request.getParameter("filters");
        applyFiltersToMatrix(matrix, filters);

        try {


            if (StringUtils.isNotEmpty(exportType) && exportType.equals("excel")) {
                MatrixExporters.getAsExcel(db, matrix, response.getOutputStream());
            } else {
                renderJsonTable(matrix, response.getWriter(), db);
            }
        } catch (Exception e) {
            HandleException.handle(e);
        }
    }

    public void renderJsonTable(SliceablePhenoMatrixMV<ObservationTarget, Measurement> matrix, PrintWriter outWriter, Database db) throws Exception {
        List<ObservedValue>[][] values = matrix.getValueLists();
        List<? extends ObservationElement> rows = matrix.getRowHeaders();

        StringBuilder out = new StringBuilder();
        
        out.append("<?xml version='1.0' encoding='utf-8'?>");
        out.append("<rows>");

        int rowLimit = matrix.getRowLimit();
        int rowCount = matrix.getRowCount();
        int rowOffset = matrix.getRowOffset();

        //int currentPage = (int) Math.ceil((float) rowOffset / (float) rowLimit);
        int totalPages = (int) Math.ceil((float) rowCount / (float) rowLimit);

        out.append(String.format("<page>%s</page>", rowOffset));
        out.append(String.format("<total>%s</total>", totalPages));
        out.append(String.format("<records>%s</records>", rowCount));
        //print rowHeader + colValues
        for (int row = 0; row < values.length; row++) {
            String rowId = rows.get(row).getName(); //patient ID?
            out.append(String.format("<row id=\"%s\">", rowId));

            List<ObservedValue>[] rowValues = values[row];
            for (List<ObservedValue> ovRec : rowValues) {
                for (ObservedValue ov : ovRec) {
                    if (StringUtils.isNotEmpty(ov.getValue())) {
                        out.append(String.format("<cell>%s</cell>", ov.getValue()));
                    } else {
                        out.append(String.format("<cell>%s</cell>", ""));
                    }
                }
            }
            out.append("</row>");
        }        
        out.append("</rows>");
        System.out.println(out.toString());
        outWriter.append(out.toString());
        outWriter.flush();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private static void getColModel(PrintWriter out, LinkedHashMap<Protocol, List<Measurement>> measurementByProtocol) {
        List<JSONObject> data = new ArrayList<JSONObject>();
        JSONArray result = null;
        try {
            for (Map.Entry<Protocol, List<Measurement>> entry : measurementByProtocol.entrySet()) {
                for (int i = 0; i < entry.getValue().size(); ++i) {
                    Measurement m = entry.getValue().get(i);
                    String colName = m.getName();
                    String dataType = m.getDataType();
                    String stype = "text";
                    //for code "select"
                    
                    
                    JSONObject column = new JSONObject();
                    
                    column.put("name", colName);
                    column.put("index", String.format("%s.%s", entry.getKey().getId(), entry.getValue().get(i).getId()));
                    column.put("width", 100);
                    

                    column.put("search", true);
                    column.put("stype", stype);
                    
                    if(dataType.equals("datetime")) {
//                        JSONObject attr = new JSONObject();
//                        attr.put("title", "Select Date");
//                        searchOptions.put("attr", attr);
//                      
//                        column.put("searchoptions", "{dataInit:function(el){$(el).datepicker({dateFormat:'yy-mm-dd'});} }");    
//                        column.p
                    }
                    

                    
                    
                    
                    
                    data.add(column);
                }
            }
            result = new JSONArray(data);
        } catch (JSONException e) {
            HandleException.handle(e);
        }
        try {
            result.write(out);
        } catch (JSONException ex) {
            Logger.getLogger(jqGrid.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //String json = String.format(result.toString(), "{dataInit:function(el){$(el).datepicker({dateFormat:\'yy-mm-dd\'});} }");
        
        //System.out.println(json);
        //out.println(json);
        out.flush();
    }

    private static void getColumnNames(PrintWriter out, LinkedHashMap<Protocol, List<Measurement>> measurementByProtocol) {
        List<String> values = new ArrayList<String>();
        
        for (Map.Entry<Protocol, List<Measurement>> entry : measurementByProtocol.entrySet()) {
            for (Measurement measurement : entry.getValue()) {
                values.add(measurement.getName());    
            }
        }
        JSONArray data = new JSONArray(values);
        out.append(data.toString());
        out.flush();
    }
    
    private static void applyFiltersToMatrix(SliceablePhenoMatrixMV<ObservationTarget, Measurement> matrix, String filters) {
        if (StringUtils.isNotEmpty(filters)) {
            JSONObject jFilter = null;
            try {
                jFilter = new JSONObject(filters);

                String groupOp = (String) jFilter.get("groupOp");
                JSONArray rules = jFilter.getJSONArray("rules");

                for (int i = 0; i < rules.length(); ++i) {
                    JSONObject searchRule = (JSONObject) rules.get(i);

                    String field = (String) searchRule.get("field");
                    
                    String[] parts = field.split("\\.");
                    int protocolId = Integer.parseInt(parts[0]);
                    int measurementId = Integer.parseInt(parts[1]);
                    
                    String op = (String) searchRule.get("op");
                    String value = (String) searchRule.get("data");
                    matrix.sliceByColValueProperty(protocolId, measurementId, op, Operator.EQUALS, value);
                }
                System.out.println(groupOp);
                System.out.println(rules);
            } catch (Exception ex) {
                Logger.getLogger(jqGrid.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void jsTreeJson(HttpServletResponse response, LinkedHashMap<Protocol, List<Measurement>> mesurementsByProtocol) throws IOException {
        try {
            List<JSONObject> tableNodes = new ArrayList<JSONObject>();

            for (Map.Entry<Protocol, List<Measurement>> entry : mesurementsByProtocol.entrySet()) {
                String tableName = entry.getKey().getName();
                Integer protocolId = entry.getKey().getId();

                JSONObject tableNode = new JSONObject();
                tableNode.put("data", tableName);
                tableNode.put("attr", new JSONObject().put("id", protocolId));
                tableNode.put("metadata", new JSONObject().put("id", protocolId));

                List<JSONObject> children = new ArrayList<JSONObject>();
                for (Measurement measurement : entry.getValue()) {
                    JSONObject columnNode = new JSONObject();
                    columnNode.put("data", measurement.getName());
                    columnNode.put("attr", new JSONObject().put("id", protocolId + "." + measurement.getId()));
                    columnNode.put("metadata", new JSONObject().put("id", protocolId + "." + measurement.getId()));
                    children.add(columnNode);
                }
                tableNode.put("children", children);
                tableNodes.add(tableNode);
            }
            JSONArray data = new JSONArray(tableNodes);

            PrintWriter out = response.getWriter();
            System.out.println(data.toString());
            out.println(data.toString());
            out.flush();
        } catch (Exception e) {
            HandleException.handle(e);
        }
    }

    private LinkedHashMap<Protocol, List<Measurement>> getMeasurementByProtocol(EntityManager em, Investigation investigation) {
        String ql = "SELECT p FROM Protocol p WHERE p.investigation = :investigation";
        List<Protocol> protocols = em.createQuery(ql, Protocol.class).setParameter("investigation", investigation).getResultList();
        LinkedHashMap<Protocol, List<Measurement>> measurementsByProtocol = new LinkedHashMap<Protocol, List<Measurement>>();
        for (Protocol protocol : protocols) {
            measurementsByProtocol.put(protocol, (List<Measurement>) (List) protocol.getFeatures());
        }
        return measurementsByProtocol;
    }

    private LinkedHashMap<Protocol, List<Measurement>> getSelectedMeasurements(EntityManager em, String selectedColumnsPar) {
        LinkedHashMap<Protocol, List<Measurement>> selectedMeasurementByProtocol = new GridBoundedLinkedHashMap<Protocol, List<Measurement>>(investigationId);
        if (StringUtils.isNotEmpty(selectedColumnsPar) && !selectedColumnsPar.equals("null")) {
            String[] cols = selectedColumnsPar.substring(2, selectedColumnsPar.length() - 2).replaceAll("\"", "").split(",");
            for (String c : cols) {
                String[] parts = c.split("\\.");
                int protocolId = Integer.parseInt(parts[0]);

                Protocol protocol = em.find(Protocol.class, protocolId);

                if (parts.length > 1) {
                    int measurementId = Integer.parseInt(parts[1]);
                    Measurement m = em.find(Measurement.class, measurementId);

//                    boolean exists = exists(selectedMeasurementByProtocol.keySet(),
//                            having(on(Protocol.class).getId(), equalTo(protocol.getId())));
                    boolean exists = false;
                    for(Protocol p : selectedMeasurementByProtocol.keySet()) {
                    	if(p.getId().equals(protocol.getId())) {
                    		exists = true;
                    		break;
                    	}
                    }
                    
                    
                    if (exists) {
                        if (!selectedMeasurementByProtocol.get(protocol).contains(m)) {
                            selectedMeasurementByProtocol.get(protocol).add(m);
                        }
                    } else {
                        List<Measurement> measurments = new ArrayList<Measurement>();
                        measurments.add(m);
                        selectedMeasurementByProtocol.put(protocol, measurments);
                    }
                } else {
                    selectedMeasurementByProtocol.put(protocol, (List<Measurement>) (List) protocol.getFeatures());
                }
            }
        }
        return selectedMeasurementByProtocol;
    }
}
