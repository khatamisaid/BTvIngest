package com.b1.testing.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
// import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Service
public class FileManager {
    @Autowired
    private Environment env;

    @Autowired
    private HttpSession httpSession;

    private ChannelSftp setupJsch() throws JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts("C:\\Users\\MTJ 03\\.ssh\\known_hosts");
        // jsch.setKnownHosts("C:\\Users\\Administrator\\.ssh\\known_hosts");
        // jsch.setKnownHosts("/.ssh/known_hosts");
        Session jschSession;
        if (httpSession.getAttribute(env.getProperty("FTP.USERNAME")) != null) {
            jschSession = (Session) httpSession.getAttribute(env.getProperty("FTP.USERNAME"));
            return (ChannelSftp) jschSession.openChannel("sftp");
        }
        jschSession = jsch.getSession(env.getProperty("FTP.USERNAME"), env.getProperty("FTP.REMOTE_HOST"));
        jschSession.setPassword(env.getProperty("FTP.PASSWORD"));
        jschSession.connect();
        httpSession.setAttribute(env.getProperty("FTP.USERNAME"), jschSession);
        return (ChannelSftp) jschSession.openChannel("sftp");
    }

    public void uploadFile(MultipartFile file, String pathWithFileName) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = setupJsch();
            channelSftp.connect();
            System.out.println("sftp is connect : " + channelSftp.isConnected());
            InputStream inputStream = new BufferedInputStream(file.getInputStream());
            channelSftp.put(inputStream, pathWithFileName);
            // channelSftp.exit();
        } catch (JSchException | SftpException | IOException e) {
            System.out.println("catch : " + e.getMessage());
        }
    }

    public String downloadFile(String file) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = setupJsch();
            channelSftp.connect();
            InputStream is = channelSftp.get(file);
            byte[] bytes = IOUtils.toByteArray(is);
            String imageStr = Base64.encodeBase64String(bytes);
            // channelSftp.exit();
            return imageStr;
        } catch (JSchException | IOException | SftpException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }
}
