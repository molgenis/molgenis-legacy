Type="Job";
JobType="Normal";

Executable = "/bin/sh";
Arguments = "${script_name}.sh";

StdError = "${error_log}";
StdOutput = "${output_log}";

InputSandbox = {"${script_location}/${script_name}.sh${extra_inputs}"};
OutputSandbox = {"${error_log}","${output_log}"${extra_outputs}};
