/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package umcg.genetica.console;

import umcg.genetica.util.RunTimer;

/**
 *
 * @author harmjan
 */
public class ProgressBar {

    private static RunTimer timer;
    private static int iterations = 0;
    private static int maxIterations = 0;
    private static int printEvery = 0;
    private static final int width = 25;

    public ProgressBar(int length) {
	maxIterations = length;
	printEvery = (int) Math.ceil((double) length / width);
	timer = new RunTimer();

	System.out.println("\nProgress:");

	String out = "|";
	for (int i = 0; i < width; i++) {
	    out += "-";
	}
	out += "| Waiting for update...";

	System.out.print(out);
    }

    public ProgressBar(int length, String title) {
	maxIterations = length;
	printEvery = (int) Math.ceil((double) length / width);
	timer = new RunTimer();

	System.out.println("\nProgress: "+title);

	String out = "|";
	for (int i = 0; i < width; i++) {
	    out += "-";
	}
	out += "| Waiting for update...";

	System.out.print(out);
    }

    public void iterate() {
	iterations++;
	if (iterations % printEvery == 0) {
	    print();
	}
    }

    public void set(int num) {
	iterations = num;
	print();
    }

    public void print() {
	if (printEvery > 0) {
	    int numToPrint = (int) Math.ceil(iterations / printEvery);
	    if(numToPrint > width){
		numToPrint = width;
	    }
	    String out = "|";
	    for (int i = 0; i < numToPrint; i++) {
		out += "#";
	    }
	    for (int i = 0; i < width - numToPrint - 1; i++) {
		out += "-";
	    }
	    out += "| ";
	    int perc = (int) Math.ceil((double) iterations / maxIterations * 100);

	    out += perc + "% - TIME: " + timer.getTimeDesc();

	    long diff = timer.getTimeDiff() / 1000000000;
	    double timePerIter = (double) diff / iterations;
	    double timeLeft = timePerIter * (maxIterations - iterations);
	    String strTimeLeft = timer.getTimeDesc(((long) timeLeft) * 1000000000);
	    out += " - ETA: " + strTimeLeft;
	    out += " - Iter: " + iterations + "/" + maxIterations + "    ";
	    System.out.print("\r" + out);
	}

    }

    public void close() {
	iterations = maxIterations;
	print();
	System.out.println("");

    }
}
