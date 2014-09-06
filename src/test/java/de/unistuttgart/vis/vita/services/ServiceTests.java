package de.unistuttgart.vis.vita.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * A suite containing the tests for the REST API
 */
@RunWith(Suite.class)
@SuiteClasses({VersionServiceTest.class, DocumentsServiceTest.class, DocumentServiceTest.class,
    ProgressServiceTest.class})
public class ServiceTests {

}
