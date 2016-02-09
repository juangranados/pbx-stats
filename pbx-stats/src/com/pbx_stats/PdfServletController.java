package com.pbx_stats;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Servlet implementation class PdfServletController
 */
public class PdfServletController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PdfServletController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/pdf");
		String title = (String) request.getAttribute("reporte");
		String reportTable[][] = (String[][]) request.getAttribute("results");
		Document document = new Document(PageSize.A4, 10, 10, 10, 10);
		try {
			PdfWriter.getInstance(document, response.getOutputStream());

			document.open();

			Font fontbold = FontFactory.getFont("Arial", 20, Font.BOLD);
			Paragraph p = new Paragraph(title, fontbold);
			p.setSpacingAfter(20);
			p.setAlignment(1); // Center

			document.add(p);

			PdfPTable table = new PdfPTable(reportTable[0].length);

			table.setWidthPercentage(100);
			table.setHeaderRows(1);

			for (int i = 0; i < reportTable.length; i++) {
				for (int j = 0; j < reportTable[i].length; j++) {
					table.addCell(new PdfPCell(new Paragraph(reportTable[i][j])));
				}
			}

			document.add(table);

			document.close();
		} catch (Exception e) {

		}
	}
}
