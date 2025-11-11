package com.core;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

/**
 *
 * @author martin on 11/4/25
 */
public interface CsStoreAccessor {
    InputStream openSmbInputStream(URI smbUrl);

    File openFile(URI smbUrl);

    URI resolveSambaStoreUri(URI uri);

    URI resolveSambaStoreUri(String path);

}
