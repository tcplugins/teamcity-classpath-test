package teamcity.plugin.classpath;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import jetbrains.buildServer.util.FuncThrow;
import jetbrains.buildServer.util.Util;

import jetbrains.buildServer.log.Loggers;

public class ClassPathTestProvider implements FactoryBean<ClassPathTest> {

	ClassPathTest myClassPathTest = null;
	private ClassLoader myTeamCityClassLoader;
	
	/** 
	 * ClassPathTestProvider
	 * @param teamCityClassLoader The classloader to use to create the instance. TeamCity will inject use useful one.
	 */
	public ClassPathTestProvider(ClassLoader teamCityClassLoader) {
		Loggers.SERVER.info("ClassPathTestProvider :: Getting ClassPathTest via TeamCity classpath method.");
		this.myTeamCityClassLoader = teamCityClassLoader;
	}
	
	public void init() {
		Loggers.SERVER.info("ClassPathTestProvider :: Initialising ClassPathTest via TeamCity classpath method.");
		try {
			for (Resource r : Util.doUnderContextClassLoader(myTeamCityClassLoader,
					new ClassLoaderPrinter(myTeamCityClassLoader)
					)) {
				Loggers.SERVER.info("Found resource " + r.getClass().getCanonicalName() );
			}
			this.myClassPathTest = Util.doUnderContextClassLoader(myTeamCityClassLoader,
					new ClassPathTestProviderInstantiationFunction()
					);
		} catch (Exception e) {
			Loggers.SERVER.error("ClassPathTestProvider :: Could not create ClassPathTest instance!");
			Loggers.SERVER.info(e);
		}	
	}
	
	public class ClassPathTestProviderInstantiationFunction implements FuncThrow<ClassPathTest, Exception> {
		
		@Override
		public ClassPathTest apply() throws Exception {
			return createClassPathTestInstance();
		}
		
	}
	
	public class ClassLoaderPrinter implements FuncThrow<Resource[], Exception> {
		final ClassLoader classLoader;
		
		public ClassLoaderPrinter(ClassLoader classLoader) {
			this.classLoader = classLoader;
		}

		@Override
		public Resource[] apply() throws Exception {
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.classLoader);
			return resolver.getResources("classpath:**");
		}
		
	}
	
	/**
	 * ClassPathTest builder.
	 * Called by ClassPathTestProviderInstantiationFunction.apply() and also
	 * is public so that it can be used by unit tests which don't have TeamCity running.
	 */
	public static ClassPathTest createClassPathTestInstance() {
		return new ClassPathTest();
	}

	public ClassPathTest getClassPathTest() {
		return this.myClassPathTest;
	}

	@Override
	public ClassPathTest getObject() throws Exception {
		return this.myClassPathTest;
	}

	@Override
	public Class<?> getObjectType() {
		return ClassPathTest.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}