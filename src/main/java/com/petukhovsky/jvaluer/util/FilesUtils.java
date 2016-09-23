package com.petukhovsky.jvaluer.util;

import com.petukhovsky.jvaluer.commons.local.OS;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;

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

    private static void delete(Path path) {
        if (Files.isDirectory(path)) {
            cleanDirectoryOld(path);
            try {
                Files.list(path).forEach(FilesUtils::delete);
            } catch (IOException e) {
                //log.log(Level.WARNING, "", e);
            }
        }
        if (Files.exists(path)) {
            try {
                FileDeleteStrategy.FORCE.delete(path.toFile());
            } catch (IOException e) {
                //log.log(Level.WARNING, "", e);
            }
        }
    }

    private static void cleanDirectoryOld(Path path) {
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
            //log.log(Level.WARNING, "can't clean directory", e);
        }
    }

    private static void cleanDirectory(Path dir) {
        FilesUtils.cleanDirectoryOld(dir);
        try {
            Files.list(dir).forEach(FilesUtils::delete);
        } catch (IOException e) {
            log.log(Level.WARNING, "", e);
        }
    }

    private static void myTryDelete(Path path) {
        try {
            FileUtils.forceDelete(path.toFile());
        } catch (IOException e) {
        }
        FileUtils.deleteQuietly(path.toFile());
        delete(path);
    }

    public static boolean removeRecursiveForce(Path path) {
        for (int i = 0; i < 20; i++) {
            if (Files.notExists(path)) return true;
            myTryDelete(path);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        log.log(Level.SEVERE, "can't delete THAT -> (" + path.toAbsolutePath().toString() + ")");
        return Files.notExists(path);
    }

    /**
     * Works too well, but also kill current java program as side effect
     * @param path
     */
    private static void killProcessesByPath(Path path) {
        if (!OS.isUnix()) return;
        String cmd = String.format("lsof | grep %s | awk '{print $2}' | xargs kill -9", path.toAbsolutePath().toString());
        try {
            Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", cmd}).waitFor();
        } catch (InterruptedException | IOException e) {
            //e.printStackTrace();
        }
    }

    public static boolean assureEmptyDir(Path path) {
        removeRecursiveForce(path);
        if (!Files.exists(path) && forceCreateDirs(path)) {
            return true;
        }
        log.log(Level.SEVERE, "can't make THAT -> (" + path.toAbsolutePath().toString() + ") directory empty");
        return false;
    }

    private static boolean forceCreateDirs(Path path) {
        try {
            Files.createDirectories(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
