# Watermark Feature Enhancement Tasks

This document contains the task list for implementing the enhanced watermark features in Stirling-PDF. Tasks are organized by implementation phase and component.

## Phase 1: Basic Random Options

### Backend Tasks

1. [x] Update AddWatermarkRequest model
   - [x] Add positioning type field (fixed/random)
   - [x] Add orientation type field (fixed/random)
   - [x] Add font selection type field (fixed/random)
   - [x] Add font size type field (fixed/random)
   - [x] Add color type field (fixed/random)
   - [x] Add min/max font size fields
   - [x] Add min/max rotation angle fields

2. [x] Update WatermarkController
   - [x] Implement random positioning logic
   - [x] Implement random orientation logic
   - [x] Implement random font selection logic
   - [x] Implement random font size logic
   - [x] Implement random color logic
   - [x] Add validation for new parameters

### Frontend Tasks

3. [x] Update add-watermark.html
   - [x] Add positioning type controls (radio buttons or dropdown)
   - [x] Add orientation type controls
   - [x] Add font selection type controls
   - [x] Add font size type controls
   - [x] Add color type controls
   - [x] Add min/max font size inputs
   - [x] Add min/max rotation angle inputs

4. [x] Add JavaScript functionality
   - [x] Toggle visibility of related controls based on selection
   - [x] Validate min/max inputs
   - [x] Update UI dynamically based on user selections

## Phase 2: Per-Letter Customization

### Backend Tasks

5. [x] Update AddWatermarkRequest model
   - [x] Add per-letter font option field
   - [x] Add per-letter color option field
   - [x] Add per-letter size option field
   - [x] Add per-letter orientation option field

6. [x] Update WatermarkController
   - [x] Refactor text watermark rendering to process individual characters
   - [x] Implement per-letter font variation
   - [x] Implement per-letter color variation
   - [x] Implement per-letter size variation
   - [x] Implement per-letter orientation variation

### Frontend Tasks

7. [x] Update add-watermark.html
   - [x] Add per-letter customization section
   - [x] Add toggles for each per-letter property (font, color, size, orientation)

8. [x] Add JavaScript functionality
   - [x] Toggle visibility of per-letter options
   - [x] Update UI based on per-letter customization selections

## Phase 3: Advanced Features

### Backend Tasks

9. [x] Update AddWatermarkRequest model
   - [x] Add watermark count field
   - [x] Add transparency level field
   - [x] Add watermark shading type field (fixed/random)
   - [x] Add mirroring option field

10. [x] Update WatermarkController
    - [x] Implement watermark count logic
    - [x] Enhance transparency handling
    - [x] Implement watermark shading effects
    - [x] Implement random mirroring

### Frontend Tasks

11. [x] Update add-watermark.html
    - [x] Add watermark count input
    - [x] Enhance transparency control
    - [x] Add shading type controls
    - [x] Add mirroring option toggle

12. [x] Add JavaScript functionality
    - [x] Toggle visibility of advanced options
    - [x] Validate watermark count input
    - [x] Update UI based on advanced feature selections

## Testing Tasks

13. [ ] Create unit tests
    - [ ] Test random positioning
    - [ ] Test random orientation
    - [ ] Test random font selection
    - [ ] Test random font size
    - [ ] Test random color
    - [ ] Test per-letter customization
    - [ ] Test advanced features

14. [ ] Perform integration testing
    - [ ] Test complete watermark process with various configurations
    - [ ] Test with different PDF types and sizes

15. [ ] Perform manual testing
    - [ ] Verify visual appearance of watermarks
    - [ ] Test edge cases and boundary conditions
    - [ ] Test performance with large PDFs and many watermarks
