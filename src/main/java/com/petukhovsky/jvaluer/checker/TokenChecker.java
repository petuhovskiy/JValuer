package com.petukhovsky.jvaluer.checker;

import com.petukhovsky.jvaluer.test.TestData;
import com.petukhovsky.jvaluer.util.AtomScanner;

import java.util.Objects;

/**
 * Created by Arthur on 12/19/2015.
 */
public class TokenChecker extends Checker {
    @Override
    public CheckResult check(TestData in, TestData answer, TestData out) {
        if (!out.exists()) return new CheckResult(false, "Presentation error");
        if (!in.exists() || !answer.exists()) return new CheckResult(false, "Internal error - jury has missing files");
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
