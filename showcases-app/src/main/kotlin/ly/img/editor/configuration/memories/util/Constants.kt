package ly.img.editor.configuration.memories.util

// The knobs for the slideshow. Tweak these to retime, resize, or restyle the montage.

// Canvas — the slideshow page size. Portrait by default; square = 1080 x 1080, landscape = 1920 x 1080.
const val PAGE_WIDTH = 1080f
const val PAGE_HEIGHT = 1920f

// Inset (dp) keeping the page off the screen edges and bottom controls.
const val CANVAS_PADDING_DP = 10

// Timing (seconds).
const val IMAGE_DURATION = 8.0
val OVERLAP_DURATION = IMAGE_DURATION * 0.4
const val TITLE_DURATION = 3.0

// Title card.
const val TITLE_FONT_SIZE = 48f
const val BURST_IMAGE_COUNT = 10 // images flashed behind the title
const val BURST_TITLE_FRACTION = 0.9 // burst spans this fraction of the title duration
