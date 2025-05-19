package stirling.software.SPDF.controller.api.security;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

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

        // Extract random options
        String positioningType = request.getPositioningType();
        String orientationType = request.getOrientationType();
        String fontSelectionType = request.getFontSelectionType();
        String fontSizeType = request.getFontSizeType();
        String colorType = request.getColorType();
        float minFontSize = request.getMinFontSize();
        float maxFontSize = request.getMaxFontSize();
        float minRotation = request.getMinRotation();
        float maxRotation = request.getMaxRotation();

        // Extract per-letter customization options
        boolean perLetterFont = request.isPerLetterFont();
        boolean perLetterColor = request.isPerLetterColor();
        boolean perLetterSize = request.isPerLetterSize();
        boolean perLetterOrientation = request.isPerLetterOrientation();

        // Extract advanced features
        int watermarkCount = request.getWatermarkCount();
        String shadingType = request.getShadingType();
        boolean enableRandomMirroring = request.isEnableRandomMirroring();

        // Load the input PDF
        PDDocument document = pdfDocumentFactory.load(pdfFile);

        // Create a page in the document
        for (PDPage page : document.getPages()) {

            // Get the page's content stream
            PDPageContentStream contentStream =
                    new PDPageContentStream(
                            document, page, PDPageContentStream.AppendMode.APPEND, true, true);

            // Set transparency
            PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
            graphicsState.setNonStrokingAlphaConstant(opacity);
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
                        positioningType,
                        orientationType,
                        fontSelectionType,
                        fontSizeType,
                        colorType,
                        minFontSize,
                        maxFontSize,
                        minRotation,
                        maxRotation,
                        perLetterFont,
                        perLetterColor,
                        perLetterSize,
                        perLetterOrientation,
                        watermarkCount,
                        shadingType,
                        enableRandomMirroring);
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
                        positioningType,
                        orientationType,
                        minRotation,
                        maxRotation,
                        watermarkCount,
                        enableRandomMirroring);
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
            String positioningType,
            String orientationType,
            String fontSelectionType,
            String fontSizeType,
            String colorType,
            float minFontSize,
            float maxFontSize,
            float minRotation,
            float maxRotation,
            boolean perLetterFont,
            boolean perLetterColor,
            boolean perLetterSize,
            boolean perLetterOrientation,
            int watermarkCount,
            String shadingType,
            boolean enableRandomMirroring)
            throws IOException {
        // Available fonts for random selection
        String[] availableFonts = {"roman", "arabic", "japanese", "korean", "chinese"};

        // Random number generator
        java.util.Random random = new java.util.Random();

        // Font selection logic
        String selectedAlphabet = alphabet;
        if ("random".equals(fontSelectionType)) {
            // Select a random font from available fonts
            selectedAlphabet = availableFonts[random.nextInt(availableFonts.length)];
        }

        // Load the font
        PDFont font = loadFont(selectedAlphabet, document, watermarkText);

        // Font size logic
        float actualFontSize = fontSize;
        if ("random".equals(fontSizeType)) {
            // Generate a random font size between minFontSize and maxFontSize
            actualFontSize = minFontSize + random.nextFloat() * (maxFontSize - minFontSize);
        }

        contentStream.setFont(font, actualFontSize);

        // Color logic
        Color redactColor;
        if ("random".equals(colorType)) {
            // Generate a random color
            redactColor = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
        } else {
            try {
                if (!colorString.startsWith("#")) {
                    colorString = "#" + colorString;
                }
                redactColor = Color.decode(colorString);
            } catch (NumberFormatException e) {
                redactColor = Color.LIGHT_GRAY;
            }
        }

        // Apply shading effects if specified
        if (!"none".equals(shadingType)) {
            // Create a shaded color based on the shading type
            if ("random".equals(shadingType)) {
                // For random shading, we'll use a random color with the same hue but different
                // brightness
                float[] hsbValues =
                        Color.RGBtoHSB(
                                redactColor.getRed(),
                                redactColor.getGreen(),
                                redactColor.getBlue(),
                                null);
                float randomBrightness =
                        0.3f + random.nextFloat() * 0.7f; // Brightness between 0.3 and 1.0
                redactColor = Color.getHSBColor(hsbValues[0], hsbValues[1], randomBrightness);
            } else if ("linear".equals(shadingType)) {
                // For linear shading, we'll use a slightly darker color
                float[] hsbValues =
                        Color.RGBtoHSB(
                                redactColor.getRed(),
                                redactColor.getGreen(),
                                redactColor.getBlue(),
                                null);
                redactColor =
                        Color.getHSBColor(
                                hsbValues[0], hsbValues[1], Math.max(0.3f, hsbValues[2] - 0.2f));
            } else if ("radial".equals(shadingType)) {
                // For radial shading, we'll use a slightly brighter color
                float[] hsbValues =
                        Color.RGBtoHSB(
                                redactColor.getRed(),
                                redactColor.getGreen(),
                                redactColor.getBlue(),
                                null);
                redactColor =
                        Color.getHSBColor(
                                hsbValues[0], hsbValues[1], Math.min(1.0f, hsbValues[2] + 0.2f));
            }
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

        // Orientation logic
        float actualRotation = rotation;
        if ("random".equals(orientationType)) {
            // Generate a random rotation angle between minRotation and maxRotation
            actualRotation = minRotation + random.nextFloat() * (maxRotation - minRotation);
        }

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

        // Positioning logic
        if ("random".equals(positioningType)) {
            // For random positioning, we'll place the specified number of watermarks at random
            // positions
            int numberOfWatermarks = watermarkCount; // Use the configurable watermark count

            for (int i = 0; i < numberOfWatermarks; i++) {
                // Generate random position within page boundaries
                float x = random.nextFloat() * (pageWidth - newWatermarkWidth);
                float y = random.nextFloat() * (pageHeight - newWatermarkHeight);

                // Generate random rotation for each watermark if orientation is random
                float watermarkRotation = actualRotation;
                if ("random".equals(orientationType)) {
                    watermarkRotation =
                            minRotation + random.nextFloat() * (maxRotation - minRotation);
                }

                // Apply random mirroring if enabled
                float scaleX = 1.0f;
                float scaleY = 1.0f;
                if (enableRandomMirroring && random.nextBoolean()) {
                    // Randomly choose horizontal or vertical mirroring
                    if (random.nextBoolean()) {
                        scaleX = -1.0f; // Horizontal mirroring
                    } else {
                        scaleY = -1.0f; // Vertical mirroring
                    }
                }

                contentStream.beginText();

                // Create transformation matrix with rotation and optional mirroring
                Matrix transformMatrix =
                        Matrix.getRotateInstance((float) Math.toRadians(watermarkRotation), x, y);

                // Apply scaling (mirroring) if needed
                if (scaleX != 1.0f || scaleY != 1.0f) {
                    // We need to apply scaling at the origin, then translate back
                    Matrix scaleMatrix = Matrix.getScaleInstance(scaleX, scaleY);
                    transformMatrix = transformMatrix.multiply(scaleMatrix);
                }

                contentStream.setTextMatrix(transformMatrix);

                for (int k = 0; k < textLines.length; ++k) {
                    if (perLetterFont || perLetterColor || perLetterSize || perLetterOrientation) {
                        // Process each character individually for per-letter customization
                        String line = textLines[k];
                        float xOffset = 0;

                        for (int charIndex = 0; charIndex < line.length(); charIndex++) {
                            char c = line.charAt(charIndex);

                            // Apply per-letter font variation if enabled
                            PDFont charFont = font;
                            if (perLetterFont) {
                                String charAlphabet =
                                        availableFonts[random.nextInt(availableFonts.length)];
                                charFont = loadFont(charAlphabet, document, String.valueOf(c));
                                contentStream.setFont(charFont, actualFontSize);
                            }

                            // Apply per-letter color variation if enabled
                            if (perLetterColor) {
                                Color charColor =
                                        new Color(
                                                random.nextFloat(),
                                                random.nextFloat(),
                                                random.nextFloat());
                                contentStream.setNonStrokingColor(charColor);
                            }

                            // Apply per-letter size variation if enabled
                            float charFontSize = actualFontSize;
                            if (perLetterSize) {
                                charFontSize =
                                        minFontSize
                                                + random.nextFloat() * (maxFontSize - minFontSize);
                                contentStream.setFont(charFont, charFontSize);
                            }

                            // Apply per-letter orientation variation if enabled
                            if (perLetterOrientation) {
                                float charRotation =
                                        minRotation
                                                + random.nextFloat() * (maxRotation - minRotation);
                                contentStream.setTextMatrix(
                                        Matrix.getRotateInstance(
                                                (float) Math.toRadians(charRotation),
                                                x + xOffset,
                                                y));
                            }

                            // Show the character
                            contentStream.showText(String.valueOf(c));

                            // Move to the next character position
                            try {
                                xOffset +=
                                        charFont.getStringWidth(String.valueOf(c))
                                                / 1000
                                                * charFontSize;
                            } catch (IOException e) {
                                // If there's an error calculating width, use a default value
                                xOffset += charFontSize * 0.5;
                            }
                        }

                        contentStream.newLineAtOffset(0, -actualFontSize);
                    } else {
                        // Process the entire line as before
                        contentStream.showText(textLines[k]);
                        contentStream.newLineAtOffset(0, -actualFontSize);
                    }
                }

                contentStream.endText();
            }
        } else {
            // For fixed positioning, use the original grid-based approach
            // Calculating the number of rows and columns.
            int watermarkRows = (int) (pageHeight / newWatermarkHeight + 1);
            int watermarkCols = (int) (pageWidth / newWatermarkWidth + 1);

            // Add the text watermark in a grid pattern
            for (int i = 0; i <= watermarkRows; i++) {
                for (int j = 0; j <= watermarkCols; j++) {
                    // Generate random rotation for each watermark if orientation is random
                    float watermarkRotation = actualRotation;
                    if ("random".equals(orientationType)) {
                        watermarkRotation =
                                minRotation + random.nextFloat() * (maxRotation - minRotation);
                    }

                    // Apply random mirroring if enabled
                    float scaleX = 1.0f;
                    float scaleY = 1.0f;
                    if (enableRandomMirroring && random.nextBoolean()) {
                        // Randomly choose horizontal or vertical mirroring
                        if (random.nextBoolean()) {
                            scaleX = -1.0f; // Horizontal mirroring
                        } else {
                            scaleY = -1.0f; // Vertical mirroring
                        }
                    }

                    contentStream.beginText();

                    // Create transformation matrix with rotation and optional mirroring
                    Matrix transformMatrix =
                            Matrix.getRotateInstance(
                                    (float) Math.toRadians(watermarkRotation),
                                    j * newWatermarkWidth,
                                    i * newWatermarkHeight);

                    // Apply scaling (mirroring) if needed
                    if (scaleX != 1.0f || scaleY != 1.0f) {
                        // We need to apply scaling at the origin, then translate back
                        Matrix scaleMatrix = Matrix.getScaleInstance(scaleX, scaleY);
                        transformMatrix = transformMatrix.multiply(scaleMatrix);
                    }

                    contentStream.setTextMatrix(transformMatrix);

                    for (int k = 0; k < textLines.length; ++k) {
                        if (perLetterFont
                                || perLetterColor
                                || perLetterSize
                                || perLetterOrientation) {
                            // Process each character individually for per-letter customization
                            String line = textLines[k];
                            float xOffset = 0;

                            for (int charIndex = 0; charIndex < line.length(); charIndex++) {
                                char c = line.charAt(charIndex);

                                // Apply per-letter font variation if enabled
                                PDFont charFont = font;
                                if (perLetterFont) {
                                    String charAlphabet =
                                            availableFonts[random.nextInt(availableFonts.length)];
                                    charFont = loadFont(charAlphabet, document, String.valueOf(c));
                                    contentStream.setFont(charFont, actualFontSize);
                                }

                                // Apply per-letter color variation if enabled
                                if (perLetterColor) {
                                    Color charColor =
                                            new Color(
                                                    random.nextFloat(),
                                                    random.nextFloat(),
                                                    random.nextFloat());
                                    contentStream.setNonStrokingColor(charColor);
                                }

                                // Apply per-letter size variation if enabled
                                float charFontSize = actualFontSize;
                                if (perLetterSize) {
                                    charFontSize =
                                            minFontSize
                                                    + random.nextFloat()
                                                            * (maxFontSize - minFontSize);
                                    contentStream.setFont(charFont, charFontSize);
                                }

                                // Apply per-letter orientation variation if enabled
                                if (perLetterOrientation) {
                                    float charRotation =
                                            minRotation
                                                    + random.nextFloat()
                                                            * (maxRotation - minRotation);
                                    contentStream.setTextMatrix(
                                            Matrix.getRotateInstance(
                                                    (float) Math.toRadians(charRotation),
                                                    j * newWatermarkWidth + xOffset,
                                                    i * newWatermarkHeight));
                                }

                                // Show the character
                                contentStream.showText(String.valueOf(c));

                                // Move to the next character position
                                try {
                                    xOffset +=
                                            charFont.getStringWidth(String.valueOf(c))
                                                    / 1000
                                                    * charFontSize;
                                } catch (IOException e) {
                                    // If there's an error calculating width, use a default value
                                    xOffset += charFontSize * 0.5;
                                }
                            }

                            contentStream.newLineAtOffset(0, -actualFontSize);
                        } else {
                            // Process the entire line as before
                            contentStream.showText(textLines[k]);
                            contentStream.newLineAtOffset(0, -actualFontSize);
                        }
                    }

                    contentStream.endText();
                }
            }
        }
    }

    /** Helper method to load a font based on the alphabet and text to be rendered */
    private PDFont loadFont(String alphabet, PDDocument document, String text) throws IOException {
        String resourceDir = "";
        PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        boolean useDefaultFont = false;

        switch (alphabet) {
            case "arabic":
                resourceDir = "static/fonts/NotoSansArabic-Regular.ttf";
                // Check if text contains non-Arabic characters
                if (text != null && !text.matches("^[\\p{InArabic}\\s\\p{Punct}]*$")) {
                    useDefaultFont = true;
                }
                break;
            case "japanese":
                resourceDir = "static/fonts/Meiryo.ttf";
                // Check if text contains non-Japanese characters
                if (text != null
                        && !text.matches(
                                "^[\\p{InHiragana}\\p{InKatakana}\\p{InCJKUnifiedIdeographs}\\s\\p{Punct}]*$")) {
                    useDefaultFont = true;
                }
                break;
            case "korean":
                resourceDir = "static/fonts/malgun.ttf";
                // Check if text contains non-Korean characters
                if (text != null && !text.matches("^[\\p{InHangulSyllables}\\s\\p{Punct}]*$")) {
                    useDefaultFont = true;
                }
                break;
            case "chinese":
                resourceDir = "static/fonts/SimSun.ttf";
                // Check if text contains non-Chinese characters
                if (text != null
                        && !text.matches("^[\\p{InCJKUnifiedIdeographs}\\s\\p{Punct}]*$")) {
                    useDefaultFont = true;
                }
                break;
            case "roman":
            default:
                resourceDir = "static/fonts/NotoSans-Regular.ttf";
                break;
        }

        // If text contains characters not supported by the selected font, use the default Roman
        // font
        if (useDefaultFont) {
            resourceDir = "static/fonts/NotoSans-Regular.ttf";
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

        return font;
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
            String positioningType,
            String orientationType,
            float minRotation,
            float maxRotation,
            int watermarkCount,
            boolean enableRandomMirroring)
            throws IOException {

        // Random number generator
        java.util.Random random = new java.util.Random();

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

        // Orientation logic
        float actualRotation = rotation;
        if ("random".equals(orientationType)) {
            // Generate a random rotation angle between minRotation and maxRotation
            actualRotation = minRotation + random.nextFloat() * (maxRotation - minRotation);
        }

        // Positioning logic
        if ("random".equals(positioningType)) {
            // For random positioning, we'll place the specified number of watermarks at random
            // positions
            int numberOfWatermarks = watermarkCount; // Use the configurable watermark count

            for (int i = 0; i < numberOfWatermarks; i++) {
                // Generate random position within page boundaries
                float x = random.nextFloat() * (pageWidth - desiredPhysicalWidth);
                float y = random.nextFloat() * (pageHeight - desiredPhysicalHeight);

                // Generate random rotation for each watermark if orientation is random
                float watermarkRotation = actualRotation;
                if ("random".equals(orientationType)) {
                    watermarkRotation =
                            minRotation + random.nextFloat() * (maxRotation - minRotation);
                }

                // Save the graphics state
                contentStream.saveGraphicsState();

                // Apply random mirroring if enabled
                float scaleX = 1.0f;
                float scaleY = 1.0f;
                if (enableRandomMirroring && random.nextBoolean()) {
                    // Randomly choose horizontal or vertical mirroring
                    if (random.nextBoolean()) {
                        scaleX = -1.0f; // Horizontal mirroring
                    } else {
                        scaleY = -1.0f; // Vertical mirroring
                    }
                }

                // Create rotation matrix and rotate
                contentStream.transform(
                        Matrix.getTranslateInstance(
                                x + desiredPhysicalWidth / 2, y + desiredPhysicalHeight / 2));
                contentStream.transform(
                        Matrix.getRotateInstance(Math.toRadians(watermarkRotation), 0, 0));

                // Apply mirroring if needed
                if (scaleX != 1.0f || scaleY != 1.0f) {
                    contentStream.transform(Matrix.getScaleInstance(scaleX, scaleY));
                }

                contentStream.transform(
                        Matrix.getTranslateInstance(
                                -desiredPhysicalWidth / 2, -desiredPhysicalHeight / 2));

                // Draw the image and restore the graphics state
                contentStream.drawImage(xobject, 0, 0, desiredPhysicalWidth, desiredPhysicalHeight);
                contentStream.restoreGraphicsState();
            }
        } else {
            // For fixed positioning, use the original grid-based approach
            int watermarkRows =
                    (int) ((pageHeight + heightSpacer) / (desiredPhysicalHeight + heightSpacer));
            int watermarkCols =
                    (int) ((pageWidth + widthSpacer) / (desiredPhysicalWidth + widthSpacer));

            for (int i = 0; i < watermarkRows; i++) {
                for (int j = 0; j < watermarkCols; j++) {
                    float x = j * (desiredPhysicalWidth + widthSpacer);
                    float y = i * (desiredPhysicalHeight + heightSpacer);

                    // Generate random rotation for each watermark if orientation is random
                    float watermarkRotation = actualRotation;
                    if ("random".equals(orientationType)) {
                        watermarkRotation =
                                minRotation + random.nextFloat() * (maxRotation - minRotation);
                    }

                    // Save the graphics state
                    contentStream.saveGraphicsState();

                    // Apply random mirroring if enabled
                    float scaleX = 1.0f;
                    float scaleY = 1.0f;
                    if (enableRandomMirroring && random.nextBoolean()) {
                        // Randomly choose horizontal or vertical mirroring
                        if (random.nextBoolean()) {
                            scaleX = -1.0f; // Horizontal mirroring
                        } else {
                            scaleY = -1.0f; // Vertical mirroring
                        }
                    }

                    // Create rotation matrix and rotate
                    contentStream.transform(
                            Matrix.getTranslateInstance(
                                    x + desiredPhysicalWidth / 2, y + desiredPhysicalHeight / 2));
                    contentStream.transform(
                            Matrix.getRotateInstance(Math.toRadians(watermarkRotation), 0, 0));

                    // Apply mirroring if needed
                    if (scaleX != 1.0f || scaleY != 1.0f) {
                        contentStream.transform(Matrix.getScaleInstance(scaleX, scaleY));
                    }

                    contentStream.transform(
                            Matrix.getTranslateInstance(
                                    -desiredPhysicalWidth / 2, -desiredPhysicalHeight / 2));

                    // Draw the image and restore the graphics state
                    contentStream.drawImage(
                            xobject, 0, 0, desiredPhysicalWidth, desiredPhysicalHeight);
                    contentStream.restoreGraphicsState();
                }
            }
        }
    }
}
