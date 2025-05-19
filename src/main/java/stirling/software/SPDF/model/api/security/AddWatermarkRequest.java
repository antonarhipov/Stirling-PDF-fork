package stirling.software.SPDF.model.api.security;

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

    // Phase 1: Basic Random Options - New fields

    @Schema(
            description = "The positioning type (fixed or random)",
            allowableValues = {"fixed", "random"},
            defaultValue = "fixed")
    private String positioningType = "fixed";

    @Schema(
            description = "The orientation type (fixed or random)",
            allowableValues = {"fixed", "random"},
            defaultValue = "fixed")
    private String orientationType = "fixed";

    @Schema(
            description = "The font selection type (fixed or random)",
            allowableValues = {"fixed", "random"},
            defaultValue = "fixed")
    private String fontSelectionType = "fixed";

    @Schema(
            description = "The font size type (fixed or random)",
            allowableValues = {"fixed", "random"},
            defaultValue = "fixed")
    private String fontSizeType = "fixed";

    @Schema(
            description = "The color type (fixed or random)",
            allowableValues = {"fixed", "random"},
            defaultValue = "fixed")
    private String colorType = "fixed";

    @Schema(description = "The minimum font size when using random font size", example = "20")
    private float minFontSize = 20;

    @Schema(description = "The maximum font size when using random font size", example = "40")
    private float maxFontSize = 40;

    @Schema(description = "The minimum rotation angle when using random orientation", example = "0")
    private float minRotation = 0;

    @Schema(
            description = "The maximum rotation angle when using random orientation",
            example = "360")
    private float maxRotation = 360;

    // Phase 2: Per-Letter Customization - New fields

    @Schema(description = "Enable per-letter font variation", defaultValue = "false")
    private boolean perLetterFont = false;

    @Schema(description = "Enable per-letter color variation", defaultValue = "false")
    private boolean perLetterColor = false;

    @Schema(description = "Enable per-letter size variation", defaultValue = "false")
    private boolean perLetterSize = false;

    @Schema(description = "Enable per-letter orientation variation", defaultValue = "false")
    private boolean perLetterOrientation = false;

    // Phase 3: Advanced Features - New fields

    @Schema(
            description = "Number of watermarks to add when using random positioning",
            example = "10",
            defaultValue = "10")
    private int watermarkCount = 10;

    @Schema(
            description = "The shading type for watermarks",
            allowableValues = {"none", "linear", "radial", "fixed", "random"},
            defaultValue = "none")
    private String shadingType = "none";

    @Schema(description = "Enable random mirroring of watermarks", defaultValue = "false")
    private boolean enableRandomMirroring = false;
}
