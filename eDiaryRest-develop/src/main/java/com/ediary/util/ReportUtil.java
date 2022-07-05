package com.ediary.util;

import com.ediary.domain.helpers.Report;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ReportUtil {

    public static byte[] convertToZip(List<Report> reports) {
        String extension = ".pdf";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        try {
            for (Report report : reports) {
                ZipEntry entry = new ZipEntry(report.getFilename() + extension);
                entry.setSize(report.getFile().length);
                zos.putNextEntry(entry);
                zos.write(report.getFile());
            }
            zos.closeEntry();
            zos.close();
            return baos.toByteArray();
        } catch (Exception ex ) {
            throw new IllegalStateException("Cannot convert files to zip");
        }
    }
}
