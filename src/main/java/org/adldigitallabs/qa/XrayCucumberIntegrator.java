package org.adldigitallabs.qa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.adldigitallabs.qa.auth.JiraJWTAuthenticator;
import org.adldigitallabs.qa.task.XrayImporter;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.testing.Test;

import java.io.File;
import java.io.IOException;

public class XrayCucumberIntegrator implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        // Register a task that runs after the tests finish.
        project.getTasks().register("sendXrayExecution", task -> {
            task.doLast(action -> {
                try {
                    // Read ROUTES.json from the project root directory.
                    String routesFilePath = project.getProjectDir() + File.separator + "ROUTES.json";
                    Routes routes = readRoutes(routesFilePath);

                    // From the configuration, the value of "XRAY" is used as issueFieldsPath,
                    // and the value of "CUCUMBER" as resultsPath.
                    String issueFieldsPath = routes.getXray();
                    String resultsPath = routes.getCucumber();

                    // Authenticate to get the JWT token.
                    String token = JiraJWTAuthenticator.getJwt();

                    // Import the Xray execution.
                    String response = XrayImporter.importXrayExecution(issueFieldsPath, resultsPath, token);
                    project.getLogger().lifecycle("Xray execution imported successfully. API response: " + response);
                } catch (IOException e) {
                    project.getLogger().error("Error importing Xray execution", e);
                }
            });
        });

        // Ensure the sendXrayExecution task runs after any test task.
        project.getTasks().withType(Test.class).configureEach(testTask -> testTask.finalizedBy("sendXrayExecution"));
    }

    /**
     * Reads the ROUTES.json file and maps it to a Routes object.
     *
     * @param routesFilePath The path to ROUTES.json.
     * @return A Routes object containing the configuration.
     * @throws IOException If an error occurs while reading the file.
     */
    private Routes readRoutes(String routesFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(routesFilePath), Routes.class);
    }

    /**
     * Inner class representing the structure of ROUTES.json.
     */
    public static class Routes {
        private String CUCUMBER;
        private String XRAY;

        // Jackson requires a default constructor
        public Routes() {
        }

        public String getCucumber() {
            return CUCUMBER;
        }

        public void setCUCUMBER(String CUCUMBER) {
            this.CUCUMBER = CUCUMBER;
        }

        public String getXray() {
            return XRAY;
        }

        public void setXRAY(String XRAY) {
            this.XRAY = XRAY;
        }
    }
}