package com.martinatanasov.management.system.images;

import com.martinatanasov.management.system.limiter.UserLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final UserLimiterService userLimiterService;



}
