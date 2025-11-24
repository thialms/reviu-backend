package br.edu.fatecpg.reviu.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvertData implements IConvertData{

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T getData(String json, Class<T> classe) {
        try{
            return mapper.readValue(json, classe);
        } catch (Exception e){
            throw new RuntimeException("Erro ao converter JSON: " + e.getMessage());
        }
    }

}
