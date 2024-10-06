package com.laptop.Laptop.services;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfReportServices {
    @Autowired
    private ProductRepository productRepository;

    // Generate PDF for sale receipt
    public ByteArrayInputStream generateSaleReceipt(Sale sale, User loggedInUser) {

        return null;
    }


    // Method to generate product stock report
    public ByteArrayInputStream generateProductsReport(Shop shop, User user) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Add shop details in the header
            addHeader(document, shop,user);

            document.add(new Paragraph("Product Report (Ordered by Stock)", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph(" "));

            // Create table with columns: No., Product Name, Stock, Price
            PdfPTable table = new PdfPTable(4); // 4 columns
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 3, 1, 1}); // Set column widths
            table.addCell(createCell("No.", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            table.addCell(createCell("Product Name", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            table.addCell(createCell("Stock", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            table.addCell(createCell("Price", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));

            // Fetch and sort products by stock in descending order
            List<Product> products = productRepository.findByShopId(shop.getId()).stream()
                    .sorted((p1, p2) -> Integer.compare(p2.getStock(), p1.getStock())) // Sort by stock descending
                    .collect(Collectors.toList());

            // Add products to the table with numbering
            int count = 1; // Initialize counter for numbering
            for (Product product : products) {
                table.addCell(String.valueOf(count++)); // Add number
                table.addCell(product.getName()); // Product name
                table.addCell(String.valueOf(product.getStock())); // Stock
                table.addCell(String.valueOf(product.getPrice())); // Price
            }

            document.add(table);

            // Add footer
            addFooter(document,user);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generateSalesReport(List<Sale> sales, Shop shop, User user) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            addHeader(document, shop, user);

            document.add(new Paragraph("Sales Report as Btw:"+ " " +LocalDate.now()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5); // Columns: Date, Product, Quantity, Total
            table.setWidthPercentage(100);
            table.addCell("Date");
            table.addCell("Product");
            table.addCell("Quantity");
            table.addCell("itemCost");
            table.addCell("Total");

            for (Sale sale : sales) {
                table.addCell(sale.getDate().toString());
                table.addCell(sale.getProduct().getName());
                table.addCell(String.valueOf(sale.getQuantity()));
                table.addCell(String.valueOf(sale.getSalePrice()));
                table.addCell(String.valueOf(sale.getSaleTotal()));
            }

            document.add(table);
            addFooter(document,user);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generateStockPurchaseReport(List<StockPurchase> stockPurchases, Shop shop, User user) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            addHeader(document, shop, user);

            document.add(new Paragraph("Stock Purchase Report"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5); // Columns: Date, Supplier, Quantity, Total
            table.setWidthPercentage(100);
            table.addCell("Date");
            table.addCell("Supplier");
            table.addCell("Quantity");
            table.addCell("Item Cost");
            table.addCell("Total");

            for (StockPurchase stockPurchase : stockPurchases) {
                table.addCell(stockPurchase.getPurchaseDate().toString());
                table.addCell(String.valueOf(stockPurchase.getSupplierName()));
                table.addCell(String.valueOf(stockPurchase.getQuantity()));
                table.addCell(String.valueOf(stockPurchase.getItemCost()));

                table.addCell(String.valueOf(stockPurchase.getTotalCost()));
            }

            document.add(table);
            addFooter(document,user);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generateShopListReport(List<Shop> shops) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Shop List Report"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4); // Columns: Shop Name, Address, Phone, Owner
            table.setWidthPercentage(100);
            table.addCell("Shop Name");
            table.addCell("Address");
            table.addCell("Phone");
            table.addCell("Owner");

            for (Shop shop : shops) {
                table.addCell(shop.getShopName());
                table.addCell(shop.getAddress());
                table.addCell(shop.getPhoneNumber());
                table.addCell(shop.getOwner());
            }

            document.add(table);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // Helper method to create cells for the table
    private PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(5);
        return cell;
    }

    // Helper method to add header with shop details and logo
    private void addHeader(Document document, Shop shop,User user) throws DocumentException {
        // Add the logo (if available)
        if (shop.getLogo() != null) {
            try {
                Image logo = Image.getInstance(shop.getLogo()); // Assuming `shop.getLogo()` returns byte[]
                logo.scaleAbsolute(50, 50); // Adjust the size as necessary
                document.add(logo);
            } catch (Exception e) {
                // Handle logo loading error
            }
        }

        // Add the shop details
        Paragraph shopDetails = new Paragraph(
                shop.getShopName() + "\n" +
                        "Address: " + shop.getAddress() + "\n" +
                        "Phone: " + shop.getPhoneNumber(),
                FontFactory.getFont(FontFactory.HELVETICA, 12)
        );
        shopDetails.setAlignment(Element.ALIGN_CENTER);
        document.add(shopDetails);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 12))); // Add space
    }

    // Helper method to add footer
    private void addFooter(Document document, User user) throws DocumentException {
        Paragraph footer = new Paragraph(
                "Generated on: " + java.time.LocalDate.now() + " by " + user.getUsername(),
                FontFactory.getFont(FontFactory.HELVETICA, 10)
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }


}
