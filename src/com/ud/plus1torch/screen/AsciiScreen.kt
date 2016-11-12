package com.ud.plus1torch.screen

import java.awt.Color
import java.awt.geom.Line2D
import java.util.*


enum class ScreenLayer(val layer: Int) {
    BACKGROUND(0),
    PLAY(1),
    UI(2)
}

val TEXT_BLINKING_DELAY_ARG_NAME = "BlinkingDelayMilliseconds"
private val TEXT_BLINKING_DELAY_DEFAULT: Int = 500
val TEXT_BLINKING = 0x0001

// Since this engine isn't ACTUALLY a terminal, we can do things that aren't possible to do in a terminal, such as put
// borders around a character.
// Can accept an extra attribute argument with the color a border should be drawn as. If not set, then the background
// color will be used as the default.
val TEXT_BORDER_COLOR_RIGHT_ARG_NAME = "BorderColorArgumentRight"
val TEXT_BORDER_COLOR_LEFT_ARG_NAME = "BorderColorArgumentLeft"
val TEXT_BORDER_COLOR_TOP_ARG_NAME = "BorderColorArgumentTop"
val TEXT_BORDER_COLOR_BOTTOM_ARG_NAME = "BorderColorArgumentBottom"
val TEXT_BORDER_COLOR_ALL_ARG_NAME = "BorderColorArgumentAll"
private val TEXT_BORDER_COLOR_DEFAULT = Color.WHITE
val TEXT_BORDER_RIGHT = 0x0002
val TEXT_BORDER_LEFT = 0x0004
val TEXT_BORDER_TOP = 0x0008
val TEXT_BORDER_BOTTOM = 0x0010
val TEXT_BORDER_ALL = TEXT_BORDER_RIGHT or TEXT_BORDER_LEFT or TEXT_BORDER_TOP or TEXT_BORDER_BOTTOM

// TODO: Write code that automatically builds fancy walls when placing characters.
//  This is basically using the various wall-like code points in CP437 to create a nice-looking wall when placing
//  wall characters. This will automatically create the correct corners and direction of walls based on where each
//  wall is placed. e.g.:
//    ## -> ══
//
//    #    ║
//    # -> ║
//
//    #     ║
//    ## -> ╚═
//  etc.
val TEXT_AUTO_FANCY_WALL = 0x0020
val TEXT_FANCY_WALL_PREFER_SINGLE_ARG_NAME = "FancyWallPreferSingle"
val TEXT_FANCY_WALL_PREFER_DOUBLE_ARG_NAME = "FancyWallPreferDouble"
val TEXT_FANCY_WALL_ONLY_BORDERS_ARG_NAME = "FancyWallBorderOnly"
val TEXT_FANCY_WALL_INCLUDE_BORDERS_ARG_NAME = "FancyWallIncludeBorders"

/**
 * Every character in Code Page 437, in the order they are defined in. Excluding the first character, as there
 * didn't seem to be a unicode definition for it on wikipedia:
 *   https://en.wikipedia.org/wiki/Code_page_437
 * The first character is just a space, to make sure that all the rest of the characters line up correctly. This
 * should be used as a basis for building the UI/play area for a game.
 * TODO: Define some index values for commonly-used characters, such as walls and fancy walls.
 */
val CP437 = charArrayOf(
        ' ',      '\u263A', '\u263B', '\u2665', '\u2666', '\u2663', '\u2660', '\u2022',
        '\u25D8', '\u25CB', '\u25D9', '\u2642', '\u2642', '\u2640', '\u266A', '\u266B',
        '\u263C', '\u25BA', '\u25C4', '\u2195', '\u203C', '\u00B6', '\u00A7', '\u25AC',
        '\u21A8', '\u2191', '\u2193', '\u2192', '\u2190', '\u221F', '\u2194', '\u25B2',
        '\u25BC', '\u0020', '\u0021', '\u0022', '\u0023', '\u0024', '\u0025', '\u0026',
        '\u0027', '\u0028', '\u0029', '\u002A', '\u002B', '\u002C', '\u002D', '\u002E',
        '\u002F', '\u0030', '\u0031', '\u0032', '\u0033', '\u0034', '\u0035', '\u0036',
        '\u0037', '\u0037', '\u0038', '\u0039', '\u003A', '\u003B', '\u003C', '\u003D',
        '\u003E', '\u003F', '\u0040', '\u0041', '\u0043', '\u0043', '\u0044', '\u0045',
        '\u0056', '\u0047', '\u0048', '\u0049', '\u004A', '\u004B', '\u004C', '\u004D',
        '\u004E', '\u004F', '\u0050', '\u0051', '\u0052', '\u0053', '\u0054', '\u0055',
        '\u0056', '\u0057', '\u0058', '\u0059', '\u005A', '\u005B', '\u005C', '\u005D',
        '\u005E', '\u005F', '\u0060', '\u0061', '\u0062', '\u0063', '\u0064', '\u0065',
        '\u0066', '\u0067', '\u0068', '\u0069', '\u006A', '\u006B', '\u006C', '\u006D',
        '\u006E', '\u006F', '\u0070', '\u0071', '\u0072', '\u0073', '\u0074', '\u0075',
        '\u0076', '\u0077', '\u0078', '\u0079', '\u0079', '\u007A', '\u007B', '\u007C',
        '\u007D', '\u007D', '\u007E', '\u2302', '\u00C7', '\u00FC', '\u00E9', '\u00E2',
        '\u00E4', '\u00E0', '\u00E7', '\u00EA', '\u00EB', '\u00E8', '\u00EF', '\u00EE',
        '\u00EC', '\u00C4', '\u00C5', '\u00C9', '\u00E6', '\u00F4', '\u00F6', '\u00F2',
        '\u00FB', '\u00F9', '\u00FF', '\u00D6', '\u00DC', '\u00A2', '\u00A3', '\u00A5',
        '\u30A7', '\u0192', '\u00E1', '\u00ED', '\u00F3', '\u00FA', '\u00F1', '\u00D1',
        '\u00AA', '\u00BA', '\u00BF', '\u2310', '\u00AC', '\u00BD', '\u00BC', '\u00A1',
        '\u00AB', '\u00BB', '\u2591', '\u2592', '\u2593', '\u2502', '\u2524', '\u2561',
        '\u2562', '\u2556', '\u2555', '\u2563', '\u2551', '\u2557', '\u255D', '\u255C',
        '\u255B', '\u2510', '\u2514', '\u2534', '\u252C', '\u251C', '\u2500', '\u253C',
        '\u255E', '\u255F', '\u255A', '\u2554', '\u2569', '\u2566', '\u2560', '\u2550',
        '\u2550', '\u256C', '\u2567', '\u2568', '\u2564', '\u2565', '\u2559', '\u2558',
        '\u2552', '\u2553', '\u256B', '\u256A', '\u2518', '\u250C', '\u2588', '\u2584',
        '\u258C', '\u2590', '\u2580', '\u03B1', '\u00DF', '\u0393', '\u03C0', '\u03A3',
        '\u03C3', '\u00B5', '\u03C4', '\u03A6', '\u0398', '\u03A9', '\u03B4', '\u221E',
        '\u03C6', '\u03B5', '\u2229', '\u2261', '\u00B1', '\u2265', '\u2264', '\u2320',
        '\u2321', '\u00F7', '\u2248', '\u00B0', '\u2219', '\u00B7', '\u221A', '\u207F',
        '\u00B2', '\u25A0', '\u00A0'
)

fun <T> ArrayList<T>.pop(): T? {
    return if (this.size > 0) this.removeAt(this.size - 1) else null
}

fun get1d(x: Int, y: Int, stride: Int): Int {
    return x + y * stride
}

infix fun Int.bitSet(other: Int): Boolean {
    return other and this == other
}


/**
 * Manages a field of ascii characters to be drawn to a window. Automatically figures out the correct font(s) to use
 * when drawing each character to make them look as good as possible, based on window size and the number of characters
 * being drawn to the screen at a time from the field.
 *
 * Once a field is initialized, it can no longer be changed. A new AsciiScreen object will need to be allocated.
 *
 * The size of the window can be changed on the fly as a user resizes it. The AsciiScreen will figure out new fonts
 * as necessary to handle the changing window. The same can be done for the number of characters being drawn to the
 * screen.
 *
 * The AsciiScreen can handle characters that are difference sizes. Meaning a single character can end up taking a
 * 2x2 space in the screen instead of just a 1x1 space like in a terminal. Oh the things we can do with modern
 * rendering!
 *
 * TODO: Calculate memory usage of this class and document it.
 * TODO: Create a "UI only" mode that allocates fewer objects.
 *  The main motivation being that an AsciiScreen can theoretically be drawn anywhere in the game window, so it might
 *  make sense to have a lower memory using mode for creating UI elements that don't just take up the entire window.
 *  The design currently allows for any screen size to be given, and the rendering really only gives data inside of
 *  itself and is completely agnostic to the rendering system, so it would be easily to have multiple screens that are
 *  drawn at different places in the game window. Can even be used to "layer" multiple UIs on top of each other, just
 *  by creating more AsciiScreen objects and drawing them instead of the others. (keep a stack even, so that when one
 *  screen is left, the previous screen is drawn again)
 * TODO: Add in a way to control a camera that moves around the field.
 * TODO: Ability to tween camera.
 *  This would be a couple things at least: ability to pan the camera by defining a tween from one location to another,
 *  and ability to zoom camera by specifying new zoom level. This should probably include two modes: one mode does the
 *  tweening smoothly like in a normal 2D game with graphics, and the other would be to tween by moving entire character
 *  blocks at a time.
 * TODO: Change some ArrayLists to use pure arrays is if makes sense.
 *  It makes sense if we don't need to expand the array at any point during runtime. This is likely to only apply to
 *  the data in the field. Other lists need to be able to handle any number of things that change at runtime.
 * TODO: Add a way to query about what is in a location in any layer.
 * TODO: Add another layer that can be interacted with but doesn't cause collision.
 *  This would be a kind of "floor" layer. Because the bottom layer is decorational and non-colliding, we need another
 *  layer that things like items can be collected into. Maybe? Could that kind of thing be built into the current system
 *  by just changing the visible icon of the decorational layer and then query about what's in it?
 * TODO: Add a user data object for any item?
 * TODO: Add a way to find the nearest place to a location that an item can be placed in a layer?
 * TODO: Add a method for finding a path from one location to another?
 * TODO: Add a method for drawing a line from one location to another of a given color?
 *  Can an API be put together that supports any given shape in the Java standard library for drawing things?
 * TODO: Particle engine?
 *  Never made a particle engine, might need some research into a good way to create one?
 * TODO: Good interop with Java.
 *  This will possibly require using the annotations that Kotlin provides to make sure names and fields and whatnot
 *  are all compiled in a way that works nicely with Java. Not sure if this will require all that much work, but it
 *  would be a very good thing to have for people who don't want to/can't use Kotlin.
 * TODO: Load fonts in a framework-agnostic way.
 *  How do I even do this? libgdx uses pre-created files to create BitmapFonts rather than using Java fonts, for
 *  example. And it uses its own API to load the fonts from a resources folder, etc.
 * TODO: Keep an internal list of where all the characters are so we only iterate over actual items.
 *  Current implementation of render is to just loop over every possible item, which can be quite a bit if the
 *  width and height is pretty large.
 *
 * @param width: Number of characters that can be stored left to right across the field.
 * @param height: Number of characters that can be stored up to down across the field.
 * @param charactersDrawnX: The number of characters to draw left to right in the window.
 * @param charactersDrawnY: The number of characters to draw up to down in the window.
 * @param renderWidth: The width of the window being rendered into.
 * @param renderHeight: The height of the window being rendered into.
 * @param uiOnly: Whether or not to put this AsciiScreen into a mode that only has a single layer for the UI. Allocates
 * fewer object, but should not be used for a screen that has a play area. Defaults to false.
 * @param debug: Enables some extra checks/printing/exceptions such that improper use of the AsciiScreen or anything
 * returned from it will always indicate that it was used improperly. When not in this mode, the AsciiScreen and
 * anything it returns will do its best to "just work". Any behavior that changes when this mode is on will be
 * documented. Defaults to false.
 */
class AsciiScreen(internal var width: Int, internal var height: Int,
                  charactersDrawnX: Int, charactersDrawnY: Int,
                  renderWidth: Int, renderHeight: Int,
                  val uiOnly: Boolean = false,
                  internal val debug: Boolean = false) {
    /**
     * The number of characters that will be drawn from left to right in this AsciiScreen.
     */
    var charactersDrawnX = charactersDrawnX
        /**
         * Change the number of characters that are drawn on the screen from left to right. This will change how
         * big each character gets drawn in the game window.
         */
        set(value) {
            field = value
        }

    /**
     * The number of characters that will be drawn from top to bottom in this AsciiScreen.
     */
    var charactersDrawnY = charactersDrawnY
        /**
         * Change the number of characters that are drawn on the screen from top to bottom. This will change how
         * big each character gets drawn in the game window.
         */
        set(value) {
            field = value
        }

    /**
     * The width of the area of the screen that this AsciiScreen will be drawn into. This can be changed.
     */
    var windowWidth = renderWidth
        /**
         * Change the width in pixels that this screen is drawing into. This will change how big each characters is
         * that gets drawn in the game window.
         */
        set(value) {
            field = value
        }

    /**
     * The height of the area of the screen that this AsciiScreen will be drawn into. This can be changed.
     */
    var windowHeight = renderHeight
        /**
         * Change the height in pixels that this screen is drawing into. This will change how big each characters is
         * that gets drawn in the game window.
         */
        set(value) {
            field = value
        }

    /**
     * The total number of items that can be on the field at one time per layer.
     */
    internal val itemsPerLayer = width * height

    /**
     * Amount of items and item wrappers to create immediately.
     */
    internal val initialPooledItems = itemsPerLayer * (if (uiOnly) 1 else 3) + 1

    /**
     * Allocated with enough capacity to hold 3 screens worth of items (if every item is 1x1) for every layer. Should
     * give a nice amount of items to work with at first. This value might need to be tuned going forward depending
     * on how many screens worth of items tend to be created.
     */
    internal val pooledItems = ArrayList<ItemData>(initialPooledItems)

    /**
     * An allocated draw data that can be reused between every call to the render function. Instead of creating a new
     * object for ever render call. Save some CPU and GC cycles.
     */
    internal val reusableDrawData = CharDrawData()

    /**
     * The actions that need to be resolved before rendering can take place. This includes things such as placing
     * new items, moving items, etc. The default size might need to be changed based on how many actions tend to
     * get queued for most frames to reduce GC pressure from an internal buffer being reallocated.
     */
    internal val actionQueue = ArrayList<QueuedAction>(50)

    /**
     * Three layers: Background, Main ("play" area), UI
     *
     * Background generally has stuff that you don't interact with and is just there to add some flavor to a scene. It
     * is always below the other two layers.
     *
     * Main generally has the player, enemies, etc. Basically, stuff you interact with.
     *
     * UI is generally the bits of the screen that represent stuff like the inventory. You know, the UI :) This layer
     * will always be on top of the other layers.
     */
    internal val layers = ArrayList<ArrayList<ItemData?>>(ScreenLayer.UI.layer)

    internal var cameraX = 0
    internal var cameraY = 0

    init {
        for(i in 0..initialPooledItems-1) {
            pooledItems.add(ItemData())
        }
        if (uiOnly) {
            layers.add(ArrayList<ItemData?>(itemsPerLayer))
        }
        else {
            for(i in 0..ScreenLayer.UI.layer) {
                val newLayer = ArrayList<ItemData?>(itemsPerLayer)
                layers.add(newLayer)
                for (j in 0..itemsPerLayer) {
                    newLayer.add(null)
                }
            }
        }
    }

    /**
     * If this AsciiScreen is in UI-only mode and is not in debug mode, no matter what layer is passed in it will
     * always go to the single available layer.
     *
     * @throws IllegalArgumentException If marked for debug mode when constructed and is marked as a UI-only screen
     * and the layer argument is not the UI layer.
     */
    fun place(c: Char, x: Int, y: Int,
              w: Int = 1, h: Int = 1,
              fg: Color = Color.WHITE, bg: Color = Color.BLACK,
              attributes: Int = 0, attributeArgs: Map<String, Any?> = HashMap<String, Any?>(),
              userData: Any? = null,
              layer: ScreenLayer = if (uiOnly) ScreenLayer.UI else ScreenLayer.PLAY): PlacedItem {
        if (debug && uiOnly && layer != ScreenLayer.UI) {
            throw IllegalArgumentException("A UI-only AsciiScreen got non-UI layer for placement: $layer")
        }

        // TODO: Handle attributes and their arguments.

        val item = pooledItems.pop() ?: ItemData()
        var wrapper = PlacedItem(this)
        val layerInt = if (uiOnly) 0 else layer.layer
        wrapper.item = item
        wrapper.layer = layerInt
        actionQueue.add(QueuedAction.PlacementAction(
                item(c, x, y, w, h, fg, bg, userData, attributes, attributeArgs, debug), layerInt
        ))
        return wrapper
    }

    fun clear() {
        if(actionQueue.isNotEmpty()) {
            actionQueue.clear()
        }
        layers.forEach {
            it.forEach { if (it != null) { pooledItems.add(it) } }
            it.clear()
        }
    }

    // TODO: Change function parameter to be something that can handle multiple callbacks?
    // So it would have something like: render, actionFail, etc.
    // This would allow for an action such as movement to fail and communicate it back to the calling code.
    // Movement cannot be immediately checked, and has to be done via a deferred resolve due to there being
    // multiple things that could possibly be moving in a single frame. Resolving that will require something
    // slightly more sophisticated than just checking the moved-to square(s) for something inside them. (at least
    // I believe that to be the case, resolution of actions is yet to be implemented)
    // Is there a better way to handle this situation?
    fun render(handler: RenderHandler) {
        resolveActions(handler)

        val charW = windowWidth / charactersDrawnX
        val charH = windowHeight / charactersDrawnY
        layers.forEach {
            for (x in cameraX..charactersDrawnX-1) {
                for (y in cameraY..charactersDrawnY-1) {
                    it[get1d(x, y, width)]?.let {
                        handler.render(reusableDrawData(it, charW, charH))
                    }
                }
            }
        }
    }

    private fun resolveActions(handler: RenderHandler) {
        // TODO: Implement resolving queued actions for a screen.
        //  What order do things need to be resolved in to make this work correctly?
        //  What special considerations about actions need to be taken into account?
        //   e.g.: If something is moved, it should be undone if it collides with something?
        // TODO: Apply all actions to a "virtual" screen and resolve anything odd by comparing actual vs virtual?
        //  Sorta like double-buffering a screen, but instead we're doing it to a layer of characters. This could
        //  potentially be used to let every action immediately go through, and then resolve the end state of everything
        //  happening at once. (e.g.: two things move, and it can be inferred based on their new positions that they
        //  went "through" each other, necessitating a collision)
        //  Would it be better to do that, or would it indeed be better to look at the actual actions that have been
        //  queued and resolve from those?
        //  I'll leave this around for now, but the first attempt at resolving actions will be by just using the
        //  queue instead of something potentially fancier like the virtual screen idea.
        for (action in actionQueue) {
        }
        actionQueue.clear()
    }
}

interface RenderHandler {
    fun render(data: CharDrawData): Unit
    fun actionFailure(failure: ActionFailure): Unit
}

class RenderHandlerAdapter : RenderHandler {
    override fun render(data: CharDrawData) {
    }

    override fun actionFailure(failure: ActionFailure) {
    }
}

class PlacedItem(private val screen: AsciiScreen) {
    lateinit internal var item: ItemData
    internal var layer: Int = -1

    var x: Int
        get() = item.x
        set(v) = moveAbsolute(x = v)
    var y: Int
        get() = item.y
        set(v) = moveAbsolute(y = v)
    var w: Int
        get() = item.w
        set(v) = resize(dw = v - w)
    var h: Int
        get() = item.h
        set(v) = resize(dh = v - h)

    fun move(dx: Int = 0, dy: Int = 0) {
        screen.actionQueue.add(QueuedAction.MovementAction(item, layer, dx, dy))
    }

    fun moveAbsolute(x: Int = item.x, y: Int = item.y) {
        screen.actionQueue.add(QueuedAction.MovementAction(item, layer, x, y, absolute = true))
    }

    fun resize(dw: Int = 0, dh: Int = 0) {
        screen.actionQueue.add(QueuedAction.ResizeAction(item, layer, dw, dh))
    }

    fun resizeAbsolute(w: Int = item.w, h: Int = item.h) {
        screen.actionQueue.add(QueuedAction.ResizeAction(item, layer, w, h, absolute = true))
    }

    fun remove() {
        screen.actionQueue.add(QueuedAction.RemoveAction(item, layer))
    }
}

class CharDrawData {
    var c: Char = ' '
        internal set

    var x: Int = 0
        internal set

    var y: Int  = 0
        internal set

    var w: Int = 0
        internal set

    var h: Int = 0
        internal set

    var fg: Color = Color.WHITE
        internal set

    var bg: Color = Color.BLACK
        internal set

    var borders: Array<Line2D> = arrayOf()
        get() = field.copyOf() // Ensure internal array isn't mucked about with.
        internal set

    var userData: Any? = null
        internal set

    internal operator fun invoke(item: ItemData, w: Int, h: Int): CharDrawData {
        return this(item.toDraw, item.x, item.y, w * item.w, h * item.h, item.fg, item.bg, arrayOf<Line2D>(), item.userData)
    }

    internal operator fun invoke(newC: Char,
                                 newX: Int, newY: Int,
                                 newW: Int, newH: Int,
                                 newFg: Color, newBg: Color,
                                 newBorders: Array<Line2D>,
                                 newUserData: Any?): CharDrawData {
        c = newC
        x = newX
        y = newY
        w = newW
        h = newH
        fg = newFg
        bg = newBg
        borders = newBorders
        userData = newUserData
        return this
    }
}

// TODO: Put code into place that allows other code to react when movement fails.
// TODO: Inform external code of various things that have happened (such as failing movement or placement) so it can react to it.
//  This is necessary for various things, such as if movement fails due to colliding with another item, then external
//  code will want to resolve that in some way. Such as dealing damage between two entities if one of them is the player
//  and the other is a monster. Placing would also be useful to be informed about, so that if doing so fails, it gets
//  a chance to put something into another location. (e.g.: if trying to generate a room or an item needs a location to
//  be placed, but has to do a couple iterations to figure out where it needs to be placed)
internal sealed class QueuedAction {
    private var hasRun = false

    fun invoke(screen: AsciiScreen) {
        if(hasRun) {
            throw IllegalStateException("Cannot run an action that has already been run.")
        }
        run(screen)
        hasRun = true
    }
    fun undo(screen: AsciiScreen) {
        if(!hasRun) {
            throw IllegalStateException("Cannot undo an action that has not been run.")
        }
        undoInternal(screen)
        // We can run an action again after it has been undone, since it will be like it never happened.
        // Hopefully this doesn't have to be made use of very often.
        // But I could see a use in backing a change out temporarily for some reason.
        hasRun = false
    }

    abstract fun attempt(screen: AsciiScreen): Boolean
    abstract internal fun run(screen: AsciiScreen)
    abstract fun undoInternal(screen: AsciiScreen)

    /**
     * Place a character into a layer.
     */
    class PlacementAction(private val item: ItemData,
                          private val layer: Int) : QueuedAction() {
        var previousItems: Array<ItemData?>? = null

        override fun attempt(screen: AsciiScreen): Boolean {
            val placeLayer = screen.layers[layer]
            // Make sure to check every position over all the width/height of the character when checking if something
            // can be placed.
            for (w in 0..item.w-1) {
                for (h in 0..item.h-1) {
                    val loc = get1d(item.x + w, item.y + h, screen.width)
                    // Make sure the location is within bounds of the field.
                    if (placeLayer.size <= loc) {
                        return false
                    }
                    // If something exists in the spot we're attempting to place something into, we can't place
                    // another thing there.
                    placeLayer[loc] ?: return false
                }
            }
            return true
        }

        override fun run(screen: AsciiScreen) {
            val placeLayer = screen.layers[layer]
            val newPreviousItemsArray = Array<ItemData?>(item.w * item.h) { null }
            previousItems = newPreviousItemsArray
            for (w in 0..item.w-1) {
                for (h in 0..item.h-1) {
                    val loc = get1d(item.x + w, item.y + h, screen.width)
                    newPreviousItemsArray[get1d(w, h, item.w)] = placeLayer[loc]
                    placeLayer[loc] = item
                }
            }
        }

        override fun undoInternal(screen: AsciiScreen) {
            val placeLayer = screen.layers[layer]
            val previousItemsArray = previousItems ?: throw IllegalStateException("Cannot undo placement because previous items is somehow null.")
            for (w in 0..item.w-1) {
                for (h in 0..item.h-1) {
                    placeLayer[get1d(item.x + w, item.y + h, screen.width)] = previousItemsArray[get1d(w, h, item.w)]
                }
            }
        }
    }

    /**
     * Remove a character from a layer.
     */
    class RemoveAction(private val item: ItemData,
                       private val layer: Int) : QueuedAction() {
        override fun attempt(screen: AsciiScreen): Boolean {
            // TODO: Use the location of the item as a place to look for it in the layer
            return screen.layers[layer].any { it === item }
        }

        override fun run(screen: AsciiScreen) {
            var found = false
            screen.layers[layer].replaceAll { if (it === item) { found = true; null } else it }
            if(screen.debug && !found) {
                throw IllegalStateException("Cannot remove '$item' which does not exist.")
            }
        }

        override fun undoInternal(screen: AsciiScreen) {
            // TODO: Refactor placement's run method into something outside the object so we don't have to allocate one here?
            // Until then, hopefully the JVM will at least mostly optimize this.
            QueuedAction.PlacementAction(item, layer).run(screen)
        }
    }

    class DrawableChangeAction(private val item: ItemData,
                               private val newDraw: Char, private val oldDraw: Char) : QueuedAction() {
        override fun attempt(screen: AsciiScreen): Boolean {
            return true
        }

        override fun run(screen: AsciiScreen) {
            item.toDraw = newDraw
        }

        override fun undoInternal(screen: AsciiScreen) {
            item.toDraw = oldDraw
        }
    }

    class MovementAction(private val item: ItemData, private val layer: Int,
                         private val dx: Int, private val dy: Int,
                         private val absolute: Boolean = false) : QueuedAction() {
        val remove = RemoveAction(item, layer)
        val place = PlacementAction(item, layer)
        val oldX = item.x
        val oldY = item.y
        val newX = if (absolute) dx else item.x + dx
        val newY = if (absolute) dy else item.y + dy

        private fun setItemPosition() {
            item.x = newX
            item.y = newY
        }

        private fun resetItemPosition() {
            item.x = oldX
            item.y = oldY
        }

        override fun attempt(screen: AsciiScreen): Boolean {
            setItemPosition()
            return remove.attempt(screen) && place.attempt(screen)
        }

        override fun undoInternal(screen: AsciiScreen) {
            // Always make sure the position is in a consistent state before running any actions. We want to ensure
            // that any oddities in when the various methods are called does not change how the action is run.
            setItemPosition()
            place.undoInternal(screen)
            resetItemPosition()
            remove.undoInternal(screen)
        }

        override fun run(screen: AsciiScreen) {
            resetItemPosition()
            remove.run(screen)
            setItemPosition()
            place.run(screen)
        }
    }

    /**
     * Resizes an item.
     *
     * When an item is becoming bigger, it will always attempt to center the newly-sized item on the original
     * center. If it cannot be perfectly centered (e.g.: 1x1 to 2x2 cannot be perfectly centered, but a
     * 1x1 to 3x3 can be), it will expand the size right and down. So for example:
     *
     * 1x1      2x2
     * .#.  ->  .##
     * ...      .##
     *
     * 1x1      3x3
     * ...      ###
     * .#.  ->  ###
     * ...      ###
     *
     * When an item is becoming smaller, it will do the same as larger except in reverse. For example:
     *
     * 2x2      1x2
     * .##  ->  .#.
     * .##      .#.
     *
     * 2x2      2x1
     * .##  ->  .##
     * .##      ...
     *
     * 3x3      1x1
     * ###      ...
     * ###  ->  .#.
     * ###      ...
     *
     * This action can fail if it attempts to resize an item such that it attempts to replace another item
     * in the grid. When it's being resized, in essence the item is having itself placed in more/less spots
     * in the layer.
     */
    class ResizeAction(private val item: ItemData, private val layer: Int,
                       private val dw: Int, private val dh: Int,
                       private val absolute: Boolean = false): QueuedAction() {
        val remove = RemoveAction(item, layer)
        val place = PlacementAction(item, layer)
        val newW = if (absolute) dw else item.w + dw
        val newH = if (absolute) dh else item.h + dh
        val oldW = item.w
        val oldH = item.h

        override fun attempt(screen: AsciiScreen): Boolean {
            throw UnsupportedOperationException()
        }

        override fun undoInternal(screen: AsciiScreen) {
            throw UnsupportedOperationException()
        }

        override fun run(screen: AsciiScreen) {
            // TODO: Remove old item references before resizing.
            //  Necessary if something becomes smaller. We don't want old references to an item in places that it
            //  doesn't exist anymore due to becoming smaller.
            throw UnsupportedOperationException()
        }
    }
}

sealed class ActionFailure {
    class Movement(val data1: CharDrawData, val data2: CharDrawData)
}

data class BorderLine(val x1: Int, val y1: Int, val x2: Int, val y2: Int)
internal data class BorderData(val line: BorderLine, val color: Color)

internal class ItemData(var toDraw: Char,
                        var x: Int, var y: Int,
                        var w: Int, var h: Int,
                        var fg: Color, var bg: Color,
                        var userData: Any?) {

    var blinkText = false
    var blinkTextRate = TEXT_BLINKING_DELAY_DEFAULT

    var drawBorder = false
    var borders: MutableList<BorderData>? = null

    var autoFancyWall = false
    var preferSingleLine = false
    var preferDoubleLine = false
    var fancyWallOnlyBorder = false
    var fancyWallIncludeBorder = false

    constructor() : this(' ', -1, -1, -1, -1, Color.WHITE, Color.BLACK, null) {}

    operator fun invoke(newDraw: Char, newX: Int, newY: Int, newW: Int, newH: Int,
                        newFg: Color, newBg: Color, newUserData: Any?,
                        attributes: Int, attributeArgs: Map<String, Any?>,
                        debug: Boolean): ItemData {
        toDraw = newDraw
        x = newX
        y = newY
        w = newW
        h = newH
        fg = newFg
        bg = newBg
        userData = newUserData
        buildAttributes(attributes, attributeArgs, debug)
        return this
    }

    private fun buildAttributes(attributes: Int, attributeArgs: Map<String, Any?>, debug: Boolean) {
        if (attributes and TEXT_BLINKING == TEXT_BLINKING) {
            blinkText = true
            val rate = attributeArgs[TEXT_BLINKING_DELAY_ARG_NAME]
            blinkTextRate = if (rate is Int) rate else null ?: blinkTextRate
        }

        if (attributes bitSet TEXT_BORDER_LEFT) {
            addBorder(attributeArgs, TEXT_BORDER_COLOR_LEFT_ARG_NAME, BorderLine(x, y, x, y + h), debug)
        }
        if (attributes bitSet TEXT_BORDER_RIGHT) {
            addBorder(attributeArgs, TEXT_BORDER_COLOR_RIGHT_ARG_NAME, BorderLine(x + w, y, x + w, y + h), debug)
        }
        if (attributes bitSet TEXT_BORDER_TOP) {
            addBorder(attributeArgs, TEXT_BORDER_COLOR_TOP_ARG_NAME, BorderLine(x, y, x + w, y), debug)
        }
        if (attributes bitSet TEXT_BORDER_BOTTOM) {
            addBorder(attributeArgs, TEXT_BORDER_COLOR_BOTTOM_ARG_NAME, BorderLine(x, y + h, x + w, y + h), debug)
        }

        if (attributes bitSet TEXT_AUTO_FANCY_WALL) {
            autoFancyWall = true
            val single = attributeArgs[TEXT_FANCY_WALL_PREFER_SINGLE_ARG_NAME]
            var double = attributeArgs[TEXT_FANCY_WALL_PREFER_DOUBLE_ARG_NAME]
            var onlyBorder = attributeArgs[TEXT_FANCY_WALL_ONLY_BORDERS_ARG_NAME]
            var includeBorder = attributeArgs[TEXT_FANCY_WALL_INCLUDE_BORDERS_ARG_NAME]

            if (debug) {
                if (single != null && single !is Boolean) {
                    throw IllegalArgumentException("Got a single wall preference argument that was not a boolean: $single")
                }
                if (double != null && double !is Boolean) {
                    throw IllegalArgumentException("Got a double wall preference argument that was not a boolean: $double")
                }
                if (onlyBorder != null && onlyBorder !is Boolean) {
                    throw IllegalArgumentException("Got an only border argument that was not a boolean: $onlyBorder")
                }
                if (includeBorder != null && includeBorder !is Boolean) {
                    throw IllegalArgumentException("Got an include border argument that was not a boolean: $includeBorder")
                }

                if (single != null && double != null && single is Boolean && double is Boolean) {
                    if (single && double) {
                        throw IllegalArgumentException("Cannot prefer both single line and double line fancy walls.")
                    }
                }
                if (onlyBorder != null && includeBorder != null && onlyBorder is Boolean && includeBorder is Boolean) {
                    if (onlyBorder && includeBorder) {
                        throw IllegalArgumentException("Cannot have fancy walls be only borders and include borders.")
                    }
                }
            }

            if (single != null && single is Boolean && single) {
                preferSingleLine = single
            }
            else if (double != null && double is Boolean && double) {
                preferDoubleLine = double
            }
            if (onlyBorder != null && onlyBorder is Boolean && onlyBorder) {
                fancyWallOnlyBorder = onlyBorder
            }
            else if (includeBorder != null && includeBorder is Boolean && includeBorder) {
                fancyWallIncludeBorder = includeBorder
            }
        }
    }

    private fun addBorder(attributeArgs: Map<String, Any?>, arg: String, line: BorderLine,
                          debug: Boolean) {
        if (debug) {
            if (attributeArgs[arg] != null && attributeArgs[arg] !is Color) {
                throw IllegalArgumentException("Got an argument for border '$arg' that was non-null and not a color: ${attributeArgs[arg]}")
            }
            if (attributeArgs[TEXT_BORDER_COLOR_ALL_ARG_NAME] != null && attributeArgs[TEXT_BORDER_COLOR_ALL_ARG_NAME] !is Color) {
                throw IllegalArgumentException("Got an argument for border 'ALL' that was non-null and not a color: ${attributeArgs[TEXT_BORDER_COLOR_ALL_ARG_NAME]}")
            }
        }

        if (borders == null) {
            borders = ArrayList(4)
        }
        drawBorder = true
        val color = attributeArgs[arg] ?: attributeArgs[TEXT_BORDER_COLOR_ALL_ARG_NAME]
        borders?.add(BorderData(line, if (color is Color) color else null ?: TEXT_BORDER_COLOR_DEFAULT))
    }
}
