package com.petukhovsky.jvaluer.invoker;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.run.RunInfo;
import com.petukhovsky.jvaluer.run.RunOptions;
import com.petukhovsky.jvaluer.run.RunVerdict;
import com.petukhovsky.jvaluer.util.Local;
import com.petukhovsky.jvaluer.util.OS;
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
        Path runexe = jValuer.getRunexe1();
        try {
            String cmd = String.format("%s -xml", runexe);

            if (options.hasParameter("folder")) cmd += " -d \"" + options.getParameter("folder") + "\"";
            if (options.hasParameter("stdin")) cmd += " -i \"" + options.getParameter("stdin") + "\"";
            if (options.hasParameter("stdout")) cmd += " -o \"" + options.getParameter("stdout") + "\"";
            if (options.hasParameter("stderr")) cmd += " -e \"" + options.getParameter("stderr") + "\"";
            if (OS.isWindows() && options.hasParameter("trusted")) cmd += " -z";
            if (options.hasParameter("login")) cmd += " -l " + options.getParameter("login");
            if (options.hasParameter("password")) cmd += " -p " + options.getParameter("password");
            if (options.hasParameter("memory_limit")) cmd += " -m " + options.getParameter("memory_limit");
            if (options.hasParameter("time_limit")) cmd += " -t " + options.getParameter("time_limit");
            cmd += " \"" + options.getParameter("executable") + "\"";
            if (options.hasParameter("args")) cmd += " " + options.getParameter("args");

            logger.info("Invoker runs runexe with cmd: " + cmd);

            Process process = Local.execute(cmd);
            process.waitFor();

            Document doc = builder.parse(process.getInputStream());
            String verdict = doc.getElementsByTagName("invocationVerdict").item(0).getTextContent();
            int exitCode = Integer.parseInt(doc.getElementsByTagName("exitCode").item(0).getTextContent());
            int userTime = Integer.parseInt(doc.getElementsByTagName("processorUserModeTime").item(0).getTextContent());
            int kernelTime = Integer.parseInt(doc.getElementsByTagName("processorKernelModeTime").item(0).getTextContent());
            int passedTime = Integer.parseInt(doc.getElementsByTagName("passedTime").item(0).getTextContent());
            int consumedMemory = Integer.parseInt(doc.getElementsByTagName("consumedMemory").item(0).getTextContent());
            String comment = doc.getElementsByTagName("comment").item(0).getTextContent();

            return RunInfo.completed(RunVerdict.valueOf(verdict), exitCode, userTime, kernelTime, passedTime, consumedMemory, comment);
        } catch (NumberFormatException | IOException | InterruptedException | SAXException e) {
            logger.log(Level.SEVERE, "run is crashed", e);
            return RunInfo.crashed("Crashed while invoking");
        }
    }

    public boolean isSupported(JValuer jValuer) {
        return jValuer.getRunexe1() != null;
    }
}
