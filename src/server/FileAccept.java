package server;

import java.net.Socket;

public class FileAccept {
    private Socket downloader;
    private Socket uploader;
    private String fileName;

    String getFileName() {
        return fileName;
    }

    void setFileName(String fileName) {
        this.fileName = fileName;
    }

    Socket getDownloader() {
        return downloader;
    }

    void setDownloader(Socket downloader) {
        this.downloader = downloader;
    }

    Socket getUploader() {
        return uploader;
    }

    void setUploader(Socket uploader) {
        this.uploader = uploader;
    }
}
