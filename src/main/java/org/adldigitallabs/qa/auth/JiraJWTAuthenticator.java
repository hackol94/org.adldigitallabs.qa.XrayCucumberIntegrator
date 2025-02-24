package org.adldigitallabs.qa.auth;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class JiraJWTAuthenticator {

    /**
     * Obtiene el token JWT realizando la autenticación contra Xray.
     *
     * @return El token JWT en formato String.
     * @throws IOException Si ocurre algún error durante la petición HTTP.
     */
    public static String getJwt() throws IOException {
        String apiUrl = "https://xray.cloud.getxray.app/api/v2/authenticate";
        // Crear el payload JSON con las credenciales
        String payload = "{"
                + "\"client_id\":\"346C2810DD4B47EEADEB4065D6C59730\","
                + "\"client_secret\":\"68b383d80bdae23a81767313e9bd1a03b66580004197ebde4c30039003961f2d\""
                + "}";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(apiUrl);
            // Establecer el Content-Type a application/json
            post.setHeader("Content-Type", "application/json");
            // Si se requieren otros encabezados se pueden agregar aquí (por ejemplo, provistos por algún fixture)
            post.setEntity(new StringEntity(payload, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                // Se elimina las comillas del token, en caso de que se devuelvan
                return responseBody.replace("\"", "");
            }
        }
    }
}