package com.petukhovsky.jvaluer.util;

import com.petukhovsky.jvaluer.commons.local.OS;
import org.apache.commons.io.FileDeleteStrategy;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Arthur Petukhovsky on 7/4/2016.
 */
public class FilesUtils {

    private final static Logger log = Logger.getLogger(FilesUtils.class.getName());

    public static void delete(Path path) {
        if (Files.isDirectory(path)) {
            if (OS.isWindows()) {
                try {
                    Runtime.getRuntime().exec("rmdir \"" + path.toAbsolutePath() + "\" /s /q").waitFor();
                } catch (IOException | InterruptedException e) {
                    log.log(Level.WARNING, "", e);
                }
            }
            cleanDirectoryOld(path);
            try {
                Files.list(path).forEach(FilesUtils::delete);
            } catch (IOException e) {
                log.log(Level.WARNING, "", e);
            }
        }
        if (Files.exists(path)) {
            try {
                FileDeleteStrategy.FORCE.delete(path.toFile());
            } catch (IOException e) {
                log.log(Level.WARNING, "", e);
            }
        }
    }

    public static void cleanDirectoryOld(Path path) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (!dir.equals(path)) Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.log(Level.WARNING, "can't clean directory", e);
        }
    }

    public static void cleanDirectory(Path dir) {
        FilesUtils.cleanDirectoryOld(dir);
        try {
            Files.list(dir).forEach(FilesUtils::delete);
        } catch (IOException e) {
            log.log(Level.WARNING, "", e);
        }
    }
}
