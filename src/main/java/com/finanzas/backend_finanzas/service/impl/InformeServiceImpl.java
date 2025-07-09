package com.finanzas.backend_finanzas.service.impl;

import com.finanzas.backend_finanzas.entity.Transaccion;
import com.finanzas.backend_finanzas.entity.Usuario;
import com.finanzas.backend_finanzas.repository.TransaccionRepository;
import com.finanzas.backend_finanzas.repository.UsuarioRepository;
import com.finanzas.backend_finanzas.service.InformeService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;


import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
@RequiredArgsConstructor
public class InformeServiceImpl implements InformeService {

    private final UsuarioRepository usuarioRepository;
    private final TransaccionRepository transaccionRepository;

    
    private static class MovimientoFinanciero {
        String nombre;
        BigDecimal valor;
        LocalDateTime fecha;
        String descripcion;
        String nota;

        public MovimientoFinanciero(String nombre, BigDecimal valor, LocalDateTime fecha, String descripcion, String nota) {
            this.nombre = nombre;
            this.valor = valor;
            this.fecha = fecha;
            this.descripcion = descripcion;
            this.nota = nota;
        }
    }

    private Usuario getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Usuario no autenticado");
        }
        
        String username = authentication.getName();
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + username));
    }

    private List<MovimientoFinanciero> obtenerMovimientosPorTipoYFechas(
            Integer usuarioId, String tipo, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Transaccion> transacciones = transaccionRepository
            .findByUsuarioIdAndFechaBetween(usuarioId, fechaInicio, fechaFin);

        return transacciones.stream()
        .filter(t -> t.getCategoria().getTipo().equalsIgnoreCase(tipo))
        .map(t -> new MovimientoFinanciero(
            t.getCategoria().getNombre(),
            t.getMonto(),
            t.getFecha(),
            t.getDescripcion(),
            t.getNota()
        ))
        .toList();
    }

    @Override
    public byte[] generarInformePdf(LocalDateTime fechaInicio, LocalDateTime fechaFin) {

        Usuario usuarioLogin = getAuthenticatedUser();

        

        List<MovimientoFinanciero> ingresos = obtenerMovimientosPorTipoYFechas(
            usuarioLogin.getId(), "I", fechaInicio, fechaFin);
        List<MovimientoFinanciero> gastos = obtenerMovimientosPorTipoYFechas(
            usuarioLogin.getId(), "G", fechaInicio, fechaFin);


        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph("INFORME FINANCIERO DE: " + usuarioLogin.getNombre()));
            document.add(new Paragraph("Periodo: " + fechaInicio + " a " + fechaFin));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("INGRESOS:"));
            for (MovimientoFinanciero ingreso : ingresos) {
                document.add(new Paragraph(String.format("%s: $%,.2f (Fecha: %s)\nDescripción: %s\nNota: %s\n",
                        ingreso.nombre, ingreso.valor, ingreso.fecha, ingreso.descripcion, ingreso.nota)));
                document.add(new Paragraph("____________________________________"));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("GASTOS:"));
            for (MovimientoFinanciero gasto : gastos) {
                document.add(new Paragraph(String.format("%s: $%,.2f (Fecha: %s)\nDescripción: %s\nNota: %s\n",
                        gasto.nombre, gasto.valor, gasto.fecha, gasto.descripcion, gasto.nota)));
                document.add(new Paragraph("____________________________________"));
            }

            BigDecimal totalIngresos = ingresos.stream()
                .map(i -> i.valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalGastos = gastos.stream()
                .map(g -> g.valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal balance = totalIngresos.subtract(totalGastos);

            document.add(new Paragraph(String.format("\nTOTAL INGRESOS: $%,.2f", totalIngresos)));
            document.add(new Paragraph(String.format("TOTAL GASTOS: $%,.2f", totalGastos)));
            document.add(new Paragraph(String.format("BALANCE: $%,.2f", balance)));

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el informe PDF", e);
        }
    }

    @Override
    public byte[] generarInformeExcel(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        
        Usuario usuarioLogin = getAuthenticatedUser();

        List<MovimientoFinanciero> ingresos = obtenerMovimientosPorTipoYFechas(
            usuarioLogin.getId(), "I", fechaInicio, fechaFin);
        List<MovimientoFinanciero> gastos = obtenerMovimientosPorTipoYFechas(
            usuarioLogin.getId(), "G", fechaInicio, fechaFin);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Informe");

            int rowIdx = 0;
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("INFORME FINANCIERO DE: " + usuarioLogin.getNombre());
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Periodo");
            row.createCell(1).setCellValue(fechaInicio + " a " + fechaFin);

            rowIdx++; // Espacio

            // Ingresos
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("INGRESOS");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Nombre");
            row.createCell(1).setCellValue("Valor");
            row.createCell(2).setCellValue("Fecha");
            row.createCell(3).setCellValue("Descripción");
            row.createCell(4).setCellValue("Nota");
            for (MovimientoFinanciero ingreso : ingresos) {
                row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(ingreso.nombre);
                row.createCell(1).setCellValue(ingreso.valor.toString());
                row.createCell(2).setCellValue(ingreso.fecha.toString());
                row.createCell(3).setCellValue(ingreso.descripcion);
                row.createCell(4).setCellValue(ingreso.nota);
            }

            rowIdx++; // Espacio

            // Gastos
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("GASTOS");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Nombre");
            row.createCell(1).setCellValue("Valor");
            row.createCell(2).setCellValue("Fecha");
            row.createCell(3).setCellValue("Descripción");
            row.createCell(4).setCellValue("Nota");
            for (MovimientoFinanciero gasto : gastos) {
                row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(gasto.nombre);
                row.createCell(1).setCellValue(gasto.valor.toString());
                row.createCell(2).setCellValue(gasto.fecha.toString());
                row.createCell(3).setCellValue(gasto.descripcion);
                row.createCell(4).setCellValue(gasto.nota);
            }

            rowIdx++; // Espacio

            BigDecimal totalIngresos = ingresos.stream()
                .map(i -> i.valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalGastos = gastos.stream()
                .map(g -> g.valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal balance = totalIngresos.subtract(totalGastos);

            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("TOTAL INGRESOS");
            row.createCell(1).setCellValue(totalIngresos.toString());

            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("TOTAL GASTOS");
            row.createCell(1).setCellValue(totalGastos.toString());

            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("BALANCE");
            row.createCell(1).setCellValue(balance.toString());

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el informe Excel", e);
        }
    }
}