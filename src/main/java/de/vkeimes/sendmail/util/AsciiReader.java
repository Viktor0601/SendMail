package de.vkeimes.sendmail.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class AsciiReader {
    private Vector<String> vectorData = new Vector<String>();
    private StringBuffer sbData;

    public AsciiReader(String pathToFile) {
        this.sbData = new StringBuffer();
        ReadData(pathToFile);
    }

    public Vector<String> getDataAsVector() {
        return this.vectorData;
    }

    public StringBuffer getDataAsStringBuffer() {
        return this.sbData;
    }

    private void ReadData(String strPathToFile) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(strPathToFile));
            int iIndex = 0;
            String strLine;
            while ((strLine = bufferedReader.readLine()) != null) {

                this.vectorData.add(iIndex++, strLine.trim());
                this.sbData.append(strLine.trim());
            }
            bufferedReader.close();
        } catch (IOException e) {
            handleException(e);
        }
    }

    private void handleException(Throwable exception) {
        System.out.println("--- BEGIN OF EXCEPTION ---------");
        exception.printStackTrace(System.out);
        System.out.println("--- END OF EXCEPTION -----------");
    }
}
