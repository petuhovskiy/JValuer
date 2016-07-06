package com.petukhovsky.jvaluer.invoker;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.local.Local;
import com.petukhovsky.jvaluer.commons.local.OS;
import com.petukhovsky.jvaluer.commons.local.UserAccount;
import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.commons.run.RunOptions;
import com.petukhovsky.jvaluer.commons.run.RunVerdict;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by petuh on 3/5/2016.
 */
public class RunexeInvoker implements Invoker {

    private static Logger logger = Logger.getLogger(RunexeInvoker.class.getName());

    private static DocumentBuilderFactory factory;
    private static DocumentBuilder builder;

    static {
        factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        logger.fine("Runexe invoker dom builder static init complete");
    }


    public RunexeInvoker() {
    }

    @Override
    public RunInfo run(JValuer jValuer, RunOptions options) {
        Path runexe = jValuer.getRunexe();
        try {
            String cmd = String.format("%s -xml", runexe);

            if (options.getFolder() != null) cmd += " -d \"" + options.getFolder().toAbsolutePath() + "\"";
            if (options.getStdinForward() != null) cmd += " -i \"" + options.getStdinForward().toAbsolutePath() + "\"";
            if (options.getStdoutForward() != null)
                cmd += " -o \"" + options.getStdoutForward().toAbsolutePath() + "\"";
            if (options.getStderrForward() != null)
                cmd += " -e \"" + options.getStderrForward().toAbsolutePath() + "\"";
            if (OS.isWindows() && options.isTrusted()) cmd += " -z";

            if (options.getUserAccount() != null) {
                UserAccount ua = options.getUserAccount();
                cmd += " -l " + ua.getLogin();
                if (ua.getPassword() != null) cmd += " -p " + ua.getPassword();
            }

            RunLimits limits = options.getLimits();
            if (limits.getMemory() != null) cmd += " -m " + limits.getMemory();
            if (limits.getTime() != null) cmd += " -t " + limits.getTime() + "ms";

            cmd += " \"" + options.getExe().toAbsolutePath() + "\" " + options.getArgs();

            logger.info("Invoker runs runexe with cmd: " + cmd);

            Process process = Local.execute(cmd);
            process.waitFor();

            Document doc = builder.parse(process.getInputStream());
            String verdict = doc.getElementsByTagName("invocationVerdict").item(0).getTextContent();
            int exitCode = Integer.parseInt(doc.getElementsByTagName("exitCode").item(0).getTextContent());
            long userTime = Integer.parseInt(doc.getElementsByTagName("processorUserModeTime").item(0).getTextContent());
            long kernelTime = Integer.parseInt(doc.getElementsByTagName("processorKernelModeTime").item(0).getTextContent());
            long passedTime = Integer.parseInt(doc.getElementsByTagName("passedTime").item(0).getTextContent());
            long consumedMemory = Integer.parseInt(doc.getElementsByTagName("consumedMemory").item(0).getTextContent());
            String comment = doc.getElementsByTagName("comment").item(0).getTextContent();

            return RunInfo.completed(RunVerdict.valueOf(verdict), exitCode, userTime, kernelTime, passedTime, consumedMemory, comment);
        } catch (NumberFormatException | IOException | InterruptedException | SAXException e) {
            logger.log(Level.SEVERE, "run is crashed", e);
            return RunInfo.crashed("Crashed while invoking");
        }
    }

    public boolean isAvailiable(JValuer jValuer) {
        return jValuer.getRunexe() != null;
    }
}
