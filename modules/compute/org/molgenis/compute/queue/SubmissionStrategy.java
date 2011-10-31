package org.molgenis.compute.queue;

/**
 * Submission strategy will submit jobs to the ComputeResource. This can be done
 * in many ways. One is to wait until all dependent jobs are done and submit at
 * the latest moment. Alternative, you could also eagerly submit jobs. Moreover,
 * you could combine jobs together into one job.
 */
public interface SubmissionStrategy
{

}
