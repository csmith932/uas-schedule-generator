/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public final class DataExport {

    public static final Logger logger = LogManager.getLogger(DataExport.class);

    public static void exportData(File destination) throws UnsupportedEncodingException{
    	exportData(destination, false);
    }
    
    public static void exportData(File destination, boolean overwrite) throws UnsupportedEncodingException{

        URL jarUrl = DataExport.class.getProtectionDomain().getCodeSource().getLocation();
        File jarFileName = new File(URLDecoder.decode(jarUrl.getPath(), StandardCharsets.UTF_8.name()));
        
        try {

            JarFile jarFile = new JarFile(jarFileName);

            exportData(jarFile, destination, "data", overwrite);
            exportData(jarFile, destination, "scenarios", overwrite);
        } catch (IOException e) {
            // We might not be working from a jar
            exportData(jarFileName, destination, "data", overwrite);
            exportData(jarFileName, destination, "scenarios", overwrite);
        }
    }

    public static void exportData(File source, File destination, String prefixMask, boolean overwrite) {
        for (Iterator<File> iter = Arrays.asList(source.listFiles()).iterator(); iter.hasNext();) {
            File currentFile = iter.next();
            File newFile = new File(destination, currentFile.getName());

            if (currentFile.isDirectory()) {
                newFile.mkdirs();
                exportData(currentFile, newFile, "", overwrite);
            } else {
                copyDataFile(newFile, new FileSystemResource(currentFile), overwrite);
            }
        }
    }

    public static void exportData(JarFile source, File destination, String prefixMask, boolean overwrite) {
        int count = 0;
        Enumeration<JarEntry> entries = source.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.startsWith(prefixMask)) {
                File outFile = new File(destination, name);

                if (name.endsWith("/")) {
                    // Directory: make directory path
                    outFile.mkdirs();
                } else {
                    copyDataFile(outFile, new ClassPathResource(name), overwrite);
                    count++;
                }
            }
        }
        logger.info("Copied " + count + " files into " + destination.getPath() + "/" + prefixMask);
    }

    public static void createDirectoryStructure(File destination) {
        File f = new File(destination, "data");
        
        if (!f.exists()) {
            f.mkdirs();
            logger.info("Created: " + destination.getPath() + "/data");
        }

        f = new File(destination, "scenarios");
        
        if (!f.exists()) {
            f.mkdirs();
            logger.info("Created: " + destination.getPath() + "/scenarios");
        }
        
        f = new File(destination, "log");
        
        if (!f.exists()) {
            f.mkdirs();
            logger.info("Created: " + destination.getPath() + "/log");
        }
        
        f = new File(destination, "temp");
        
        if (!f.exists()) {
            f.mkdirs();
            logger.info("Created: " + destination.getPath() + "/temp");
        }
    }

    private static void copyDataFile(File outFile, Resource name, boolean overwrite) {
        // Export data from jar to SWAC_WORK/data
        logger.trace("Exporting \"" + name + "\" to \"" + outFile.getPath() + "\"");

        if (overwrite || !outFile.exists()) {
            InputStream inStream = null;
            FileOutputStream outStream = null;
            try {
                inStream = name.getInputStream();
                outStream = FileUtils.openOutputStream(outFile);
                StreamUtils.copy(inStream, outStream);
            } catch (IOException ex) {
                throw new RuntimeException("Error exporting data...", ex);
            } finally {
                try {
                    if (inStream != null) {
                        inStream.close();
                    }
                    if (outStream != null) {
                        outStream.close();
                    }
                } catch (Exception ex) {
                }
            }
        }
    }
}
