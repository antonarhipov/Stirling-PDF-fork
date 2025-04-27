package stirling.software.SPDF.model.api.security;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

import stirling.software.SPDF.model.api.PDFFile;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddWatermarkRequest extends PDFFile {

    @Schema(
            description = "The watermark type (text or image)",
            allowableValues = {"text", "image"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String watermarkType;

    @Schema(description = "The watermark text")
    private String watermarkText;

    @Schema(description = "The watermark image")
    private MultipartFile watermarkImage;

    @Schema(
            description = "The selected alphabet",
            allowableValues = {"roman", "arabic", "japanese", "korean", "chinese"},
            defaultValue = "roman")
    private String alphabet = "roman";

    @Schema(description = "The font size of the watermark text", example = "30")
    private float fontSize = 30;

    @Schema(description = "The rotation of the watermark in degrees", example = "0")
    private float rotation = 0;

    @Schema(description = "The opacity of the watermark (0.0 - 1.0)", example = "0.5")
    private float opacity;

    @Schema(description = "The width spacer between watermark elements", example = "50")
    private int widthSpacer;

    @Schema(description = "The height spacer between watermark elements", example = "50")
    private int heightSpacer;

    @Schema(description = "The color for watermark", defaultValue = "#d3d3d3")
    private String customColor = "#d3d3d3";

    @Schema(description = "Convert the redacted PDF to an image", defaultValue = "false")
    private boolean convertPDFToImage;

    // Positioning
    @Schema(description = "Whether to position watermarks randomly", defaultValue = "false")
    private boolean randomPosition;

    // Orientation
    @Schema(
            description = "Whether to apply random orientation to watermarks",
            defaultValue = "false")
    private boolean randomOrientation;

    @Schema(
            description = "Minimum rotation angle in degrees (for random orientation)",
            example = "0")
    private float minRotation = 0;

    @Schema(
            description = "Maximum rotation angle in degrees (for random orientation)",
            example = "360")
    private float maxRotation = 360;

    // Font
    @Schema(description = "Whether to use random fonts for watermarks", defaultValue = "false")
    private boolean randomFont;

    @Schema(description = "List of fonts to use when random font is enabled")
    private List<String> selectedFonts;

    // Font Size
    @Schema(description = "Whether to use random font sizes for watermarks", defaultValue = "false")
    private boolean randomFontSize;

    @Schema(description = "Minimum font size (for random font size)", example = "10")
    private float minFontSize = 10;

    @Schema(description = "Maximum font size (for random font size)", example = "50")
    private float maxFontSize = 50;

    // Letter Color
    @Schema(
            description = "Whether to apply random colors to individual letters",
            defaultValue = "false")
    private boolean randomLetterColor;

    @Schema(description = "List of colors to use when random letter color is enabled")
    private List<String> selectedColors;

    // Letter Orientation
    @Schema(
            description = "Whether to apply random orientation to individual letters",
            defaultValue = "false")
    private boolean randomLetterOrientation;

    @Schema(
            description =
                    "Minimum letter rotation angle in degrees (for random letter orientation)",
            example = "0")
    private float minLetterRotation = 0;

    @Schema(
            description =
                    "Maximum letter rotation angle in degrees (for random letter orientation)",
            example = "360")
    private float maxLetterRotation = 360;

    // Mixed Styling
    @Schema(
            description = "Whether to mix font, color, size and orientation in the same watermark",
            defaultValue = "false")
    private boolean mixedStyling;

    // Watermark Count
    @Schema(
            description = "Number of watermarks to include (0 for automatic grid-based placement)",
            example = "0")
    private int watermarkCount = 0;

    // Shading
    @Schema(description = "Whether to apply random shading to watermarks", defaultValue = "false")
    private boolean randomShading;

    @Schema(description = "Minimum opacity value (for random shading)", example = "0.1")
    private float minOpacity = 0.1f;

    @Schema(description = "Maximum opacity value (for random shading)", example = "0.9")
    private float maxOpacity = 0.9f;

    // Mirroring
    @Schema(description = "Whether to randomly mirror watermarks", defaultValue = "false")
    private boolean randomMirroring;

    @Schema(description = "Probability of mirroring a watermark (0-100)", example = "50")
    private float mirrorProbability = 50;
}
