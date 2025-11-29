package com.nunespaz.boletofacil2.infrastructure.pdf;

import com.nunespaz.boletofacil2.application.dto.PdfExtractionData;
import com.nunespaz.boletofacil2.application.dto.PdfGenerationRequest;
import com.nunespaz.boletofacil2.application.port.PdfService;
import com.nunespaz.boletofacil2.domain.valueobject.Endereco;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PdfBoxPdfService implements PdfService {

    private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");

    @Override
    public PdfExtractionData extrairDados(String caminhoPdf) {
        try (PDDocument document = PDDocument.load(new File(caminhoPdf))) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setEndPage(1);
            String texto = stripper.getText(document);
            List<String> linhas = Arrays.stream(texto.split("\r?\n"))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();

            String nome = buscarDepoisDe(linhas, "Sacado/Cliente");
            String enderecoLinha = normalizarEndereco(buscarDepoisDe(linhas, nome));
            String vendaParcela = buscarDepoisDe(linhas, "Instruções");
            LocalDate vencimento = parseData(buscarDepoisDe(linhas, "Vencimento"));

            if (nome == null || enderecoLinha == null || vencimento == null) {
                return null;
            }

            Endereco endereco = criarEnderecoAPartirDaLinha(enderecoLinha);
            return new PdfExtractionData(nome, endereco, vendaParcela, vencimento);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao ler PDF", e);
        }
    }

    private String buscarDepoisDe(List<String> linhas, String marcador) {
        for (int i = 0; i < linhas.size(); i++) {
            if (linhas.get(i).contains(marcador) && i + 1 < linhas.size()) {
                return linhas.get(i + 1);
            }
        }
        return null;
    }

    private String normalizarEndereco(String linha) {
        if (linha == null) {
            return null;
        }
        return linha.replaceAll("R\\$\\s*\\d+[.,]\\d{2}?", "").trim();
    }

    private LocalDate parseData(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/uuuu");
            return LocalDate.parse(texto.replaceAll("[^0-9/]", "").trim(), formatter);
        } catch (Exception e) {
            return null;
        }
    }

    private Endereco criarEnderecoAPartirDaLinha(String enderecoLinha) {
        String[] partes = enderecoLinha.split("-");
        String primeira = partes[0].trim();
        String complemento = partes.length > 1 ? partes[1].trim() : "";
        String[] logradouroNumero = primeira.split(",");
        String logradouro = logradouroNumero.length > 0 ? logradouroNumero[0].trim() : primeira;
        String numero = logradouroNumero.length > 1 ? logradouroNumero[1].trim() : "s/n";
        String bairro = complemento.isEmpty() ? "" : complemento;
        return new Endereco(logradouro, numero, complemento, bairro.isEmpty() ? "Centro" : bairro, "00000-000", "Cidade", "UF");
    }

    @Override
    public String gerarPdfFinal(PdfGenerationRequest request) {
        try {
            String caminhoDestino = construirCaminhoDestino(request);
            gerarPdfComEndereco(request, caminhoDestino);
            return caminhoDestino;
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao gerar PDF final", e);
        }
    }

    private String construirCaminhoDestino(PdfGenerationRequest request) {
        LocalDate data = request.getDataVencimento();
        String mes = data.getMonth().getDisplayName(TextStyle.FULL, LOCALE_PT_BR);
        String pasta = "Documentos/Boletos/" + data.getYear() + "/" + capitalizar(mes);
        new File(pasta).mkdirs();

        String dataFormatada = data.format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
        String nomeArquivo = request.getNomeCliente() + " - " + dataFormatada;
        if (!request.getVendaParcela().isBlank()) {
            nomeArquivo += " - " + request.getVendaParcela().replace("/", "-");
        }
        return pasta + "/" + nomeArquivo + ".pdf";
    }

    private void gerarPdfComEndereco(PdfGenerationRequest request, String destino) throws IOException {
        try (PDDocument document = new PDDocument();
             PDDocument original = PDDocument.load(new File(request.getCaminhoOriginal()))) {

            PDPage paginaEndereco = new PDPage(PDRectangle.A4);
            document.addPage(paginaEndereco);
            escreverEnderecoNaPagina(request, document, paginaEndereco);

            PDFMergerUtility merger = new PDFMergerUtility();
            merger.appendDocument(document, original);
            merger.setDestinationFileName(destino);
            merger.mergeDocuments(null);
        }
    }

    private void escreverEnderecoNaPagina(PdfGenerationRequest request, PDDocument document, PDPage pagina) throws IOException {
        try (PDPageContentStream contentStream = new PDPageContentStream(document, pagina)) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(25, 750);
            contentStream.showText(request.getNomeCliente());
            contentStream.newLineAtOffset(0, -15);
            for (String linha : request.getEnderecoFormatado().split("\n")) {
                contentStream.showText(linha);
                contentStream.newLineAtOffset(0, -15);
            }
            contentStream.endText();
        }
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) {
            return "";
        }
        return texto.substring(0, 1).toUpperCase(LOCALE_PT_BR) + texto.substring(1);
    }
}
