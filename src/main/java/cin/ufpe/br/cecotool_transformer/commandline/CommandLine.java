package cin.ufpe.br.cecotool_transformer.commandline;

import picocli.CommandLine.Option;

public final class CommandLine {
	@Option(names = {"-t", "--target"}, description = "The target project base classes directory, root of the Java classes", required = true)
	public String targetProjectDirectory;
	
	@Option(names = {"-f", "--file"}, description = "The recommendations file", required = true)
	public String recommendationFile;
	
	@Option(names = {"-h", "--help"}, usageHelp = true, description = "Displays this help")
	public boolean usageHelpRequested = false;
}
