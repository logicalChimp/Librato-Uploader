package uk.co.mentalspace.libratouploader;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.OptionBuilder;

public class Main {
  
  public static void main (String[] args) {
    Options options = new Options();
	 options.addOption( "?", "help", false, "display this message" );
    options.addOption( "??", "errors", false, "Display the list of error codes");
    options.addOption( "mp", "metric-prefix", true, "the prefix to add to every metric name (e.g. environment name)");
    options.addOption( "lk", "librato-key", true, "the librato key to use to authenticate with Librato");
    options.addOption( "lsk", "librato-secret-key", true, "the librato secret key to validate your use of the librato key");
    options.addOption(OptionBuilder.withLongOpt("files").withArgName("JUnitReport> <CoberturaReport> <FindBugsReport> <CheckstyleReport").withValueSeparator(' ').hasArgs(2).withDescription("The report files to parse (in any order) - just omit files as required.").create("t"));    

    CommandLineParser parser = new BasicParser();
    try {
      CommandLine cmd = parser.parse( options, args);
      if (cmd.hasOption("help") || !cmd.hasOption("lk") || !cmd.hasOption("lsk") || !cmd.hasOption("files") || cmd.getOptionValues("files").length < 1) {
        System.out.println(options.toString());
        return;
      }
      if (cmd.hasOption("errors")) {
        displayErrorCodes();
        return;
      }
      
      Processor processor = new Processor(cmd.getOptionValue("mp"), cmd.getOptionValue("lk"), cmd.getOptionValue("lsk"));
      for (String file : cmd.getOptionValues("files")) {
        int response = processor.process(file).getCode();
        if (response != 0) {
          System.exit(response);
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
