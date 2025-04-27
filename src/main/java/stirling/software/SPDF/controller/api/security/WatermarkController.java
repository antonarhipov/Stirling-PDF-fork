package stirling.software.SPDF.controller.api.security;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.pixee.security.Filenames;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import stirling.software.SPDF.model.api.security.AddWatermarkRequest;
import stirling.software.SPDF.service.CustomPDFDocumentFactory;
import stirling.software.SPDF.utils.PdfUtils;
import stirling.software.SPDF.utils.WebResponseUtils;

@RestController
@RequestMapping("/api/v1/security")
@Tag(name = "Security", description = "Security APIs")
@RequiredArgsConstructor
public class WatermarkController {

    private final CustomPDFDocumentFactory pdfDocumentFactory;

    @PostMapping(consumes = "multipart/form-data", value = "/add-watermark")
    @Operation(
            summary = "Add watermark to a PDF file",
            description =
                    "This endpoint adds a watermark to a given PDF file. Users can specify the"
                            + " watermark type (text or image), rotation, opacity, width spacer, and"
                            + " height spacer. Input:PDF Output:PDF Type:SISO")
    public ResponseEntity<byte[]> addWatermark(@ModelAttribute AddWatermarkRequest request)
            throws IOException, Exception {
        MultipartFile pdfFile = request.getFileInput();
        String watermarkType = request.getWatermarkType();
        String watermarkText = request.getWatermarkText();
        MultipartFile watermarkImage = request.getWatermarkImage();
        String alphabet = request.getAlphabet();
        float fontSize = request.getFontSize();
        float rotation = request.getRotation();
        float opacity = request.getOpacity();
        int widthSpacer = request.getWidthSpacer();
        int heightSpacer = request.getHeightSpacer();
        String customColor = request.getCustomColor();
        boolean convertPdfToImage = request.isConvertPDFToImage();

        // Phase 2: Extract new parameters
        boolean randomPosition = request.isRandomPosition();
        boolean randomOrientation = request.isRandomOrientation();
        float minRotation = request.getMinRotation();
        float maxRotation = request.getMaxRotation();
        boolean randomFont = request.isRandomFont();
        List<String> selectedFonts = request.getSelectedFonts();
        boolean randomFontSize = request.isRandomFontSize();
        float minFontSize = request.getMinFontSize();
        float maxFontSize = request.getMaxFontSize();

        // Phase 3: Extract advanced text rendering parameters
        boolean randomLetterColor = request.isRandomLetterColor();
        List<String> selectedColors = request.getSelectedColors();
        boolean randomLetterOrientation = request.isRandomLetterOrientation();
        float minLetterRotation = request.getMinLetterRotation();
        float maxLetterRotation = request.getMaxLetterRotation();
        boolean mixedStyling = request.isMixedStyling();

        // Phase 4: Extract additional features parameters
        int watermarkCount = request.getWatermarkCount();
        boolean randomShading = request.isRandomShading();
        float minOpacity = request.getMinOpacity();
        float maxOpacity = request.getMaxOpacity();
        boolean randomMirroring = request.isRandomMirroring();
        float mirrorProbability = request.getMirrorProbability();

        // Load the input PDF
        PDDocument document = pdfDocumentFactory.load(pdfFile);

        // Create a page in the document
        for (PDPage page : document.getPages()) {

            // Get the page's content stream
            PDPageContentStream contentStream =
                    new PDPageContentStream(
                            document, page, PDPageContentStream.AppendMode.APPEND, true, true);

            // Set transparency (handle random shading if enabled)
            PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();

            // Apply random shading if enabled
            float actualOpacity = opacity;
            if (randomShading) {
                Random random = new Random();
                actualOpacity = minOpacity + random.nextFloat() * (maxOpacity - minOpacity);
            }

            graphicsState.setNonStrokingAlphaConstant(actualOpacity);
            contentStream.setGraphicsStateParameters(graphicsState);

            if ("text".equalsIgnoreCase(watermarkType)) {
                addTextWatermark(
                        contentStream,
                        watermarkText,
                        document,
                        page,
                        rotation,
                        widthSpacer,
                        heightSpacer,
                        fontSize,
                        alphabet,
                        customColor,
                        randomPosition,
                        randomOrientation,
                        minRotation,
                        maxRotation,
                        randomFont,
                        selectedFonts,
                        randomFontSize,
                        minFontSize,
                        maxFontSize,
                        randomLetterColor,
                        selectedColors,
                        randomLetterOrientation,
                        minLetterRotation,
                        maxLetterRotation,
                        mixedStyling,
                        watermarkCount,
                        randomMirroring,
                        mirrorProbability);
            } else if ("image".equalsIgnoreCase(watermarkType)) {
                addImageWatermark(
                        contentStream,
                        watermarkImage,
                        document,
                        page,
                        rotation,
                        widthSpacer,
                        heightSpacer,
                        fontSize,
                        randomPosition,
                        randomOrientation,
                        minRotation,
                        maxRotation,
                        watermarkCount,
                        randomMirroring,
                        mirrorProbability);
            }

            // Close the content stream
            contentStream.close();
        }

        if (convertPdfToImage) {
            PDDocument convertedPdf = PdfUtils.convertPdfToPdfImage(document);
            document.close();
            document = convertedPdf;
        }

        return WebResponseUtils.pdfDocToWebResponse(
                document,
                Filenames.toSimpleFileName(pdfFile.getOriginalFilename())
                                .replaceFirst("[.][^.]+$", "")
                        + "_watermarked.pdf");
    }

    private void addTextWatermark(
            PDPageContentStream contentStream,
            String watermarkText,
            PDDocument document,
            PDPage page,
            float rotation,
            int widthSpacer,
            int heightSpacer,
            float fontSize,
            String alphabet,
            String colorString,
            boolean randomPosition,
            boolean randomOrientation,
            float minRotation,
            float maxRotation,
            boolean randomFont,
            List<String> selectedFonts,
            boolean randomFontSize,
            float minFontSize,
            float maxFontSize,
            boolean randomLetterColor,
            List<String> selectedColors,
            boolean randomLetterOrientation,
            float minLetterRotation,
            float maxLetterRotation,
            boolean mixedStyling,
            int watermarkCount,
            boolean randomMirroring,
            float mirrorProbability)
            throws IOException {
        Random random = new Random();

        // Handle random orientation if enabled
        float actualRotation = rotation;
        if (randomOrientation) {
            actualRotation = minRotation + random.nextFloat() * (maxRotation - minRotation);
        }

        // Handle random font size if enabled
        float actualFontSize = fontSize;
        if (randomFontSize) {
            actualFontSize = minFontSize + random.nextFloat() * (maxFontSize - minFontSize);
        }

        String resourceDir = "";
        PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        // Handle random font if enabled
        if (randomFont && selectedFonts != null && !selectedFonts.isEmpty()) {
            String selectedFont = selectedFonts.get(random.nextInt(selectedFonts.size()));
            switch (selectedFont) {
                case "helvetica":
                    font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                    break;
                case "times":
                    font = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
                    break;
                case "courier":
                    font = new PDType1Font(Standard14Fonts.FontName.COURIER);
                    break;
                default:
                    // Use default font handling
                    switch (alphabet) {
                        case "arabic":
                            resourceDir = "static/fonts/NotoSansArabic-Regular.ttf";
                            break;
                        case "japanese":
                            resourceDir = "static/fonts/Meiryo.ttf";
                            break;
                        case "korean":
                            resourceDir = "static/fonts/malgun.ttf";
                            break;
                        case "chinese":
                            resourceDir = "static/fonts/SimSun.ttf";
                            break;
                        case "roman":
                        default:
                            resourceDir = "static/fonts/NotoSans-Regular.ttf";
                            break;
                    }
            }
        } else {
            // Use default font handling
            switch (alphabet) {
                case "arabic":
                    resourceDir = "static/fonts/NotoSansArabic-Regular.ttf";
                    break;
                case "japanese":
                    resourceDir = "static/fonts/Meiryo.ttf";
                    break;
                case "korean":
                    resourceDir = "static/fonts/malgun.ttf";
                    break;
                case "chinese":
                    resourceDir = "static/fonts/SimSun.ttf";
                    break;
                case "roman":
                default:
                    resourceDir = "static/fonts/NotoSans-Regular.ttf";
                    break;
            }
        }

        if (!"".equals(resourceDir)) {
            ClassPathResource classPathResource = new ClassPathResource(resourceDir);
            String fileExtension = resourceDir.substring(resourceDir.lastIndexOf("."));
            File tempFile = Files.createTempFile("NotoSansFont", fileExtension).toFile();
            try (InputStream is = classPathResource.getInputStream();
                    FileOutputStream os = new FileOutputStream(tempFile)) {
                IOUtils.copy(is, os);
                font = PDType0Font.load(document, tempFile);
            } finally {
                if (tempFile != null) Files.deleteIfExists(tempFile.toPath());
            }
        }

        contentStream.setFont(font, actualFontSize);

        Color redactColor;
        try {
            if (!colorString.startsWith("#")) {
                colorString = "#" + colorString;
            }
            redactColor = Color.decode(colorString);
        } catch (NumberFormatException e) {
            redactColor = Color.LIGHT_GRAY;
        }
        contentStream.setNonStrokingColor(redactColor);

        String[] textLines = watermarkText.split("\\\\n");
        float maxLineWidth = 0;

        for (int i = 0; i < textLines.length; ++i) {
            maxLineWidth = Math.max(maxLineWidth, font.getStringWidth(textLines[i]));
        }

        // Set size and location of text watermark
        float watermarkWidth = widthSpacer + maxLineWidth * actualFontSize / 1000;
        float watermarkHeight = heightSpacer + actualFontSize * textLines.length;
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();

        // Calculating the new width and height depending on the angle.
        float radians = (float) Math.toRadians(actualRotation);
        float newWatermarkWidth =
                (float)
                        (Math.abs(watermarkWidth * Math.cos(radians))
                                + Math.abs(watermarkHeight * Math.sin(radians)));
        float newWatermarkHeight =
                (float)
                        (Math.abs(watermarkWidth * Math.sin(radians))
                                + Math.abs(watermarkHeight * Math.cos(radians)));

        // Check if any advanced text rendering features are enabled
        boolean useAdvancedRendering = randomLetterColor || randomLetterOrientation || mixedStyling;

        if (randomPosition) {
            // For random positioning, add watermarks based on watermarkCount
            int numWatermarks = (watermarkCount > 0) ? watermarkCount : 1;

            for (int i = 0; i < numWatermarks; i++) {
                // Generate new random parameters for each watermark instance
                float instanceRotation = rotation;
                if (randomOrientation) {
                    instanceRotation =
                            minRotation + random.nextFloat() * (maxRotation - minRotation);
                }

                float instanceFontSize = fontSize;
                if (randomFontSize) {
                    instanceFontSize =
                            minFontSize + random.nextFloat() * (maxFontSize - minFontSize);
                }

                PDFont instanceFont = font;
                if (randomFont && selectedFonts != null && !selectedFonts.isEmpty()) {
                    String selectedFont = selectedFonts.get(random.nextInt(selectedFonts.size()));
                    switch (selectedFont) {
                        case "helvetica":
                            instanceFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                            break;
                        case "times":
                            instanceFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
                            break;
                        case "courier":
                            instanceFont = new PDType1Font(Standard14Fonts.FontName.COURIER);
                            break;
                        default:
                            instanceFont = font; // Use the default font
                            break;
                    }
                }

                // Recalculate watermark dimensions based on instance-specific parameters
                float instanceMaxLineWidth = 0;
                for (int j = 0; j < textLines.length; ++j) {
                    instanceMaxLineWidth =
                            Math.max(
                                    instanceMaxLineWidth,
                                    instanceFont.getStringWidth(textLines[j]));
                }

                float instanceWatermarkWidth =
                        widthSpacer + instanceMaxLineWidth * instanceFontSize / 1000;
                float instanceWatermarkHeight = heightSpacer + instanceFontSize * textLines.length;

                // Calculating the new width and height depending on the angle
                float instanceRadians = (float) Math.toRadians(instanceRotation);
                float instanceNewWatermarkWidth =
                        (float)
                                (Math.abs(instanceWatermarkWidth * Math.cos(instanceRadians))
                                        + Math.abs(
                                                instanceWatermarkHeight
                                                        * Math.sin(instanceRadians)));
                float instanceNewWatermarkHeight =
                        (float)
                                (Math.abs(instanceWatermarkWidth * Math.sin(instanceRadians))
                                        + Math.abs(
                                                instanceWatermarkHeight
                                                        * Math.cos(instanceRadians)));

                float x = random.nextFloat() * (pageWidth - instanceNewWatermarkWidth);
                float y = random.nextFloat() * (pageHeight - instanceNewWatermarkHeight);

                // Apply random mirroring if enabled
                boolean applyMirroring =
                        randomMirroring && (random.nextFloat() * 100 < mirrorProbability);

                // Set font and color for this instance
                contentStream.setFont(instanceFont, instanceFontSize);

                Color instanceRedactColor = redactColor;
                if (!useAdvancedRendering
                        && randomLetterColor
                        && selectedColors != null
                        && !selectedColors.isEmpty()) {
                    String colorStr = selectedColors.get(random.nextInt(selectedColors.size()));
                    try {
                        if (!colorStr.startsWith("#")) {
                            colorStr = "#" + colorStr;
                        }
                        instanceRedactColor = Color.decode(colorStr);
                        contentStream.setNonStrokingColor(instanceRedactColor);
                    } catch (NumberFormatException e) {
                        // Keep default color
                    }
                } else {
                    contentStream.setNonStrokingColor(redactColor);
                }

                if (useAdvancedRendering) {
                    // Use advanced text rendering for individual letters
                    // Save the current graphics state for mirroring
                    contentStream.saveGraphicsState();

                    // Apply mirroring if needed
                    if (applyMirroring) {
                        // Create a mirroring transformation
                        contentStream.transform(Matrix.getScaleInstance(-1, 1));
                        // Adjust x position for mirroring
                        x = -x - instanceNewWatermarkWidth;
                    }

                    renderAdvancedText(
                            contentStream,
                            textLines,
                            document,
                            instanceFont,
                            instanceFontSize,
                            x,
                            y,
                            instanceRotation,
                            instanceRedactColor,
                            random,
                            randomLetterColor,
                            selectedColors,
                            randomLetterOrientation,
                            minLetterRotation,
                            maxLetterRotation,
                            mixedStyling,
                            randomFont,
                            selectedFonts,
                            randomFontSize,
                            minFontSize,
                            maxFontSize,
                            alphabet);

                    // Restore the graphics state
                    contentStream.restoreGraphicsState();
                } else {
                    // Use standard text rendering
                    contentStream.beginText();

                    // Apply mirroring if needed
                    if (applyMirroring) {
                        // Create a mirroring transformation matrix (scale by -1 in x direction)
                        contentStream.setTextMatrix(
                                Matrix.getScaleInstance(-1, 1) // Mirror horizontally
                                        .multiply(
                                                Matrix.getRotateInstance(
                                                        (float) Math.toRadians(instanceRotation),
                                                        x,
                                                        y)));
                    } else {
                        contentStream.setTextMatrix(
                                Matrix.getRotateInstance(
                                        (float) Math.toRadians(instanceRotation), x, y));
                    }

                    for (int k = 0; k < textLines.length; ++k) {
                        contentStream.showText(textLines[k]);
                        contentStream.newLineAtOffset(0, -instanceFontSize);
                    }

                    contentStream.endText();
                }
            }
        } else {
            // For grid-based positioning
            // Calculating the number of rows and columns.
            int watermarkRows = (int) (pageHeight / newWatermarkHeight + 1);
            int watermarkCols = (int) (pageWidth / newWatermarkWidth + 1);

            // Calculate total watermarks in the grid
            int totalGridWatermarks = (watermarkRows + 1) * (watermarkCols + 1);

            // If watermarkCount is specified and less than the total grid watermarks,
            // we'll place them randomly within the grid
            boolean useRandomGrid = watermarkCount > 0 && watermarkCount < totalGridWatermarks;

            // Create a 2D array to track which grid positions are filled (for random grid
            // placement)
            boolean[][] filledPositions = null;
            if (useRandomGrid) {
                filledPositions = new boolean[watermarkRows + 1][watermarkCols + 1];
            }

            // Determine how many watermarks to place
            int watermarksToPlace = useRandomGrid ? watermarkCount : totalGridWatermarks;
            int watermarksPlaced = 0;

            // Add the text watermarks
            for (int i = 0; i <= watermarkRows && watermarksPlaced < watermarksToPlace; i++) {
                for (int j = 0; j <= watermarkCols && watermarksPlaced < watermarksToPlace; j++) {
                    // For random grid placement, randomly decide whether to place a watermark at
                    // this position
                    if (useRandomGrid) {
                        // Skip if we've already filled this position
                        if (filledPositions[i][j]) {
                            continue;
                        }

                        // Randomly decide whether to place a watermark here
                        // The probability increases as we get closer to the end to ensure we place
                        // exactly watermarkCount watermarks
                        int remainingPositions =
                                totalGridWatermarks - (i * (watermarkCols + 1) + j);
                        int remainingWatermarks = watermarksToPlace - watermarksPlaced;

                        if (remainingPositions <= remainingWatermarks
                                || random.nextFloat()
                                        < (float) remainingWatermarks / remainingPositions) {
                            filledPositions[i][j] = true;
                        } else {
                            continue; // Skip this position
                        }
                    }

                    // Generate new random parameters for each watermark instance
                    float instanceRotation = rotation;
                    if (randomOrientation) {
                        instanceRotation =
                                minRotation + random.nextFloat() * (maxRotation - minRotation);
                    }

                    float instanceFontSize = fontSize;
                    if (randomFontSize) {
                        instanceFontSize =
                                minFontSize + random.nextFloat() * (maxFontSize - minFontSize);
                    }

                    PDFont instanceFont = font;
                    if (randomFont && selectedFonts != null && !selectedFonts.isEmpty()) {
                        String selectedFont =
                                selectedFonts.get(random.nextInt(selectedFonts.size()));
                        switch (selectedFont) {
                            case "helvetica":
                                instanceFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                                break;
                            case "times":
                                instanceFont =
                                        new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
                                break;
                            case "courier":
                                instanceFont = new PDType1Font(Standard14Fonts.FontName.COURIER);
                                break;
                            default:
                                instanceFont = font; // Use the default font
                                break;
                        }
                    }

                    // Recalculate watermark dimensions based on instance-specific parameters
                    float instanceMaxLineWidth = 0;
                    for (int k = 0; k < textLines.length; ++k) {
                        instanceMaxLineWidth =
                                Math.max(
                                        instanceMaxLineWidth,
                                        instanceFont.getStringWidth(textLines[k]));
                    }

                    float instanceWatermarkWidth =
                            widthSpacer + instanceMaxLineWidth * instanceFontSize / 1000;
                    float instanceWatermarkHeight =
                            heightSpacer + instanceFontSize * textLines.length;

                    // Calculating the new width and height depending on the angle
                    float instanceRadians = (float) Math.toRadians(instanceRotation);
                    float instanceNewWatermarkWidth =
                            (float)
                                    (Math.abs(instanceWatermarkWidth * Math.cos(instanceRadians))
                                            + Math.abs(
                                                    instanceWatermarkHeight
                                                            * Math.sin(instanceRadians)));
                    float instanceNewWatermarkHeight =
                            (float)
                                    (Math.abs(instanceWatermarkWidth * Math.sin(instanceRadians))
                                            + Math.abs(
                                                    instanceWatermarkHeight
                                                            * Math.cos(instanceRadians)));

                    float x = j * instanceNewWatermarkWidth;
                    float y = i * instanceNewWatermarkHeight;

                    // Apply random mirroring if enabled
                    boolean applyMirroring =
                            randomMirroring && (random.nextFloat() * 100 < mirrorProbability);

                    // Set font and color for this instance
                    contentStream.setFont(instanceFont, instanceFontSize);

                    Color instanceRedactColor = redactColor;
                    if (!useAdvancedRendering
                            && randomLetterColor
                            && selectedColors != null
                            && !selectedColors.isEmpty()) {
                        String colorStr = selectedColors.get(random.nextInt(selectedColors.size()));
                        try {
                            if (!colorStr.startsWith("#")) {
                                colorStr = "#" + colorStr;
                            }
                            instanceRedactColor = Color.decode(colorStr);
                            contentStream.setNonStrokingColor(instanceRedactColor);
                        } catch (NumberFormatException e) {
                            // Keep default color
                        }
                    } else {
                        contentStream.setNonStrokingColor(redactColor);
                    }

                    if (useAdvancedRendering) {
                        // Use advanced text rendering for individual letters
                        // Save the current graphics state for mirroring
                        contentStream.saveGraphicsState();

                        // Apply mirroring if needed
                        if (applyMirroring) {
                            // Create a mirroring transformation
                            contentStream.transform(Matrix.getScaleInstance(-1, 1));
                            // Adjust x position for mirroring
                            x = -x - instanceNewWatermarkWidth;
                        }

                        renderAdvancedText(
                                contentStream,
                                textLines,
                                document,
                                instanceFont,
                                instanceFontSize,
                                x,
                                y,
                                instanceRotation,
                                instanceRedactColor,
                                random,
                                randomLetterColor,
                                selectedColors,
                                randomLetterOrientation,
                                minLetterRotation,
                                maxLetterRotation,
                                mixedStyling,
                                randomFont,
                                selectedFonts,
                                randomFontSize,
                                minFontSize,
                                maxFontSize,
                                alphabet);

                        // Restore the graphics state
                        contentStream.restoreGraphicsState();
                    } else {
                        // Use standard text rendering
                        contentStream.beginText();

                        // Apply mirroring if needed
                        if (applyMirroring) {
                            // Create a mirroring transformation matrix (scale by -1 in x direction)
                            contentStream.setTextMatrix(
                                    Matrix.getScaleInstance(-1, 1) // Mirror horizontally
                                            .multiply(
                                                    Matrix.getRotateInstance(
                                                            (float)
                                                                    Math.toRadians(
                                                                            instanceRotation),
                                                            x,
                                                            y)));
                        } else {
                            contentStream.setTextMatrix(
                                    Matrix.getRotateInstance(
                                            (float) Math.toRadians(instanceRotation), x, y));
                        }

                        for (int k = 0; k < textLines.length; ++k) {
                            contentStream.showText(textLines[k]);
                            contentStream.newLineAtOffset(0, -instanceFontSize);
                        }

                        contentStream.endText();
                    }

                    watermarksPlaced++;
                }
            }
        }
    }

    /**
     * Renders text with advanced styling features like random letter color, random letter
     * orientation, and mixed styling.
     */
    private void renderAdvancedText(
            PDPageContentStream contentStream,
            String[] textLines,
            PDDocument document,
            PDFont defaultFont,
            float defaultFontSize,
            float x,
            float y,
            float globalRotation,
            Color defaultColor,
            Random random,
            boolean randomLetterColor,
            List<String> selectedColors,
            boolean randomLetterOrientation,
            float minLetterRotation,
            float maxLetterRotation,
            boolean mixedStyling,
            boolean randomFont,
            List<String> selectedFonts,
            boolean randomFontSize,
            float minFontSize,
            float maxFontSize,
            String alphabet)
            throws IOException {

        // Save the current graphics state
        contentStream.saveGraphicsState();

        // Apply the global rotation and position
        contentStream.transform(
                Matrix.getRotateInstance((float) Math.toRadians(globalRotation), x, y));

        float currentY = 0;

        // Process each line of text
        for (int lineIndex = 0; lineIndex < textLines.length; lineIndex++) {
            String line = textLines[lineIndex];
            float currentX = 0;

            // Process each character in the line
            for (int charIndex = 0; charIndex < line.length(); charIndex++) {
                char c = line.charAt(charIndex);
                String charStr = String.valueOf(c);

                // Begin text operation for this character
                contentStream.beginText();

                // Apply styling based on enabled features
                PDFont fontToUse = defaultFont;
                float fontSizeToUse = defaultFontSize;
                Color colorToUse = defaultColor;
                float letterRotation = 0;

                if (mixedStyling) {
                    // Apply random combination of all styles

                    // Random font if enabled
                    if (randomFont && selectedFonts != null && !selectedFonts.isEmpty()) {
                        String selectedFont =
                                selectedFonts.get(random.nextInt(selectedFonts.size()));
                        switch (selectedFont) {
                            case "helvetica":
                                fontToUse = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                                break;
                            case "times":
                                fontToUse = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
                                break;
                            case "courier":
                                fontToUse = new PDType1Font(Standard14Fonts.FontName.COURIER);
                                break;
                            default:
                                fontToUse = defaultFont;
                                break;
                        }
                    }

                    // Random font size if enabled
                    if (randomFontSize) {
                        fontSizeToUse =
                                minFontSize + random.nextFloat() * (maxFontSize - minFontSize);
                    }

                    // Random color if enabled
                    if (randomLetterColor && selectedColors != null && !selectedColors.isEmpty()) {
                        String colorStr = selectedColors.get(random.nextInt(selectedColors.size()));
                        try {
                            if (!colorStr.startsWith("#")) {
                                colorStr = "#" + colorStr;
                            }
                            colorToUse = Color.decode(colorStr);
                        } catch (NumberFormatException e) {
                            colorToUse = defaultColor;
                        }
                    }

                    // Random letter rotation if enabled
                    if (randomLetterOrientation) {
                        letterRotation =
                                minLetterRotation
                                        + random.nextFloat()
                                                * (maxLetterRotation - minLetterRotation);
                    }
                } else {
                    // Apply individual styling features

                    // Random color if enabled
                    if (randomLetterColor && selectedColors != null && !selectedColors.isEmpty()) {
                        String colorStr = selectedColors.get(random.nextInt(selectedColors.size()));
                        try {
                            if (!colorStr.startsWith("#")) {
                                colorStr = "#" + colorStr;
                            }
                            colorToUse = Color.decode(colorStr);
                        } catch (NumberFormatException e) {
                            colorToUse = defaultColor;
                        }
                    }

                    // Random letter rotation if enabled
                    if (randomLetterOrientation) {
                        letterRotation =
                                minLetterRotation
                                        + random.nextFloat()
                                                * (maxLetterRotation - minLetterRotation);
                    }
                }

                // Set font and font size
                contentStream.setFont(fontToUse, fontSizeToUse);

                // Set color
                contentStream.setNonStrokingColor(colorToUse);

                // Calculate character width for positioning
                float charWidth = fontToUse.getStringWidth(charStr) * fontSizeToUse / 1000;

                // Apply letter rotation if needed
                if (letterRotation != 0) {
                    // Rotate around the center of the character
                    contentStream.setTextMatrix(
                            Matrix.getRotateInstance(
                                    (float) Math.toRadians(letterRotation),
                                    currentX + charWidth / 2,
                                    currentY - fontSizeToUse / 2));
                } else {
                    // No rotation, just position
                    contentStream.setTextMatrix(Matrix.getTranslateInstance(currentX, currentY));
                }

                // Draw the character
                contentStream.showText(charStr);
                contentStream.endText();

                // Move to the next character position
                currentX += charWidth;
            }

            // Move to the next line
            currentY -= defaultFontSize;
        }

        // Restore the graphics state
        contentStream.restoreGraphicsState();
    }

    private void addImageWatermark(
            PDPageContentStream contentStream,
            MultipartFile watermarkImage,
            PDDocument document,
            PDPage page,
            float rotation,
            int widthSpacer,
            int heightSpacer,
            float fontSize,
            boolean randomPosition,
            boolean randomOrientation,
            float minRotation,
            float maxRotation,
            int watermarkCount,
            boolean randomMirroring,
            float mirrorProbability)
            throws IOException {
        Random random = new Random();

        // Handle random orientation if enabled
        float actualRotation = rotation;
        if (randomOrientation) {
            actualRotation = minRotation + random.nextFloat() * (maxRotation - minRotation);
        }

        // Load the watermark image
        BufferedImage image = ImageIO.read(watermarkImage.getInputStream());

        // Compute width based on original aspect ratio
        float aspectRatio = (float) image.getWidth() / (float) image.getHeight();

        // Desired physical height (in PDF points)
        float desiredPhysicalHeight = fontSize;

        // Desired physical width based on the aspect ratio
        float desiredPhysicalWidth = desiredPhysicalHeight * aspectRatio;

        // Convert the BufferedImage to PDImageXObject
        PDImageXObject xobject = LosslessFactory.createFromImage(document, image);

        // Calculate the number of rows and columns for watermarks
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();

        if (randomPosition) {
            // For random positioning, add watermarks based on watermarkCount
            int numWatermarks = (watermarkCount > 0) ? watermarkCount : 1;

            for (int i = 0; i < numWatermarks; i++) {
                // Generate new random parameters for each watermark instance
                float instanceRotation = rotation;
                if (randomOrientation) {
                    instanceRotation =
                            minRotation + random.nextFloat() * (maxRotation - minRotation);
                }

                float x = random.nextFloat() * (pageWidth - desiredPhysicalWidth);
                float y = random.nextFloat() * (pageHeight - desiredPhysicalHeight);

                // Apply random mirroring if enabled
                boolean applyMirroring =
                        randomMirroring && (random.nextFloat() * 100 < mirrorProbability);

                // Save the graphics state
                contentStream.saveGraphicsState();

                // Create rotation matrix and rotate
                contentStream.transform(
                        Matrix.getTranslateInstance(
                                x + desiredPhysicalWidth / 2, y + desiredPhysicalHeight / 2));
                contentStream.transform(
                        Matrix.getRotateInstance(Math.toRadians(instanceRotation), 0, 0));

                // Apply mirroring if needed
                if (applyMirroring) {
                    contentStream.transform(Matrix.getScaleInstance(-1, 1)); // Mirror horizontally
                }

                contentStream.transform(
                        Matrix.getTranslateInstance(
                                -desiredPhysicalWidth / 2, -desiredPhysicalHeight / 2));

                // Draw the image and restore the graphics state
                contentStream.drawImage(xobject, 0, 0, desiredPhysicalWidth, desiredPhysicalHeight);
                contentStream.restoreGraphicsState();
            }
        } else {
            // For grid-based positioning
            int watermarkRows =
                    (int) ((pageHeight + heightSpacer) / (desiredPhysicalHeight + heightSpacer));
            int watermarkCols =
                    (int) ((pageWidth + widthSpacer) / (desiredPhysicalWidth + widthSpacer));

            // Calculate total watermarks in the grid
            int totalGridWatermarks = watermarkRows * watermarkCols;

            // If watermarkCount is specified and less than the total grid watermarks,
            // we'll place them randomly within the grid
            boolean useRandomGrid = watermarkCount > 0 && watermarkCount < totalGridWatermarks;

            // Create a 2D array to track which grid positions are filled (for random grid
            // placement)
            boolean[][] filledPositions = null;
            if (useRandomGrid) {
                filledPositions = new boolean[watermarkRows][watermarkCols];
            }

            // Determine how many watermarks to place
            int watermarksToPlace = useRandomGrid ? watermarkCount : totalGridWatermarks;
            int watermarksPlaced = 0;

            for (int i = 0; i < watermarkRows && watermarksPlaced < watermarksToPlace; i++) {
                for (int j = 0; j < watermarkCols && watermarksPlaced < watermarksToPlace; j++) {
                    // For random grid placement, randomly decide whether to place a watermark at
                    // this position
                    if (useRandomGrid) {
                        // Skip if we've already filled this position
                        if (filledPositions[i][j]) {
                            continue;
                        }

                        // Randomly decide whether to place a watermark here
                        // The probability increases as we get closer to the end to ensure we place
                        // exactly watermarkCount watermarks
                        int remainingPositions = totalGridWatermarks - (i * watermarkCols + j);
                        int remainingWatermarks = watermarksToPlace - watermarksPlaced;

                        if (remainingPositions <= remainingWatermarks
                                || random.nextFloat()
                                        < (float) remainingWatermarks / remainingPositions) {
                            filledPositions[i][j] = true;
                        } else {
                            continue; // Skip this position
                        }
                    }

                    // Generate new random parameters for each watermark instance
                    float instanceRotation = rotation;
                    if (randomOrientation) {
                        instanceRotation =
                                minRotation + random.nextFloat() * (maxRotation - minRotation);
                    }

                    float x = j * (desiredPhysicalWidth + widthSpacer);
                    float y = i * (desiredPhysicalHeight + heightSpacer);

                    // Apply random mirroring if enabled
                    boolean applyMirroring =
                            randomMirroring && (random.nextFloat() * 100 < mirrorProbability);

                    // Save the graphics state
                    contentStream.saveGraphicsState();

                    // Create rotation matrix and rotate
                    contentStream.transform(
                            Matrix.getTranslateInstance(
                                    x + desiredPhysicalWidth / 2, y + desiredPhysicalHeight / 2));
                    contentStream.transform(
                            Matrix.getRotateInstance(Math.toRadians(instanceRotation), 0, 0));

                    // Apply mirroring if needed
                    if (applyMirroring) {
                        contentStream.transform(
                                Matrix.getScaleInstance(-1, 1)); // Mirror horizontally
                    }

                    contentStream.transform(
                            Matrix.getTranslateInstance(
                                    -desiredPhysicalWidth / 2, -desiredPhysicalHeight / 2));

                    // Draw the image and restore the graphics state
                    contentStream.drawImage(
                            xobject, 0, 0, desiredPhysicalWidth, desiredPhysicalHeight);
                    contentStream.restoreGraphicsState();

                    watermarksPlaced++;
                }
            }
        }
    }
}
