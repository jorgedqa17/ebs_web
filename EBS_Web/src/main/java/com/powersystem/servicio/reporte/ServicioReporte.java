/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.servicio.reporte;

import com.ebs.constantes.enums.Reportes;
import com.ebs.constantes.enums.TiposMimeTypes;
import static com.ebs.constantes.enums.TiposMimeTypes.PDF;
import com.ebs.exception.ExcepcionManager;
import com.powersystem.util.ServicioBase;
import com.powersystem.utilitario.Utilitario;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Slf4j
@Stateless
public class ServicioReporte extends ServicioBase {

    @Getter
    @Setter
    private Map parametros;
    @Inject
    private ServletContext context;

//    public byte[] generarReporte(Reportes reporteGenerar, TiposMimeTypes formato, Map<String, Object> parametros, boolean tieneSubReportes, Factura factura) throws JRException {
//        JasperPrint print = null;
//        try {
//            String ruta = context.getRealPath("/WEB-INF/reportes") + File.separator;
//            String rutaImagenes = context.getRealPath("/resources/imagenes") + File.separator;
//
//            if (tieneSubReportes) {
//                parametros.put("rutaSubReportes", ruta);
//            }
//            parametros.put("rutaImagen", rutaImagenes);
//
//            Connection conection = Utilitario.obtenerConexion();
//            JasperReport report = JasperCompileManager.compileReport(ruta + reporteGenerar.getJasper());
//            print = JasperFillManager.fillReport(report, parametros, conection);
//            JasperExportManager.exportReportToPdfFile(print, "C:\\factura-" + factura.getNumero_consecutivo()+".pdf");
//            print.setName(reporteGenerar.getNombreReporte());
//            Utilitario.cerrarConexion(conection);
//        } catch (JRException | SQLException ex) {
//            ex.printStackTrace();
//            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
//                    "mensaje.generar.reporte",
//                    "mensaje.generar.reporte.final");
//        }
//        return exportar(print, formato);
//    }
//    public String obtenerRutaReporte(){
//    
//    final ProtectionDomain domain;
//    final CodeSource source;
//    final URL url;
//    final URI uri;
//    String DirectoryPath;
//    String separador_directorios=System.getProperty("file.separator");
//    String JarURL;
//    File auxiliar;
//    domain = Fichero.class.getProtectionDomain();
//    source = domain.getCodeSource();
//    url = source.getLocation();
//    uri = url.toURI();
//    JarURL = uri.getPath();
//    auxiliar = new File(JarURL);
//    //Si es un directorio es que estamos ejecutando desde el IDE. En este caso
//    // habrá que buscar el fichero en la carperta  abuela(junto a las carpetas "src" y "target·
//    if (auxiliar.isDirectory()) {
//        auxiliar = new File(auxiliar.getParentFile().getParentFile().getPath());
//        DirectoryPath = auxiliar.getCanonicalPath() + separador_directorios;
//    } else {
//        JarURL=auxiliar.getCanonicalPath();
//        DirectoryPath = JarURL.substring(0, JarURL.lastIndexOf(separador_directorios) + 1);
//
//    }
//
//    System.out.println(DirectoryPath + filename);
//    return DirectoryPath + filename;
//    }
    public byte[] generarReporte(Reportes reporteGenerar, TiposMimeTypes formato, Map<String, Object> parametros, boolean tieneSubReportes) throws JRException {
        JasperPrint print = null;
        try {
            String ruta = context.getRealPath("/WEB-INF/reportes") + File.separator;
            if (tieneSubReportes) {
                parametros.put("rutaSubReportes", ruta);
            }
            parametros.put("rutaImagen", ruta);

            Connection conection = Utilitario.obtenerConexion();
            JasperReport report = JasperCompileManager.compileReport(ruta + reporteGenerar.getJasper());
            print = JasperFillManager.fillReport(report, parametros, conection);
            print.setName(reporteGenerar.getNombreReporte());
            Utilitario.cerrarConexion(conection);
        } catch (JRException | SQLException ex) {
            ex.printStackTrace();
            throw ExcepcionManager.lanzarExcepcionServicio(getClass(), log, ex,
                    "mensaje.generar.reporte",
                    "mensaje.generar.reporte.final");
        }
        return exportar(print, formato);
    }

    private byte[] exportar(JasperPrint jp, TiposMimeTypes formato) throws JRException {
        Exporter exporter = crearExporter(formato);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        exporter.setExporterInput(new SimpleExporterInput(jp));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
        exporter.exportReport();
        return os.toByteArray();
    }

    private Exporter crearExporter(TiposMimeTypes formato) {
        Exporter exporter = null;

        switch (formato) {
            case PDF: {
                exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration
                        = new SimplePdfExporterConfiguration();
                exporter.setConfiguration(configuration);
                break;
            }
            case XLSX: {
                exporter = new JRXlsxExporter();
                SimpleXlsxReportConfiguration configuration
                        = new SimpleXlsxReportConfiguration();
                configuration.setDetectCellType(Boolean.TRUE);
                configuration.setOnePagePerSheet(Boolean.FALSE);
                configuration.setRemoveEmptySpaceBetweenColumns(Boolean.TRUE);
                configuration.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);
                configuration.setWhitePageBackground(Boolean.TRUE);
                configuration.setIgnoreCellBorder(Boolean.TRUE);
                configuration.setIgnorePageMargins(Boolean.TRUE);
                configuration.setSheetNames(new String[]{"SearMedica"});
                exporter.setConfiguration(configuration);
                break;
            }
            case HTML: {
                exporter = new HtmlExporter();
                SimpleHtmlExporterConfiguration configuration
                        = new SimpleHtmlExporterConfiguration();
                exporter.setConfiguration(configuration);
                break;
            }
            case CSV: {
                exporter = new JRCsvExporter();
                SimpleCsvExporterConfiguration configuration
                        = new SimpleCsvExporterConfiguration();
                exporter.setConfiguration(configuration);
                break;
            }
            default:
                break;
        }

        return exporter;
    }

}
