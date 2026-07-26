// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.invalidatePlacement
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.daio.wild.style.Border
import io.daio.wild.style.BorderDefaults
import io.daio.wild.style.Style
import io.daio.wild.style.StyleScope
import io.daio.wild.style.defaultScaleAnimationSpec
import io.daio.wild.style.forInnerShape
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Internal benchmark-only candidate path for THE-217. Installs a single [InteractionStyleNode]
 * directly instead of the six-element traversal chain installed by
 * [io.daio.wild.style.interactionStyle]. Used only by tests and playbook benchmark wiring; never
 * exposed publicly, and does not change the behavior of [io.daio.wild.style.interactionStyle].
 */
internal fun Modifier.interactionStyleComposite(
    interactionSource: InteractionSource?,
    enabled: Boolean = true,
    selected: Boolean = false,
    style: Style,
): Modifier =
    this then
        InteractionStyleElement(
            interactionSource = interactionSource,
            enabled = enabled,
            selected = selected,
            resolver = StyleResolver.Value(style),
        )

/**
 * Block-based overload of [interactionStyleComposite]. See that function for details.
 */
internal fun Modifier.interactionStyleComposite(
    interactionSource: InteractionSource?,
    enabled: Boolean = true,
    selected: Boolean = false,
    block: StyleScope.() -> Unit,
): Modifier =
    this then
        InteractionStyleElement(
            interactionSource = interactionSource,
            enabled = enabled,
            selected = selected,
            resolver = StyleResolver.Block(block),
        )

/**
 * Internal benchmark-only candidate element for THE-217. Installs a single [InteractionStyleNode]
 * that owns interaction collection, style resolution and its visual delegates directly, with no
 * style-parent-to-child traversal. This does not replace the public [io.daio.wild.style.interactionStyle]
 * traversal chain; it exists so [io.daio.wild.style.modifiers.interactionStyleComposite] can be
 * measured against the current implementation.
 */
internal class InteractionStyleElement(
    val interactionSource: InteractionSource?,
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val resolver: StyleResolver,
) : ModifierNodeElement<InteractionStyleNode>() {
    override fun create(): InteractionStyleNode =
        InteractionStyleNode(
            interactionSource = interactionSource,
            enabled = enabled,
            selected = selected,
            resolver = resolver,
        )

    override fun update(node: InteractionStyleNode) {
        node.updateState(
            interactionSource = interactionSource,
            enabled = enabled,
            selected = selected,
            resolver = resolver,
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "InteractionStyleElement"
        properties["interactionSource"] = interactionSource
        properties["enabled"] = enabled
        properties["selected"] = selected
        properties["resolver"] = resolver
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as InteractionStyleElement

        if (interactionSource !== other.interactionSource) return false
        if (enabled != other.enabled) return false
        if (selected != other.selected) return false
        if (resolver != other.resolver) return false

        return true
    }

    override fun hashCode(): Int {
        var result = interactionSource?.hashCode() ?: 0
        result = 31 * result + enabled.hashCode()
        result = 31 * result + selected.hashCode()
        result = 31 * result + resolver.hashCode()
        return result
    }
}

/**
 * Candidate composite node prototyped for THE-217.
 *
 * Owns interaction collection, [StyleScope] resolution and direct references to its layout and
 * draw delegates. There is deliberately no [androidx.compose.ui.node.TraversableNode]
 * participation anywhere in this subtree: interaction state is collected directly from
 * [interactionSource], resolved style fields are compared against the previous resolution to
 * build a change mask, and only the delegates affected by that mask are updated. Delegates that
 * are not updated never call `invalidateDraw`/`invalidatePlacement`.
 *
 * There is a single [LayoutModifierNode] delegate, [scaleNode], applying scale/z-index around the
 * whole surface (including the border), and a single [DrawModifierNode] delegate,
 * [backgroundBorderNode], drawing the background, then clipping/group-alpha-compositing the
 * actual content, then the border. Shape clip and alpha are applied by the draw delegate directly
 * around its `drawContent()` call rather than through a second layout layer: the Compose UI
 * runtime does not allow a [DelegatingNode] to delegate to more than one [LayoutModifierNode]
 * unless the owner itself implements [LayoutModifierNode], and a second layer positioned outside
 * the border (to keep scale surrounding everything) would incorrectly clip/alpha-composite the
 * border too. Applying clip/alpha at draw time, after the border delegate's own background pass
 * but before its border pass, keeps the border outside the clipped region (preserving positive
 * inset/focus-ring behavior) while still scaling with the rest of the surface via [scaleNode].
 */
internal class InteractionStyleNode(
    interactionSource: InteractionSource?,
    enabled: Boolean,
    selected: Boolean,
    resolver: StyleResolver,
) : DelegatingNode(),
    StyleScope,
    ObserverModifierNode {
    /** Visible internally so tests can assert on per-delegate update call counts. */
    internal val scaleNode = delegate(InteractionStyleScaleLayoutNode())
    internal val backgroundBorderNode = delegate(InteractionStyleBackgroundBorderNode())

    override var color: Color = Color.Unspecified
    override var alpha: Float = 1f
    override var scale: Float = 1f
    override var shape: Shape = RectangleShape
    override var border: Border = BorderDefaults.None
    override var scaleAnimationSpec: AnimationSpec<Float>? = null

    override val focused: Boolean
        get() = _focused

    override val hovered: Boolean
        get() = _hovered

    override val pressed: Boolean
        get() = _pressed

    override val selected: Boolean
        get() = _selected

    override val enabled: Boolean
        get() = _enabled

    private var interactionSource: InteractionSource? = interactionSource
    private var resolver: StyleResolver = resolver
    private var _enabled: Boolean = enabled
    private var _selected: Boolean = selected
    private var _focused: Boolean = false
    private var _hovered: Boolean = false
    private var _pressed: Boolean = false

    private var collectionJob: Job? = null
    private val activePresses = mutableSetOf<PressInteraction.Press>()
    private val activeHovers = mutableSetOf<HoverInteraction.Enter>()
    private val activeFocuses = mutableSetOf<FocusInteraction.Focus>()

    private var isUpdating = false
    private var needsUpdate = false
    private var lastResolvedSnapshot: InteractionStyleSnapshot? = null

    /** Number of times [dispatchIfChanged] found a changed resolution. Exposed for tests. */
    internal var dispatchCount: Int = 0
        private set

    fun updateState(
        interactionSource: InteractionSource?,
        enabled: Boolean,
        selected: Boolean,
        resolver: StyleResolver,
    ) {
        val sourceChanged = this.interactionSource !== interactionSource
        if (!sourceChanged && _enabled == enabled && _selected == selected && this.resolver == resolver) {
            return
        }

        _enabled = enabled
        _selected = selected
        this.resolver = resolver

        if (sourceChanged) {
            this.interactionSource = interactionSource
            restartCollection()
        } else {
            updateStyle()
        }
    }

    override fun onAttach() {
        restartCollection()
    }

    override fun onDetach() {
        collectionJob?.cancel()
        collectionJob = null
    }

    override fun onReset() {
        collectionJob?.cancel()
        collectionJob = null
        activePresses.clear()
        activeHovers.clear()
        activeFocuses.clear()
        _focused = false
        _hovered = false
        _pressed = false
        resetResolvedStyle()
        lastResolvedSnapshot = null
        isUpdating = false
        needsUpdate = false
        dispatchCount = 0
    }

    override fun onObservedReadsChanged() {
        if (isAttached) updateStyle()
    }

    private fun restartCollection() {
        collectionJob?.cancel()
        collectionJob = null
        activePresses.clear()
        activeHovers.clear()
        activeFocuses.clear()
        _focused = false
        _hovered = false
        _pressed = false

        val source = interactionSource
        if (source != null) {
            collectionJob =
                coroutineScope.launch {
                    source.interactions.collect { interaction -> handleInteraction(interaction) }
                }
        }
        updateStyle()
    }

    private fun handleInteraction(interaction: Interaction) {
        when (interaction) {
            is PressInteraction.Press -> activePresses += interaction
            is PressInteraction.Release -> activePresses -= interaction.press
            is PressInteraction.Cancel -> activePresses -= interaction.press
            is HoverInteraction.Enter -> activeHovers += interaction
            is HoverInteraction.Exit -> activeHovers -= interaction.enter
            is FocusInteraction.Focus -> activeFocuses += interaction
            is FocusInteraction.Unfocus -> activeFocuses -= interaction.focus
            else -> return
        }

        val focused = activeFocuses.isNotEmpty()
        val hovered = activeHovers.isNotEmpty()
        val pressed = activePresses.isNotEmpty()
        if (focused != _focused || hovered != _hovered || pressed != _pressed) {
            _focused = focused
            _hovered = hovered
            _pressed = pressed
            updateStyle()
        }
    }

    private fun updateStyle() {
        if (isUpdating) {
            needsUpdate = true
            return
        }

        isUpdating = true
        try {
            do {
                needsUpdate = false
                resolveStyle()
                dispatchIfChanged()
            } while (needsUpdate)
        } finally {
            isUpdating = false
        }
    }

    private fun resolveStyle() {
        when (val currentResolver = resolver) {
            is StyleResolver.Block ->
                observeReads {
                    resetResolvedStyle()
                    currentResolver.block(this)
                }

            is StyleResolver.Value -> resolveValue(currentResolver.style)
        }
    }

    private fun resolveValue(style: Style) {
        color =
            style.colors.colorFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        scale =
            style.scale.scaleFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        alpha =
            style.alpha.alphaFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        shape =
            style.shapes.shapeFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        border =
            style.borders.borderFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        scaleAnimationSpec = style.scale.animationSpec
    }

    private fun resetResolvedStyle() {
        color = Color.Unspecified
        alpha = 1f
        scale = 1f
        shape = RectangleShape
        border = BorderDefaults.None
        scaleAnimationSpec = null
    }

    /**
     * Compares the freshly resolved fields against the last dispatched snapshot and only invokes
     * the delegates whose inputs actually changed. Delegates perform their own equality checks
     * before calling `invalidateDraw`/`invalidatePlacement`, so an unchanged mask entry never
     * triggers a phase invalidation.
     */
    private fun dispatchIfChanged() {
        val newSnapshot = currentSnapshot()
        val old = lastResolvedSnapshot
        if (newSnapshot == old) return
        lastResolvedSnapshot = newSnapshot

        val backgroundChanged = old == null || old.color != newSnapshot.color || old.shape != newSnapshot.shape
        val borderChanged = old == null || old.border != newSnapshot.border || old.shape != newSnapshot.shape
        val contentChanged = old == null || old.shape != newSnapshot.shape || old.alpha != newSnapshot.alpha
        val scaleChanged =
            old == null ||
                old.scale != newSnapshot.scale ||
                old.scaleAnimationSpec != newSnapshot.scaleAnimationSpec ||
                old.focused != newSnapshot.focused ||
                old.hovered != newSnapshot.hovered ||
                old.pressed != newSnapshot.pressed

        if (backgroundChanged) {
            backgroundBorderNode.updateBackground(color = newSnapshot.color, shape = newSnapshot.shape)
        }
        if (borderChanged) {
            backgroundBorderNode.updateBorder(
                shape = newSnapshot.border.forInnerShape(newSnapshot.shape),
                borderStroke = newSnapshot.border.borderStroke,
                inset = newSnapshot.border.inset,
            )
        }
        if (contentChanged) {
            backgroundBorderNode.updateContent(shape = newSnapshot.shape, alpha = newSnapshot.alpha)
        }
        if (scaleChanged) {
            scaleNode.updateScale(
                scale = newSnapshot.scale,
                zIndex = if (newSnapshot.focused || newSnapshot.hovered) 0.5f else 0f,
                focused = newSnapshot.focused,
                pressed = newSnapshot.pressed,
                hovered = newSnapshot.hovered,
                animationSpec = newSnapshot.scaleAnimationSpec,
            )
        }

        dispatchCount++
    }

    private fun currentSnapshot() =
        InteractionStyleSnapshot(
            color = color,
            alpha = alpha,
            scale = scale,
            shape = shape,
            border = border,
            scaleAnimationSpec = scaleAnimationSpec,
            focused = focused,
            hovered = hovered,
            pressed = pressed,
            selected = selected,
            enabled = enabled,
        )
}

/** Complete resolved-state snapshot used to detect whether a resolution actually changed. */
private data class InteractionStyleSnapshot(
    val color: Color,
    val alpha: Float,
    val scale: Float,
    val shape: Shape,
    val border: Border,
    val scaleAnimationSpec: AnimationSpec<Float>?,
    val focused: Boolean,
    val hovered: Boolean,
    val pressed: Boolean,
    val selected: Boolean,
    val enabled: Boolean,
)

/**
 * Scale/z-index layout delegate for [InteractionStyleNode]. Surrounds the whole surface,
 * including the background/content/border drawn by [InteractionStyleBackgroundBorderNode], so
 * scale/z-index animate the entire element. Driven entirely by direct calls from the owning
 * [InteractionStyleNode]; this node never participates in traversal.
 */
internal class InteractionStyleScaleLayoutNode : LayoutModifierNode, Modifier.Node() {
    private var zIndex: Float = 0f
    private var scale: Float = 1f
    private var scaleState = AnimationState(initialValue = scale)
    private var updateJob: Job? = null
    private var customAnimationSpec: AnimationSpec<Float>? = null
    private val animationRequestCoalescer = ScaleAnimationRequestCoalescer()

    internal val animatedScaleForTest: Float
        get() = scaleState.value

    internal val isScaleAnimationRunningForTest: Boolean
        get() = updateJob?.isActive == true

    /** Number of times [updateScale] was invoked by the owning node. Exposed for tests. */
    internal var updateCallCountForTest: Int = 0
        private set

    /** Invalidation is handled explicitly by [updateScale]. */
    override val shouldAutoInvalidate: Boolean
        get() = false

    override fun onReset() {
        updateJob?.cancel()
        updateJob = null
        animationRequestCoalescer.reset()
        scaleState = AnimationState(initialValue = 1f)
        scale = 1f
        zIndex = 0f
        customAnimationSpec = null
        updateCallCountForTest = 0
    }

    fun updateScale(
        scale: Float,
        zIndex: Float,
        focused: Boolean,
        pressed: Boolean,
        hovered: Boolean,
        animationSpec: AnimationSpec<Float>?,
    ) {
        if (!isAttached) return
        updateCallCountForTest++

        this.scale = scale
        this.customAnimationSpec = animationSpec
        if (this.zIndex != zIndex) {
            this.zIndex = zIndex
            invalidatePlacement()
        }

        if (
            !animationRequestCoalescer.shouldAnimate(
                scale = scale,
                animationSpec = animationSpec,
                focused = focused,
                pressed = pressed,
                hovered = hovered,
            )
        ) {
            return
        }

        val effectiveAnimationSpec =
            animationSpec
                ?: defaultScaleAnimationSpec(focused = focused, pressed = pressed, hovered = hovered)

        updateJob?.cancel()
        updateJob =
            coroutineScope.launch {
                try {
                    scaleState.animateTo(
                        targetValue = scale,
                        animationSpec = effectiveAnimationSpec,
                    )
                } finally {
                    invalidatePlacement()
                }
            }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            val animatedScale = scaleState.value
            if (needsScaleLayer(animatedScale, zIndex)) {
                placeable.placeWithLayer(0, 0, zIndex = zIndex) {
                    scaleX = animatedScale
                    scaleY = animatedScale
                }
            } else {
                placeable.place(0, 0)
            }
        }
    }
}

/**
 * Combined background + content-clip/alpha + border draw delegate for [InteractionStyleNode].
 * Draws the background, then the wrapped content (clipped/group-alpha-composited to the current
 * shape/alpha as needed), then the border, matching the externally visible draw order of the
 * current traversal chain. Clip/alpha are applied manually around `drawContent()` (clipping via
 * [clipPath] and, when `alpha < 1f`, an explicit `Canvas.saveLayer` for correct group-alpha
 * compositing of overlapping content) rather than through a [LayoutModifierNode] graphics layer,
 * see [InteractionStyleNode] for why. Driven entirely by direct calls from the owning node; never
 * participates in traversal.
 */
internal class InteractionStyleBackgroundBorderNode :
    DrawModifierNode,
    Modifier.Node(),
    ObserverModifierNode {
    private var backgroundColor: Color = Color.Unspecified
    private var backgroundShape: Shape = RectangleShape
    private var contentShape: Shape = RectangleShape
    private var contentAlpha: Float = 1f
    private var borderShape: Shape = RectangleShape
    private var borderStroke: BorderStroke = BorderStroke(0.dp, Color.Unspecified)
    private var borderInset: Dp = 0.dp

    private var lastSize: Size = Size.Unspecified
    private var lastLayoutDirection: LayoutDirection? = null
    private var lastOutline: Outline? = null
    private var lastBackgroundShape: Shape? = null

    private var lastContentSize: Size = Size.Unspecified
    private var lastContentLayoutDirection: LayoutDirection? = null
    private var lastContentOutline: Outline? = null
    private var lastContentShape: Shape? = null

    private var borderOutlineCache: ShapeOutlineCache? = null
    private var borderStrokeCache: OutlineStrokeCache? = null

    /** Number of times [updateBackground] was invoked by the owning node. Exposed for tests. */
    internal var backgroundUpdateCallCountForTest: Int = 0
        private set

    /** Number of times [updateContent] was invoked by the owning node. Exposed for tests. */
    internal var contentUpdateCallCountForTest: Int = 0
        private set

    /** Number of times [updateBorder] was invoked by the owning node. Exposed for tests. */
    internal var borderUpdateCallCountForTest: Int = 0
        private set

    /** Invalidation is handled explicitly by [updateBackground]/[updateContent]/[updateBorder]. */
    override val shouldAutoInvalidate: Boolean
        get() = false

    override fun onReset() {
        backgroundColor = Color.Unspecified
        backgroundShape = RectangleShape
        contentShape = RectangleShape
        contentAlpha = 1f
        borderShape = RectangleShape
        borderStroke = BorderStroke(0.dp, Color.Unspecified)
        borderInset = 0.dp
        lastSize = Size.Unspecified
        lastLayoutDirection = null
        lastOutline = null
        lastBackgroundShape = null
        lastContentSize = Size.Unspecified
        lastContentLayoutDirection = null
        lastContentOutline = null
        lastContentShape = null
        borderOutlineCache = null
        borderStrokeCache = null
        backgroundUpdateCallCountForTest = 0
        contentUpdateCallCountForTest = 0
        borderUpdateCallCountForTest = 0
    }

    fun updateBackground(
        color: Color,
        shape: Shape,
    ) {
        backgroundUpdateCallCountForTest++
        if (this.backgroundColor != color || this.backgroundShape != shape) {
            this.backgroundColor = color
            this.backgroundShape = shape
            invalidateDraw()
        }
    }

    fun updateContent(
        shape: Shape,
        alpha: Float,
    ) {
        contentUpdateCallCountForTest++
        if (this.contentShape != shape || this.contentAlpha != alpha) {
            this.contentShape = shape
            this.contentAlpha = alpha
            invalidateDraw()
        }
    }

    fun updateBorder(
        shape: Shape,
        borderStroke: BorderStroke,
        inset: Dp,
    ) {
        borderUpdateCallCountForTest++
        if (this.borderShape != shape || this.borderStroke != borderStroke || this.borderInset != inset) {
            this.borderShape = shape
            this.borderStroke = borderStroke
            this.borderInset = inset
            invalidateDraw()
        }
    }

    override fun ContentDrawScope.draw() {
        drawBackground()
        drawClippedContent()
        drawBorder()
    }

    override fun onObservedReadsChanged() {
        lastSize = Size.Unspecified
        lastLayoutDirection = null
        lastOutline = null
        lastBackgroundShape = null
        lastContentSize = Size.Unspecified
        lastContentLayoutDirection = null
        lastContentOutline = null
        lastContentShape = null
        invalidateDraw()
    }

    private fun ContentDrawScope.drawClippedContent() {
        if (!needsShapeLayer(contentShape, contentAlpha)) {
            drawContent()
            return
        }

        val contentDrawScope = this
        val needsClip = contentShape !== RectangleShape
        val needsGroupAlpha = contentAlpha != 1f

        if (needsGroupAlpha) {
            val paint = Paint().apply { alpha = contentAlpha }
            drawContext.canvas.saveLayer(Rect(Offset.Zero, size), paint)
            try {
                if (needsClip) {
                    clipToContentShape(contentDrawScope)
                } else {
                    contentDrawScope.drawContent()
                }
            } finally {
                drawContext.canvas.restore()
            }
        } else {
            clipToContentShape(contentDrawScope)
        }
    }

    private fun ContentDrawScope.clipToContentShape(contentDrawScope: ContentDrawScope) {
        val path = Path().apply { addOutline(getContentOutline()) }
        clipPath(path) { contentDrawScope.drawContent() }
    }

    private fun ContentDrawScope.getContentOutline(): Outline {
        var outline: Outline? = null
        if (
            size == lastContentSize &&
            layoutDirection == lastContentLayoutDirection &&
            lastContentShape == contentShape &&
            lastContentOutline != null
        ) {
            outline = lastContentOutline
        } else {
            observeReads {
                outline = contentShape.createOutline(size, layoutDirection, this)
            }
        }
        lastContentOutline = outline
        lastContentSize = size
        lastContentLayoutDirection = layoutDirection
        lastContentShape = contentShape
        return outline!!
    }

    private fun ContentDrawScope.drawBackground() {
        if (backgroundShape === RectangleShape) {
            if (backgroundColor.isSpecified) {
                drawRect(color = backgroundColor)
            }
        } else {
            if (backgroundColor.isSpecified) {
                drawOutline(getBackgroundOutline(), color = backgroundColor)
            }
        }
    }

    private fun ContentDrawScope.getBackgroundOutline(): Outline {
        var outline: Outline? = null
        if (
            size == lastSize &&
            layoutDirection == lastLayoutDirection &&
            lastBackgroundShape == backgroundShape &&
            lastOutline != null
        ) {
            outline = lastOutline
        } else {
            observeReads {
                outline = backgroundShape.createOutline(size, layoutDirection, this)
            }
        }
        lastOutline = outline
        lastSize = size
        lastLayoutDirection = layoutDirection
        lastBackgroundShape = backgroundShape
        return outline!!
    }

    private fun ContentDrawScope.drawBorder() {
        val borderStrokeWidthPx = with(requireDensity()) { borderStroke.width.toPx() }
        if (borderStrokeWidthPx <= 0f) return

        if (borderOutlineCache == null) {
            borderOutlineCache =
                ShapeOutlineCache(
                    shape = borderShape,
                    size = size,
                    layoutDirection = layoutDirection,
                    density = this,
                )
        }
        if (borderStrokeCache == null) {
            borderStrokeCache = OutlineStrokeCache(widthPx = borderStrokeWidthPx)
        }

        inset(inset = with(requireDensity()) { -borderInset.toPx() }) {
            val shapeOutline =
                borderOutlineCache!!.updatedOutline(
                    shape = borderShape,
                    size = size,
                    layoutDirection = layoutDirection,
                    density = requireDensity(),
                )
            val outlineStroke = borderStrokeCache!!.updatedOutlineStroke(widthPx = borderStrokeWidthPx)

            drawOutline(
                outline = shapeOutline,
                brush = borderStroke.brush,
                alpha = 1f,
                style = outlineStroke,
            )
        }
    }
}
