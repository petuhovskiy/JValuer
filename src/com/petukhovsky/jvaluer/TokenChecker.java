package com.petukhovsky.jvaluer;

import com.petukhovsky.util.AtomScanner;

import java.io.InputStream;
import java.util.Objects;

/**
 * Created by Arthur on 12/19/2015.
 */
public class TokenChecker extends Checker {
    @Override
    public CheckResult check(InputStream in, InputStream answer, InputStream out) {
        if (out == null) return new CheckResult(false, "presentation error");
        AtomScanner answerScanner = new AtomScanner(answer);
        AtomScanner outScanner = new AtomScanner(out);
        int token = 0;
        String comment;
        boolean correct = false;
        while (true) {
            String answerString = answerScanner.next();
            String outString = outScanner.next();
            if (!Objects.equals(answerString, outString)) {
                if (outString == null) comment = "Expected " + answerString + " but reached end of file";
                else if (answerString == null) comment = "Expected end of file, but read " + outString;
                else comment = "Expected " + answerString + " but read " + outString;
                break;
            }
            if (answerString == null) {
                correct = true;
                comment = "ok " + token + " tokens";
                break;
            }
            token++;
        }
        answerScanner.close();
        outScanner.close();
        return new CheckResult(correct, comment);
    }
}
