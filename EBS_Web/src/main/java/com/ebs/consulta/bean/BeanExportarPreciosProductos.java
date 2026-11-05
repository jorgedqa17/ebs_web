package com.ebs.consulta.bean;

import com.ebs.consulta.servicio.ServicioExportarPreciosProductos;
import com.ebs.entidades.TipoPrecio;
import com.ebs.exception.ExcepcionManager;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.utilitario.MensajeUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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
public class BeanExportarPreciosProductos {

    @Getter
    @Setter
    private Long idTipoPrecio;

    @Getter
    private List<TipoPrecio> listaTiposPrecio;

    @Setter
    private StreamedContent reporteDesplegar = null;

    @Inject
    private ServicioExportarPreciosProductos servicioExportarPreciosProductos;

    @Inject
    private ServicioProducto servicioProducto;

    @PostConstruct
    public void inicializar() {
        this.listaTiposPrecio = servicioProducto.obtenerListaTiposPrecio();
    }

    public StreamedContent getReporteDesplegar() {
        return reporteDesplegar;
    }

    public void generarExcel(ActionEvent evt) {
        if (idTipoPrecio == null) {
            MensajeUtil.agregarMensajeError("Debe seleccionar un tipo de precio.");
            return;
        }
        XSSFWorkbook workbook = null;
        try {
            String descripcionTipo = listaTiposPrecio.stream()
                    .filter(t -> idTipoPrecio.equals(t.getId_tipo()))
                    .map(TipoPrecio::getDescripcion)
                    .findFirst().orElse("Precio");

            List<Object[]> datos = servicioExportarPreciosProductos.obtenerPreciosProductosActivos(idTipoPrecio);

            workbook = new XSSFWorkbook();
            CellStyle headerStyle = crearEstiloEncabezado(workbook);
            crearHoja(workbook, "Precios", datos, descripcionTipo, headerStyle);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] bytes = bos.toByteArray();

            InputStream input = new ByteArrayInputStream(bytes);
            reporteDesplegar = new DefaultStreamedContent(input,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "Precios_" + descripcionTipo.replaceAll("[^A-Za-z0-9]+", "_") + ".xlsx");

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

    private void crearHoja(XSSFWorkbook workbook, String nombreHoja, List<Object[]> datos, String descripcionTipo, CellStyle headerStyle) {
        XSSFSheet sheet = workbook.createSheet(nombreHoja);
        String[] columnas = {"Descripción", "Precio " + descripcionTipo};

        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < columnas.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Object[] fila : datos) {
            XSSFRow row = sheet.createRow(rowNum++);

            if (fila[0] != null) {
                row.createCell(0).setCellValue(fila[0].toString());
            }
            if (fila[1] != null) {
                row.createCell(1).setCellValue(((BigDecimal) fila[1]).doubleValue());
            }
        }

        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
