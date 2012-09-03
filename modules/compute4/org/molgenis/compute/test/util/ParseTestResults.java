package org.molgenis.compute.test.util;

/**
 * Created with IntelliJ IDEA.
 * User: georgebyelas
 * Date: 23/08/2012
 * Time: 13:14
 * To change this template use File | Settings | File Templates.
 */
public class ParseTestResults
{
    public static void main(String[] args)
    {
        new ParseTestResults().doMaar();
    }

    private void doMaar()
    {
        String input = "TASKID:Step1b_test1\n" +
                "Step1\n" +
                "Thu Aug 23 13:13:26 CEST 2012\n" +
                "Running on node: wn-car-078.farm.nikhef.nl\n" +
                "Thu Aug 23 13:13:26 CEST 2012";

        int idPos = input.indexOf("TASKID:") + 7;
        int endPos = input.indexOf("\n");

        input = input.substring(idPos, endPos).trim();

        System.out.println("|" + input + "|");
    }
}
