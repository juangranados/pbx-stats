package com.pbx_stats;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 * Genera un archivo PDF a partir de una tabla que recibe a
 * través de un atributo del request del Servlet ReportServletController
 */
public class PdfServletController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger("PdfServletController: ");
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
		//Se recuperan los atributos del request del Servlet ReportServletController
		//Título del informe
		String title = (String) request.getAttribute("reporte");
		//Tabla de resultados
		String reportTable[][] = (String[][]) request.getAttribute("results");
		//Se crea un nuevo objeto PDF
		Document document = new Document(PageSize.A4, 10, 10, 10, 10);
		try {
			//Array de Bytes donde se escribe el documento
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
			document.open();
			//Tipo de fuente para el título
			Font fontTitle = FontFactory.getFont("Arial", 20, Font.BOLD);
			//Fuente Negrita
			Font fontBold = FontFactory.getFont("Arial", 10, Font.BOLD);
			//Fuente Normal
			Font fontNormal = FontFactory.getFont("Arial", 10, Font.NORMAL);
			//Párrafo de título
			Paragraph p = new Paragraph(title, fontTitle);
			p.setSpacingAfter(20);
			p.setAlignment(1); // Center
			document.add(p);
			//Tabla con los resultados
			PdfPTable table = new PdfPTable(reportTable[0].length);
			//La tabla ocupa todo el ancho de la página
			table.setWidthPercentage(100);
			//Se define como cabecera la primera fila, que se repetirá en todas las páginas
			table.setHeaderRows(1);
			//Se crea la primera fila en negrita
			for (int j = 0; j < reportTable[0].length; j++) {
				table.addCell(new PdfPCell(new Paragraph(reportTable[0][j],fontBold)));
			}
			//Se crea la tabla recorriendo la matriz de resultados
			for (int i = 1; i < reportTable.length; i++) {
				for (int j = 0; j < reportTable[i].length; j++) {
					table.addCell(new PdfPCell(new Paragraph(reportTable[i][j],fontNormal)));
				}
			}
			//Se añade la tabla al documento
			document.add(table);
			document.close();
			// setting some response headers
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            // setting the content type
            response.setContentType("application/pdf");
            // the contentlength
            response.setContentLength(baos.size());
            // write ByteArrayOutputStream to the ServletOutputStream
            OutputStream os = response.getOutputStream();
            baos.writeTo(os);
            os.flush();
            os.close();
		} catch (Exception e) {
			log.error("Error al generar el documento PDF: " + e.getMessage());
		}
	}
}
