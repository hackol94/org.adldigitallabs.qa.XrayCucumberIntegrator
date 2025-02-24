package org.adldigitallabs.qa.task;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

public class XrayImporter {

    /**
     * Importa la ejecución de Xray utilizando dos archivos JSON.
     *
     * @param issueFieldsPath Ruta del archivo JSON que contiene la información del issue.
     * @param resultsPath     Ruta del archivo JSON que contiene los resultados.
     * @param token           Token JWT obtenido en la autenticación.
     * @return La respuesta de la API Xray.
     * @throws IOException Si ocurre algún error durante la petición HTTP.
     */
    public static String importXrayExecution(String issueFieldsPath, String resultsPath, String token) throws IOException {
        // API endpoint para importar la ejecución en Xray.
        String apiUrl = "https://xray.cloud.getxray.app/api/v2/import/execution/cucumber/multipart";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);

            // Construir la entidad multipart con los dos archivos
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            // Parte "info"
            File issueFile = new File(issueFieldsPath);
            builder.addBinaryBody("info", issueFile, ContentType.APPLICATION_JSON, issueFile.getName());

            // Parte "results"
            File resultsFile = new File(resultsPath);
            builder.addBinaryBody("results", resultsFile, ContentType.APPLICATION_JSON, resultsFile.getName());

            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);

            // Establecer el header Authorization usando el token JWT obtenido
            httpPost.setHeader("Authorization", "Bearer " + token);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : "";
                if (statusCode >= 200 && statusCode < 300) {
                    return responseBody;
                } else {
                    throw new IOException("Error importando la ejecución Xray. Código: " + statusCode + " Respuesta: " + responseBody);
                }
            }
        }
    }
}