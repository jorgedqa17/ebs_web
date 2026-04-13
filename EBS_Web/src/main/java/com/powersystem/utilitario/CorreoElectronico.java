/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.utilitario;

import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 *
 * @author Jorge GBSYS
 */
public class CorreoElectronico {

    private final Properties properties = new Properties();

    private String password;

    private Session session;

    public void sendEmailRespuestaHacienda(String asunto, String cuerpo, byte[] arregloBytes, String nombreArchivo,
            List<String> correos, byte[] arregloXML, byte[] arregloRespuesta) {

        // Sender's email ID needs to be mentioned
        String from = "noreply@searmedica.com";
        final String username = "noreply@searmedica.com";//change accordingly
        final String password = "c42sxamdk";//change accordingly
        String host = "mail.searmedica.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMultipart multiParte = new MimeMultipart();
            BodyPart texto = new MimeBodyPart();
            texto.setText(cuerpo);
            multiParte.addBodyPart(texto);

            if (arregloBytes != null) {
                DataSource dtEmail = new ByteArrayDataSource(arregloBytes, "application/pdf");

                BodyPart adjunto = new MimeBodyPart();
                adjunto.setDataHandler(new DataHandler(dtEmail));
                adjunto.setFileName("Factura-" + asunto + ".pdf");
                multiParte.addBodyPart(adjunto);
            }

            if (arregloXML != null) {
                DataSource dtEmail = new ByteArrayDataSource(arregloXML, "application/xml");

                BodyPart adjunto = new MimeBodyPart();
                adjunto.setDataHandler(new DataHandler(dtEmail));
                adjunto.setFileName("ComprobanteElectronico-" + asunto + ".xml");
                multiParte.addBodyPart(adjunto);
            }
            if (arregloRespuesta != null) {
                DataSource dtEmail = new ByteArrayDataSource(arregloRespuesta, "application/xml");

                BodyPart adjunto = new MimeBodyPart();
                adjunto.setDataHandler(new DataHandler(dtEmail));
                adjunto.setFileName("RespuestaHacienda-" + asunto + ".xml");
                multiParte.addBodyPart(adjunto);
            }

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            for (String correosElectronico : correos) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(correosElectronico));
            }
            message.setContent(multiParte);
            message.setSubject("Respuesta de Hacienda Comprobante Electrónico - " + asunto);

            Transport t = session.getTransport("smtp");
            t.connect("noreply@searmedica.com", "c42sxamdk");
            t.sendMessage(message, message.getAllRecipients());
            t.close();
            System.out.println(" RESPUESTA FACTURA  - Sent message successfully TO " + asunto);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendEmailFacturaPDF(String asunto, String cuerpo, byte[] arregloBytes, String nombreArchivo,
            List<String> correos, byte[] arregloXML, byte[] arregloRespuesta) {

        // Sender's email ID needs to be mentioned
        String from = "noreply@searmedica.com";
        final String username = "noreply@searmedica.com";//change accordingly
        final String password = "c42sxamdk";//change accordingly
        String host = "mail.searmedica.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMultipart multiParte = new MimeMultipart();
            BodyPart texto = new MimeBodyPart();
            texto.setText(cuerpo);
            multiParte.addBodyPart(texto);

            if (arregloBytes != null) {
                DataSource dtEmail = new ByteArrayDataSource(arregloBytes, "application/pdf");

                BodyPart adjunto = new MimeBodyPart();
                adjunto.setDataHandler(new DataHandler(dtEmail));
                adjunto.setFileName("Factura-" + asunto + ".pdf");
                multiParte.addBodyPart(adjunto);
            }

            if (arregloXML != null) {
                DataSource dtEmail = new ByteArrayDataSource(arregloXML, "application/xml");

                BodyPart adjunto = new MimeBodyPart();
                adjunto.setDataHandler(new DataHandler(dtEmail));
                adjunto.setFileName("ComprobanteElectronico-" + asunto + ".xml");
                multiParte.addBodyPart(adjunto);
            }
            if (arregloRespuesta != null) {
                DataSource dtEmail = new ByteArrayDataSource(arregloRespuesta, "application/xml");

                BodyPart adjunto = new MimeBodyPart();
                adjunto.setDataHandler(new DataHandler(dtEmail));
                adjunto.setFileName("RespuestaHacienda-" + asunto + ".xml");
                multiParte.addBodyPart(adjunto);
            }

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            for (String correosElectronico : correos) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(correosElectronico));
            }
            message.setContent(multiParte);
            message.setSubject("Factura-" + asunto);

            Transport t = session.getTransport("smtp");
            t.connect("noreply@searmedica.com", "c42sxamdk");
            t.sendMessage(message, message.getAllRecipients());
            t.close();
            System.out.println("FACTURA - Sent message successfully TO " + asunto);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendEmailNotaCredito(String asunto, String cuerpo, byte[] arregloBytes, String nombreArchivo,
            List<String> correos, byte[] arregloXML, byte[] arregloRespuesta) {

        // Sender's email ID needs to be mentioned
        String from = "noreply@searmedica.com";
        final String username = "noreply@searmedica.com";//change accordingly
        final String password = "c42sxamdk";//change accordingly
        String host = "mail.searmedica.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMultipart multiParte = new MimeMultipart();
            BodyPart texto = new MimeBodyPart();
            texto.setText(cuerpo);
            multiParte.addBodyPart(texto);

            if (arregloBytes != null) {
                DataSource dtEmail = new ByteArrayDataSource(arregloBytes, "application/pdf");

                BodyPart adjunto = new MimeBodyPart();
                adjunto.setDataHandler(new DataHandler(dtEmail));
                adjunto.setFileName("Nota de Crédito -" + asunto + ".pdf");
                multiParte.addBodyPart(adjunto);
            }

            if (arregloXML != null) {
                DataSource dtEmail = new ByteArrayDataSource(arregloXML, "application/xml");

                BodyPart adjunto = new MimeBodyPart();
                adjunto.setDataHandler(new DataHandler(dtEmail));
                adjunto.setFileName("ComprobanteElectronico-" + asunto + ".xml");
                multiParte.addBodyPart(adjunto);
            }
            if (arregloRespuesta != null) {
                DataSource dtEmail = new ByteArrayDataSource(arregloRespuesta, "application/xml");

                BodyPart adjunto = new MimeBodyPart();
                adjunto.setDataHandler(new DataHandler(dtEmail));
                adjunto.setFileName("RespuestaHacienda-" + asunto + ".xml");
                multiParte.addBodyPart(adjunto);
            }

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            for (String correosElectronico : correos) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(correosElectronico));
            }
            message.setContent(multiParte);
            message.setSubject("Nota de Crédito-" + asunto);

            Transport t = session.getTransport("smtp");
            t.connect("noreply@searmedica.com", "c42sxamdk");
            t.sendMessage(message, message.getAllRecipients());
            t.close();
            System.out.println("NOTA DE CREDITO - Sent message successfully TO " + asunto);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendEmailConfirmacion(String asunto, String cuerpo, byte[] arregloBytes, String nombreArchivo,
            List<String> correos, byte[] arregloXML, byte[] arregloRespuesta, String numeroConsecutivoConfirmacion) {

        // Sender's email ID needs to be mentioned
        String from = "noreply@searmedica.com";
        final String username = "noreply@searmedica.com";//change accordingly
        final String password = "c42sxamdk";//change accordingly
        String host = "mail.searmedica.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMultipart multiParte = new MimeMultipart();
            BodyPart texto = new MimeBodyPart();
            texto.setText(cuerpo);
            multiParte.addBodyPart(texto);

            if (arregloBytes != null) {
                DataSource dtEmail = new ByteArrayDataSource(arregloBytes, "application/pdf");

                BodyPart adjunto = new MimeBodyPart();
                adjunto.setDataHandler(new DataHandler(dtEmail));
                adjunto.setFileName("Confirmacion_" + numeroConsecutivoConfirmacion + ".pdf");
                multiParte.addBodyPart(adjunto);
            }

            if (arregloXML != null) {
                DataSource dtEmail = new ByteArrayDataSource(arregloXML, "application/xml");

                BodyPart adjunto = new MimeBodyPart();
                adjunto.setDataHandler(new DataHandler(dtEmail));
                adjunto.setFileName("ComprobanteElectronico-" + numeroConsecutivoConfirmacion + ".xml");
                multiParte.addBodyPart(adjunto);
            }
            if (arregloRespuesta != null) {
                DataSource dtEmail = new ByteArrayDataSource(arregloRespuesta, "application/xml");

                BodyPart adjunto = new MimeBodyPart();
                adjunto.setDataHandler(new DataHandler(dtEmail));
                adjunto.setFileName("RespuestaHacienda-" + numeroConsecutivoConfirmacion + ".xml");
                multiParte.addBodyPart(adjunto);
            }

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            for (String correosElectronico : correos) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(correosElectronico));
            }
            message.setContent(multiParte);
            message.setSubject(asunto + numeroConsecutivoConfirmacion);

            Transport t = session.getTransport("smtp");
            t.connect("noreply@searmedica.com", "c42sxamdk");
            t.sendMessage(message, message.getAllRecipients());
            t.close();
            System.out.println("CONFIRMACION DE COMPROBANTES - Sent message successfully TO " + asunto + numeroConsecutivoConfirmacion);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

}
