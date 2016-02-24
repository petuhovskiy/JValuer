package com.petukhovsky.util;

import com.petukhovsky.jvaluer.test.TestData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Arthur on 12/19/2015.
 */
public class AtomScanner {

    final static int BUFFER_SIZE = 65536;

    BufferedReader br;
    char[] buf = new char[BUFFER_SIZE];
    int len = 0;
    int it = 0;
    boolean end = false;

    public AtomScanner(TestData testData) {
        this(testData.openInputStream());
    }

    public AtomScanner(InputStream is) {
        br = new BufferedReader(new InputStreamReader(is), BUFFER_SIZE);
    }

    boolean delim(char c) {
        return c == ' ' || c == '\n' || c == '\r';
    }

    void fillBuffer() {
        try {
            len = br.read(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void ensureBuffer() {
        if (it == len) {
            it = 0;
            fillBuffer();
            if (len == -1) end = true;
        }
    }

    void moveNext() {
        while (!end) {
            ensureBuffer();
            if (!delim(buf[it])) return;
            while (it < len && delim(buf[it])) it++;
        }
    }

    public char nextChar() {
        if (end) throw new NullPointerException("End was reached");
        ensureBuffer();
        return buf[it++];
    }

    public String next() {
        moveNext();
        if (end) return null;
        StringBuilder sb = new StringBuilder();
        while (!end) {
            int l = it;
            while (++it < len && !delim(buf[it])) ;
            sb.append(buf, l, it - l);
            ensureBuffer();
            if (delim(buf[it])) break;
        }
        return sb.toString();
    }

    public int nextInt() {
        moveNext();
        if (!end && buf[it] == '-') {
            it++;
            return -nextInt();
        }
        int result = 0;
        while (!end) {
            int l = it;
            while (it < len && !delim(buf[it])) {
                result = (result * 10) + buf[it] - '0';
                it++;
            }
            ensureBuffer();
            if (end || delim(buf[it])) break;
        }
        return result;
    }

    public long nextLong() {
        moveNext();
        if (!end && buf[it] == '-') {
            it++;
            return -nextLong();
        }
        long result = 0;
        while (!end) {
            int l = it;
            while (it < len && !delim(buf[it])) {
                result = (result * 10) + buf[it] - '0';
                it++;
            }
            ensureBuffer();
            if (delim(buf[it])) break;
        }
        return result;
    }

    public void close() {
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}