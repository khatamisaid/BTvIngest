package com.b1.testing.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FTPClientConnection {

    public void uploadFile(MultipartFile file, String pathWithFileName) {
        String server = "192.168.10.243";
        int port = 21;
        String user = "amberfin";
        String pass = "123amberfin";
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            showServerReply(ftpClient);
            ftpClient.login(user, pass);
            showServerReply(ftpClient);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            // APPROACH #1: uploads first file using an InputStream
            InputStream inputStream = new BufferedInputStream(file.getInputStream());
            System.out.println("Start uploading first file");
            boolean done = ftpClient.storeFile(pathWithFileName, inputStream);
            inputStream.close();
            if (done) {
                System.out.println("The first file is uploaded successfully.");
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
}
