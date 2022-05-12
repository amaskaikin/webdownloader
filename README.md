# Webdownloader

A Console application that provides an ability to recursively traverse and download
given website to disk keeping online file structure.  

## Features
- Designed in the way to easily switch from Console to Web application.
- Configurable via _application.yaml_ config file.
- Displaying downloading progress.
- Extracting media content.
- Code is covered by Unit tests.
- Logging to external file

## Technology stack
- Spring Boot
- Jsoup: Parsing html content
- Spring WebFlux: Asynchronous downloading web content in reactive way
- Java Stream API: Providing an ability to traverse and download web content using parallel streams
- [Progressbar](https://github.com/ctongfei/progressbar): Display downloading progress
- lombok
- log4j
- JUnit
- Mockito

## How to use
- Configure necessary parameters in _application.yaml_ file.
  - `webdownloader.base-url` - URL of site to be downloaded
  - `webdownloader.base-dir` - Target base directory
- Run the application: 
  - Via console executing: `mvn spring-boot:run`
  - Or directly from IDE by executing `main` method in _WebDownloaderApplication_ class
- Navigate to target directory and verify the results :)

