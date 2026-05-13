package com.tss.AmlSystem.service;

import com.cloudinary.Cloudinary;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.tss.AmlSystem.dto.pdf.StrAlertDto;
import com.tss.AmlSystem.dto.pdf.StrCustomerDto;
import com.tss.AmlSystem.dto.pdf.StrReportDto;
import com.tss.AmlSystem.dto.pdf.StrTransactionDto;
import com.tss.AmlSystem.entity.tenant.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.lowagie.text.PageSize;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private final TemplateEngine templateEngine;


    public byte[] generateTransactionReport(List<Transaction> transactions, String customerNumber) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, outputStream);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Customer Transaction Report For Customer: " + customerNumber, titleFont);
        title.setSpacingAfter(20);
        document.add(title);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);

        float[] columnWidths = {1.4f, 3.2f, 1.8f, 2.0f, 1.2f, 0.8f, 1.6f};
        try {
            table.setWidths(columnWidths);
        } catch (Exception e) {
        }

        table.getDefaultCell().setPaddingTop(8f);
        table.getDefaultCell().setPaddingBottom(8f);

        table.getDefaultCell().setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
        table.addCell("Date");
        table.addCell("Ref_Number");
        table.addCell("Acc_Number");
        table.addCell("Receiver_Acc_Number");
        table.addCell("Amount");
        table.addCell("Type");
        table.addCell("Mode");


        for (Transaction txn : transactions) {
            table.addCell(txn.getTransactionDate().toString());
            table.addCell(txn.getTransactionReferenceNumber());
            table.addCell(txn.getAccountNumber());
            table.addCell(txn.getCounterPartyAccountNumber());
            table.addCell(txn.getAmount().toString());
            table.addCell(txn.getTransactionType().toString());
            table.addCell(txn.getTransactionMode().toString());
        }

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }

    public byte[] generateStrReportPdf(StrReportDto reportDto, StrCustomerDto customerDto, List<StrAlertDto> alertDtoList, List<StrTransactionDto> transactionDtoList) {

        Context context = new Context();

        context.setVariable("report", reportDto);
        context.setVariable("customer", customerDto);
        context.setVariable("alerts", alertDtoList);
        context.setVariable("transactions", transactionDtoList);

        String htmlContent = templateEngine.process("STR_Template", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();

            builder.withHtmlContent(htmlContent, getClass().getResource("/").toExternalForm());
            builder.useFastMode();
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
