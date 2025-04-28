# Watermark Enhancement Task List

This document contains a detailed task list for implementing the enhanced watermark functionality in Stirling-PDF as outlined in the development plan.

## Phase 1: Model and Basic UI Changes

### 1. Model Changes
- [x] 1.1. Update `AddWatermarkRequest.java` with new fields:
  - [x] 1.1.1. Add positioning fields (randomPosition)
  - [x] 1.1.2. Add orientation fields (randomOrientation, minRotation, maxRotation)
  - [x] 1.1.3. Add font fields (randomFont, selectedFonts)
  - [x] 1.1.4. Add font size fields (randomFontSize, minFontSize, maxFontSize)
  - [x] 1.1.5. Add letter color fields (randomLetterColor, selectedColors)
  - [x] 1.1.6. Add letter orientation fields (randomLetterOrientation, minLetterRotation, maxLetterRotation)
  - [x] 1.1.7. Add mixed styling field (mixedStyling)
  - [x] 1.1.8. Add watermark count field (watermarkCount)
  - [x] 1.1.9. Add shading fields (randomShading, minOpacity, maxOpacity)
  - [x] 1.1.10. Add mirroring fields (randomMirroring, mirrorProbability)

### 2. UI Changes
- [x] 2.1. Add basic UI elements to `add-watermark.html`:
  - [x] 2.1.1. Add random positioning checkbox
  - [x] 2.1.2. Add random orientation controls (checkbox, min/max rotation inputs)
  - [x] 2.1.3. Add random font controls (checkbox, font selection checkboxes)
  - [x] 2.1.4. Add random font size controls (checkbox, min/max size inputs)
  - [x] 2.1.5. Add random letter color controls (checkbox, color pickers)
  - [x] 2.1.6. Add random letter orientation controls (checkbox, min/max rotation inputs)
  - [x] 2.1.7. Add mixed styling checkbox
  - [x] 2.1.8. Add watermark count input
  - [x] 2.1.9. Add random shading controls (checkbox, min/max opacity inputs)
  - [x] 2.1.10. Add random mirroring controls (checkbox, probability input)

- [x] 2.2. Add JavaScript for UI interactions:
  - [x] 2.2.1. Implement toggleRotationRange() function
  - [x] 2.2.2. Implement toggleFontSelection() function
  - [x] 2.2.3. Implement toggleFontSizeRange() function
  - [x] 2.2.4. Implement toggleColorSelection() function
  - [x] 2.2.5. Implement toggleLetterRotationRange() function
  - [x] 2.2.6. Implement toggleOpacityRange() function
  - [x] 2.2.7. Implement toggleMirrorProbability() function
  - [x] 2.2.8. Add event listeners for all toggle functions

## Phase 2: Core Functionality Implementation

### 3. Random Positioning
- [x] 3.1. Modify `addTextWatermark` method:
  - [x] 3.1.1. Add logic to check randomPosition flag
  - [x] 3.1.2. Implement random coordinate generation within page bounds
  - [x] 3.1.3. Update watermark placement logic

- [x] 3.2. Modify `addImageWatermark` method:
  - [x] 3.2.1. Add logic to check randomPosition flag
  - [x] 3.2.2. Implement random coordinate generation within page bounds
  - [x] 3.2.3. Update watermark placement logic

### 4. Random Orientation
- [x] 4.1. Modify rotation logic in `addTextWatermark`:
  - [x] 4.1.1. Add logic to check randomOrientation flag
  - [x] 4.1.2. Implement random angle generation between minRotation and maxRotation
  - [x] 4.1.3. Update rotation matrix application

- [x] 4.2. Modify rotation logic in `addImageWatermark`:
  - [x] 4.2.1. Add logic to check randomOrientation flag
  - [x] 4.2.2. Implement random angle generation between minRotation and maxRotation
  - [x] 4.2.3. Update rotation matrix application

### 5. Random Font
- [x] 5.1. Enhance font selection logic:
  - [x] 5.1.1. Add logic to check randomFont flag
  - [x] 5.1.2. Implement random font selection from selectedFonts list
  - [x] 5.1.3. Update font loading and application logic

### 6. Random Font Size
- [x] 6.1. Modify font size logic:
  - [x] 6.1.1. Add logic to check randomFontSize flag
  - [x] 6.1.2. Implement random font size generation between minFontSize and maxFontSize
  - [x] 6.1.3. Update font size application

## Phase 3: Advanced Text Rendering

### 7. Random Letter Color
- [x] 7.1. Create new method for rendering text with random colors:
  - [x] 7.1.1. Add logic to check randomLetterColor flag
  - [x] 7.1.2. Implement random color selection from selectedColors list
  - [x] 7.1.3. Apply different colors to individual letters
  - [x] 7.1.4. Integrate with existing text rendering

### 8. Random Letter Orientation
- [x] 8.1. Enhance text rendering logic:
  - [x] 8.1.1. Add logic to check randomLetterOrientation flag
  - [x] 8.1.2. Implement random rotation for individual letters
  - [x] 8.1.3. Apply rotation transformations to each letter
  - [x] 8.1.4. Integrate with existing text rendering

### 9. Mixed Styling
- [x] 9.1. Create comprehensive text rendering method:
  - [x] 9.1.1. Add logic to check mixedStyling flag
  - [x] 9.1.2. Implement combined random styling (font, color, size, orientation)
  - [x] 9.1.3. Apply different styles to individual letters
  - [x] 9.1.4. Integrate with existing text rendering

## Phase 4: Additional Features

### 10. Watermark Count Control
- [x] 10.1. Modify watermark placement logic:
  - [x] 10.1.1. Add logic to check watermarkCount value
  - [x] 10.1.2. Implement exact watermark count placement
  - [x] 10.1.3. Update grid calculation for fixed count
  - [x] 10.1.4. Implement random distribution for random positioning

### 11. Random Shading
- [x] 11.1. Enhance opacity setting:
  - [x] 11.1.1. Add logic to check randomShading flag
  - [x] 11.1.2. Implement random opacity generation between minOpacity and maxOpacity
  - [x] 11.1.3. Apply varying opacity to watermarks

### 12. Random Mirroring
- [x] 12.1. Add mirroring functionality:
  - [x] 12.1.1. Add logic to check randomMirroring flag
  - [x] 12.1.2. Implement random mirroring based on mirrorProbability
  - [x] 12.1.3. Apply negative scale transformation to content stream

## Testing

### 13. Unit Testing
- [x] 13.1. Create unit tests for new features:
  - [x] 13.1.1. Test random positioning
  - [x] 13.1.2. Test random orientation
  - [x] 13.1.3. Test random font selection
  - [x] 13.1.4. Test random font size
  - [x] 13.1.5. Test random letter color
  - [x] 13.1.6. Test random letter orientation
  - [x] 13.1.7. Test mixed styling
  - [x] 13.1.8. Test watermark count control
  - [x] 13.1.9. Test random shading
  - [x] 13.1.10. Test random mirroring

- [x] 13.2. Test edge cases:
  - [x] 13.2.1. Test extreme rotation values
  - [x] 13.2.2. Test very small/large font sizes
  - [x] 13.2.3. Test with empty or invalid inputs

- [x] 13.3. Test feature combinations:
  - [x] 13.3.1. Test random positioning with random orientation
  - [x] 13.3.2. Test random font with random font size
  - [x] 13.3.3. Test random letter color with random letter orientation
  - [x] 13.3.4. Test all features enabled simultaneously

### 14. Integration Testing
- [ ] 14.1. Test complete watermark workflow:
  - [ ] 14.1.1. Test UI to PDF generation process
  - [ ] 14.1.2. Verify UI controls affect generated PDF correctly
  - [ ] 14.1.3. Test with different browsers

### 15. Manual Testing
- [ ] 15.1. Visual inspection:
  - [ ] 15.1.1. Inspect watermarked PDFs for correct rendering
  - [ ] 15.1.2. Test with different PDF types and sizes
  - [ ] 15.1.3. Test with complex layouts

- [ ] 15.2. Usability testing:
  - [ ] 15.2.1. Test enhanced UI for usability
  - [ ] 15.2.2. Gather feedback from test users

- [ ] 15.3. Feature verification:
  - [ ] 15.3.1. Verify all features work with text watermarks
  - [ ] 15.3.2. Verify all features work with image watermarks

## Documentation and Deployment

### 16. Documentation
- [ ] 16.1. Update user documentation:
  - [ ] 16.1.1. Document new watermark features
  - [ ] 16.1.2. Add examples of different watermark configurations
  - [ ] 16.1.3. Document any limitations or edge cases

### 17. Release Preparation
- [ ] 17.1. Prepare for release:
  - [ ] 17.1.1. Include enhancements in next minor version
  - [ ] 17.1.2. Write release notes highlighting new features
  - [ ] 17.1.3. Consider beta release for early feedback
