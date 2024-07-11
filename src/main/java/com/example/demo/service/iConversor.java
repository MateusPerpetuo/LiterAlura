package com.example.demo.service;

public interface iConversor {
    <T> T obterDados(String json, Class<T> classe);
}
