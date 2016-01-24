package com.ud.quill.screen

import sun.plugin.dom.exception.InvalidStateException
import java.awt.Color
import java.awt.Font
import java.util.*

/**
 * Created by vince_000 on 11/23/2015.
 */


enum class ScreenLayer(val layer: Int) {
    BACKGROUND(0),
    PLAY(1),
    UI(2)
}


fun <T> ArrayList<T>.pop(): T {
    return this.removeAt(this.size - 1);
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
 * @param width: Number of characters that can be stored left to right across the field.
 * @param height: Number of characters that can be stored up to down across the field.
 * @param charactersDrawnX: The number of characters to draw left to right in the window.
 * @param charactersDrawnY: The number of characters to draw up to down in the window.
 * @param windowWidth: The width of the window being rendered into.
 * @param windowHeight: The height of the window being rendered into.
 */
class AsciiScreen(internal var width: Int, internal var height: Int,
                  internal var charactersDrawnX: Int, internal var charactersDrawnY: Int,
                  internal var windowWidth: Int, internal var windowHeight: Int) {
    /**
     * The number of items that fit on a single screen in a layer if every item was 1x1.
     */
    internal val itemsPerScreenPerLayer = width * height;

    internal val initialPooledItems = itemsPerScreenPerLayer * 3 + 1

    /**
     * Allocated with enough capacity to hold 3 screens worth of items (if every item is 1x1) for every layer. Should
     * give a nice amount of items to work with at first. This value might need to be tuned going forward depending
     * on how many screens worth of items tend to be created.
     */
    internal val pooledItems = ArrayList<ItemData?>(initialPooledItems);

    /**
     * Allocated with enough capacity to hold the same number of pre-allocated items, as each item gets wrapped in
     * a proxy so that externally nothing has to worry about finicky details such as queueing up actions. It just
     * provides a nice API. Items are purely internal instead of being an outward-facing API.
     */
    internal val pooledItemProxies = ArrayList<PlacedItem?>(initialPooledItems);

    /**
     * The actions that need to be resolved before rendering can take place. This includes things such as placing
     * new items, moving items, etc. The default size might need to be changed based on how many actions tend to
     * get queued for most frames to reduce GC pressure from an internal buffer being reallocated.
     */
    internal val actionQueue = ArrayList<QueuedAction>(50);

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
    internal val layers = ArrayList<ArrayList<ItemData?>>(ScreenLayer.UI.layer);

    // How fonts work internally will likely change. Just a single font is likely simplistic when items can be larger
    // than 1x1. A double size font is likely desired if something is 2x2, for example.
    // TODO: Figure out font handling so every drawn character will look nice, regardless of width and height.

    init {
        for(i in 0..initialPooledItems-1) {
            pooledItems.add(ItemData(' ', -1, -1, -1, -1));
            pooledItemProxies.add(PlacedItem(this));
        }
        for(i in 0..ScreenLayer.UI.layer) {
            layers.add(ArrayList<ItemData?>(itemsPerScreenPerLayer));
        }
    }

    fun place(c: Char, x: Int, y: Int,
              w: Int = 1, h: Int = 1,
              fg: Color = Color.WHITE, bg: Color = Color.BLACK,
              attributes: Int = 0,
              layer: ScreenLayer = ScreenLayer.PLAY): PlacedItem {
        val item = pooledItems.pop() ?: ItemData(' ', -1, -1, -1, -1);
        var wrapper = pooledItemProxies.pop() ?: PlacedItem(this);
        wrapper.item = item;
        wrapper.layer = layer.layer;
        actionQueue.add(QueuedAction.PlacementAction(
                item(c, x, y, w, h), layer.layer
        ));
        return wrapper;
    }

    fun clear() {
        if(actionQueue.isNotEmpty()) {
            throw InvalidStateException("Cannot clear the screen when actions are queued!");
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
        layers.forEach {
            it.forEach {
                it?.let {
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
    }
}

class PlacedItem(private val screen: AsciiScreen) {
    lateinit internal var item: ItemData;
    internal var layer: Int = -1;

    var x: Int
        get() = item.x;
        set(v) = move(dx = v - x);
    var y: Int
        get() = item.y;
        set(v) = move(dy = v - y);
    var w: Int
        get() = item.w;
        set(v) = resize(dw = v - w);
    var h: Int
        get() = item.h
        set(v) = resize(dh = v - h);

    fun move(dx: Int = 0, dy: Int = 0) {
        screen.actionQueue.add(QueuedAction.MovementAction(item, dx, dy));
    }

    fun moveAbsolute(x: Int = 0, y: Int = 0) {
        screen.actionQueue.add(QueuedAction.MovementAction(item, x, y, absolute = true));
    }

    fun resize(dw: Int = 0, dh: Int = 0) {
        screen.actionQueue.add(QueuedAction.ResizeAction(item, dw, dh));
    }

    fun resizeAbsolute(w: Int = 0, h: Int = 0) {
        screen.actionQueue.add(QueuedAction.ResizeAction(item, w, h, absolute = true));
    }

    fun remove() {
        screen.actionQueue.add(QueuedAction.RemoveAction(item, layer));
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

    abstract internal fun run(screen: AsciiScreen);
    abstract fun undoInternal(screen: AsciiScreen);

    class PlacementAction(private val item: ItemData,
                          private val layer: Int) : QueuedAction() {
        override fun run(screen: AsciiScreen) {
            val loc = get1d(item.x, item.y, screen.width);
            val existing = screen.layers[layer][loc];
            // TODO: Implement placing a character.
            //  This should take into account the size of the character along with allowing characters
            //  to be placed "off screen" so that a moved camera might start to send them to be drawn
            //  in the function passed to render.
            throw UnsupportedOperationException()
        }

        override fun undoInternal(screen: AsciiScreen) {
            // TODO: Implement undoing a placed character.
            throw UnsupportedOperationException()
        }
    }

    class RemoveAction(private val item: ItemData,
                       private val layer: Int) : QueuedAction() {
        override fun run(screen: AsciiScreen) {
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
        override fun undoInternal(screen: AsciiScreen) {
            throw UnsupportedOperationException()
        }

        override fun run(screen: AsciiScreen) {
            throw UnsupportedOperationException()
        }
    }

    class ResizeAction(private val item: ItemData,
                       private val dw: Int, private val dh: Int,
                       private val absolute: Boolean = false): QueuedAction() {
        override fun undoInternal(screen: AsciiScreen) {
            throw UnsupportedOperationException()
        }

        override fun run(screen: AsciiScreen) {
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

internal class DataStore(private var width: Int, private var height: Int) {
    fun addItem(item: ItemData): DataStoreUpdater {
        return DataStoreUpdater(item);
    }

    fun updateItem(item: ItemData) {
    }

    fun itemExistsAt(x: Int, y: Int): Boolean {
        return false;
    }
}

internal class DataStoreUpdater(internal val item: ItemData) {
    fun update() {
    }
}