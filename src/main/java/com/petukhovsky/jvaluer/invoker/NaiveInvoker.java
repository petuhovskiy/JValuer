package com.petukhovsky.jvaluer.invoker;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.local.OS;
import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.commons.run.RunOptions;
import com.petukhovsky.jvaluer.commons.run.RunVerdict;
import com.petukhovsky.jvaluer.commons.util.PidUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by petuh on 3/5/2016.
 */
public class NaiveInvoker implements Invoker {

    private static final Logger logger = Logger.getLogger(NaiveInvoker.class.getName());

    @Override
    public RunInfo run(JValuer jValuer, RunOptions options) {
        try {
            String args = options.getArgs();
            RunLimits limits = options.getLimits();

            logger.info("Naive invoker: " + "no memory limit" +
                    ", " + (limits.getTime() == null ? "no time limit" : limits.getTime() + " ms") + ", no process limit. " + options.getExe().toAbsolutePath());


            ProcessBuilder builder = new ProcessBuilder(OS.isWindows() ? new String[]{options.getExe().toAbsolutePath().toString(), args}
                    : new String[]{"bash", "-c", options.getExe().toAbsolutePath().toString(), args});

            logger.info("creating process = " + options.getExe().toAbsolutePath().toString() + " " + args);

            File dir = options.getFolder() != null ? options.getFolder().toFile() : Paths.get("").toFile();
            if (options.getFolder() != null) builder.directory(dir);

            if (options.getStdinForward() != null) builder.redirectInput(options.getStdinForward().toFile());
            if (options.getStdoutForward() != null) builder.redirectOutput(options.getStdoutForward().toFile());
            if (options.getStderrForward() != null) builder.redirectError(options.getStderrForward().toFile());

            long invokeTime = System.currentTimeMillis();
            Process process = builder.start();

            String pid = PidUtils.getPid(process);

            if (limits.getTime() != null) process.waitFor(limits.getTime(), TimeUnit.MILLISECONDS);
            else process.waitFor();

            while (process.isAlive()) {
                process.destroy();
                process.destroyForcibly();
                process.waitFor();
            }

            long endTime = System.currentTimeMillis();

            long time = endTime - invokeTime;

            process.destroy();
            process.destroyForcibly();
            process.waitFor();

            RunVerdict verdict = RunVerdict.SUCCESS;
            if (limits.getTime() != null && time > limits.getTime()) verdict = RunVerdict.TIME_LIMIT_EXCEEDED;

            return RunInfo.completed(verdict, process.exitValue(), time, time, time, 0, "");
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "", e);
            return RunInfo.crashed("wrong format");
        } catch (IOException e) {
            logger.log(Level.WARNING, "", e);
            return RunInfo.crashed("executing exception");
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "", e);
            return RunInfo.crashed("waiting interrupted");
        }
    }
}
