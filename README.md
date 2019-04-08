# Spring MVC Generic ResponseEntity

This is a small learning-by-doing project I did to figure out how to create a Spring Boot web project that:

- uses Spring Boot native utils and tools like `ResponseEntity` to
- build a simple REST backend server which
- allows the API to return data in different manifestations, e.g. minimal and maximal representation of an object (here `TypeAResponse` and `TypeBResponse`)
- while being based on the same data model, so class TypeB extends the basic TypeA (here the basic version has attribute `A` while the specific version has `A` and `B`)

The solution utilizes Java generics in a way that allow runtime generation of the necessary object, despite Java generics are based of Type Erasure. In this case that would result in the lack of needed information on the requested type at runtime. Using Java's `Supplier` interface allows to inject the call of a constructor as lambda function which is implemented as factory. Doing so helps to create the requested object at runtime.