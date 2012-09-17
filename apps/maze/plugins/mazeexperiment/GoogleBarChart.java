package plugins.mazeexperiment;

import com.googlecode.charts4j.*;
import static com.googlecode.charts4j.Color.*;
//import static com.googlecode.charts4j.UrlUtil.normalize;

/**
 * 
 * @author A.S.Boerema
 *
 */


public class GoogleBarChart {

	private String url;
	
    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void createBarChart(double[] dataArray) {
		// EXAMPLE CODE START
        // Defining data plots.
		
		//double[] blaat = {8,9,10,11,12,13,14};
		//BarChartPlot test1 = Plots.newBarChartPlot(Data.newData(1,2,3,4,5,6,7),ORANGERED);
		//BarChartPlot test2 = Plots.newBarChartPlot(Data.newData(blaat),LIMEGREEN);
		BarChartPlot dayAct = Plots.newBarChartPlot(Data.newData(dataArray),BLACK);
		
        //BarChartPlot team1 = Plots.newBarChartPlot(Data.newData(25, 43, 12, 30), BLUEVIOLET, "Team A");
       // BarChartPlot team2 = Plots.newBarChartPlot(Data.newData(8, 35, 11, 5), ORANGERED, "Team B");
        //BarChartPlot team3 = Plots.newBarChartPlot(Data.newData(10, 20, 30, 30), LIMEGREEN, "Team C");

        // Instantiating chart.
        BarChart chart = GCharts.newBarChart(dayAct);

        // Defining axis info and styles
        //AxisStyle axisStyle = AxisStyle.newAxisStyle(BLACK, 13, AxisTextAlignment.CENTER);
        //AxisStyle xAxisStyle = AxisStyle.newAxisStyle(null, 0, null);
       // AxisStyle yAxisStyle = AxisStyle.newAxisStyle(null, 0, null);
        
        //AxisLabels score = AxisLabelsFactory.newAxisLabels("Score", 50.0);
        //score.setAxisStyle(axisStyle);
        //AxisLabels year = AxisLabelsFactory.newAxisLabels("Year", 50.0);
        //year.setAxisStyle(axisStyle);

        // Adding axis info to chart.
        
        //chart.addXAxisLabels(AxisLabelsFactory.newAxisLabels("2002", "2003", "2004", "2005"));
        //chart.addYAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(0, 100));
        //chart.addYAxisLabels(score);
        //chart.addXAxisLabels(year);

        chart.setSize(720, 20);
        chart.setBarWidth(1);
        chart.setSpaceWithinGroupsOfBars(0);
        chart.setSpaceBetweenGroupsOfBars(0);
        chart.setDataStacked(true);
        //chart.setTitle("Team Scores", BLACK, 16);
        //chart.setGrid(100, 10, 3, 2);
        chart.setBackgroundFill(Fills.newSolidFill(ALICEBLUE));
        //LinearGradientFill fill = Fills.newLinearGradientFill(0, LAVENDER, 100);
        //fill.addColorAndOffset(WHITE, 0);
        //chart.setAreaFill(fill);

        this.url = chart.toURLString();
	}
}
