package com.nunespaz.boletofacil2.infrastructure.pdf;

import com.nunespaz.boletofacil2.application.dto.PdfExtractionData;
import com.nunespaz.boletofacil2.application.dto.PdfGenerationRequest;
import com.nunespaz.boletofacil2.application.port.PdfService;
import com.nunespaz.boletofacil2.domain.valueobject.Endereco;
import java.awt.Color;
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
    private static final BoundingBox ENDERECO_BOUNDING_BOX = new BoundingBox(30f, 750f, 550f, 810f);

    @Override
    public PdfExtractionData extrairDados(String caminhoPdf) {
        try (PDDocument document = PDDocument.load(new File(caminhoPdf))) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setEndPage(1);
            String texto = stripper.getText(document);

            if (texto == null || texto.isBlank()) {
                return null;
            }

            List<String> linhas = Arrays.asList(texto.split("\r?\n"));

            String nomeCliente = null;
            String enderecoCliente = null;
            String vendaParcela = null;
            LocalDate vencimento = null;

            for (int i = 0; i < linhas.size(); i++) {
                String linhaAtual = linhas.get(i).trim();

                if (linhaAtual.contains("Sacado/Cliente")) {
                    if (i + 1 < linhas.size()) {
                        String possivelNome = linhas.get(i + 1).trim();
                        nomeCliente = possivelNome.replaceAll("R\\$\\s*\\d+([.,]\\d{2})?", "").trim();
                    }
                    if (i + 2 < linhas.size()) {
                        String enderecoLinha = linhas.get(i + 2).trim();
                        if (enderecoLinha.endsWith("-") && i + 3 < linhas.size()) {
                            enderecoLinha = enderecoLinha.substring(0, enderecoLinha.length() - 1).trim() + " " + linhas.get(i + 3).trim();
                        }
                        enderecoCliente = enderecoLinha;
                    }
                }

                if (linhaAtual.contains("Instruções") && i + 1 < linhas.size()) {
                    vendaParcela = linhas.get(i + 1).trim();
                }

                if (linhaAtual.contains("Vencimento") && i + 1 < linhas.size()) {
                    String dataTexto = linhas.get(i + 1).replaceAll("[^0-9/]", "").trim();
                    vencimento = parseData(dataTexto);
                }
            }

            if (nomeCliente == null || enderecoCliente == null || vencimento == null) {
                return null;
            }

            Endereco endereco = criarEnderecoAPartirDaLinha(enderecoCliente);
            String enderecoFormatadoOriginal = formatarEnderecoParaPdf(enderecoCliente);
            return new PdfExtractionData(nomeCliente, endereco, vendaParcela, vencimento, enderecoFormatadoOriginal);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao ler PDF", e);
        }
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
        String bairro = complemento.isEmpty() ? "Centro" : complemento;
        return new Endereco(logradouro, numero, complemento, bairro, "00000-000", "Cidade", "UF");
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

            PDPage primeiraPagina = original.getPage(0);
            primeiraPagina.setRotation(180);
            limparAreaDeEndereco(original, primeiraPagina);

            PDPage paginaEndereco = new PDPage(PDRectangle.A4);
            document.addPage(paginaEndereco);
            escreverEnderecoNaPagina(request, document, paginaEndereco);

            document.importPage(primeiraPagina);

            document.save(destino);
        }
    }

    private void escreverEnderecoNaPagina(PdfGenerationRequest request, PDDocument document, PDPage pagina) throws IOException {
        try (PDPageContentStream contentStream = new PDPageContentStream(document, pagina)) {
            PDType1Font fonte = PDType1Font.HELVETICA;
            int tamanhoFonte = 10;
            float xTexto = 25;
            float yTexto = 450;
            float larguraMaxima = 510;

            contentStream.beginText();
            contentStream.setFont(fonte, tamanhoFonte);
            contentStream.newLineAtOffset(xTexto, yTexto);

            for (String linha : montarLinhasDeEndereco(request)) {
                String linhaAjustada = limitarLargura(linha, fonte, tamanhoFonte, larguraMaxima);
                contentStream.showText(linhaAjustada);
                contentStream.newLineAtOffset(0, -15);
            }

            contentStream.endText();
        }
    }

    private void limparAreaDeEndereco(PDDocument document, PDPage page) throws IOException {
        BoundingBox areaAlvo = ENDERECO_BOUNDING_BOX.ajustarParaRotacao(page.getRotation(), page.getMediaBox());
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
            contentStream.setStrokingColor(Color.WHITE);
            contentStream.setNonStrokingColor(Color.WHITE);
            contentStream.addRect(areaAlvo.x0, areaAlvo.y0, areaAlvo.largura(), areaAlvo.altura());
            contentStream.fill();
        }
    }

    private List<String> montarLinhasDeEndereco(PdfGenerationRequest request) {
        String enderecoPreparado = request.getEnderecoFormatado();
        if (enderecoPreparado.contains("-")) {
            int indiceSeparador = enderecoPreparado.indexOf('-');
            String primeiraParte = enderecoPreparado.substring(0, indiceSeparador).trim();
            String segundaParte = enderecoPreparado.substring(indiceSeparador + 1).trim();
            enderecoPreparado = primeiraParte + "\n" + segundaParte;
        }
        String textoCompleto = request.getNomeCliente() + "\n" + enderecoPreparado;
        return Arrays.asList(textoCompleto.split("\n"));
    }

    private String limitarLargura(String linha, PDType1Font fonte, int tamanhoFonte, float larguraMaxima) throws IOException {
        String texto = linha;
        while (fonte.getStringWidth(texto) / 1000 * tamanhoFonte > larguraMaxima && !texto.isEmpty()) {
            texto = texto.substring(0, texto.length() - 1);
        }
        return texto;
    }

    private String formatarEnderecoParaPdf(String enderecoCliente) {
        if (enderecoCliente.contains("-")) {
            int indiceSeparador = enderecoCliente.indexOf('-');
            String primeiraParte = enderecoCliente.substring(0, indiceSeparador).trim();
            String segundaParte = enderecoCliente.substring(indiceSeparador + 1).trim();
            return primeiraParte + "\n" + segundaParte;
        }
        return enderecoCliente;
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) {
            return "";
        }
        return texto.substring(0, 1).toUpperCase(LOCALE_PT_BR) + texto.substring(1);
    }

    private record BoundingBox(float x0, float y0, float x1, float y1) {
        BoundingBox ajustarParaRotacao(int rotacao, PDRectangle mediaBox) {
            if (rotacao == 180) {
                return new BoundingBox(mediaBox.getWidth() - x1, mediaBox.getHeight() - y1, mediaBox.getWidth() - x0, mediaBox.getHeight() - y0);
            }
            return this;
        }

        float largura() {
            return x1 - x0;
        }

        float altura() {
            return y1 - y0;
        }
    }
}
