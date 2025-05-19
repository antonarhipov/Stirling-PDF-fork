# Watermark Feature Enhancement Plan

## Overview
This document outlines the plan for enhancing the watermark feature in Stirling-PDF. The enhancements will provide users with more customization options for watermarks, including random positioning, orientation, font properties, and more.

## Current Implementation
The current watermark feature allows users to:
- Add text or image watermarks to PDFs
- Set a fixed rotation angle
- Set opacity level
- Choose from a limited set of fonts (Roman, Arabic, Japanese, Korean, Chinese)
- Set font size
- Set spacing between watermarks
- Choose a custom color
- Convert the PDF to an image after watermarking

## Planned Enhancements

### 1. Positioning Options
- **Fixed Position**: Continue to support the current grid-based positioning
- **Random Position**: Add option to randomly position watermarks across the page
  - Implementation: Generate random x,y coordinates within page boundaries
  - Consider: Ensure watermarks don't go off-page

### 2. Orientation Options
- **Fixed Orientation**: Continue to support fixed rotation angle
- **Random Orientation**: Add option to apply random rotation to each watermark
  - Implementation: Generate random rotation angles for each watermark
  - Consider: Allow setting min/max rotation angles

### 3. Font Options
- **Fixed Font**: Continue to support selecting a single font
- **Random Font**: Add option to randomly select fonts for each watermark
  - Implementation: Create a list of available fonts and randomly select for each watermark
  - Consider: Allow selecting which fonts to include in the random selection

### 4. Font Size Options
- **Fixed Size**: Continue to support fixed font size
- **Random Size**: Add option to randomly vary font size
  - Implementation: Generate random sizes within a specified range
  - Consider: Add min/max font size settings

### 5. Color Options
- **Fixed Color**: Continue to support fixed color
- **Random Color**: Add option to randomly color each watermark
  - Implementation: Generate random colors or select from a predefined palette
  - Consider: Allow setting color palette or range

### 6. Letter-Level Customization
- **Per-Letter Properties**: Allow setting different properties for each letter in the watermark text
  - Implementation: Process each character individually when rendering
  - Properties to vary per letter:
    - Font
    - Color
    - Size
    - Orientation

### 7. Additional Enhancements
- **Watermark Count**: Allow specifying the number of watermarks to include
  - Implementation: Replace grid-based layout with specific count when random positioning is used
- **Transparency Level**: Enhance the existing opacity control
  - Implementation: Improve UI for setting transparency
- **Watermark Shading**: Add options for shading effects
  - Implementation: Add gradient or pattern fills for text
  - Options: Fixed or random shading
- **Mirrored Watermarks**: Add option to randomly mirror watermarks
  - Implementation: Randomly flip watermarks horizontally or vertically

## Technical Implementation Approach

### Backend Changes
1. Enhance `AddWatermarkRequest` model:
   - Add new fields for all the new options
   - Add validation for new fields

2. Update `WatermarkController`:
   - Modify the watermark generation logic to handle new options
   - Implement random positioning, orientation, etc.
   - Add support for per-letter customization
   - Implement mirroring and shading effects

### Frontend Changes
1. Update `add-watermark.html`:
   - Add UI controls for all new options
   - Implement dynamic form behavior (show/hide options based on selections)
   - Add preview functionality if possible

2. Add JavaScript for enhanced UI interactions:
   - Toggle between fixed and random options
   - Update min/max inputs based on selections
   - Validate inputs

## Implementation Phases
1. **Phase 1**: Basic random options (position, orientation, font, size, color)
2. **Phase 2**: Per-letter customization
3. **Phase 3**: Advanced features (shading, mirroring, count control)

## Testing Strategy
- Unit tests for new controller methods
- Integration tests for the complete watermark process
- Manual testing with various PDF types and watermark configurations
