package com.petukhovsky.jvaluer;

import com.petukhovsky.jvaluer.checker.CheckResult;
import com.petukhovsky.jvaluer.checker.Checker;
import com.petukhovsky.jvaluer.test.Test;
import com.petukhovsky.jvaluer.test.TestVerdict;

/**
 * Created by Arthur on 12/20/2015.
 */
public class Estimator {

    private Checker checker;

    public Estimator(Checker checker) {
        this.checker = checker;
    }

    public TestVerdict estimate(Test in, Test answer, Test out, RunInfo info) {
        if (info.getRunVerdict() != RunVerdict.SUCCESS)
            return new TestVerdict(null, info, info.getRunVerdict().getText());
        if (!out.exists())
            return new TestVerdict(null, info, "Presentation Error");
        CheckResult result = checker.check(in, answer, out);
        return new TestVerdict(result, info, result.isCorrect() ? "Accepted" : "Wrong answer");
    }
}
