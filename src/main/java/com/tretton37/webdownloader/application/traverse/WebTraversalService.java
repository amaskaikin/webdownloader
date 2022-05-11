package com.tretton37.webdownloader.application.traverse;

import java.net.URL;
import java.util.Set;

public interface WebTraversalService {

    Set<URL> traverse(String baseUrl);
}
