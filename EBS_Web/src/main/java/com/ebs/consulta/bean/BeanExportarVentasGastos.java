package com.ebs.consulta.bean;

import com.ebs.consulta.servicio.ServicioExportarVentasGastos;
import com.ebs.exception.ExcepcionManager;
import com.powersystem.utilitario.MensajeUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
@ViewScoped
public class BeanExportarVentasGastos {

    @Getter
    @Setter
    private Integer anno;

    @Setter
    private StreamedContent reporteDesplegar = null;

    @Inject
    private ServicioExportarVentasGastos servicioExportarVentasGastos;

    public BeanExportarVentasGastos() {
    }

    @PostConstruct
    public void inicializar() {
        this.anno = Calendar.getInstance().get(Calendar.YEAR);
    }

    public StreamedContent getReporteDesplegar() {
        return reporteDesplegar;
    }

    public void generarExcel(ActionEvent evt) {
        XSSFWorkbook workbook = null;
        try {
            List<Object[]> ventas = servicioExportarVentasGastos.obtenerVentas(anno);
            List<Object[]> gastos = servicioExportarVentasGastos.obtenerGastos(anno);

            workbook = new XSSFWorkbook();

            CellStyle headerStyle = crearEstiloEncabezado(workbook);

            crearHoja(workbook, "Ventas", ventas, headerStyle);
            crearHoja(workbook, "Gastos", gastos, headerStyle);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] bytes = bos.toByteArray();

            InputStream input = new ByteArrayInputStream(bytes);
            reporteDesplegar = new DefaultStreamedContent(input,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "Reporte_Ventas_Gastos_" + anno + ".xlsx");

            MensajeUtil.agregarMensajeInfo("El reporte se generó satisfactoriamente.");
        } catch (Exception ex) {
            ex.printStackTrace();
            ExcepcionManager.manejarExcepcion(ex);
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private CellStyle crearEstiloEncabezado(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private void crearHoja(XSSFWorkbook workbook, String nombreHoja, List<Object[]> datos, CellStyle headerStyle) {
        XSSFSheet sheet = workbook.createSheet(nombreHoja);
        String[] columnas = {"Año", "Mes", "Tarifa", "Monto"};

        // Encabezados
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < columnas.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        int rowNum = 1;
        for (Object[] fila : datos) {
            XSSFRow row = sheet.createRow(rowNum++);

            // Año
            if (fila[0] != null) {
                row.createCell(0).setCellValue(((Number) fila[0]).doubleValue());
            }
            // Mes
            if (fila[1] != null) {
                row.createCell(1).setCellValue(fila[1].toString().trim());
            }
            // Tarifa
            if (fila[2] != null) {
                row.createCell(2).setCellValue(fila[2].toString().trim());
            }
            // Monto
            if (fila[3] != null) {
                if (fila[3] instanceof BigDecimal) {
                    row.createCell(3).setCellValue(((BigDecimal) fila[3]).doubleValue());
                } else {
                    row.createCell(3).setCellValue(((Number) fila[3]).doubleValue());
                }
            }
        }

        // Auto-ajustar ancho de columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
