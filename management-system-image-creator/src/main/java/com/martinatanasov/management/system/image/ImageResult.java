package com.martinatanasov.management.system.image;

public sealed interface ImageResult {

    record Success(ImageDto imageDto) implements ImageResult {}
    record Failure(String message) implements ImageResult {}

}
