package com.martinatanasov.management.system.image;

import com.martinatanasov.management.system.limiter.UserLimiterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ImageServiceImpl implements ImageService {

    private final UserLimiterService userLimiterService;

}
