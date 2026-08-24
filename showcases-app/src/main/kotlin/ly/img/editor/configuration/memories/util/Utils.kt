package ly.img.editor.configuration.memories.util

import ly.img.editor.configuration.memories.model.ImageItem

/** Sort images chronologically by creation date; images without a date sort last (asc)/first (desc). */
fun sortByDate(
    images: List<ImageItem>,
    ascending: Boolean,
): List<ImageItem> = if (ascending) {
    images.sortedWith(compareBy<ImageItem> { it.creationDate ?: Long.MAX_VALUE }.thenBy { it.id })
} else {
    images.sortedWith(compareByDescending<ImageItem> { it.creationDate ?: Long.MIN_VALUE }.thenByDescending { it.id })
}

/**
 * Detect whether [images] is already in chronological order.
 * Returns true for ascending (oldest first), false for descending (newest first),
 * or null if the list is unsorted (or too small / has no dates to be meaningfully ordered).
 */
fun detectSortOrder(images: List<ImageItem>): Boolean? {
    if (images.size < 2) return null
    // Note: images without a creation date fall back to id order, so a freshly-added
    // batch still reads as "in order" (ascending).
    if (images == sortByDate(images, ascending = true)) return true
    if (images == sortByDate(images, ascending = false)) return false
    return null
}
