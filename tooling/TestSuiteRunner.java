import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

public final class TestSuiteRunner {
    public static void main(String[] args) {
        String targetPackage = args.length > 0 ? args[0] : "hollowrealm.performance";
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage(targetPackage))
                .build();
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(summaryListener, new TestExecutionListener() {
            @Override
            public void executionFinished(TestIdentifier id, TestExecutionResult result) {
                if (id.isTest()) {
                    System.out.println("[JUNIT] " + result.getStatus() + " :: " + id.getDisplayName());
                    if (result.getThrowable().isPresent()) {
                        result.getThrowable().get().printStackTrace(System.out);
                    }
                }
            }
        });
        launcher.execute(request);
        TestExecutionSummary summary = summaryListener.getSummary();
        summary.printTo(new PrintWriter(System.out, true, StandardCharsets.UTF_8));
        if (summary.getTestsFoundCount() == 0 || summary.getTotalFailureCount() > 0) {
            System.exit(1);
        }
    }
}
