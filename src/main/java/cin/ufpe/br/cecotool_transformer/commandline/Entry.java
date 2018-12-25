package cin.ufpe.br.cecotool_transformer.commandline;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Entry {
	public static final Logger logger = LogManager.getLogger(Entry.class);
	
	public static void main(String[] args) {
		try {
			CommandLine cmd = new CommandLine();
			new picocli.CommandLine(cmd).parse(args);
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
