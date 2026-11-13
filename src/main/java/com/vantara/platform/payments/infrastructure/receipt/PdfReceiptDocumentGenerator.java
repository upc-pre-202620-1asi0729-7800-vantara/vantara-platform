package com.vantara.platform.payments.infrastructure.receipt;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.vantara.platform.payments.application.ports.ReceiptDocumentGenerator;
import com.vantara.platform.payments.domain.model.aggregates.Payment;
import com.vantara.platform.payments.domain.model.entities.PaymentReceipt;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

/**
 * Renders a simple PDF receipt with OpenPDF.
 */
@Component
public class PdfReceiptDocumentGenerator implements ReceiptDocumentGenerator {

    @Override
    public byte[] generate(Payment payment, PaymentReceipt receipt) {
        var output = new ByteArrayOutputStream();
        var document = new Document(PageSize.A4, 48, 48, 48, 48);
        try {
            PdfWriter.getInstance(document, output);
            document.open();

            var title = FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLD);
            var label = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);
            var text = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL);

            document.add(new Paragraph("Vantara / Hatarium", title));
            document.add(new Paragraph("Comprobante de pago", label));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Número: " + receipt.getReceiptNumber(), text));
            document.add(new Paragraph("Emitido: " + receipt.getIssuedAt(), text));
            document.add(new Paragraph("Rancher: " + payment.getRancherId(), text));
            document.add(new Paragraph("Concepto: " + payment.getConcept(), text));
            document.add(new Paragraph(
                    "Monto: " + payment.getCurrency() + " " + payment.getAmount().toPlainString(), text));
            document.add(new Paragraph("Estado: " + payment.getStatus().value(), text));

            if (payment.getPaymentMethod() != null) {
                document.add(new Paragraph("Método de pago: " + payment.getPaymentMethod().masked(), text));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Gracias por su pago.", text));
        } catch (Exception exception) {
            throw new IllegalStateException("payment.error.receipt.document", exception);
        } finally {
            document.close();
        }
        return output.toByteArray();
    }
}
