package com.petukhovsky.jvaluer.invoker;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.invoker.Invoker;
import com.petukhovsky.jvaluer.commons.local.Local;
import com.petukhovsky.jvaluer.commons.local.OS;
import com.petukhovsky.jvaluer.commons.local.UserAccount;
import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.commons.run.RunOptions;
import com.petukhovsky.jvaluer.commons.run.RunVerdict;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
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

    private final Path runexe;

    public RunexeInvoker(Path runexe) {
        this.runexe = runexe;
    }

    @Override
    public RunInfo run(JValuer jValuer, RunOptions options) {
        String xmlOutput = null;
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

            xmlOutput = IOUtils.toString(process.getInputStream(), "UTF-8");
            logger.log(System.getenv("JVALUER_RUNEXE_DEBUG") != null ? Level.INFO : Level.FINE, xmlOutput);

            int xmlIndex = xmlOutput.indexOf("<?xml");
            if (xmlIndex != -1) xmlOutput = xmlOutput.substring(xmlIndex);

            Document doc = builder.parse(IOUtils.toInputStream(xmlOutput, "UTF-8"));
            String verdict = doc.getElementsByTagName("invocationVerdict").item(0).getTextContent();
            int exitCode = Integer.parseInt(doc.getElementsByTagName("exitCode").item(0).getTextContent());
            long userTime = Long.parseLong(doc.getElementsByTagName("processorUserModeTime").item(0).getTextContent());
            long kernelTime = Long.parseLong(doc.getElementsByTagName("processorKernelModeTime").item(0).getTextContent());
            long passedTime = Long.parseLong(doc.getElementsByTagName("passedTime").item(0).getTextContent());
            long consumedMemory = Long.parseLong(doc.getElementsByTagName("consumedMemory").item(0).getTextContent());
            String comment = doc.getElementsByTagName("comment").item(0).getTextContent();

            return RunInfo.completed(RunVerdict.valueOf(verdict), exitCode, userTime, kernelTime, passedTime, consumedMemory, comment);
        } catch (NumberFormatException | IOException | InterruptedException | SAXException e) {
            logger.log(Level.SEVERE, "run is crashed, xmlOutput = " + xmlOutput, e);
            return RunInfo.crashed("Crashed while invoking");
        }
    }
}
