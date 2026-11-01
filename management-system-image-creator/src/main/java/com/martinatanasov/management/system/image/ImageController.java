package com.martinatanasov.management.system.image;

import com.martinatanasov.management.system.limiter.TokensResponseDto;
import com.martinatanasov.management.system.limiter.UserLimiterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ImageController {

    private final UserLimiterService userLimiterService;
    private final ImageService imageService;

    @GetMapping("/images/get-remaining-tokens/{userId}")
    public ResponseEntity<TokensResponseDto> getRemainingTokens(@PathVariable String userId) {
        if (userId != null && !userId.isEmpty()) {
            long remainingTokens = userLimiterService.getRemainingTokens(userId);
//            if (remainingTokens == null) {
//                return ResponseEntity.notFound().build();
//            } else {
            return ResponseEntity.ok().body(new TokensResponseDto(remainingTokens));
//            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/images/upload")
    public ResponseEntity<?> uploadUnmodifiedImage() {

        return ResponseEntity.ok().build();
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<?> getImage(@PathVariable String imageId) {

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable String imageId) {

        return ResponseEntity.ok().build();
    }

    @PutMapping("/images/{imageId}")
    public ResponseEntity<?> updateImage(@PathVariable String imageId) {

        return ResponseEntity.ok().build();
    }

    @PostMapping("/images/convert")
    public ResponseEntity<Void> convertImage() {

        return ResponseEntity.ok().build();
    }

    @GetMapping("/images")
    public ResponseEntity<Void> getAllImages() {

        return ResponseEntity.ok().build();
    }

}
