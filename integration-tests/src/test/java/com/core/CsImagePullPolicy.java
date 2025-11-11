package com.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.images.AbstractImagePullPolicy;
import org.testcontainers.images.ImageData;
import org.testcontainers.utility.DockerImageName;

/**
 *
 * @author martin on 11/4/25
 */
public class CsImagePullPolicy extends AbstractImagePullPolicy {

    private static final Logger logger = LoggerFactory.getLogger(CsImagePullPolicy.class);
    private final String  networkAlias;
    private final boolean pullImage;

    public CsImagePullPolicy(String networkAlias, boolean pullImage) {
        this.networkAlias = networkAlias;
        this.pullImage = pullImage;
    }

    @Override
    protected boolean shouldPullCached(DockerImageName dockerImageName, ImageData imageData) {
        logger.info("Pulling image with network alias {}: {}", networkAlias, pullImage ? "Yes" : "No");
        return pullImage;
    }

}
