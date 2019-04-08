package com.example.demo.model;

import org.springframework.http.HttpHeaders;

// generic version with restriction to either use TypeAResponse or its sub-class TypeBResponse
public class InternalResponse<T extends TypeAResponse> {
    T response;
    HttpHeaders headers;

    public InternalResponse(T response, HttpHeaders headers) {
        this.response = response;
        this.headers = headers;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }
}
