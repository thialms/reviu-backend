package br.edu.fatecpg.reviu.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class DictionaryAPIService {

    private final ObjectMapper mapper = new ObjectMapper();

    public String getFirstAudioUrl(String word){
        try{
            HttpClient client = HttpClient.newHttpClient();
            String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            return extractFirstAudio(response.body());

        } catch (Exception e){
            throw new RuntimeException("Erro ao consumir API: " + e.getMessage());
        }
    }

    private String extractFirstAudio(String json) {
        try {
            JsonNode root = mapper.readTree(json);

            // root[0].phonetics[i].audio
            for (JsonNode phonetic : root.get(0).get("phonetics")) {
                JsonNode audioNode = phonetic.get("audio");
                if (audioNode != null && !audioNode.asText().isBlank()) {
                    return audioNode.asText(); // retorna o primeiro áudio disponível
                }
            }

            return null; // caso não tenha áudio

        } catch (Exception e){
            throw new RuntimeException("Erro ao extrair áudio: " + e.getMessage());
        }
    }
}
