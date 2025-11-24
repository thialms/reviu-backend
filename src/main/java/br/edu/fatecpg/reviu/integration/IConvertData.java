package br.edu.fatecpg.reviu.integration;

public interface IConvertData {
    <T> T getData(String json, Class<T> classe);
}
