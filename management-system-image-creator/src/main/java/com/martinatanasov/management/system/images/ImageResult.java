package com.martinatanasov.management.system.images;

public sealed interface ImageResult {

    record Success(ImageDTO imageDto) implements ImageResult {}
    record Failure(String message) implements ImageResult {}

}
