# Librato-Uploader
Uploads semi-detailed metrics from JUnit, Cobertura, FindBugs, and Checkstyle reports

Command line options:
    -h  , --help                  Display this message
    -mp , --metric-prefix         The prefix to add to every metric name (e.g. environment code - DEV, Test, Live, etc)
    -lu , --librato-user          The librato user to authenticate with Librato
    -ls , --librato-secret-token  The librato secret token to validate the use of the librato user account
    -src, --librato-source        The 'Source' of the metric data (e.g. build server name)
        , --files                 The report files to parse (in any order) - just omit files as required.  Will accept JUnit, Coverage, Findbugs, Checkstyle, and BuildStatus reports (for now).
        , --errors                Display the list of error codes

Example command line:
    java -jar application.jar -mp DEV- -src Travis -lu <user-account> -ls <secret-token> -files findbugs.xml checkstyle.xml ./junit/TESTS-TestSuites.xml ./cobertura/coverage.xml buildstate.xml

Note:
BuildStatus is a custom report designed to allow reporting on a build status (hence the name :p).
The format is very simple - one line of XML, as follows:
<buildstatus startime="millisecond timestamp" endtime="millisecond timestamp" buildid="numeric build id" buildstate="numeric-value" />

Unfortunately, 'buildstate' has to be reported as a numeric value (this is a limitation of Librato - all measurements are numeric).  Personally, I use 0=false, 1=success.

To Do:
    At some point, I'd like to make the list of file consumers / Metric generators configurable via a properties file or similar (so simplify adding new consumers, etc), and perhaps add the ability to set parameters for individual consumers (e.g. to set exlusion or similar).