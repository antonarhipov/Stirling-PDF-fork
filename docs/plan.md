# Watermark Enhancement Development Plan

## Overview
This document outlines the development plan for enhancing the watermark functionality in Stirling-PDF as specified in the requirements.md file. The plan includes technical approaches, necessary code changes, and testing strategies.

## Current Implementation
The current watermark functionality is implemented in:
- `WatermarkController.java`: Handles API requests for adding watermarks
- `AddWatermarkRequest.java`: Model class for watermark request parameters
- `add-watermark.html`: UI for the watermark feature

The system currently supports:
- Text or image watermarks
- Font selection for different languages
- Font size, rotation, opacity, and spacing configuration
- Custom color for text watermarks

## Requirements Analysis

### New Features to Implement
1. **Random Positioning**: Allow watermarks to be fixed or randomly positioned
2. **Random Orientation**: Allow fixed or random orientation of watermarks
3. **Random Font**: Support fixed or random font selection
4. **Random Font Size**: Support fixed or random font size
5. **Random Letter Color**: Allow color of each letter to be fixed or random
6. **Random Letter Orientation**: Allow orientation of each letter to be fixed or random
7. **Mixed Styling**: Support mixing font, color, size, and orientation in the same sentence
8. **Font Size Range**: Allow setting minimum and maximum font size
9. **Watermark Count**: Allow defining the number of watermarks in the document
10. **Transparency**: Set watermark transparency level (already implemented)
11. **Watermark Source**: Use image or text as watermark source (already implemented)
12. **Random Shading**: Allow fixed or random watermark shading
13. **Random Mirroring**: Support randomly mirrored watermarks

## Technical Approach

### 1. Model Changes (AddWatermarkRequest.java)
Add the following fields to the `AddWatermarkRequest` class:

```java
// Positioning
private boolean randomPosition;

// Orientation
private boolean randomOrientation;
private float minRotation;
private float maxRotation;

// Font
private boolean randomFont;
private List<String> selectedFonts;

// Font Size
private boolean randomFontSize;
private float minFontSize;
private float maxFontSize;

// Letter Color
private boolean randomLetterColor;
private List<String> selectedColors;

// Letter Orientation
private boolean randomLetterOrientation;
private float minLetterRotation;
private float maxLetterRotation;

// Mixed Styling
private boolean mixedStyling;

// Watermark Count
private int watermarkCount;

// Shading
private boolean randomShading;
private float minOpacity;
private float maxOpacity;

// Mirroring
private boolean randomMirroring;
private float mirrorProbability;
```

### 2. Controller Changes (WatermarkController.java)

#### 2.1 Random Positioning
Modify the `addTextWatermark` and `addImageWatermark` methods to support random positioning:
- If `randomPosition` is true, generate random x,y coordinates within the page bounds
- If false, use the current grid-based positioning

#### 2.2 Random Orientation
Modify the rotation logic:
- If `randomOrientation` is true, generate a random rotation angle between `minRotation` and `maxRotation` for each watermark
- If false, use the fixed rotation value

#### 2.3 Random Font
Enhance the font selection logic:
- If `randomFont` is true, randomly select a font from `selectedFonts` for each watermark
- If false, use the specified font

#### 2.4 Random Font Size
Modify the font size logic:
- If `randomFontSize` is true, generate a random font size between `minFontSize` and `maxFontSize` for each watermark
- If false, use the fixed font size

#### 2.5 Random Letter Color
Create a new method for rendering text with random colors:
- If `randomLetterColor` is true, assign a random color from `selectedColors` to each letter
- If false, use the fixed color

#### 2.6 Random Letter Orientation
Enhance the text rendering logic:
- If `randomLetterOrientation` is true, apply a random rotation to each letter between `minLetterRotation` and `maxLetterRotation`
- If false, use the fixed orientation

#### 2.7 Mixed Styling
Create a comprehensive text rendering method that combines random font, color, size, and orientation:
- If `mixedStyling` is true, apply random styling to each letter
- If false, use the other settings as configured

#### 2.8 Watermark Count
Modify the watermark placement logic:
- If `watermarkCount` is specified, place exactly that many watermarks
- If using random positioning, distribute them randomly
- If using grid positioning, adjust the grid to accommodate the specified count

#### 2.9 Random Shading
Enhance the opacity setting:
- If `randomShading` is true, generate a random opacity between `minOpacity` and `maxOpacity` for each watermark
- If false, use the fixed opacity

#### 2.10 Random Mirroring
Add mirroring functionality:
- If `randomMirroring` is true, randomly mirror watermarks based on `mirrorProbability`
- Implement by applying a negative scale transformation to the content stream

### 3. UI Changes (add-watermark.html)

Add the following UI elements:

#### 3.1 Random Positioning
```html
<div class="mb-3 form-check">
  <input type="checkbox" id="randomPosition" name="randomPosition">
  <label for="randomPosition">Random positioning</label>
</div>
```

#### 3.2 Random Orientation
```html
<div class="mb-3 form-check">
  <input type="checkbox" id="randomOrientation" name="randomOrientation" onchange="toggleRotationRange()">
  <label for="randomOrientation">Random orientation</label>
</div>
<div id="rotationRangeGroup" style="display: none;">
  <div class="mb-3">
    <label for="minRotation">Minimum rotation (degrees)</label>
    <input type="text" id="minRotation" name="minRotation" class="form-control" value="0">
  </div>
  <div class="mb-3">
    <label for="maxRotation">Maximum rotation (degrees)</label>
    <input type="text" id="maxRotation" name="maxRotation" class="form-control" value="360">
  </div>
</div>
```

#### 3.3 Random Font
```html
<div class="mb-3 form-check">
  <input type="checkbox" id="randomFont" name="randomFont" onchange="toggleFontSelection()">
  <label for="randomFont">Random font</label>
</div>
<div id="fontSelectionGroup" style="display: none;">
  <div class="mb-3">
    <label>Select fonts to use</label>
    <div class="form-check">
      <input type="checkbox" class="form-check-input" id="fontHelvetica" name="selectedFonts" value="helvetica">
      <label class="form-check-label" for="fontHelvetica">Helvetica</label>
    </div>
    <div class="form-check">
      <input type="checkbox" class="form-check-input" id="fontTimes" name="selectedFonts" value="times">
      <label class="form-check-label" for="fontTimes">Times</label>
    </div>
    <!-- Add more fonts as needed -->
  </div>
</div>
```

#### 3.4 Random Font Size
```html
<div class="mb-3 form-check">
  <input type="checkbox" id="randomFontSize" name="randomFontSize" onchange="toggleFontSizeRange()">
  <label for="randomFontSize">Random font size</label>
</div>
<div id="fontSizeRangeGroup" style="display: none;">
  <div class="mb-3">
    <label for="minFontSize">Minimum font size</label>
    <input type="text" id="minFontSize" name="minFontSize" class="form-control" value="10">
  </div>
  <div class="mb-3">
    <label for="maxFontSize">Maximum font size</label>
    <input type="text" id="maxFontSize" name="maxFontSize" class="form-control" value="50">
  </div>
</div>
```

#### 3.5 Random Letter Color
```html
<div class="mb-3 form-check">
  <input type="checkbox" id="randomLetterColor" name="randomLetterColor" onchange="toggleColorSelection()">
  <label for="randomLetterColor">Random letter color</label>
</div>
<div id="colorSelectionGroup" style="display: none;">
  <div class="mb-3">
    <label>Select colors to use</label>
    <div id="colorPickers">
      <div class="color-picker-container">
        <input type="color" class="form-control form-control-color" name="selectedColors" value="#d3d3d3">
        <button type="button" class="btn btn-sm btn-primary add-color">+</button>
      </div>
    </div>
  </div>
</div>
```

#### 3.6 Random Letter Orientation
```html
<div class="mb-3 form-check">
  <input type="checkbox" id="randomLetterOrientation" name="randomLetterOrientation" onchange="toggleLetterRotationRange()">
  <label for="randomLetterOrientation">Random letter orientation</label>
</div>
<div id="letterRotationRangeGroup" style="display: none;">
  <div class="mb-3">
    <label for="minLetterRotation">Minimum letter rotation (degrees)</label>
    <input type="text" id="minLetterRotation" name="minLetterRotation" class="form-control" value="0">
  </div>
  <div class="mb-3">
    <label for="maxLetterRotation">Maximum letter rotation (degrees)</label>
    <input type="text" id="maxLetterRotation" name="maxLetterRotation" class="form-control" value="360">
  </div>
</div>
```

#### 3.7 Mixed Styling
```html
<div class="mb-3 form-check">
  <input type="checkbox" id="mixedStyling" name="mixedStyling">
  <label for="mixedStyling">Mix font, color, size and orientation in the same sentence</label>
</div>
```

#### 3.8 Watermark Count
```html
<div class="mb-3">
  <label for="watermarkCount">Number of watermarks</label>
  <input type="number" id="watermarkCount" name="watermarkCount" class="form-control" value="0" min="0">
  <small class="form-text text-muted">0 for automatic (grid-based)</small>
</div>
```

#### 3.9 Random Shading
```html
<div class="mb-3 form-check">
  <input type="checkbox" id="randomShading" name="randomShading" onchange="toggleOpacityRange()">
  <label for="randomShading">Random watermark shading</label>
</div>
<div id="opacityRangeGroup" style="display: none;">
  <div class="mb-3">
    <label for="minOpacity">Minimum opacity (%)</label>
    <input type="text" id="minOpacity" name="minOpacityText" class="form-control" value="10">
    <input type="hidden" id="minOpacityReal" name="minOpacity" value="0.1">
  </div>
  <div class="mb-3">
    <label for="maxOpacity">Maximum opacity (%)</label>
    <input type="text" id="maxOpacity" name="maxOpacityText" class="form-control" value="90">
    <input type="hidden" id="maxOpacityReal" name="maxOpacity" value="0.9">
  </div>
</div>
```

#### 3.10 Random Mirroring
```html
<div class="mb-3 form-check">
  <input type="checkbox" id="randomMirroring" name="randomMirroring" onchange="toggleMirrorProbability()">
  <label for="randomMirroring">Random mirroring</label>
</div>
<div id="mirrorProbabilityGroup" style="display: none;">
  <div class="mb-3">
    <label for="mirrorProbability">Mirror probability (%)</label>
    <input type="text" id="mirrorProbability" name="mirrorProbability" class="form-control" value="50">
  </div>
</div>
```

#### 3.11 JavaScript for UI Interactions
Add JavaScript functions to toggle visibility of conditional UI elements:
```javascript
function toggleRotationRange() {
  document.getElementById('rotationRangeGroup').style.display =
    document.getElementById('randomOrientation').checked ? 'block' : 'none';
}

function toggleFontSelection() {
  document.getElementById('fontSelectionGroup').style.display =
    document.getElementById('randomFont').checked ? 'block' : 'none';
}

function toggleFontSizeRange() {
  document.getElementById('fontSizeRangeGroup').style.display =
    document.getElementById('randomFontSize').checked ? 'block' : 'none';
}

function toggleColorSelection() {
  document.getElementById('colorSelectionGroup').style.display =
    document.getElementById('randomLetterColor').checked ? 'block' : 'none';
}

function toggleLetterRotationRange() {
  document.getElementById('letterRotationRangeGroup').style.display =
    document.getElementById('randomLetterOrientation').checked ? 'block' : 'none';
}

function toggleOpacityRange() {
  document.getElementById('opacityRangeGroup').style.display =
    document.getElementById('randomShading').checked ? 'block' : 'none';
}

function toggleMirrorProbability() {
  document.getElementById('mirrorProbabilityGroup').style.display =
    document.getElementById('randomMirroring').checked ? 'block' : 'none';
}
```

## Implementation Strategy

### Phase 1: Model and Basic UI Changes
1. Update `AddWatermarkRequest.java` with all new fields
2. Add basic UI elements to `add-watermark.html`
3. Add JavaScript for UI interactions

### Phase 2: Core Functionality Implementation
1. Implement random positioning
2. Implement random orientation
3. Implement random font selection
4. Implement random font size

### Phase 3: Advanced Text Rendering
1. Implement random letter color
2. Implement random letter orientation
3. Implement mixed styling

### Phase 4: Additional Features
1. Implement watermark count control
2. Implement random shading
3. Implement random mirroring

## Testing Strategy

### Unit Tests
1. Create unit tests for each new feature in the `WatermarkController`
2. Test edge cases (e.g., extreme rotation values, very small/large font sizes)
3. Test combinations of features (e.g., random positioning with random orientation)
4. Test performance with large documents and many watermarks

### Integration Tests
1. Test the complete watermark workflow from UI to PDF generation
2. Verify that all UI controls correctly affect the generated PDF
3. Test with different browsers to ensure cross-browser compatibility

### Manual Testing
1. Visual inspection of watermarked PDFs to verify correct rendering
2. Usability testing of the enhanced UI
3. Verify that all features work correctly with both text and image watermarks

## Deployment Plan

### Documentation
1. Update user documentation to explain new watermark features
2. Add examples of different watermark configurations
3. Document any known limitations or edge cases

### Release
1. Include the watermark enhancements in the next minor version release
2. Highlight the new features in release notes
3. Consider a beta release for early feedback from power users

## Conclusion
This development plan outlines a comprehensive approach to implementing the enhanced watermark functionality in Stirling-PDF. By following the phased implementation strategy and thorough testing, we can ensure that the new features are robust, user-friendly, and meet the requirements specified in the requirements.md file.
