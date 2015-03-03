package uk.co.mentalspace.libratouploader;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.OptionBuilder;
import uk.co.mentalspace.libratouploader.Processor.Error;

public class Main {
  
  public static void main (String[] args) {
    Options options = new Options();
	 options.addOption( "h", "help", false, "display this message" );
    options.addOption( "errors", false, "Display the list of error codes");
    options.addOption( "mp", "metric-prefix", true, "the prefix to add to every metric name (e.g. environment name)");
    options.addOption( "lu", "librato-user", true, "the librato user to authenticate with Librato");
    options.addOption( "ls", "librato-secret-token", true, "the librato secret token to validate the use of the librato user account");
    options.addOption( "src", "librato-source", true, "the 'Source' of the metric data (e.g. machine name)");
    options.addOption(OptionBuilder.withLongOpt("files").withArgName("JUnitReport> <CoberturaReport> <FindBugsReport> <CheckstyleReport").withValueSeparator(' ').hasArgs(4).withDescription("The report files to parse (in any order) - just omit files as required.").create("t"));    

    CommandLineParser parser = new BasicParser();
    try {
      CommandLine cmd = parser.parse( options, args);
      if (cmd.hasOption("errors")) {
        displayErrorCodes();
        return;
      }
      
      if (cmd.hasOption("help") || !cmd.hasOption("src") || !cmd.hasOption("lk") || !cmd.hasOption("lsk") || !cmd.hasOption("files") || cmd.getOptionValues("files").length < 1) {
        System.out.println(options.toString());
        return;
      }

      Processor processor = new Processor(cmd.getOptionValue("mp"), cmd.getOptionValue("src"), cmd.getOptionValue("lk"), cmd.getOptionValue("lsk"));
      for (String file : cmd.getOptionValues("files")) {
        System.out.println("Processing file [" + file + "]");
        Error response = processor.process(file);
        if (response != Error.NO_ERROR) {
          if (response == Error.INVALID_FILE) {
            System.err.println("Invalid file [" + file + "]");
          } else {
	          System.exit(response.getCode());
          }
        }
      }
      
      int response = processor.upload().getCode();
      if (response != 0) {
        System.exit(response);
      }
      
    } catch (ParseException pe) {
      // oops, something went wrong
      System.err.println( "Parsing failed.  Reason: " + pe.getMessage() );
    }
  }
  
  public static void displayErrorCodes() {
    for (Processor.Error error : Processor.Error.values()) {
      System.out.println(error.formatForOutput());
    }
  }
}
