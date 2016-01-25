package com.ud.plus1torch.screen

import java.awt.Color
import java.awt.Font
import java.awt.geom.Line2D
import java.util.*

/**
 * Created by vince_000 on 11/23/2015.
 */


enum class ScreenLayer(val layer: Int) {
    BACKGROUND(0),
    PLAY(1),
    UI(2)
}

public val TEXT_BLINKING = 0x0001

// Since this engine isn't ACTUALLY a terminal, we can do things that aren't possible to do in a terminal, such as put
// borders around a character.
// Can accept an extra attribute argument with the color a border should be drawn as. If not set, then the background
// color will be used as the default.
public val TEXT_BORDER_COLOR_ARG_NAME = "BorderColorArgument"
public val TEXT_BORDER_RIGHT = 0x0002
public val TEXT_BORDER_LEFT = 0x0004
public val TEXT_BORDER_TOP = 0x0008
public val TEXT_BORDER_BOTTOM = 0x0010
public val TEXT_BORDER_ALL = TEXT_BORDER_RIGHT or TEXT_BORDER_LEFT or TEXT_BORDER_TOP or TEXT_BORDER_BOTTOM

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
public val TEXT_AUTO_FANCY_WALL = 0x0020

/**
 * Every character in Code Page 437, in the order they are defined in. Excluding the first character, as there
 * didn't seem to be a unicode definition for it on wikipedia:
 *   https://en.wikipedia.org/wiki/Code_page_437
 * The first character is just a space, to make sure that all the rest of the characters line up correctly. This
 * should be used as a basis for building the UI/play area for a game.
 * TODO: Define some index values for commonly-used characters, such as walls and fancy walls.
 */
public val CP437 = charArrayOf(
        ' ', '\u263A', '\u263B', '\u2665', '\u2666', '\u2663', '\u2660', '\u2022',
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
    return if (this.size > 0) this.removeAt(this.size - 1) else null;
}

fun get1d(x: Int, y: Int, stride: Int): Int {
    return x + y * stride;
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
 * TODO: Change some ArrayLists to use pure arrays is if makes sense.
 *  It makes sense if we don't need to expand the array at any point during runtime. This is likely to only apply to
 *  the data in the field. Other lists need to be able to handle any number of things that change at runtime.
 *
 * @param width: Number of characters that can be stored left to right across the field.
 * @param height: Number of characters that can be stored up to down across the field.
 * @param charactersDrawnX: The number of characters to draw left to right in the window.
 * @param charactersDrawnY: The number of characters to draw up to down in the window.
 * @param windowWidth: The width of the window being rendered into.
 * @param windowHeight: The height of the window being rendered into.
 * @param uiOnly: Whether or not to put this AsciiScreen into a mode that only has a single layer for the UI. Allocates
 * fewer object, but should not be used for a screen that has a play area. Defaults to false.
 * @param debug: Enables some extra checks/printing/exceptions such that improper use of the AsciiScreen or anything
 * returned from it will always indicate that it was used improperly. When not in this mode, the AsciiScreen and
 * anything it returns will do its best to "just work". Any behavior that changes when this mode is on will be
 * documented. Defaults to false.
 */
class AsciiScreen(internal var width: Int, internal var height: Int,
                  charactersDrawnX: Int, charactersDrawnY: Int,
                  windowWidth: Int, windowHeight: Int,
                  internal val uiOnly: Boolean = false,
                  internal val debug: Boolean = false) {
    /**
     * The number of characters that will be drawn from left to right in this AsciiScreen.
     */
    public var charactersDrawnX = charactersDrawnX
        /**
         * Change the number of characters that are drawn on the screen from left to right. This will change how
         * big each character gets drawn in the game window.
         */
        set(value) {
            field = value
            // TODO: Modify fonts and whatnot based on the new number of characters being drawn.
        }

    /**
     * The number of characters that will be drawn from top to bottom in this AsciiScreen.
     */
    public var charactersDrawnY = charactersDrawnY
        /**
         * Change the number of characters that are drawn on the screen from top to bottom. This will change how
         * big each character gets drawn in the game window.
         */
        set(value) {
            field = value
            // TODO: Modify fonts and whatnot based on the new number of characters being drawn.
        }

    /**
     * The width of the area of the screen that this AsciiScreen will be drawn into. This can be changed.
     */
    public var windowWidth = windowWidth
        /**
         * Change the width in pixels that this screen is drawing into. This will change how big each characters is
         * that gets drawn in the game window.
         */
        set(value) {
            field = value
            // TODO: Modify fonts and whatnot based on the new width of the drawable area.
        }

    /**
     * The height of the area of the screen that this AsciiScreen will be drawn into. This can be changed.
     */
    public var windowHeight = windowHeight
        /**
         * Change the height in pixels that this screen is drawing into. This will change how big each characters is
         * that gets drawn in the game window.
         */
        set(value) {
            field = value
            // TODO: Modify fonts and whatnot based on the new height of the drawable area.
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
     * Allocated with enough capacity to hold the same number of pre-allocated items, as each item gets wrapped in
     * a proxy so that externally nothing has to worry about finicky details such as queueing up actions. It just
     * provides a nice API. Items are purely internal instead of being an outward-facing API.
     */
    internal val pooledItemProxies = ArrayList<PlacedItem>(initialPooledItems)

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

    // How fonts work internally will likely change. Just a single font is likely simplistic when items can be larger
    // than 1x1. A double size font is likely desired if something is 2x2, for example.
    // TODO: Figure out font handling so every drawn character will look nice, regardless of width and height.

    init {
        for(i in 0..initialPooledItems-1) {
            pooledItems.add(ItemData(' ', -1, -1, -1, -1))
            pooledItemProxies.add(PlacedItem(this))
        }
        if (uiOnly) {
            layers.add(ArrayList<ItemData?>(itemsPerLayer))
        }
        else {
            for(i in 0..ScreenLayer.UI.layer) {
                layers.add(ArrayList<ItemData?>(itemsPerLayer))
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
              layer: ScreenLayer = if (uiOnly) ScreenLayer.UI else ScreenLayer.PLAY): PlacedItem {
        if (debug && uiOnly && layer != ScreenLayer.UI) {
            throw IllegalArgumentException("A UI-only AsciiScreen got non-UI layer for placement: $layer")
        }

        // TODO: Handle attributes and their arguments.
        // TODO: Handle foreground and background colors.

        val item = pooledItems.pop() ?: ItemData(' ', -1, -1, -1, -1);
        var wrapper = pooledItemProxies.pop() ?: PlacedItem(this);
        val layerInt = if (uiOnly) 0 else layer.layer
        wrapper.item = item;
        wrapper.layer = layerInt;
        actionQueue.add(QueuedAction.PlacementAction(
                item(c, x, y, w, h), layerInt
        ));
        return wrapper;
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

    fun render(f: (Char, Int, Int, Font) -> Unit) {
        resolveActions()
        // TODO: Handle characters of different sizes.
        //  This should be done by calculating the end size of the character and somehow making sure
        //  the function renders the character at that size.
        // TODO: Somehow represent the information necessary for drawing a character in a friendly way.
        //  Basically I don't want any function that is called for rendering to having to deal with all sorts of
        //  complexity due to the various options that are available to be used when placing a character. As much
        //  as possible I want something relatively abstract and easy to use to be given to the render function
        //  to keep the logic in it to a minimum and keep it able to run pretty fast.
        // ? How do we handle drawing borders in a nice way?
        // TODO: Only iterate over the possible item data in the camera.
        layers.forEach {
            it.forEach {
                it?.let {
                    // TODO: Build the reusable draw data to pass to render function.
//                    f(it.toDraw, it.x, it.y, font)
                }
            }
        }
    }

    private fun resolveActions() {
        // TODO: Implement resolving queued actions for a screen.
        //  What order do things need to be resolved in to make this work correctly?
        //  What special considerations about actions need to be taken into account?
        //   e.g.: If something is moved, it should be undone if it collides with something?
        for (action in actionQueue) {
        }
        actionQueue.clear()
    }
}

public class PlacedItem(private val screen: AsciiScreen) {
    lateinit internal var item: ItemData;
    internal var layer: Int = -1;

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
        screen.actionQueue.add(QueuedAction.MovementAction(item, dx, dy))
    }

    fun moveAbsolute(x: Int = item.x, y: Int = item.y) {
        screen.actionQueue.add(QueuedAction.MovementAction(item, x, y, absolute = true))
    }

    fun resize(dw: Int = 0, dh: Int = 0) {
        screen.actionQueue.add(QueuedAction.ResizeAction(item, dw, dh))
    }

    fun resizeAbsolute(w: Int = item.w, h: Int = item.h) {
        screen.actionQueue.add(QueuedAction.ResizeAction(item, w, h, absolute = true))
    }

    fun remove() {
        screen.actionQueue.add(QueuedAction.RemoveAction(item, layer))
    }
}

public class CharDrawData {
    public var c: Char = ' '
        internal set(value) {
            field = value
        }

    public var x: Int = 0
        internal set(value) {
            field = value
        }

    public var y: Int  = 0
        internal set(value) {
            field = value
        }

    public var fg: Color = Color.WHITE
        internal set(value) {
            field = value
        }

    public var bg: Color = Color.BLACK
        internal set(value) {
            field = value
        }

    public var borders: Array<Line2D> = arrayOf()
        get() = field.copyOf() // Ensure internal array isn't mucked about with.
        internal set(value) {
            field = value
        }
}

internal sealed class QueuedAction {
    private var hasRun = false;

    final fun invoke(screen: AsciiScreen) {
        if(hasRun) {
            throw IllegalStateException("Cannot run an action that has already been run.");
        }
        run(screen);
        hasRun = true;
    }
    final fun undo(screen: AsciiScreen) {
        if(!hasRun) {
            throw IllegalStateException("Cannot undo an action that has not been run.");
        }
        undoInternal(screen);
        // We can run an action again after it has been undone, since it will be like it never happened.
        // Hopefully this doesn't have to be made use of very often.
        // But I could see a use in backing a change out temporarily for some reason.
        hasRun = false
    }

    abstract fun attempt(screen: AsciiScreen): Boolean;
    abstract internal fun run(screen: AsciiScreen);
    abstract fun undoInternal(screen: AsciiScreen);

    class PlacementAction(private val item: ItemData,
                          private val layer: Int) : QueuedAction() {
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
            for (w in 0..item.w-1) {
                for (h in 0..item.h-1) {
                    placeLayer[get1d(item.x + w, item.y + h, screen.width)] = item
                }
            }
        }

        override fun undoInternal(screen: AsciiScreen) {
            // TODO: Implement undoing a placed character.
            throw UnsupportedOperationException()
        }
    }

    class RemoveAction(private val item: ItemData,
                       private val layer: Int) : QueuedAction() {
        override fun attempt(screen: AsciiScreen): Boolean {
            return true
        }

        override fun run(screen: AsciiScreen) {
            // TODO: Remove all items across the width and height of the character.
            if(!screen.layers[layer].remove(item)) {
                throw IllegalStateException("Cannot remove '$item' which does not exist.");
            }
        }

        override fun undoInternal(screen: AsciiScreen) {
            screen.layers[layer].add(item);
        }
    }

    class DrawableChangeAction(private val item: ItemData,
                               private val newDraw: Char, private val oldDraw: Char) : QueuedAction() {
        override fun attempt(screen: AsciiScreen): Boolean {
            return true
        }

        override fun run(screen: AsciiScreen) {
            throw UnsupportedOperationException()
        }

        override fun undoInternal(screen: AsciiScreen) {
            throw UnsupportedOperationException()
        }
    }

    class MovementAction(private val item: ItemData,
                         private val dx: Int, private val dy: Int,
                         private val absolute: Boolean = false) : QueuedAction() {
        override fun attempt(screen: AsciiScreen): Boolean {
            // TODO: Put code into place that allows other code to react when movement fails.
            throw UnsupportedOperationException()
        }

        override fun undoInternal(screen: AsciiScreen) {
            throw UnsupportedOperationException()
        }

        override fun run(screen: AsciiScreen) {
            // TODO: Make sure to remove all old item placements before adding the new ones.
            //  Likely it's not desirable to have a remove then a place queued instead of having a move, as that
            //  might cause issues due to having two separate actions. Movement can also check that something can
            //  move, and respond in some way based on the inability to move. (e.g.: attack a monster if you bump
            //  into them)
            throw UnsupportedOperationException()
        }
    }

    class ResizeAction(private val item: ItemData,
                       private val dw: Int, private val dh: Int,
                       private val absolute: Boolean = false): QueuedAction() {
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

internal class ItemData(var toDraw: Char,
                        var x: Int, var y: Int,
                        var w: Int, var h: Int) {
    constructor() : this(' ', -1, -1, -1, -1) {}

    operator fun invoke(newDraw: Char, newX: Int, newY: Int, newW: Int, newH: Int): ItemData {
        toDraw = newDraw;
        x = newX;
        y = newY;
        w = newW;
        h = newH;
        return this;
    }
}
