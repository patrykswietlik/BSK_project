package pl.bsk.project.bsk_project.utils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class Util {
    public static String getUSBPath() {
        File[] roots = File.listRoots();
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();

        for (File root : roots) {
            String systemName = fileSystemView.getSystemDisplayName(root);
            if (systemName.contains("USB") || systemName.contains("Pendrive")) {
                return root.getPath();
            }
        }

        return null;
    }
}
