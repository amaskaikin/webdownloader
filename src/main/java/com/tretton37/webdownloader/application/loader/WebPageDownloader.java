package com.tretton37.webdownloader.application.loader;

import me.tongfei.progressbar.ProgressBar;

import java.net.URL;
import java.util.Set;

public interface WebPageDownloader {

    void downloadAsync(final Set<URL> urls);

    void downloadAsync(final Set<URL> urls, final ProgressBar progressBar);
}
