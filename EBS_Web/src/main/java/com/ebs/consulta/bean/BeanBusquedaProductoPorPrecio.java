package com.ebs.consulta.bean;

import com.ebs.entidades.TipoPrecio;
import com.ebs.exception.ExcepcionManager;
import com.ebs.modelos.ModeloProductoPorPrecio;
import com.powersystem.productos.servicios.ServicioProducto;
import com.powersystem.utilitario.MensajeUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
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
public class BeanBusquedaProductoPorPrecio {

    @Getter
    @Setter
    private Long idTipoPrecioSeleccionado;

    @Getter
    @Setter
    private List<TipoPrecio> listaTiposPrecios;

    @Getter
    @Setter
    private List<ModeloProductoPorPrecio> listaResultados;

    @Setter
    private StreamedContent reporteDesplegar;

    @Inject
    private ServicioProducto servicioProducto;

    public BeanBusquedaProductoPorPrecio() {
    }

    @PostConstruct
    public void inicializar() {
        this.listaTiposPrecios = servicioProducto.obtenerListaTiposPrecio();
        this.listaResultados = new ArrayList<>();
    }

    public void buscarProductosPorTipoPrecio() {
        try {
            listaResultados = new ArrayList<>();
            if (idTipoPrecioSeleccionado == null) {
                MensajeUtil.agregarMensajeError("Debe seleccionar un tipo de precio.");
                return;
            }
            List<Object[]> filas = servicioProducto.listarProductosPorTipoPrecio(idTipoPrecioSeleccionado);
            for (Object[] fila : filas) {
                String nombre = fila[0] != null ? fila[0].toString() : "";
                BigDecimal precio = fila[1] != null ? new BigDecimal(fila[1].toString()) : BigDecimal.ZERO;
                listaResultados.add(new ModeloProductoPorPrecio(nombre, precio));
            }
        } catch (Exception ex) {
            ExcepcionManager.manejarExcepcion(ex);
        }
    }

    public StreamedContent getReporteDesplegar() {
        return reporteDesplegar;
    }

    public void generarExcel(ActionEvent evt) {
        XSSFWorkbook workbook = null;
        try {
            if (listaResultados == null || listaResultados.isEmpty()) {
                MensajeUtil.agregarMensajeError("No hay datos para exportar. Realice una búsqueda primero.");
                return;
            }

            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Productos por Precio");

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            XSSFRow headerRow = sheet.createRow(0);
            XSSFCell cellNombre = headerRow.createCell(0);
            cellNombre.setCellValue("Nombre Producto");
            cellNombre.setCellStyle(headerStyle);
            XSSFCell cellPrecio = headerRow.createCell(1);
            cellPrecio.setCellValue("Precio");
            cellPrecio.setCellStyle(headerStyle);

            int rowNum = 1;
            for (ModeloProductoPorPrecio item : listaResultados) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getNombreProducto());
                if (item.getPrecio() != null) {
                    row.createCell(1).setCellValue(item.getPrecio().doubleValue());
                }
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] bytes = bos.toByteArray();

            InputStream input = new ByteArrayInputStream(bytes);
            reporteDesplegar = new DefaultStreamedContent(input,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "Productos_Por_Precio.xlsx");

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
}
