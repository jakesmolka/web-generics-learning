package com.example.demo.controller;

import com.example.demo.model.InternalResponse;
import com.example.demo.model.TypeAResponse;
import com.example.demo.model.TypeBResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;

@RestController
@RequestMapping(path = "/")
public class GenericsController {

    @PostMapping
    // PREFER is the API's fixed way of changing representation (not format) of response
    public ResponseEntity create(@RequestHeader(value = "REPRESENTATION", required = false) String representation) {

        // generic way to decide on using TypeAResponse or TypeBResponse depending on `representation`
        Optional<InternalResponse<TypeAResponse>> respData;
        if (representation.equals("specific")) {
            // set class for InternalResponse to TypeBResponse
            respData = buildResponseData( () -> new TypeBResponse());
        } else {    // "basic" is default fallback
            // set class for InternalResponse to TypeAResponse
            respData = buildResponseData( () -> new TypeAResponse());
        }

        return respData.map(i -> ResponseEntity.created(URI.create("xyz")).headers(i.getHeaders()).body(i.getResponse()))
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    // generic solution based on factories by https://stackoverflow.com/a/46444056
    private <T extends TypeAResponse> Optional<InternalResponse<T>> buildResponseData(Supplier<T> factory) {
        T typeAorB = factory.get();
        typeAorB.setArgumentA("A");

        // and if more specific version set argumentB too
        if (typeAorB.getClass().equals(TypeBResponse.class)) {
            TypeBResponse test = (TypeBResponse)typeAorB;
                    test.setArgumentB("B");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // just set anything for proof of concept

        return Optional.of(new InternalResponse<>(typeAorB, headers));
    }
}
