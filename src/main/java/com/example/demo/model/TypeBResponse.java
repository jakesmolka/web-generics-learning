package com.example.demo.model;

// additional set of response data necessary for some specific requests
public class TypeBResponse extends TypeAResponse{
    String argumentB;

    public String getArgumentB() {
        return argumentB;
    }

    public void setArgumentB(String argumentB) {
        this.argumentB = argumentB;
    }
}
