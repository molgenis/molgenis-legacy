
package lifelines.loaders;

import javax.faces.event.ActionEvent;

/**
 *
 * @author jorislops
 */
public class Setup {

    String dataDictonary = "/Users/jorislops/Desktop/Archive/datadescription/Top10.nl.xls";
    String dataDirectory = "/Users/jorislops/Desktop/Archive/data";

//    String dataDictonary = "/home/antonakd/lifelinesData/datadescription/Top10.nl.xls";
//    String dataDirectory = "/home/antonakd/lifelinesData/data";

    public static void main(String[] args) {
        Setup setup = new Setup();
        setup.convertLIfeLiensToPheno(null);
        setup.loadLifeLinesData(null);
        setup.EAVToRelational(null);
    }
    
    public void convertLIfeLiensToPheno(ActionEvent ae) {
        try {
            ConverterLifeLinesPheno.main(new String[]{dataDictonary});
            //new LoadLifeLinesData().main(new String[] {dataDirectory});
            //new EAVToRelational().main(null);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }

    }
    
    public void loadLifeLinesData(ActionEvent ae) {
        try {
            //new ConverterLifeLinesPheno(dataDictonary).main(new String[]{dataDictonary});
             LoadLifeLinesData.main(new String[] {dataDirectory});
            //new EAVToRelational().main(null);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public void EAVToRelational(ActionEvent ae) {
        try {
            //new ConverterLifeLinesPheno(dataDictonary).main(new String[]{dataDictonary});
            //new LoadLifeLinesData().main(new String[] {dataDirectory});
            new EAVToRelational().main(null);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
    
    public void oracleToText(ActionEvent ar) {
        try {
            //new ConverterLifeLinesPheno(dataDictonary).main(new String[]{dataDictonary});
//             OracleToText.main(null);
            //new EAVToRelational().main(null);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }        
    }


}
