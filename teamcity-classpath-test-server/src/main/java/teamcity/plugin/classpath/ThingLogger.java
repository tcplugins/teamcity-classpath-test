package teamcity.plugin.classpath;

import jetbrains.buildServer.log.Loggers;

public class ThingLogger {
	
	public ThingLogger(Thing thing) {
		Loggers.SERVER.info("Thing is of type: " + thing.getType());
	}

}
