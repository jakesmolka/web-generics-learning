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

    /**
     * Basic version that handles the "PREFER" header to allow minimal or maximal representation of resources in the response
     * @param representation
     * @return
     */
    @PostMapping(path = "test1")
    // PREFER is the API's fixed way of changing representation (not format) of response
    public ResponseEntity create1(@RequestHeader(value = "REPRESENTATION", required = false) String representation) {

        // generic way to decide on using TypeAResponse or TypeBResponse depending on `representation`
        Optional<InternalResponse<TypeAResponse>> respData;
        if (representation.equals("specific")) {
            // set class for InternalResponse to TypeBResponse
            respData = buildResponseData1( () -> new TypeBResponse());
        } else {    // "basic" is default fallback
            // set class for InternalResponse to TypeAResponse
            respData = buildResponseData1( () -> new TypeAResponse());
        }

        return respData.map(i -> ResponseEntity.created(URI.create("xyz")).headers(i.getHeaders()).body(i.getResponse()))
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    // generic solution based on factories by https://stackoverflow.com/a/46444056
    private <T extends TypeAResponse> Optional<InternalResponse<T>> buildResponseData1(Supplier<T> factory) {
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

    /**
     * Modified version of above, that uses a response without any body at all as `minimal` representation. Therefore also omits one response data class.
     * @param representation
     * @return
     */
    @PostMapping(path = "test2")
    // PREFER is the API's fixed way of changing representation (not format) of response
    public ResponseEntity create2(@RequestHeader(value = "REPRESENTATION", required = false) String representation) {

        // generic way to decide on using TypeAResponse or no body depending on `representation`
        Optional<InternalResponse<TypeAResponse>> respData;
        if (representation.equals("specific")) {
            // set class for InternalResponse to TypeAResponse
            respData = buildResponseData2( () -> new TypeAResponse());
        } else {    // "basic" is default fallback
            // set class for InternalResponse to null to signal `no body`
            respData = buildResponseData2(() -> null);
        }

        /*return respData.map(i -> ResponseEntity.created(URI.create("xyz")).headers(i.getHeaders()).body(i.getResponse()))
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());*/

        URI uri = URI.create("xyz");

        // returns 201 with body + headers, 204 only with headers or 500 error depending on what processing above yields
        return respData.map(i -> Optional.ofNullable(i.getResponse()).map(j -> ResponseEntity.created(uri).headers(i.getHeaders()).body(j))
                            // when the body is empty
                            .orElse(ResponseEntity.noContent().headers(i.getHeaders()).build()))
                            // when no response could be created at all
                            .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    private <T extends TypeAResponse> Optional<InternalResponse<T>> buildResponseData2(Supplier<T> factory) {
        T typeAorB = factory.get();

        HttpHeaders headers = new HttpHeaders();
        headers.setETag("\"ItWorked\""); // just set anything for proof of concept

        // if more specific version set argumentA
        if (typeAorB != null) {
            if (typeAorB.getClass().equals(TypeBResponse.class)) {
                TypeBResponse test = (TypeBResponse) typeAorB;
                test.setArgumentA("A");
            }

            return Optional.of(new InternalResponse<>(typeAorB, headers));
        } else {
            // in case of `null` no body should be set
            return Optional.of(new InternalResponse<>(null, headers));
        }
    }
}
