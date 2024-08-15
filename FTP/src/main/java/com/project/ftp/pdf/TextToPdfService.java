package com.project.ftp.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.project.ftp.config.AppConstant;
import com.project.ftp.parser.TextFileParser;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

public class TextToPdfService {
    final static Logger logger = LoggerFactory.getLogger(TextToPdfService.class);
    private String pdfTitle;
    private String pdfSubject;
    public TextToPdfService() {}
    public TextToPdfService(String pdfTitle, String pdfSubject) {
        this.pdfTitle = pdfTitle;
        this.pdfSubject = pdfSubject;
    }

    private void convertTextToPdf(String pdfFileName, ArrayList<String> fileData) {
        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFileName));
            document.open();
            Paragraph paragraph;
            for (String paragraphText : fileData) {
                if (paragraphText.equals("")) {
                    paragraphText = AppConstant.EmptyParagraph;
                }
                paragraph = new Paragraph(paragraphText);
                document.add(paragraph);
            }
            if (fileData.isEmpty()) {
                document.add(new Paragraph(AppConstant.EmptyParagraph));
            }
            document.addCreationDate();
            document.addAuthor(AppConstant.PDF_AUTHOR);
            document.addCreator(AppConstant.PDF_CREATOR);
            if (pdfTitle != null) {
                document.addTitle(pdfTitle);
            }
            if (pdfSubject != null) {
                document.addSubject(pdfSubject);
            }
            document.close();
            writer.close();
            logger.info("Pdf file created: {}", pdfFileName);
        } catch (DocumentException e) {
            logger.info("DocumentException error, {}, {}", pdfFileName, e.getMessage());
        } catch (FileNotFoundException e) {
            logger.info("FileNotFoundException error, {}, {}", pdfFileName, e.getMessage());
        }
    }
    public void createPdf(String textFilename, String pdfFilename, String pdfTitle, String pdfSubject) {
        if (StaticService.isInValidString(textFilename)) {
            logger.info("createPdf, invalid textFilename: '{}'", textFilename);
            return;
        }
        if (StaticService.isInValidString(pdfFilename)) {
            logger.info("createPdf, invalid pdfFilename: '{}'", pdfFilename);
            return;
        }
        if (StaticService.isInValidString(pdfTitle)) {
            logger.info("createPdf, invalid pdfTitle: '{}'", pdfTitle);
            return;
        }
        if (StaticService.isInValidString(pdfSubject)) {
            logger.info("createPdf, invalid pdfSubject: '{}'", pdfSubject);
            return;
        }
        TextToPdfService textToPdfService = new TextToPdfService(pdfTitle, pdfSubject);
        TextFileParser textFileParser = new TextFileParser(textFilename);
        ArrayList<String> fileData = textFileParser.readTextFile();
        fileData.add("");
        fileData.add("AppVersion: " + AppConstant.AppVersion +
                ", Dated: " + StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat3));
        textToPdfService.convertTextToPdf(pdfFilename, fileData);
        logger.info("createPdf, request completed. '{}' to '{}'", textFilename, pdfFilename);
    }
}
