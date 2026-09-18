import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

// highlight-android-types
data class BoundingBox(
    val minX: Float,
    val minY: Float,
    val maxX: Float,
    val maxY: Float,
)

enum class ValidationSeverity {
    ERROR,
    WARNING,
}

enum class ValidationIssueKind {
    OUTSIDE_PAGE,
    PROTRUDING,
    TEXT_OBSCURED,
    UNFILLED_PLACEHOLDER,
}

data class ValidationIssue(
    val kind: ValidationIssueKind,
    val severity: ValidationSeverity,
    val block: DesignBlock,
    val blockName: String,
    val message: String,
)

data class ValidationResult(
    val errors: List<ValidationIssue>,
    val warnings: List<ValidationIssue>,
)

private fun displayName(
    engine: Engine,
    block: DesignBlock,
): String {
    val name = engine.block.getName(block)
    if (name.isNotBlank()) return name

    val kind = engine.block.getKind(block).substringAfterLast("/")
    return kind.replaceFirstChar { it.uppercaseChar() }
}
// highlight-android-types

// highlight-android-get-bounding-box
private fun boundingBox(
    engine: Engine,
    block: DesignBlock,
): BoundingBox {
    val x = engine.block.getGlobalBoundingBoxX(block)
    val y = engine.block.getGlobalBoundingBoxY(block)
    val width = engine.block.getGlobalBoundingBoxWidth(block)
    val height = engine.block.getGlobalBoundingBoxHeight(block)
    return BoundingBox(minX = x, minY = y, maxX = x + width, maxY = y + height)
}

private fun overlapRatio(
    first: BoundingBox,
    second: BoundingBox,
): Float {
    val overlapWidth = maxOf(
        0F,
        minOf(first.maxX, second.maxX) - maxOf(first.minX, second.minX),
    )
    val overlapHeight = maxOf(
        0F,
        minOf(first.maxY, second.maxY) - maxOf(first.minY, second.minY),
    )
    val firstArea = (first.maxX - first.minX) * (first.maxY - first.minY)
    return if (firstArea <= 0F) 0F else (overlapWidth * overlapHeight) / firstArea
}

private fun pageDescendantsInRenderOrder(
    engine: Engine,
    parent: DesignBlock,
): List<DesignBlock> = engine.block.getChildren(parent).flatMap { child ->
    listOf(child) + pageDescendantsInRenderOrder(engine = engine, parent = child)
}

private fun validationCandidates(
    engine: Engine,
    page: DesignBlock,
): List<DesignBlock> = pageDescendantsInRenderOrder(engine = engine, parent = page).filter { block ->
    if (!engine.block.isValid(block)) return@filter false
    if (!engine.block.isVisible(block)) return@filter false
    if (!engine.block.isIncludedInExport(block)) return@filter false

    val blockType = engine.block.getType(block)
    blockType == DesignBlockType.Text.key || blockType == DesignBlockType.Graphic.key
}
// highlight-android-get-bounding-box

// highlight-android-find-outside-blocks
private fun findOutsideBlocks(
    engine: Engine,
    page: DesignBlock,
): List<ValidationIssue> {
    val pageBounds = boundingBox(engine = engine, block = page)
    val candidates = validationCandidates(engine = engine, page = page)

    return candidates.mapNotNull { block ->
        val blockBounds = boundingBox(engine = engine, block = block)
        if (overlapRatio(blockBounds, pageBounds) == 0F) {
            ValidationIssue(
                kind = ValidationIssueKind.OUTSIDE_PAGE,
                severity = ValidationSeverity.ERROR,
                block = block,
                blockName = displayName(engine = engine, block = block),
                message = "Element is completely outside the visible page area",
            )
        } else {
            null
        }
    }
}
// highlight-android-find-outside-blocks

// highlight-android-find-protruding-blocks
private fun findProtrudingBlocks(
    engine: Engine,
    page: DesignBlock,
): List<ValidationIssue> {
    val pageBounds = boundingBox(engine = engine, block = page)
    val candidates = validationCandidates(engine = engine, page = page)

    return candidates.mapNotNull { block ->
        val blockBounds = boundingBox(engine = engine, block = block)
        val overlap = overlapRatio(blockBounds, pageBounds)
        if (overlap > 0F && overlap < 0.99F) {
            ValidationIssue(
                kind = ValidationIssueKind.PROTRUDING,
                severity = ValidationSeverity.WARNING,
                block = block,
                blockName = displayName(engine = engine, block = block),
                message = "Element extends beyond page boundaries",
            )
        } else {
            null
        }
    }
}
// highlight-android-find-protruding-blocks

// highlight-android-find-obscured-text
private fun findObscuredText(
    engine: Engine,
    page: DesignBlock,
): List<ValidationIssue> {
    val blocksInRenderOrder = pageDescendantsInRenderOrder(engine = engine, parent = page)
    val textBlocks = blocksInRenderOrder.filter { block ->
        engine.block.isValid(block) &&
            engine.block.isVisible(block) &&
            engine.block.isIncludedInExport(block) &&
            engine.block.getType(block) == DesignBlockType.Text.key
    }

    return textBlocks.mapNotNull { textBlock ->
        val textIndex = blocksInRenderOrder.indexOf(textBlock)
        if (textIndex == -1) return@mapNotNull null

        val textBounds = boundingBox(engine = engine, block = textBlock)
        val isObscured = blocksInRenderOrder.drop(textIndex + 1).any { blockAbove ->
            canObscureText(engine = engine, block = blockAbove) &&
                overlapRatio(textBounds, boundingBox(engine = engine, block = blockAbove)) > 0F
        }

        if (isObscured) {
            ValidationIssue(
                kind = ValidationIssueKind.TEXT_OBSCURED,
                severity = ValidationSeverity.WARNING,
                block = textBlock,
                blockName = displayName(engine = engine, block = textBlock),
                message = "Text may be partially hidden by overlapping elements",
            )
        } else {
            null
        }
    }
}

private fun canObscureText(
    engine: Engine,
    block: DesignBlock,
): Boolean {
    if (!engine.block.isValid(block)) return false

    val blockType = engine.block.getType(block)
    if (blockType == DesignBlockType.Text.key || blockType == DesignBlockType.Group.key) return false

    return engine.block.isVisible(block) && engine.block.isIncludedInExport(block)
}
// highlight-android-find-obscured-text

// highlight-android-find-unfilled-placeholders
private fun findUnfilledPlaceholders(
    engine: Engine,
    page: DesignBlock,
): List<ValidationIssue> {
    val pageBlocks = pageDescendantsInRenderOrder(engine = engine, parent = page).toSet()
    return engine.block.findAllPlaceholders().filter { it in pageBlocks }.mapNotNull { placeholder ->
        if (!engine.block.isValid(placeholder)) return@mapNotNull null
        if (!engine.block.isPlaceholderEnabled(placeholder)) return@mapNotNull null

        if (!isPlaceholderFilled(engine = engine, block = placeholder)) {
            ValidationIssue(
                kind = ValidationIssueKind.UNFILLED_PLACEHOLDER,
                severity = ValidationSeverity.ERROR,
                block = placeholder,
                blockName = displayName(engine = engine, block = placeholder),
                message = "Placeholder has not been filled with content",
            )
        } else {
            null
        }
    }
}

private fun isPlaceholderFilled(
    engine: Engine,
    block: DesignBlock,
): Boolean {
    if (!engine.block.supportsFill(block)) return false

    val fill = engine.block.getFill(block)
    if (!engine.block.isValid(fill)) return false
    if (engine.block.getType(fill) != FillType.Image.key) return true

    return engine.block.getUri(
        block = fill,
        property = "fill/image/imageFileURI",
    ).toString().isNotBlank()
}
// highlight-android-find-unfilled-placeholders

// highlight-android-validate-design
private fun validateDesign(
    engine: Engine,
    page: DesignBlock,
): ValidationResult {
    val allIssues =
        findOutsideBlocks(engine = engine, page = page) +
            findProtrudingBlocks(engine = engine, page = page) +
            findObscuredText(engine = engine, page = page) +
            findUnfilledPlaceholders(engine = engine, page = page)

    val result = ValidationResult(
        errors = allIssues.filter { it.severity == ValidationSeverity.ERROR },
        warnings = allIssues.filter { it.severity == ValidationSeverity.WARNING },
    )

    result.errors.firstOrNull()?.block?.takeIf(engine.block::isValid)?.let { firstError ->
        engine.block.select(firstError)
    }

    return result
}
// highlight-android-validate-design

fun preExportValidation(engine: Engine): ValidationResult {
    // This demo scene gives the smoke test one block for each validation outcome.
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val outsideBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(outsideBlock, name = "Outside Element")
    engine.block.setShape(outsideBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = outsideBlock, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = outsideBlock,
        color = Color.fromRGBA(r = 0.9F, g = 0.2F, b = 0.2F, a = 1F),
    )
    engine.block.setWidth(outsideBlock, value = 150F)
    engine.block.setHeight(outsideBlock, value = 100F)
    engine.block.setPositionX(outsideBlock, value = -200F)
    engine.block.setPositionY(outsideBlock, value = 80F)
    engine.block.appendChild(parent = page, child = outsideBlock)

    val protrudingBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(protrudingBlock, name = "Protruding Element")
    engine.block.setShape(protrudingBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = protrudingBlock, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = protrudingBlock,
        color = Color.fromRGBA(r = 0.95F, g = 0.65F, b = 0.1F, a = 1F),
    )
    engine.block.setWidth(protrudingBlock, value = 150F)
    engine.block.setHeight(protrudingBlock, value = 100F)
    engine.block.setPositionX(protrudingBlock, value = 725F)
    engine.block.setPositionY(protrudingBlock, value = 80F)
    engine.block.appendChild(parent = page, child = protrudingBlock)

    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.setName(textBlock, name = "Obscured Text")
    engine.block.replaceText(block = textBlock, text = "Hidden")
    engine.block.setFloat(block = textBlock, property = "text/fontSize", value = 48F)
    engine.block.setPositionX(textBlock, value = 200F)
    engine.block.setPositionY(textBlock, value = 250F)
    engine.block.setWidth(textBlock, value = 220F)
    engine.block.setHeight(textBlock, value = 100F)
    engine.block.appendChild(parent = page, child = textBlock)

    val groupCompanionBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(groupCompanionBlock, name = "Grouped Companion")
    engine.block.setShape(groupCompanionBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = groupCompanionBlock, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = groupCompanionBlock,
        color = Color.fromRGBA(r = 0.1F, g = 0.7F, b = 0.45F, a = 1F),
    )
    engine.block.setPositionX(groupCompanionBlock, value = 450F)
    engine.block.setPositionY(groupCompanionBlock, value = 250F)
    engine.block.setWidth(groupCompanionBlock, value = 60F)
    engine.block.setHeight(groupCompanionBlock, value = 60F)
    engine.block.appendChild(parent = page, child = groupCompanionBlock)

    if (engine.block.isGroupable(listOf(textBlock, groupCompanionBlock))) {
        engine.block.group(listOf(textBlock, groupCompanionBlock))
    } else {
        error("Expected demo blocks to be groupable.")
    }

    val clearText = engine.block.create(DesignBlockType.Text)
    engine.block.setName(clearText, name = "Clear Text")
    engine.block.replaceText(block = clearText, text = "Readable")
    engine.block.setFloat(block = clearText, property = "text/fontSize", value = 32F)
    engine.block.setPositionX(clearText, value = 300F)
    engine.block.setPositionY(clearText, value = 80F)
    engine.block.setWidth(clearText, value = 180F)
    engine.block.setHeight(clearText, value = 80F)
    engine.block.appendChild(parent = page, child = clearText)

    val leftFrameBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(leftFrameBlock, name = "Left Frame")
    engine.block.setShape(leftFrameBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = leftFrameBlock, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = leftFrameBlock,
        color = Color.fromRGBA(r = 0.55F, g = 0.25F, b = 0.7F, a = 1F),
    )
    engine.block.setPositionX(leftFrameBlock, value = 260F)
    engine.block.setPositionY(leftFrameBlock, value = 80F)
    engine.block.setWidth(leftFrameBlock, value = 20F)
    engine.block.setHeight(leftFrameBlock, value = 80F)
    engine.block.appendChild(parent = page, child = leftFrameBlock)

    val rightFrameBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(rightFrameBlock, name = "Right Frame")
    engine.block.setShape(rightFrameBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = rightFrameBlock, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = rightFrameBlock,
        color = Color.fromRGBA(r = 0.55F, g = 0.25F, b = 0.7F, a = 1F),
    )
    engine.block.setPositionX(rightFrameBlock, value = 500F)
    engine.block.setPositionY(rightFrameBlock, value = 80F)
    engine.block.setWidth(rightFrameBlock, value = 20F)
    engine.block.setHeight(rightFrameBlock, value = 80F)
    engine.block.appendChild(parent = page, child = rightFrameBlock)

    if (engine.block.isGroupable(listOf(leftFrameBlock, rightFrameBlock))) {
        engine.block.group(listOf(leftFrameBlock, rightFrameBlock))
    } else {
        error("Expected frame blocks to be groupable.")
    }

    val coveringBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(coveringBlock, name = "Overlapping Shape")
    engine.block.setShape(coveringBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = coveringBlock, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = coveringBlock,
        color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.8F, a = 0.8F),
    )
    engine.block.setPositionX(coveringBlock, value = 200F)
    engine.block.setPositionY(coveringBlock, value = 250F)
    engine.block.setWidth(coveringBlock, value = 220F)
    engine.block.setHeight(coveringBlock, value = 100F)
    engine.block.appendChild(parent = page, child = coveringBlock)

    val hiddenTextBlock = engine.block.create(DesignBlockType.Text)
    engine.block.setName(hiddenTextBlock, name = "Hidden Text")
    engine.block.replaceText(block = hiddenTextBlock, text = "Hidden from export")
    engine.block.setFloat(block = hiddenTextBlock, property = "text/fontSize", value = 30F)
    engine.block.setPositionX(hiddenTextBlock, value = 80F)
    engine.block.setPositionY(hiddenTextBlock, value = 250F)
    engine.block.setWidth(hiddenTextBlock, value = 220F)
    engine.block.setHeight(hiddenTextBlock, value = 80F)
    engine.block.setVisible(block = hiddenTextBlock, visible = false)
    engine.block.appendChild(parent = page, child = hiddenTextBlock)

    val hiddenTextCover = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(hiddenTextCover, name = "Hidden Text Cover")
    engine.block.setShape(hiddenTextCover, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = hiddenTextCover, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = hiddenTextCover,
        color = Color.fromRGBA(r = 0.65F, g = 0.25F, b = 0.15F, a = 1F),
    )
    engine.block.setPositionX(hiddenTextCover, value = 80F)
    engine.block.setPositionY(hiddenTextCover, value = 250F)
    engine.block.setWidth(hiddenTextCover, value = 220F)
    engine.block.setHeight(hiddenTextCover, value = 80F)
    engine.block.appendChild(parent = page, child = hiddenTextCover)

    val excludedTextBlock = engine.block.create(DesignBlockType.Text)
    engine.block.setName(excludedTextBlock, name = "Export Excluded Text")
    engine.block.replaceText(block = excludedTextBlock, text = "Excluded from export")
    engine.block.setFloat(block = excludedTextBlock, property = "text/fontSize", value = 30F)
    engine.block.setPositionX(excludedTextBlock, value = 470F)
    engine.block.setPositionY(excludedTextBlock, value = 250F)
    engine.block.setWidth(excludedTextBlock, value = 220F)
    engine.block.setHeight(excludedTextBlock, value = 80F)
    engine.block.setIncludedInExport(block = excludedTextBlock, enabled = false)
    engine.block.appendChild(parent = page, child = excludedTextBlock)

    val excludedTextCover = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(excludedTextCover, name = "Export Excluded Text Cover")
    engine.block.setShape(excludedTextCover, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = excludedTextCover, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = excludedTextCover,
        color = Color.fromRGBA(r = 0.15F, g = 0.45F, b = 0.65F, a = 1F),
    )
    engine.block.setPositionX(excludedTextCover, value = 470F)
    engine.block.setPositionY(excludedTextCover, value = 250F)
    engine.block.setWidth(excludedTextCover, value = 220F)
    engine.block.setHeight(excludedTextCover, value = 80F)
    engine.block.appendChild(parent = page, child = excludedTextCover)

    val placeholder = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(placeholder, name = "Unfilled Placeholder")
    engine.block.setShape(placeholder, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = placeholder, fill = engine.block.createFill(FillType.Image))
    engine.block.setPositionX(placeholder, value = 50F)
    engine.block.setPositionY(placeholder, value = 400F)
    engine.block.setWidth(placeholder, value = 150F)
    engine.block.setHeight(placeholder, value = 100F)
    engine.block.appendChild(parent = page, child = placeholder)
    engine.block.setScopeEnabled(block = placeholder, key = "fill/change", enabled = true)
    engine.block.setPlaceholderEnabled(block = placeholder, enabled = true)
    if (engine.block.supportsPlaceholderBehavior(placeholder)) {
        engine.block.setPlaceholderBehaviorEnabled(block = placeholder, enabled = true)
    }

    val filledColorPlaceholder = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(filledColorPlaceholder, name = "Filled Color Placeholder")
    engine.block.setShape(filledColorPlaceholder, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = filledColorPlaceholder, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(
        block = filledColorPlaceholder,
        color = Color.fromRGBA(r = 0.25F, g = 0.55F, b = 0.9F, a = 1F),
    )
    engine.block.setPositionX(filledColorPlaceholder, value = 560F)
    engine.block.setPositionY(filledColorPlaceholder, value = 400F)
    engine.block.setWidth(filledColorPlaceholder, value = 150F)
    engine.block.setHeight(filledColorPlaceholder, value = 100F)
    engine.block.appendChild(parent = page, child = filledColorPlaceholder)
    engine.block.setScopeEnabled(block = filledColorPlaceholder, key = "fill/change", enabled = true)
    engine.block.setPlaceholderEnabled(block = filledColorPlaceholder, enabled = true)
    if (engine.block.supportsPlaceholderBehavior(filledColorPlaceholder)) {
        engine.block.setPlaceholderBehaviorEnabled(block = filledColorPlaceholder, enabled = true)
    }

    val secondPage = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(secondPage, value = 800F)
    engine.block.setHeight(secondPage, value = 600F)
    engine.block.appendChild(parent = scene, child = secondPage)

    val otherPagePlaceholder = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(otherPagePlaceholder, name = "Other Page Placeholder")
    engine.block.setShape(otherPagePlaceholder, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(block = otherPagePlaceholder, fill = engine.block.createFill(FillType.Image))
    engine.block.setPositionX(otherPagePlaceholder, value = 300F)
    engine.block.setPositionY(otherPagePlaceholder, value = 300F)
    engine.block.setWidth(otherPagePlaceholder, value = 150F)
    engine.block.setHeight(otherPagePlaceholder, value = 100F)
    engine.block.appendChild(parent = secondPage, child = otherPagePlaceholder)
    engine.block.setScopeEnabled(block = otherPagePlaceholder, key = "fill/change", enabled = true)
    engine.block.setPlaceholderEnabled(block = otherPagePlaceholder, enabled = true)
    if (engine.block.supportsPlaceholderBehavior(otherPagePlaceholder)) {
        engine.block.setPlaceholderBehaviorEnabled(block = otherPagePlaceholder, enabled = true)
    }

    return validateDesign(engine = engine, page = page)
}
