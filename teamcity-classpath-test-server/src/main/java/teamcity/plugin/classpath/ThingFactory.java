package teamcity.plugin.classpath;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import jetbrains.buildServer.serverSide.SBuildServer;

public class ThingFactory extends AbstractFactoryBean<Thing> {
	
	private static final int MINIMUM_SUPPORTED_VERSION = 46533;
	
	private final SBuildServer sBuildServer;
	
	public ThingFactory(SBuildServer sBuildServer) {
		this.sBuildServer = sBuildServer;
	}

	@Override
	public Class<Thing> getObjectType() {
		return Thing.class;
	}

	@Override
	protected Thing createInstance() throws Exception {
		if (MINIMUM_SUPPORTED_VERSION <= Integer.parseInt(sBuildServer.getBuildNumber())) {
			return new OldThing();
		}
		return new NewThing();
	}

}
