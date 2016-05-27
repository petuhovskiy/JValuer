package com.petukhovsky.jvaluer.invoker;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.local.OS;
import com.petukhovsky.jvaluer.run.RunInfo;
import com.petukhovsky.jvaluer.run.RunOptions;
import com.petukhovsky.jvaluer.run.RunVerdict;
import com.petukhovsky.jvaluer.util.PidUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by petuh on 3/5/2016.
 */
public class NaiveInvoker implements Invoker {

    private static final Logger logger = Logger.getLogger(NaiveInvoker.class.getName());

    @Override
    public RunInfo run(JValuer jValuer, RunOptions options) {
        try {
            int memoryLimit = 0;
            int timeLimit = 0;

            if (options.hasParameter("memory_limit")) {
                String ml = options.getParameter("memory_limit").toLowerCase();
                if (ml.endsWith("m")) {
                    memoryLimit = (int) Double.parseDouble(ml.substring(0, ml.length() - 1)) * 1024 * 1024;
                } else if (ml.endsWith("kb")) {
                    memoryLimit = (int) Double.parseDouble(ml.substring(0, ml.length() - 2)) * 1024;
                } else if (ml.endsWith("b")) {
                    memoryLimit = Integer.parseInt(ml.substring(0, ml.length() - 1));
                } else {
                    memoryLimit = Integer.parseInt(ml);
                }
            }

            if (options.hasParameter("time_limit")) {
                String tl = options.getParameter("time_limit").toLowerCase();
                if (tl.endsWith("h")) {
                    timeLimit = (int) Double.parseDouble(tl.substring(0, tl.length() - 1)) * 1000 * 60 * 60;
                } else if (tl.endsWith("m")) {
                    timeLimit = (int) Double.parseDouble(tl.substring(0, tl.length() - 1)) * 1000 * 60;
                } else if (tl.endsWith("ms")) {
                    timeLimit = Integer.parseInt(tl.substring(0, tl.length() - 2));
                } else if (tl.endsWith("s")) {
                    timeLimit = (int) Double.parseDouble(tl.substring(0, tl.length() - 1)) * 1000;
                } else {
                    timeLimit = Integer.parseInt(tl);
                }
            }

            String args = "";
            if (options.hasParameter("args")) args = options.getParameter("args");

            logger.info("Naive invoker: " + ((memoryLimit == 0) && false ? "no memory limit" : memoryLimit + " bytes") +
                    ", " + (timeLimit == 0 ? "no time limit" : timeLimit + " ms") + ", no process limit. " + options.getParameter("executable"));


            ProcessBuilder builder = new ProcessBuilder(OS.isWindows() ? new String[]{options.getParameter("executable"), args}
                    : new String[]{"bash", "-c", options.getParameter("executable"), args});

            logger.info("creating process = " + options.getParameter("executable") + " " + args);

            File dir = options.hasParameter("folder") ? new File(options.getParameter("folder")) : Paths.get("").toFile();
            if (options.hasParameter("folder")) builder.directory(dir);

            if (options.hasParameter("stdin")) builder.redirectInput(new File(options.getParameter("stdin")));
            if (options.hasParameter("stdout")) builder.redirectOutput(new File(options.getParameter("stdout")));
            if (options.hasParameter("stderr")) builder.redirectError(new File(options.getParameter("stderr")));

            long invokeTime = System.currentTimeMillis();
            Process process = builder.start();

            String pid = PidUtils.getPid(process);

            if (timeLimit > 0) process.waitFor(timeLimit, TimeUnit.MILLISECONDS);
            else process.waitFor();

            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor();
            }

            long endTime = System.currentTimeMillis();

            int time = (int) (endTime - invokeTime);

            RunVerdict verdict = RunVerdict.SUCCESS;
            if (timeLimit > 0 && time > timeLimit) verdict = RunVerdict.TIME_LIMIT_EXCEEDED;

            return RunInfo.completed(verdict, process.exitValue(), time, time, time, 0, "");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return RunInfo.crashed("wrong format");
        } catch (IOException e) {
            e.printStackTrace();
            return RunInfo.crashed("executing exception");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return RunInfo.crashed("waiting interrupted");
        }
    }
}
