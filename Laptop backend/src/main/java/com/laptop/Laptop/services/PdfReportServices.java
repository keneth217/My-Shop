package com.laptop.Laptop.services;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.laptop.Laptop.dto.ShopUpdateRequestDto;
import com.laptop.Laptop.dto.StockPurchaseDto;
import com.laptop.Laptop.entity.*;
import com.laptop.Laptop.exceptions.ProductNotFoundException;
import com.laptop.Laptop.repository.ProductRepository;
import com.laptop.Laptop.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfReportServices {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SaleRepository saleRepository ;

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal(); // Assuming your `User` implements `UserDetails`
    }

    // Method to get specific details of the logged-in user
    public User getLoggedInUserDetails() {
        return getLoggedInUser(); // Directly return the logged-in user
    }

    public ByteArrayInputStream generateReceiptForSale(Long saleId) throws IOException {
        // Fetch the sale by ID, handle case if not found
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ProductNotFoundException("Sale not found"));

        // Create a new Document with A5 page size and ByteArrayOutputStream
        Document document = new Document(PageSize.A5); // Set the page size to A5
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Add shop and user details to the header
            addHeader(document, sale.getShop(), sale.getUser());
            addHorizontalLine(document); // Add a horizontal line after the header

            // Empty line for spacing
            document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 10)));

            // Add "RECEIPT" title, centered and bold
            Paragraph receiptTitle = new Paragraph("RECEIPT", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
            receiptTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(receiptTitle);

            document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 10)));

            // Create a table for right-aligned receipt information with 2 columns
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(50); // Set the table width to 50% of the page width (small table)
            infoTable.setWidths(new float[]{1, 2}); // Adjust the column widths to align to the right

// Add an empty cell in the first column to "push" the content to the right
            PdfPCell emptyCell = new PdfPCell(new Phrase(" "));
            emptyCell.setBorder(Rectangle.NO_BORDER); // No border for the empty cell
            infoTable.addCell(emptyCell);

// Right-aligned receipt details in the second column (database content)
            PdfPCell receiptInfoCell = new PdfPCell();
            receiptInfoCell.setBorder(Rectangle.BOX); // Adding border around the info cell
            receiptInfoCell.setPadding(5); // Adding some padding for better readability
            receiptInfoCell.setHorizontalAlignment(Element.ALIGN_RIGHT); // Right-align the content in this cell

// Hardcoded content on the left (like "Receipt No", etc.)
// These elements will be in the first column but aligned to the left.
            receiptInfoCell.addElement(new Paragraph("Receipt No: " + sale.getId(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
            receiptInfoCell.addElement(new Paragraph("Date: " + sale.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            receiptInfoCell.addElement(new Paragraph("Seller: " + sale.getSalePerson(), FontFactory.getFont(FontFactory.HELVETICA, 8)));

// Add the receipt info cell to the second column (database details side)
            infoTable.addCell(receiptInfoCell);

// Add the table to the document
            document.add(infoTable);




            // Received from section
            document.add(new Paragraph("RECEIVED FROM", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
            document.add(new Paragraph("Name: " + sale.getCustomerName(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
            document.add(new Paragraph("Phone: " + sale.getCustomerPhone(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
            document.add(new Paragraph("ID: " + sale.getUser().getId(), FontFactory.getFont(FontFactory.HELVETICA, 8)));
            document.add(new Paragraph("PAID VIA: Cash", FontFactory.getFont(FontFactory.HELVETICA, 8)));

            document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 8)));

            // Total amount in words
            document.add(new Paragraph("RECEIVED SUM OF " + NumberToWordsConverter.convert(sale.getTotalPrice()) + " ONLY",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 10)));

            // Table for product details
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 2,1, 1, 1});

            // Table headers
            table.addCell(new PdfPCell(new Phrase(" NAME", FontFactory.getFont(FontFactory.HELVETICA,8))));
            table.addCell(new PdfPCell(new Phrase("FEATURES", FontFactory.getFont(FontFactory.HELVETICA,8))));
            table.addCell(new PdfPCell(new Phrase("QTY", FontFactory.getFont(FontFactory.HELVETICA,8))));
            table.addCell(new PdfPCell(new Phrase("COST", FontFactory.getFont(FontFactory.HELVETICA,8))));
            table.addCell(new PdfPCell(new Phrase("TOTAL", FontFactory.getFont(FontFactory.HELVETICA,8))));

            // Create a font with size 6
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 6);

// Add SaleItem details to table
            for (SaleItem item : sale.getSaleItems()) {
                Product product = item.getProduct();

                // Add product name with font size 6
                table.addCell(new PdfPCell(new Phrase(product.getName(), smallFont)));

                // Add product features with font size 6
                table.addCell(new PdfPCell(new Phrase(String.join(", ", product.getProductFeatures()), smallFont)));

                // Add quantity with font size 6
                table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), smallFont)));

                // Add sale price with font size 6
                table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getSalePrice()), smallFont)));

                // Add total price (quantity * sale price) with font size 6
                table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getQuantity() * item.getSalePrice()), smallFont)));
            }

            document.add(table);

            // Subtotal and total sections
            document.add(new Paragraph("SUBTOTAL: " + sale.getTotalPrice(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            document.add(new Paragraph("TOTAL: " + sale.getTotalPrice(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));

            document.add(new Paragraph("Thank you for your business", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        // Return the generated PDF
        return new ByteArrayInputStream(out.toByteArray());
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
            PdfPTable table = new PdfPTable(5); // 4 columns
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 2, 1, 1,1}); // Set column widths
            table.addCell(createCell("No.", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            table.addCell(createCell("Product Name", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            table.addCell(createCell("Stock", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));

            table.addCell(createCell("Buying Price", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            table.addCell(createCell("Selling Price", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));

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

                table.addCell(String.valueOf(product.getCost())); // Price
                table.addCell(String.valueOf(product.getSellingPrice())); // Price
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

    public ByteArrayInputStream generateSalesReport(List<Sale> sales, LocalDate startDate, LocalDate endDate, Shop shop, User user) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Add the header to the report
            addHeader(document, shop, user);

            // Add the title and date range for the report
            document.add(new Paragraph("Sales Report"));
            document.add(new Paragraph("From: " + startDate + " To: " + endDate));
            document.add(new Paragraph("Generated on: " + LocalDate.now()));
            document.add(new Paragraph(" "));  // Add space

            // Create a table with 4 columns: Date, Seller, Customer, Total
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("Date");
            table.addCell("Seller");
            table.addCell("Customer");
            table.addCell("Total");

            // Filter and add sales within the given date range
            sales.stream()
                    .filter(sale -> !sale.getDate().isBefore(startDate) && !sale.getDate().isAfter(endDate))
                    .forEach(sale -> {
                        table.addCell(sale.getDate().toString());
                        table.addCell(String.valueOf(sale.getSalePerson()));
                        table.addCell(sale.getCustomerName());
                        table.addCell(String.format("%.2f", sale.getTotalPrice()));
                    });

            // Add the table to the document
            document.add(table);

            // Add the footer to the report
            addFooter(document, user);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // Return the generated PDF as a ByteArrayInputStream
        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generateStockPurchaseReport(List<StockPurchaseDto> stockPurchases, Shop shop, User user) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            addHeader(document, shop, user);

            document.add(new Paragraph("Stock Purchase Report"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(7); // Columns: Date, Supplier, Quantity, Total
            table.setWidthPercentage(100);
            table.addCell("Date");
            table.addCell("Supplier");
            table.addCell("Item");
            table.addCell("Quantity");
            table.addCell("Buy");
            table.addCell("Sell");
            table.addCell("Total");

            for (StockPurchaseDto stockPurchase : stockPurchases) {
                table.addCell(stockPurchase.getPurchaseDate().toString());
                table.addCell(String.valueOf(stockPurchase.getSupplierName()));
                table.addCell(String.valueOf(stockPurchase.getProductName()));
                table.addCell(String.valueOf(stockPurchase.getQuantity()));
                table.addCell(String.valueOf(stockPurchase.getBuyingPrice()));
                table.addCell(String.valueOf(stockPurchase.getSellingPrice()));

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

    public ByteArrayInputStream generateShopListReport(List<ShopUpdateRequestDto> shops) {
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

            for (ShopUpdateRequestDto shop : shops) {
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
    private void addHeader(Document document, Shop shop, User user) throws DocumentException {
        // Create a table with two columns: one for the logo and one for the shop details
        PdfPTable table = new PdfPTable(2); // 2 columns
        table.setWidthPercentage(100); // Set table width to 100% of the page
        table.setSpacingBefore(10); // Adds some space before the table (optional, you can adjust it)

        // Add the logo (if available) in the first column
        if (shop.getShopLogo() != null) {
            try {
                Image logo = Image.getInstance(shop.getShopLogo()); // Assuming `shop.getLogo()` returns byte[]
                logo.scaleAbsolute(100, 100); // Adjust the size as necessary
                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(Rectangle.NO_BORDER); // No border for logo cell
                logoCell.setHorizontalAlignment(Element.ALIGN_LEFT); // Align logo to the left
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Vertically align logo in the cell
                table.addCell(logoCell);
            } catch (Exception e) {
                // Handle logo loading error
                e.printStackTrace();
            }
        } else {
            // If no logo, add an empty cell to maintain alignment
            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(emptyCell);
        }

        // Add the shop details in the second column
        PdfPCell shopDetailsCell = new PdfPCell();
        String shopDetailsText = shop.getShopName() + "\n" +
                "P.O BOX-" + shop.getAddress() + "\n" +
                shop.getPhoneNumber();
        shopDetailsCell.setPhrase(new Phrase(shopDetailsText, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        shopDetailsCell.setBorder(Rectangle.NO_BORDER); // No border for shop details cell
        shopDetailsCell.setHorizontalAlignment(Element.ALIGN_LEFT); // Align text to the left
        shopDetailsCell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Vertically center the text
        table.addCell(shopDetailsCell);

        // Add the table to the document
        document.add(table);

        // Add some space after the table (optional)
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 12))); // Add an empty space (this can be customized)
    }


    // Helper method to add footer
    private void addFooter(Document document, User user) throws DocumentException {
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 12))); // Add space after the table
        Paragraph footer = new Paragraph(
                "Generated on: " + java.time.LocalDate.now() + " by " + user.getUsername(),
                FontFactory.getFont(FontFactory.HELVETICA, 10)
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }

    // Method to add a horizontal line
    private void addHorizontalLine(Document document) throws DocumentException {
        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(100); // Set the table to occupy full width
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorder(PdfPCell.NO_BORDER); // No border around the cell
        lineCell.setBorderWidthBottom(2); // Set the bottom border width (thickness of the line)
        lineCell.setBorderColorBottom(BaseColor.BLACK); // Set the border color (black)
        lineCell.setPadding(5); // Padding for spacing above and below the line
        lineTable.addCell(lineCell); // Add the line cell to the table
        document.add(lineTable); // Add the table (line) to the document
    }


    public class NumberToWordsConverter {

        private static final String[] units = {
                "", "One", "Two", "Three", "Four", "Five",
                "Six", "Seven", "Eight", "Nine", "Ten",
                "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
                "Sixteen", "Seventeen", "Eighteen", "Nineteen"
        };

        private static final String[] tens = {
                "", "", "Twenty", "Thirty", "Forty", "Fifty",
                "Sixty", "Seventy", "Eighty", "Ninety"
        };

        private static final String[] thousands = {
                "", "Thousand", "Million", "Billion"
        };

        public static String convert(double number) {
            if (number == 0) {
                return "Zero";
            }

            String words = "";

            // Handle decimals
            if (number % 1 != 0) {
                String[] parts = String.valueOf(number).split("\\.");
                words += convert(Long.parseLong(parts[0])) + " and " + convert(Long.parseLong(parts[1])) + " cents";
            } else {
                words = convert((long) number);
            }

            return words.trim();
        }

        private static String convert(long number) {
            if (number < 100) {
                return convertTwoDigits(number);
            }

            String words = "";

            if (number >= 1000) {
                words += convert(number / 1000) + " " + thousands[1] + " ";
                number %= 1000;
            }

            if (number >= 100) {
                words += convertTwoDigits(number / 100) + " Hundred ";
                number %= 100;
            }

            if (number > 0) {
                words += convertTwoDigits(number);
            }

            return words.trim();
        }

        private static String convertTwoDigits(long number) {
            if (number < 20) {
                return units[(int) number];
            }

            String words = tens[(int) (number / 10)];
            if (number % 10 != 0) {
                words += " " + units[(int) (number % 10)];
            }

            return words.trim();
        }
    }


}
