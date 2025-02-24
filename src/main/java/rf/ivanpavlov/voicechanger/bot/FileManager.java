package rf.ivanpavlov.voicechanger.bot;

import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    private static final String DOWNLOAD_DIR = "downloads";
    private static final String FILE_TO_SEND_DIR = "file_to_send";

    public Path getDownloadPath(User user, String fileName) {
        return Paths.get(DOWNLOAD_DIR, sanitizeFileName(user.getUserName()) + "_" + user.getId(),
                fileName);
    }

    public Path getSendFilePath(User user, java.io.File file) {
        System.out.println(file.getName());
        String filePath = FILE_TO_SEND_DIR + java.io.File.separator +
                (user.getUserName()) + "_" + user.getId() +
                java.io.File.separator + file.getName();

        if (isPath(filePath)) {
            return Paths.get(filePath);
        } else {
            return null;
        }

    }

    private String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "_");
    }

    private boolean isPath(String filePath) {
        java.io.File file = new java.io.File(filePath);
        return file.exists();
    }

    private String getFileName(File file) {
        return file.getFilePath().substring(file.getFilePath().lastIndexOf("/") + 1);
    }
}
