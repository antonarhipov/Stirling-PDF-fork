package stirling.software.SPDF.controller.api.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

import java.io.IOException;
import java.util.Arrays;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import stirling.software.SPDF.model.api.security.AddWatermarkRequest;
import stirling.software.SPDF.service.CustomPDFDocumentFactory;

@ExtendWith(MockitoExtension.class)
public class WatermarkControllerTest {

    @Mock private CustomPDFDocumentFactory pdfDocumentFactory;

    @InjectMocks private WatermarkController watermarkController;

    private AddWatermarkRequest request;
    private MockMultipartFile mockPdfFile;
    private PDDocument mockDocument;

    @BeforeEach
    public void setUp() throws IOException {
        // Create mock PDF file
        mockPdfFile =
                new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[] {1, 2, 3});

        // Create mock document
        mockDocument = new PDDocument();

        // Set up basic request
        request = new AddWatermarkRequest();
        request.setFileInput(mockPdfFile);
        request.setWatermarkType("text");
        request.setWatermarkText("Test Watermark");
        request.setAlphabet("roman");
        request.setFontSize(30);
        request.setRotation(0);
        request.setOpacity(0.5f);
        request.setWidthSpacer(50);
        request.setHeightSpacer(50);
        request.setCustomColor("#d3d3d3");
        request.setConvertPDFToImage(false);

        // Mock document loading
        lenient()
                .when(pdfDocumentFactory.load(any(MockMultipartFile.class)))
                .thenReturn(mockDocument);
    }

    @Test
    public void testRandomPositioning() throws Exception {
        // Set up request with random positioning
        request.setRandomPosition(true);

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testRandomOrientation() throws Exception {
        // Set up request with random orientation
        request.setRandomOrientation(true);
        request.setMinRotation(0);
        request.setMaxRotation(360);

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testRandomFont() throws Exception {
        // Set up request with random font
        request.setRandomFont(true);
        request.setSelectedFonts(Arrays.asList("helvetica", "times", "courier"));

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testRandomFontSize() throws Exception {
        // Set up request with random font size
        request.setRandomFontSize(true);
        request.setMinFontSize(10);
        request.setMaxFontSize(50);

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testRandomLetterColor() throws Exception {
        // Set up request with random letter color
        request.setRandomLetterColor(true);
        request.setSelectedColors(Arrays.asList("#ff0000", "#00ff00", "#0000ff"));

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testRandomLetterOrientation() throws Exception {
        // Set up request with random letter orientation
        request.setRandomLetterOrientation(true);
        request.setMinLetterRotation(0);
        request.setMaxLetterRotation(360);

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testMixedStyling() throws Exception {
        // Set up request with mixed styling
        request.setMixedStyling(true);
        request.setRandomFont(true);
        request.setSelectedFonts(Arrays.asList("helvetica", "times", "courier"));
        request.setRandomFontSize(true);
        request.setMinFontSize(10);
        request.setMaxFontSize(50);
        request.setRandomLetterColor(true);
        request.setSelectedColors(Arrays.asList("#ff0000", "#00ff00", "#0000ff"));
        request.setRandomLetterOrientation(true);
        request.setMinLetterRotation(0);
        request.setMaxLetterRotation(360);

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testWatermarkCountControl() throws Exception {
        // Set up request with watermark count
        request.setWatermarkCount(5);

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testRandomShading() throws Exception {
        // Set up request with random shading
        request.setRandomShading(true);
        request.setMinOpacity(0.1f);
        request.setMaxOpacity(0.9f);

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testRandomMirroring() throws Exception {
        // Set up request with random mirroring
        request.setRandomMirroring(true);
        request.setMirrorProbability(50);

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    // Edge case tests

    @Test
    public void testExtremeRotationValues() throws Exception {
        // Test with extreme rotation values
        request.setRotation(720); // Two full rotations

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testVerySmallFontSize() throws Exception {
        // Test with very small font size
        request.setFontSize(1);

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testLargeFontSize() throws Exception {
        // Test with very large font size
        request.setFontSize(1000);

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testEmptyWatermarkText() throws Exception {
        // Test with empty watermark text
        request.setWatermarkText("");

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testInvalidColor() throws Exception {
        // Test with invalid color
        request.setCustomColor("invalid-color");

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    // Feature combination tests

    @Test
    public void testRandomPositioningWithRandomOrientation() throws Exception {
        // Set up request with random positioning and random orientation
        request.setRandomPosition(true);
        request.setRandomOrientation(true);
        request.setMinRotation(0);
        request.setMaxRotation(360);

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testRandomFontWithRandomFontSize() throws Exception {
        // Set up request with random font and random font size
        request.setRandomFont(true);
        request.setSelectedFonts(Arrays.asList("helvetica", "times", "courier"));
        request.setRandomFontSize(true);
        request.setMinFontSize(10);
        request.setMaxFontSize(50);

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testRandomLetterColorWithRandomLetterOrientation() throws Exception {
        // Set up request with random letter color and random letter orientation
        request.setRandomLetterColor(true);
        request.setSelectedColors(Arrays.asList("#ff0000", "#00ff00", "#0000ff"));
        request.setRandomLetterOrientation(true);
        request.setMinLetterRotation(0);
        request.setMaxLetterRotation(360);

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }

    @Test
    public void testAllFeaturesEnabled() throws Exception {
        // Set up request with all features enabled
        request.setRandomPosition(true);
        request.setRandomOrientation(true);
        request.setMinRotation(0);
        request.setMaxRotation(360);
        request.setRandomFont(true);
        request.setSelectedFonts(Arrays.asList("helvetica", "times", "courier"));
        request.setRandomFontSize(true);
        request.setMinFontSize(10);
        request.setMaxFontSize(50);
        request.setRandomLetterColor(true);
        request.setSelectedColors(Arrays.asList("#ff0000", "#00ff00", "#0000ff"));
        request.setRandomLetterOrientation(true);
        request.setMinLetterRotation(0);
        request.setMaxLetterRotation(360);
        request.setMixedStyling(true);
        request.setWatermarkCount(5);
        request.setRandomShading(true);
        request.setMinOpacity(0.1f);
        request.setMaxOpacity(0.9f);
        request.setRandomMirroring(true);
        request.setMirrorProbability(50);

        // Call the controller method and verify response
        ResponseEntity<byte[]> response = watermarkController.addWatermark(request);
        assertNotNull(response);
    }
}
